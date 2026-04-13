package com.ansh.repository;
import com.ansh.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    /**
     * Returns projects where the user is the owner OR is assigned to at least one task.
     */
    @Query("""
        SELECT DISTINCT p FROM Project p
        LEFT JOIN Task t ON t.project = p
        WHERE p.owner.id = :userId
           OR t.assignee.id = :userId
        ORDER BY p.createdAt DESC
        """)
    Page<Project> findAccessibleByUserId(@Param("userId") UUID userId, Pageable pageable);
}
