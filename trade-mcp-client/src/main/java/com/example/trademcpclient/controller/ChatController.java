package com.example.trademcpclient.controller;

import com.example.trademcpclient.service.TraderMcpClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/mcp")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final TraderMcpClientService traderMcpClientService;

    public record ChatRequest(String message, String conversationId) {}
    public record ChatResponse(String content, String conversationId) {}

    @PostMapping(path = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String content = traderMcpClientService.runPrompt(request.message());
        return ResponseEntity.ok(new ChatResponse(content, request.conversationId()));
    }

    @PostMapping(path = "/stream", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<StreamingResponseBody> stream(@RequestBody ChatRequest request) {
        // Simple streaming: write the full response; can be adapted to token streaming later
        StreamingResponseBody body = outputStream -> {
            String content = traderMcpClientService.runPrompt(request.message());
            // Write as a single chunk; UI will still read it as a stream
            outputStream.write(content.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        };
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(body);
    }
}

