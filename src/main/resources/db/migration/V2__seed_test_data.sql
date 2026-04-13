-- V2__seed_test_data.sql
-- Password for test@example.com is: password123
-- bcrypt hash (cost 12) generated offline

INSERT INTO users (id, name, email, password) VALUES
                                                  ('a0000000-0000-0000-0000-000000000001',
                                                   'Test User',
                                                   'test@example.com',
                                                   '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj6hsxq7zGES'),
                                                  ('a0000000-0000-0000-0000-000000000002',
                                                   'Alice Smith',
                                                   'alice@example.com',
                                                   '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj6hsxq7zGES');

INSERT INTO projects (id, name, description, owner_id) VALUES
    ('b0000000-0000-0000-0000-000000000001',
     'Website Redesign',
     'Q2 redesign of the marketing site',
     'a0000000-0000-0000-0000-000000000001');

INSERT INTO tasks (id, title, description, status, priority, project_id, assignee_id, due_date) VALUES
                                                                                                    ('c0000000-0000-0000-0000-000000000001',
                                                                                                     'Design homepage wireframes',
                                                                                                     'Create lo-fi wireframes for the new homepage layout',
                                                                                                     'done',
                                                                                                     'high',
                                                                                                     'b0000000-0000-0000-0000-000000000001',
                                                                                                     'a0000000-0000-0000-0000-000000000001',
                                                                                                     '2026-04-10'),
                                                                                                    ('c0000000-0000-0000-0000-000000000002',
                                                                                                     'Implement responsive navbar',
                                                                                                     'Mobile-first navbar with hamburger menu',
                                                                                                     'in_progress',
                                                                                                     'medium',
                                                                                                     'b0000000-0000-0000-0000-000000000001',
                                                                                                     'a0000000-0000-0000-0000-000000000002',
                                                                                                     '2026-04-20'),
                                                                                                    ('c0000000-0000-0000-0000-000000000003',
                                                                                                     'Write content for About page',
                                                                                                     NULL,
                                                                                                     'todo',
                                                                                                     'low',
                                                                                                     'b0000000-0000-0000-0000-000000000001',
                                                                                                     NULL,
                                                                                                     NULL);