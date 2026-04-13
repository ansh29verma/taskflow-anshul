package com.ansh.controller;

import com.ansh.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerTest extends BaseIntegrationTest {

    @Autowired TestRestTemplate rest;

    @Test
    void register_returns_201_and_token() {
        var body = Map.of("name", "Test User", "email", "reg@test.com", "password", "password123");
        ResponseEntity<Map> res = rest.postForEntity("/auth/register", body, Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody()).containsKey("token");
        assertThat(res.getBody()).containsKey("user");
    }

    @Test
    void register_duplicate_email_returns_409() {
        var body = Map.of("name", "Dup User", "email", "dup@test.com", "password", "password123");
        rest.postForEntity("/auth/register", body, Map.class);
        ResponseEntity<Map> res = rest.postForEntity("/auth/register", body, Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void login_valid_credentials_returns_token() {
        // Register first
        var reg = Map.of("name", "Login User", "email", "login@test.com", "password", "password123");
        rest.postForEntity("/auth/register", reg, Map.class);

        // Login
        var login = Map.of("email", "login@test.com", "password", "password123");
        ResponseEntity<Map> res = rest.postForEntity("/auth/login", login, Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).containsKey("token");
    }

    @Test
    void login_wrong_password_returns_401() {
        var reg = Map.of("name", "Wrong Pass", "email", "wrong@test.com", "password", "password123");
        rest.postForEntity("/auth/register", reg, Map.class);

        var login = Map.of("email", "wrong@test.com", "password", "notthepassword");
        ResponseEntity<Map> res = rest.postForEntity("/auth/login", login, Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void protected_endpoint_without_token_returns_401() {
        ResponseEntity<Map> res = rest.getForEntity("/projects", Map.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void register_missing_fields_returns_400_with_field_errors() {
        var body = Map.of("name", "No Email"); // missing email and password
        ResponseEntity<Map> res = rest.postForEntity("/auth/register", body, Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).containsKey("fields");
    }
}
