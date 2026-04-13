package com.ansh.dto.request;


import com.ansh.entity.Priority;
import com.ansh.entity.Status;
import com.ansh.entity.Task;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.UUID;

public sealed interface TaskRequest permits TaskRequest.Create, TaskRequest.Update {

    record Create(
            @NotBlank(message = "is required")
            String title,
            String description,
            Status status,
            Priority priority,
            UUID assigneeId,
            LocalDate dueDate
    ) implements TaskRequest {}

    record Update(
            String title,
            String description,
            Status status,
            Priority priority,
            UUID assigneeId,
            LocalDate dueDate
    ) implements TaskRequest {}
}