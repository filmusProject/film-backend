package com.filmus.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookmarkResponseDTO {
    private Long movieId;
    private String title;
    private String posterUrl;
}