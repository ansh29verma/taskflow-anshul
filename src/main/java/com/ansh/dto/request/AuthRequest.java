package com.ansh.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public sealed interface AuthRequest permits AuthRequest.Register, AuthRequest.Login {

    record Register(
            @NotBlank(message = "is required")
            String name,

            @NotBlank(message = "is required")
            @Email(message = "must be a valid email")
            String email,

            @NotBlank(message = "is required")
            @Size(min = 8, message = "must be at least 8 characters")
            String password
    ) implements AuthRequest {}

    record Login(
            @NotBlank(message = "is required")
            @Email(message = "must be a valid email")
            String email,

            @NotBlank(message = "is required")
            String password
    ) implements AuthRequest {}
}
