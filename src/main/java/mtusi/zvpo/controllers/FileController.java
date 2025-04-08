package mtusi.zvpo.controllers;

import mtusi.zvpo.entites.FileEntity;
import mtusi.zvpo.repositories.MyFileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {
    private MyFileRepository fileRepository;

    public FileController(MyFileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @PostMapping
    @RequestMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam(name = "file") MultipartFile file) {
        // TODO: Сделать сканирование на сигнатуры для файлов
        if (file.isEmpty()) {
            return  ResponseEntity.badRequest().body("Empty file");
        }

        try {
            String filename = file.getOriginalFilename();
            byte[] bytes = file.getBytes();

            FileEntity fileEntity = new FileEntity();
            fileEntity.setFilename(filename);
            fileEntity.setData(bytes);

            fileRepository.save(fileEntity);

            return ResponseEntity.ok("Uploaded");
        } catch (IOException exc) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exc.getMessage());
        }
    }

    @GetMapping
    @RequestMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable(name = "id") Long id) {
        var entity = fileRepository.findById(id);
        if (entity.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(entity.get().getData());
    }

}
