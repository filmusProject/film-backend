package com.filmus.backend.user.controller;


import com.filmus.backend.security.UserDetailsImpl;
import com.filmus.backend.user.dto.ChangePasswordRequestDto;
import com.filmus.backend.user.dto.UpdateUserInfoRequestDto;
import com.filmus.backend.user.dto.UserInfoResponseDto;
import com.filmus.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 정보를 반환합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponseDto> getMyInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(userService.getUserInfo(userDetails.getUser()));
    }

    @Operation(summary = "회원 정보 수정", description = "닉네임, 성별, 생년월일을 수정합니다.")
    @PutMapping("/update")
    public ResponseEntity<String> updateUserInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody UpdateUserInfoRequestDto request) {
        userService.updateUserInfo(userDetails.getUser(), request);
        return ResponseEntity.ok("사용자 정보가 수정되었습니다.");
    }

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인하고 새 비밀번호로 변경합니다.")
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ChangePasswordRequestDto request) {
        userService.changePassword(userDetails.getUser(), request);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자를 탈퇴 처리합니다.")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.deleteUser(userDetails.getUser());
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }
}