package com.supamenu.www.dtos.user;

import com.supamenu.www.dtos.auth.RegisterUserDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class CreateUserDTO extends RegisterUserDTO {
    private List<UUID> roles;

    public CreateUserDTO(RegisterUserDTO registerUserDTO) {
        this.setEmail(registerUserDTO.getEmail());
        this.setPassword(registerUserDTO.getPassword());
        this.setPhoneNumber(registerUserDTO.getPhoneNumber());
        this.setFirstName(registerUserDTO.getFirstName());
        this.setLastName(registerUserDTO.getLastName());
        this.setDob(registerUserDTO.getDob());
    }

    public CreateUserDTO(String email, String phoneNumber, String password, String firstName, String lastName, String dob) {
        this.setEmail(email);
        this.setPhoneNumber(phoneNumber);
        this.setPassword(password);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setDob(new Date(dob));
    }
}
