package com.ansh.controller;


import com.ansh.dto.request.TaskRequest;
import com.ansh.dto.response.ApiResponse.*;
import com.ansh.entity.Status;
import com.ansh.entity.Task;
import com.ansh.entity.User;
import com.ansh.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /** GET /projects/:id/tasks?status=&assignee=&page=&limit= */
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskListResponse> listByProject(
            @PathVariable UUID projectId,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) UUID assignee,
            @RequestParam(defaultValue = "1")  int page,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(taskService.listByProject(projectId, status, assignee, page, limit));
    }

    /** POST /projects/:id/tasks */
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> create(
            @AuthenticationPrincipal User caller,
            @PathVariable UUID projectId,
            @Valid @RequestBody TaskRequest.Create req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(caller, projectId, req));
    }

    /** PATCH /tasks/:id */
    @PatchMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> update(
            @AuthenticationPrincipal User caller,
            @PathVariable UUID taskId,
            @RequestBody TaskRequest.Update req) {
        return ResponseEntity.ok(taskService.update(caller, taskId, req));
    }

    /** DELETE /tasks/:id */
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User caller,
            @PathVariable UUID taskId) {
        taskService.delete(caller, taskId);
        return ResponseEntity.noContent().build();
    }
}