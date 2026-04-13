package com.ansh.service;


import com.ansh.dto.request.TaskRequest;
import com.ansh.dto.response.ApiResponse.*;
import com.ansh.entity.*;
import com.ansh.exception.ForbiddenException;
import com.ansh.exception.NotFoundException;
import com.ansh.repository.ProjectRepository;
import com.ansh.repository.TaskRepository;
import com.ansh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository    taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository    userRepository;

    @Transactional(readOnly = true)
    public TaskListResponse listByProject(UUID projectId, Status status,
                                          UUID assigneeId, int page, int limit) {
        findProjectOrThrow(projectId);
        PageRequest pr = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Page<Task> pg  = taskRepository.findByProjectFiltered(projectId, status, assigneeId, pr);
        return new TaskListResponse(
                pg.getContent().stream().map(TaskResponse::of).toList(),
                new PageMeta(page, limit, pg.getTotalElements(), pg.getTotalPages()));
    }

    @Transactional
    public TaskResponse create(User caller, UUID projectId, TaskRequest.Create req) {
        Project project = findProjectOrThrow(projectId);

        User assignee = null;
        if (req.assigneeId() != null) {
            assignee = userRepository.findById(req.assigneeId())
                    .orElseThrow(() -> new NotFoundException("assignee not found"));
        }

        Task task = Task.builder()
                .title(req.title())
                .description(req.description())
                .status(req.status() != null ? req.status() : Status.todo)
                .priority(req.priority() != null ? req.priority() : Priority.medium)
                .project(project)
                .assignee(assignee)
                .dueDate(req.dueDate())
                .build();

        taskRepository.save(task);
        log.info("Task created: {} in project {} by {}", task.getId(), projectId, caller.getEmail());
        return TaskResponse.of(task);
    }

    @Transactional
    public TaskResponse update(User caller, UUID taskId, TaskRequest.Update req) {
        Task task = findTaskOrThrow(taskId);

        // Only project owner or assignee can update
        assertCanModify(caller, task);

        if (req.title() != null && !req.title().isBlank()) {
            task.setTitle(req.title());
        }
        if (req.description() != null) {
            task.setDescription(req.description());
        }
        if (req.status() != null) {
            task.setStatus(req.status());
        }
        if (req.priority() != null) {
            task.setPriority(req.priority());
        }
        if (req.dueDate() != null) {
            task.setDueDate(req.dueDate());
        }
        // assigneeId update — null means "clear assignee", absent means "don't touch"
        // We use a sentinel: if the field is present in the request we apply it
        if (req.assigneeId() != null) {
            User assignee = userRepository.findById(req.assigneeId())
                    .orElseThrow(() -> new NotFoundException("assignee not found"));
            task.setAssignee(assignee);
        }

        taskRepository.save(task);
        log.info("Task updated: {} by {}", taskId, caller.getEmail());
        return TaskResponse.of(task);
    }

    @Transactional
    public void delete(User caller, UUID taskId) {
        Task task = findTaskOrThrow(taskId);
        assertCanDelete(caller, task);
        taskRepository.delete(task);
        log.info("Task deleted: {} by {}", taskId, caller.getEmail());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Project findProjectOrThrow(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("project not found"));
    }

    private Task findTaskOrThrow(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("task not found"));
    }

    private void assertCanModify(User caller, Task task) {
        boolean isOwner    = task.getProject().getOwner().getId().equals(caller.getId());
        boolean isAssignee = task.getAssignee() != null &&
                task.getAssignee().getId().equals(caller.getId());
        if (!isOwner && !isAssignee) {
            throw new ForbiddenException("only the project owner or task assignee can modify this task");
        }
    }

    private void assertCanDelete(User caller, Task task) {
        boolean isOwner = task.getProject().getOwner().getId().equals(caller.getId());
        if (!isOwner) {
            throw new ForbiddenException("only the project owner can delete tasks");
        }
    }
}
