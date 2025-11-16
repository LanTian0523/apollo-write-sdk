package org.bluesky.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class ExampleController {
    private final YourHighLevelService writeService;

    public ExampleController(YourHighLevelService writeService) {
        this.writeService = writeService;
    }

    @PostMapping("/publish")
    public ResponseEntity<String> publish(@RequestBody PublishRequest r) {
        boolean ok = writeService.publishSingle(r.getKey(), r.getValue(), r.getComment());
        return ok ? ResponseEntity.ok("ok") : ResponseEntity.status(500).body("fail");
    }
}
