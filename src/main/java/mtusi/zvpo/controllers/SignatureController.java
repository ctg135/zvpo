package mtusi.zvpo.controllers;

import mtusi.zvpo.RabinKarpAlgorithm;
import mtusi.zvpo.controllers.requestEntities.*;
import mtusi.zvpo.entites.AuditEntity;
import mtusi.zvpo.entites.HistoryEntity;
import mtusi.zvpo.entites.SignatureEntity;
import mtusi.zvpo.repositories.AuditRepository;
import mtusi.zvpo.repositories.HistoryRepository;
import mtusi.zvpo.repositories.SignatureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/signature")
public class SignatureController {
    private SignatureRepository signatureRepository;
    private HistoryRepository historyRepository;
    private AuditRepository auditRepository;

    public SignatureController(
            SignatureRepository signatureRepository,
            HistoryRepository historyRepository,
            AuditRepository auditRepository){
        this.signatureRepository    = signatureRepository;
        this.auditRepository        = auditRepository;
        this.historyRepository      = historyRepository;
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
            @RequestBody SignatureAddRaw add){
        // Создает сигнатуру по запросу
        var algorithm = new RabinKarpAlgorithm();
        var signature = algorithm.extractSignature(add.input);

        signature.id = null;
        signature.fileType = add.fileType;
        signature.threatName = add.name;
        signature.updatedAt = new Date();
        signature.status = "ACTUAL";
        var addedItem = this.signatureRepository.save(signature);

        var audit = AuditEntity.generateAdded(addedItem);
        auditRepository.save(audit);

        return ResponseEntity.status(200).body(addedItem);
    }

    @PostMapping
    @RequestMapping("/scan")
    public ResponseEntity<List<SignatureEntity>> scanSignature(
            @RequestBody String input){
        // Сканирует сигнтуру (тесты) в сыром формате
        var algorithm = new RabinKarpAlgorithm();
        var result = algorithm.scanString(input, signatureRepository.findAll());

        return ResponseEntity.status(200).body(result);
    }

    @DeleteMapping
    @RequestMapping("/delete")
    public ResponseEntity<Void> deleteSignatureByUUID(
            @RequestBody SignatureRequestDeleteByUUID body){
        // "Удаление" записи по UUID
        var signature = signatureRepository.findById(body.uuid);
        if (signature.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        var deleted = signature.get();
        deleted.status = "DELETED";

        signatureRepository.save(deleted);

        AuditEntity audit = AuditEntity.generateDeleted(deleted);
        auditRepository.save(audit);

        return ResponseEntity.status(200).build();
    }

    @PostMapping
    @RequestMapping("/update")
    public ResponseEntity<SignatureEntity> updateSignature(
            @RequestBody SignatureEntity signature){
        // Обновление сигнатуры
        // TODO: Сделать как в /add чтобы сигнатура перерасчитывалась сама
        var old = signatureRepository.findById(signature.id);

        if (old.isEmpty())
            return ResponseEntity.status(404).build();

        var oldSignature = old.get().clone();
        signature.id = null;
        signature.updatedAt = new Date();
        var newSignature = signatureRepository.save(signature);

        AuditEntity audit = AuditEntity.generateUpdated(oldSignature, newSignature);
        auditRepository.save(audit);
        HistoryEntity history = HistoryEntity.generate(oldSignature);
        historyRepository.save(history);

        oldSignature.status = "UPDATED";
        signatureRepository.save(oldSignature);

        return ResponseEntity.status(200).body(newSignature);
    }
}
