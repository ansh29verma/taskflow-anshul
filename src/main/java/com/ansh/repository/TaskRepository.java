package com.ansh.repository;

import com.ansh.entity.Status;
import com.ansh.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query("""
        SELECT t FROM Task t
        WHERE t.project.id = :projectId
          AND (:status IS NULL OR t.status = :status)
          AND (:assigneeId IS NULL OR t.assignee.id = :assigneeId)
        ORDER BY t.createdAt DESC
        """)
    Page<Task> findByProjectFiltered(
            @Param("projectId")  UUID projectId,
            @Param("status") Status status,
            @Param("assigneeId") UUID assigneeId,
            Pageable pageable);

    List<Task> findByProjectIdOrderByCreatedAtDesc(UUID projectId);

    /** Count by status for a project. */
    @Query("""
        SELECT t.status AS status, COUNT(t) AS count
        FROM Task t
        WHERE t.project.id = :projectId
        GROUP BY t.status
        """)
    List<Map<String, Object>> countByStatusForProject(@Param("projectId") UUID projectId);

    /** Count by assignee for a project. */
    @Query("""
        SELECT COALESCE(t.assignee.name, 'Unassigned') AS assignee,
               COUNT(t) AS count
        FROM Task t
        WHERE t.project.id = :projectId
        GROUP BY t.assignee.name
        """)
    List<Map<String, Object>> countByAssigneeForProject(@Param("projectId") UUID projectId);
}