package com.ansh.controller;



import com.ansh.BaseIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskControllerTest extends BaseIntegrationTest {

    @Autowired TestRestTemplate rest;

    static String token;
    static String projectId;
    static String taskId;

    // ── Auth helper ───────────────────────────────────────────────────────────

    HttpHeaders authHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    // ── Setup ─────────────────────────────────────────────────────────────────

    @BeforeAll
    static void setup(@Autowired TestRestTemplate r) {
        // Register and capture token
        var reg = Map.of("name", "Task Tester", "email", "tasks@test.com", "password", "password123");
        @SuppressWarnings("unchecked")
        Map<String, Object> body = r.postForEntity("/auth/register", reg, Map.class).getBody();
        token = (String) body.get("token");

        // Create a project
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        var proj = Map.of("name", "Test Project");
        @SuppressWarnings("unchecked")
        Map<String, Object> projBody = r.exchange(
                "/projects", HttpMethod.POST,
                new HttpEntity<>(proj, h), Map.class).getBody();
        projectId = (String) projBody.get("id");
    }

    @Test @Order(1)
    void create_task_returns_201() {
        var req = Map.of("title", "My First Task", "priority", "high");
        var res = rest.exchange(
                "/projects/" + projectId + "/tasks",
                HttpMethod.POST, new HttpEntity<>(req, authHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).containsKey("id");
        assertThat(res.getBody().get("title")).isEqualTo("My First Task");
        taskId = (String) res.getBody().get("id");
    }

    @Test @Order(2)
    void list_tasks_returns_created_task() {
        var res = rest.exchange(
                "/projects/" + projectId + "/tasks",
                HttpMethod.GET, new HttpEntity<>(authHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).containsKey("tasks");
    }

    @Test @Order(3)
    void update_task_status() {
        var req = Map.of("status", "in_progress");
        var res = rest.exchange(
                "/tasks/" + taskId,
                HttpMethod.PATCH, new HttpEntity<>(req, authHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().get("status")).isEqualTo("in_progress");
    }

    @Test @Order(4)
    void filter_tasks_by_status() {
        var res = rest.exchange(
                "/projects/" + projectId + "/tasks?status=in_progress",
                HttpMethod.GET, new HttpEntity<>(authHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test @Order(5)
    void create_task_missing_title_returns_400() {
        var req = Map.of("priority", "low"); // no title
        var res = rest.exchange(
                "/projects/" + projectId + "/tasks",
                HttpMethod.POST, new HttpEntity<>(req, authHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).containsKey("fields");
    }

    @Test @Order(6)
    void delete_task_returns_204() {
        var res = rest.exchange(
                "/tasks/" + taskId,
                HttpMethod.DELETE, new HttpEntity<>(authHeaders()), Void.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}