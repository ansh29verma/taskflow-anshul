-- V1__create_initial_schema.sql

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ── Users ────────────────────────────────────────────────────────────────────
CREATE TABLE users (
                       id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                       name       VARCHAR(255) NOT NULL,
                       email      VARCHAR(255) NOT NULL UNIQUE,
                       password   VARCHAR(255) NOT NULL,
                       created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);

-- ── Projects ──────────────────────────────────────────────────────────────────
CREATE TABLE projects (
                          id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                          name        VARCHAR(255) NOT NULL,
                          description TEXT,
                          owner_id    UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                          created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_projects_owner_id ON projects(owner_id);

-- ── Tasks ─────────────────────────────────────────────────────────────────────
CREATE TYPE task_status   AS ENUM ('todo', 'in_progress', 'done');
CREATE TYPE task_priority AS ENUM ('low', 'medium', 'high');

CREATE TABLE tasks (
                       id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
                       title       VARCHAR(255)  NOT NULL,
                       description TEXT,
                       status      task_status   NOT NULL DEFAULT 'todo',
                       priority    task_priority NOT NULL DEFAULT 'medium',
                       project_id  UUID          NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
                       assignee_id UUID          REFERENCES users(id) ON DELETE SET NULL,
                       due_date    DATE,
                       created_at  TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
                       updated_at  TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tasks_project_id   ON tasks(project_id);
CREATE INDEX idx_tasks_assignee_id  ON tasks(assignee_id);
CREATE INDEX idx_tasks_status       ON tasks(status);

-- Auto-update updated_at
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tasks_updated_at
    BEFORE UPDATE ON tasks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();