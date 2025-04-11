package mtusi.zvpo.controllers;

import mtusi.zvpo.RabinKarpAlgorithm;
import mtusi.zvpo.entites.FileEntity;
import mtusi.zvpo.entites.SignatureEntity;
import mtusi.zvpo.repositories.MyFileRepository;
import mtusi.zvpo.repositories.SignatureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {
    private MyFileRepository fileRepository;
    private SignatureRepository signatureRepository;

    public FileController(MyFileRepository fileRepository,
        SignatureRepository sigantureRepository)
    {
        this.fileRepository = fileRepository;
        this.signatureRepository = sigantureRepository;
    }

    @PostMapping
    @RequestMapping("/upload")
    public ResponseEntity<List<SignatureEntity>> uploadFile(
            @RequestParam(name = "file") MultipartFile file) {
        // Проводит сканирование сигнатуры внутри файла
        if (file.isEmpty()) {
            return  ResponseEntity.badRequest().build();
        }

        try {
            String filename = file.getOriginalFilename();
            byte[] bytes = file.getBytes();
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFilename(filename);
            fileEntity.setData(bytes);
            fileRepository.save(fileEntity);

            var algorithm = new RabinKarpAlgorithm();
            var result = algorithm.scanString(
                    new String(bytes, StandardCharsets.UTF_8),
                    signatureRepository.findAll()
            );

            return ResponseEntity.status(200).body(result);
        } catch (IOException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @RequestMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable(name = "id") Long id) {
        // Скачивание файла по id
        var entity = fileRepository.findById(id);
        if (entity.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(entity.get().getData());
    }
}
