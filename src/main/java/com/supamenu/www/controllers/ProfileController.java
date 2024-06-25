package com.supamenu.www.controllers;

import com.supamenu.www.dtos.profile.*;
import com.supamenu.www.dtos.response.ApiResponse;
import com.supamenu.www.services.interfaces.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/get-profile")
    public ResponseEntity<ApiResponse<ProfileResponseDTO>> getProfile() {
        return profileService.getProfile();
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<ApiResponse<ProfileResponseDTO>> updateProfile(@Valid @RequestBody UpdateProfileRequestDTO updateProfileRequestDTO) {
        return profileService.updateProfile(updateProfileRequestDTO);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<ProfileResponseDTO>> changePassword(@Valid @RequestBody ChangePasswordRequestDTO
                                                                                  changePasswordRequestDTO) {
        return profileService.changePassword(changePasswordRequestDTO);
    }

    @PostMapping("/request-reset-password")
    public ResponseEntity<ApiResponse<Object>> requestResetPassword(@Valid @RequestBody RequestResetPasswordRequestDTO
                                                                                  requestResetPasswordRequestDTO) {
        return profileService.requestResetPassword(requestResetPasswordRequestDTO);
    }
}
