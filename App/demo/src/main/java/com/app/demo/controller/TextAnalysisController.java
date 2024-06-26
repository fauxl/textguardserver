package com.app.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

@RestController
@RequestMapping("/api")
public class TextAnalysisController {

    private Map<String, String> responses = new HashMap<>();

    @Value("${ollama.api.url}")
    private String ollamaApiUrl;

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeText(@RequestBody String text) {

        var message = text.split("_")[1];
        var id = text.split("_")[0];
        RestTemplate restTemplate = new RestTemplate();
        
        String ollamaRequestUrl = ollamaApiUrl + "/v1/chat/completions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var prefix = " Come test, analizza i seguenti messaggi e suggersci se si tratta di un comportamento assimilabile a stalking ";
        var suffix = " Rispondi solo TRUE o FALSE facendo una media dei giudizi";
        var question = prefix + message + suffix;
        System.out.println(id);
        System.out.println(question);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "llama3");
        
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant.");
        
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", question);

        requestBody.put("messages", new JSONArray().put(systemMessage).put(userMessage));
        
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(ollamaRequestUrl, entity, String.class);
        
        System.out.println(response.getBody());
        responses.put(id, response.getBody());

        return ResponseEntity.ok(response.getBody());
    }


    @GetMapping("/answer/{id}")
    public ResponseEntity<String> provideAnswer(@PathVariable String id) {
        String response = responses.get(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
