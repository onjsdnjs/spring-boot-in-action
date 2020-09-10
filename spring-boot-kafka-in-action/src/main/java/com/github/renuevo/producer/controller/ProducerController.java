package com.github.renuevo.producer.controller;

import com.github.renuevo.producer.service.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ProducerController {

    private final KafkaService kafkaService;

    @GetMapping("/push/msg")
    public void pushMassage(@RequestParam String msg) {
        this.kafkaService.sendMessage(msg);
    }

    @GetMapping("/push_async/msg")
    public ResponseEntity<String> pushAsyncMassage(@RequestParam String msg) {
        this.kafkaService.sendAsyncMessage(msg);
        return ResponseEntity.ok("success");
    }

}