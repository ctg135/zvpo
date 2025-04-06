package mtusi.zvpo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MainController {
    private final MainService mainService;

    public MainController(MainService mainService) {
        this.mainService = mainService;
    }

    @GetMapping
    @RequestMapping("/test")
    public String getMessage() {
        return mainService.getMessage();
    }

    @PostMapping
    @RequestMapping("/test1")
    public ResponseEntity<String>  getPostMessage(
            @RequestParam String key){
        return ResponseEntity.ok(key);
    }
}
