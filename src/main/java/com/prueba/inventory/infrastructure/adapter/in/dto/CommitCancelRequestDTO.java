package com.prueba.inventory.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class CommitCancelRequestDTO {
   @NotBlank(message = "requestId es obligatorio")
    private String requestId;
}