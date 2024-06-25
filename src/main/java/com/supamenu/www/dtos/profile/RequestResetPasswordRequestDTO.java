package com.supamenu.www.dtos.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class RequestResetPasswordRequestDTO {
    @Schema(example = "example@gmail.com")
    @Email(message = "Email should be valid")
    private String email;
}
