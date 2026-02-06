package com.example.mini_ecom.dto.auth;

import com.example.mini_ecom.util.constants.GenderEnum;
import com.example.mini_ecom.util.constants.RoleEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginDTO {
    private Long id;
    private String name;
    private String email;
    private RoleEnum role;
    private GenderEnum gender;
}
