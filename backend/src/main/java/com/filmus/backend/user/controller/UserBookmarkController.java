package com.filmus.backend.user.controller;

import com.filmus.backend.user.dto.BookmarkResponseDTO;
import com.filmus.backend.user.entity.MovieBookmark;
import com.filmus.backend.user.service.UserBookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.filmus.backend.security.UserDetailsImpl;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class UserBookmarkController {

    private final UserBookmarkService bookmarkService;

    @Operation(summary = "영화 찜 등록", description = "로그인한 사용자가 보고 싶은 영화를 찜합니다.")
    @PostMapping
    public ResponseEntity<Void> addBookmark(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestParam Long movieId) {
        bookmarkService.addBookmark(userDetails.getUser().getId(), movieId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "영화 찜 삭제", description = "로그인한 사용자가 찜한 영화를 목록에서 제거합니다.")
    @DeleteMapping
    public ResponseEntity<Void> removeBookmark(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @RequestParam Long movieId) {
        bookmarkService.removeBookmark(userDetails.getUser().getId(), movieId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "찜한 영화 목록 조회", description = "로그인한 사용자가 찜한 영화 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<BookmarkResponseDTO>> getBookmarks(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(bookmarkService.getBookmarks(userDetails.getUser().getId()));
    }

    @Operation(summary = "북마크 여부 확인", description = "해당 영화가 사용자의 북마크에 있는지 확인합니다.")
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkBookmark(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @RequestParam Long movieId) {
        boolean bookmarked = bookmarkService.isBookmarked(userDetails.getUser().getId(), movieId);
        return ResponseEntity.ok(Map.of("bookmarked", bookmarked));
    }

}
