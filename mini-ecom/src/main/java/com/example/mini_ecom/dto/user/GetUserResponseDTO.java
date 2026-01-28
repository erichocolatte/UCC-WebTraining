package com.example.mini_ecom.dto.user;

import com.example.mini_ecom.util.constants.GenderEnum;
import com.example.mini_ecom.util.constants.RoleEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GetUserResponseDTO {
    private Long id;
    private String name;
    private String email;
    // private String password;

    private RoleEnum role;

    private GenderEnum gender;
}
