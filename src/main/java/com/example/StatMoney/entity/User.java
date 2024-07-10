package com.example.StatMoney.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.util.Date;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 8, message = "Пароль должен быть длиннее семи символов.")
    private String password;
    private String role;

    private String avatarUrl; // ссылка на аватар

    @PrePersist
    public void prePersist() {
        if (this.role == null) {
            this.role = "ROLE_USER";
        }
    }
}
