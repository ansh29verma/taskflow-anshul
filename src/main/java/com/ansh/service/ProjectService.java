package com.ansh.service;


import com.ansh.dto.request.ProjectRequest;
import com.ansh.dto.response.ApiResponse.*;
import com.ansh.entity.Project;
import com.ansh.entity.User;
import com.ansh.exception.ForbiddenException;
import com.ansh.exception.NotFoundException;
import com.ansh.repository.ProjectRepository;
import com.ansh.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository    taskRepository;

    @Transactional(readOnly = true)
    public ProjectListResponse listAccessible(User caller, int page, int limit) {
        PageRequest pr = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
        Page<Project> pg = projectRepository.findAccessibleByUserId(caller.getId(), pr);
        return new ProjectListResponse(
                pg.getContent().stream().map(ProjectResponse::of).toList(),
                new PageMeta(page, limit, pg.getTotalElements(), pg.getTotalPages()));
    }

    @Transactional
    public ProjectResponse create(User caller, ProjectRequest.Create req) {
        Project project = Project.builder()
                .name(req.name())
                .description(req.description())
                .owner(caller)
                .build();
        projectRepository.save(project);
        log.info("Project created: {} by {}", project.getId(), caller.getEmail());
        return ProjectResponse.of(project);
    }

    @Transactional(readOnly = true)
    public ProjectDetailResponse getById(UUID id) {
        Project project = findOrThrow(id);
        var tasks = taskRepository.findByProjectIdOrderByCreatedAtDesc(id);
        return ProjectDetailResponse.of(project, tasks);
    }

    @Transactional
    public ProjectResponse update(User caller, UUID id, ProjectRequest.Update req) {
        Project project = findOrThrow(id);
        assertOwner(caller, project);

        if (req.name() != null && !req.name().isBlank()) {
            project.setName(req.name());
        }
        if (req.description() != null) {
            project.setDescription(req.description());
        }
        projectRepository.save(project);
        log.info("Project updated: {}", id);
        return ProjectResponse.of(project);
    }

    @Transactional
    public void delete(User caller, UUID id) {
        Project project = findOrThrow(id);
        assertOwner(caller, project);
        projectRepository.delete(project);
        log.info("Project deleted: {} by {}", id, caller.getEmail());
    }

    @Transactional(readOnly = true)
    public ProjectStatsResponse getStats(UUID id) {
        findOrThrow(id); // ensure exists
        List<StatusCount> byStatus = taskRepository.countByStatusForProject(id).stream()
                .map(m -> new StatusCount(
                        String.valueOf(m.get("status")),
                        ((Number) m.get("count")).longValue()))
                .toList();
        List<AssigneeCount> byAssignee = taskRepository.countByAssigneeForProject(id).stream()
                .map(m -> new AssigneeCount(
                        String.valueOf(m.get("assignee")),
                        ((Number) m.get("count")).longValue()))
                .toList();
        return new ProjectStatsResponse(byStatus, byAssignee);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Project findOrThrow(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("project not found"));
    }

    private void assertOwner(User caller, Project project) {
        if (!project.getOwner().getId().equals(caller.getId())) {
            throw new ForbiddenException("only the project owner can perform this action");
        }
    }
}