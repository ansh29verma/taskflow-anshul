package com.ansh.controller;


import com.ansh.dto.request.ProjectRequest;
import com.ansh.dto.response.ApiResponse.*;
import com.ansh.entity.User;
import com.ansh.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<ProjectListResponse> list(
            @AuthenticationPrincipal User caller,
            @RequestParam(defaultValue = "1")  int page,
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(projectService.listAccessible(caller, page, limit));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            @AuthenticationPrincipal User caller,
            @Valid @RequestBody ProjectRequest.Create req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(caller, req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDetailResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> update(
            @AuthenticationPrincipal User caller,
            @PathVariable UUID id,
            @RequestBody ProjectRequest.Update req) {
        return ResponseEntity.ok(projectService.update(caller, id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User caller,
            @PathVariable UUID id) {
        projectService.delete(caller, id);
        return ResponseEntity.noContent().build();
    }

    /** Bonus: task counts by status and assignee */
    @GetMapping("/{id}/stats")
    public ResponseEntity<ProjectStatsResponse> stats(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getStats(id));
    }
}