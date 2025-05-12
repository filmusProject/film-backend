package com.filmus.backend.movie.dto;

import org.springframework.batch.core.BatchStatus;

public record BatchRunResponseDTO(
        Long         jobInstanceId,
        Long         jobExecutionId,
        BatchStatus status,
        String       message
) {}