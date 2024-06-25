package com.supamenu.www.services.interfaces;

import com.supamenu.www.dtos.profile.*;
import com.supamenu.www.dtos.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface ProfileService {
    public ResponseEntity<ApiResponse<ProfileResponseDTO>> getProfile();

    public ResponseEntity<ApiResponse<ProfileResponseDTO>> updateProfile(UpdateProfileRequestDTO updateProfileRequestDTO);

    public ResponseEntity<ApiResponse<ProfileResponseDTO>> changePassword(ChangePasswordRequestDTO changePasswordRequestDTO);

    public ResponseEntity<ApiResponse<Object>> requestResetPassword(RequestResetPasswordRequestDTO requestResetPasswordRequestDTO);
}
