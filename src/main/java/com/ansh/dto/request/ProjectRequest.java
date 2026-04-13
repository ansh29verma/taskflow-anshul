package com.ansh.dto.request;


import jakarta.validation.constraints.NotBlank;

public sealed interface ProjectRequest permits ProjectRequest.Create, ProjectRequest.Update {

    record Create(
            @NotBlank(message = "is required")
            String name,
            String description
    ) implements ProjectRequest {}

    record Update(
            String name,
            String description
    ) implements ProjectRequest {}
}