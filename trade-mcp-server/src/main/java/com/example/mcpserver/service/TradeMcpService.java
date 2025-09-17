package com.example.mcpserver.service;

import com.example.mcpserver.dto.TradeDto;
import com.example.mcpserver.entity.Trade;
import com.example.mcpserver.mapper.TradeMapper;
import com.example.mcpserver.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TradeMcpService {
    private final TradeRepository tradeRepository;
    private final TradeMapper tradeMapper;

    @Tool(name = "getAllTrades", description = "Retrieve all trades from the repository")
    public List<TradeDto> getAllTrades() {
        return tradeMapper.toDtoList(tradeRepository.findAll());
    }

    @Tool(name = "getTradeById", description = "Retrieve a trade by its ID")
    public Optional<TradeDto> getTradeById(@ToolParam Long id) {
        return tradeRepository.findById(id).map(tradeMapper::toDto);
    }

    @Tool(name = "createTrade", description = "Create a new trade")
    public TradeDto createTrade(@ToolParam TradeDto trade) {
        Trade entity = tradeMapper.toEntity(trade);
        Trade saved = tradeRepository.save(entity);
        return tradeMapper.toDto(saved);
    }

    @Tool(name = "updateTrade", description = "Update an existing trade by ID")
    public TradeDto updateTrade(
            @ToolParam Long id,
            @ToolParam TradeDto tradeDetails) {
        return tradeRepository.findById(id)
                .map(existing -> {
                    tradeMapper.updateEntityFromDto(tradeDetails, existing);
                    Trade saved = tradeRepository.save(existing);
                    return tradeMapper.toDto(saved);
                })
                .orElse(null);
    }

    @Tool(name = "deleteTrade", description = "Delete a trade by its ID")
    public boolean deleteTrade(@ToolParam Long id) {
        return tradeRepository.findById(id)
                .map(trade -> {
                    tradeRepository.delete(trade);
                    return true;
                })
                .orElse(false);
    }
}
