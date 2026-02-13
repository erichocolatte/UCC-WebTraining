package com.example.mini_ecom.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import java.util.List;

import com.example.mini_ecom.util.constants.GenderEnum;
import com.example.mini_ecom.util.constants.RoleEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Cart> carts;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<Order> orders;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    // @Column(name = "refresh_token_expired_at")
    // private Instant refreshTokenExpiredAt;
}
