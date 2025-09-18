package com.prueba.inventory.infrastructure.adapter.in.mapper;

import com.prueba.inventory.domain.model.CommitCancelRequest;
import com.prueba.inventory.infrastructure.adapter.in.dto.CommitCancelRequestDTO;

public class CommitCancelRequestMapper {
    public static CommitCancelRequest fromDTO(CommitCancelRequestDTO dto) {
        return new CommitCancelRequest(dto.getRequestId());
    }
}
