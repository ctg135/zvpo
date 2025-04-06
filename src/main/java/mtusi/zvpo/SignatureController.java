package mtusi.zvpo;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/signature")
public class SignatureController {
    private SignatureRepository signatureRepository;

    public SignatureController(SignatureRepository signatureRepository){
        this.signatureRepository = signatureRepository;
    }

    /*
    TODO: Контроллеры
        0. Полный возврат всех сигнатур GET
            Возвращает все сигнатуры с нормальным статусом (не DELETED и т.п.)
        1. Получение диффа POST
            По параметру since получить все сигнатуры с updated_at позднее
        2. По GUID
            По списку GUID вернуть список сигнатур
        3. Добавление новой сигнатуры
        4. Удаление сигнатуры
            Ставит сигнатуре статус DELETED
        5. Обновление записи
            1. По новым данным создается новая запись
            2. На старой записи ставится статус UPDATED
            3. Измененные поля записываются в таблицу audit
        6. Доступ к записям по статусу
            По статусу вернуть список сигнатур
     */

    @GetMapping
    @RequestMapping("/get")
    public ResponseEntity<List<SignatureEntity>> getSignatures(){
        // Список актуальных сигнатур
        List<SignatureEntity> signatures = signatureRepository.findByStatus("ACTUAL");
        return ResponseEntity.status(200).body(signatures);
    }

    @PostMapping
    @RequestMapping("/get/diff")
    public ResponseEntity<List<SignatureEntity>> getSignaturesByDiff(
            @RequestBody
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Date since){
        // Список сигнатур с определенной даты
        var signatures = signatureRepository.findByUpdatedAtAfter(since);
        return ResponseEntity.status(200).body(signatures);
    }

    @PostMapping
    @RequestMapping("/get/uuids")
    public ResponseEntity<List<SignatureEntity>> getSignaturesByUUID(
            @BindParam("uuids") List<UUID> uuids){
        // Список сигнатур по списку UUID
        /*
        TODO: Падает на этапе обработки входных данных массива
         */
        var signatures = signatureRepository.findByIdIn(uuids);
        return ResponseEntity.status(200).body(signatures);
    }

}
