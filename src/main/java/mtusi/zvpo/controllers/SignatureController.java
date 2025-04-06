package mtusi.zvpo.controllers;

import mtusi.zvpo.controllers.requestEntities.SignatureRequestByDiff;
import mtusi.zvpo.controllers.requestEntities.SignatureRequestByUUIDs;
import mtusi.zvpo.entites.SignatureEntity;
import mtusi.zvpo.repositories.SignatureRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestBody SignatureRequestByDiff body){
        // Список сигнатур с определенной даты
        var signatures = signatureRepository.findByUpdatedAtAfter(body.since);
        return ResponseEntity.status(200).body(signatures);
    }

    @PostMapping
    @RequestMapping("/get/uuid")
    public ResponseEntity<List<SignatureEntity>> getSignaturesByUUID(
            @RequestBody SignatureRequestByUUIDs body){
        // Список сигнатур по списку UUID
        var signatures = signatureRepository.findByIdIn(body.uuids);
        return ResponseEntity.status(200).body(signatures);
    }

}
