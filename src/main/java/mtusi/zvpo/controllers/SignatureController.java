package mtusi.zvpo.controllers;

import mtusi.zvpo.controllers.requestEntities.*;
import mtusi.zvpo.entites.SignatureEntity;
import mtusi.zvpo.repositories.SignatureRepository;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    @RequestMapping("/get")
    public ResponseEntity<List<SignatureEntity>> getSignatures(){
        // Список актуальных сигнатур
        List<SignatureEntity> signatures = signatureRepository.findByStatus("ACTUAL");
        return ResponseEntity.status(200).body(signatures);
    }

    @PostMapping
    @RequestMapping("/get/status")
    public ResponseEntity<List<SignatureEntity>> getSignaturesByStatus(
            @RequestBody SignatureRequestByStatus body){
        // Список сигнатур по статусу
        List<SignatureEntity> signatures = signatureRepository.findByStatus(body.status);
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

    @PostMapping
    @RequestMapping("/add")
    public ResponseEntity<SignatureEntity> addSignature(
            @RequestBody SignatureRequestAdd signature){
        // Создает сигнатуру по запросу
        /*
        TODO: При добавлении алгоритма хеширования
         пересмотреть работу контроллера
        */
        SignatureEntity newItem = new SignatureEntity();
        newItem.threatName           = signature.threatName;
        newItem.firstBytes           = signature.firstBytes;
        newItem.remainderHash        = signature.remainderHash;
        newItem.remainderLength      = signature.remainderLength;
        newItem.fileType             = signature.fileType;
        newItem.offsetStart          = signature.offsetStart;
        newItem.offsetEnd            = signature.offsetEnd;
        newItem.updatedAt            = signature.updatedAt;
        newItem.status               = signature.status;
        var addedItem = this.signatureRepository.save(newItem);
        return ResponseEntity.status(200).body(addedItem);
    }

    @DeleteMapping
    @RequestMapping("/delete")
    public ResponseEntity<Void> deleteSignatureByUUID(
            @RequestBody SignatureRequestDeleteByUUID body){
        // "Удаление" записи по UUID
        // TODO: Добавить аудит
        var signature = signatureRepository.findById(body.uuid);
        if (signature.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        var deleted = signature.get();
        deleted.status = "DELETED";
        this.signatureRepository.save(deleted);

        return ResponseEntity.status(200).build();
    }

    @PostMapping
    @RequestMapping("/update")
    public ResponseEntity<Void> updateSignature(
            @RequestBody SignatureEntity signature){
        // Обновление сигнатуры
        // TODO: добавить аудит
        //        1. По новым данным создается новая запись
        //        2. На старой записи ставится статус UPDATED
        //        3. Измененные поля записываются в таблицу audit
        signatureRepository.save(signature);
        return ResponseEntity.status(200).build();
    }
}
