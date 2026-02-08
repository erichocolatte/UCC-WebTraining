package com.example.mini_ecom.dto.auth;

import com.example.mini_ecom.util.constants.GenderEnum;
import com.example.mini_ecom.util.constants.RoleEnum;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDTO {
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Role is required")
    private RoleEnum role;
    private GenderEnum gender;
}
