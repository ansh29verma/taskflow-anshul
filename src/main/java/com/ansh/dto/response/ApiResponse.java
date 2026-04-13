package com.ansh.dto.response;

import com.ansh.entity.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class ApiResponse {

    // ── Auth ─────────────────────────────────────────────────────────────────
    public record AuthResponse(String token, UserResponse user) {
        public static AuthResponse of(String token, User u) {
            return new AuthResponse(token, UserResponse.of(u));
        }
    }

    // ── User ─────────────────────────────────────────────────────────────────
    public record UserResponse(UUID id, String name, String email, OffsetDateTime createdAt) {
        public static UserResponse of(User u) {
            return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getCreatedAt());
        }
    }

    // ── Project ───────────────────────────────────────────────────────────────
    public record ProjectResponse(
            UUID id, String name, String description,
            UUID ownerId, OffsetDateTime createdAt) {
        public static ProjectResponse of(Project p) {
            return new ProjectResponse(
                    p.getId(), p.getName(), p.getDescription(),
                    p.getOwner().getId(), p.getCreatedAt());
        }
    }

    public record ProjectDetailResponse(
            UUID id, String name, String description,
            UUID ownerId, OffsetDateTime createdAt,
            List<TaskResponse> tasks) {
        public static ProjectDetailResponse of(Project p, List<Task> tasks) {
            return new ProjectDetailResponse(
                    p.getId(), p.getName(), p.getDescription(),
                    p.getOwner().getId(), p.getCreatedAt(),
                    tasks.stream().map(TaskResponse::of).toList());
        }
    }

    public record ProjectListResponse(List<ProjectResponse> projects, PageMeta page) {}

    // ── Task ──────────────────────────────────────────────────────────────────
    public record TaskResponse(
            UUID id, String title, String description,
            Status status, Priority priority,
            UUID projectId, UUID assigneeId,
            LocalDate dueDate, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        public static TaskResponse of(Task t) {
            return new TaskResponse(
                    t.getId(), t.getTitle(), t.getDescription(),
                    t.getStatus(), t.getPriority(),
                    t.getProject().getId(),
                    t.getAssignee() != null ? t.getAssignee().getId() : null,
                    t.getDueDate(), t.getCreatedAt(), t.getUpdatedAt());
        }
    }

    public record TaskListResponse(List<TaskResponse> tasks, PageMeta page) {}

    // ── Stats ─────────────────────────────────────────────────────────────────
    public record ProjectStatsResponse(
            List<StatusCount> byStatus,
            List<AssigneeCount> byAssignee) {}

    public record StatusCount(String status, long count) {}
    public record AssigneeCount(String assignee, long count) {}

    // ── Pagination ────────────────────────────────────────────────────────────
    public record PageMeta(int page, int limit, long total, int totalPages) {}

    // ── Error ─────────────────────────────────────────────────────────────────
    public record ErrorResponse(String error) {}
    public record ValidationErrorResponse(String error, Object fields) {}
}