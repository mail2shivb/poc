package com.example.mcpserver.mapper;

import com.example.mcpserver.dto.TradeDto;
import com.example.mcpserver.entity.Trade;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TradeMapper {
    TradeDto toDto(Trade entity);
    Trade toEntity(TradeDto dto);

    List<TradeDto> toDtoList(List<Trade> entities);
    List<Trade> toEntityList(List<TradeDto> dtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(TradeDto dto, @MappingTarget Trade entity);
}

