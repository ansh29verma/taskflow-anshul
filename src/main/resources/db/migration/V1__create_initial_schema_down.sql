-- V1__create_initial_schema_down.sql (for manual rollback)
DROP TRIGGER  IF EXISTS tasks_updated_at ON tasks;
DROP FUNCTION IF EXISTS update_updated_at();
DROP TABLE    IF EXISTS tasks;
DROP TABLE    IF EXISTS projects;
DROP TABLE    IF EXISTS users;
DROP TYPE     IF EXISTS task_priority;
DROP TYPE     IF EXISTS task_status;