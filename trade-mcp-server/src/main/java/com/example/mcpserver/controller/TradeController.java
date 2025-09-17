package com.example.mcpserver.controller;

import com.example.mcpserver.dto.TradeDto;
import com.example.mcpserver.service.TradeMcpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trades")
public class TradeController {
    @Autowired
    private TradeMcpService tradeMcpService;

    @GetMapping
    public ResponseEntity<List<TradeDto>> getAllTrades() {
        return ResponseEntity.ok(tradeMcpService.getAllTrades());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TradeDto> getTradeById(@PathVariable Long id) {
        Optional<TradeDto> trade = tradeMcpService.getTradeById(id);
        return trade.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TradeDto> createTrade(@RequestBody TradeDto trade) {
        TradeDto created = tradeMcpService.createTrade(trade);
        URI location = URI.create("/api/trades/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TradeDto> updateTrade(@PathVariable Long id, @RequestBody TradeDto tradeDetails) {
        TradeDto updatedTrade = tradeMcpService.updateTrade(id, tradeDetails);
        if (updatedTrade != null) {
            return ResponseEntity.ok(updatedTrade);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrade(@PathVariable Long id) {
        boolean deleted = tradeMcpService.deleteTrade(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
