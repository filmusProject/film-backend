package com.filmus.backend.dto;

import java.util.List;

public record SearchResponseDTO(
        int totalCount,
        List<MovieDTO> movies
) {}
