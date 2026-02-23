-- ============================================================
-- V1__init_schema.sql  — Student Success Platform
-- Flyway migration for PostgreSQL 15
-- ============================================================

-- ─────────────────────── ENUMS ──────────────────────────────
CREATE TYPE role_type    AS ENUM ('STUDENT', 'LECTURER', 'ADMIN', 'RECRUITER');
CREATE TYPE status_type  AS ENUM ('PENDING', 'ACTIVE', 'REJECTED', 'CANCELLED', 'COMPLETED');
CREATE TYPE priority_type AS ENUM ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL');

-- ─────────────────────── AUTH ────────────────────────────────

CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    full_name   VARCHAR(150) NOT NULL,
    email       VARCHAR(200) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(30)  NOT NULL DEFAULT 'STUDENT',
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE students (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT  NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    registration_number VARCHAR(20) UNIQUE,
    degree_programme    VARCHAR(150),
    year_of_study       INT,
    semester            INT,
    gpa                 NUMERIC(3, 2),
    skills              TEXT[],
    bio                 TEXT,
    profile_picture_url TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ─────────────────────── MODULE 1 — Smart Team Matchmaker ────

CREATE TABLE project_groups (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(150) NOT NULL,
    description     TEXT,
    max_members     INT         NOT NULL,
    required_skills TEXT[],
    subject         VARCHAR(100),
    owner_id        BIGINT      NOT NULL REFERENCES users(id),
    is_open         BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE TABLE group_members (
    id          BIGSERIAL PRIMARY KEY,
    group_id    BIGINT    NOT NULL REFERENCES project_groups(id) ON DELETE CASCADE,
    user_id     BIGINT    NOT NULL REFERENCES users(id)          ON DELETE CASCADE,
    is_leader   BOOLEAN   NOT NULL DEFAULT FALSE,
    joined_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_group_member UNIQUE (group_id, user_id)
);

CREATE TABLE team_invitations (
    id          BIGSERIAL PRIMARY KEY,
    group_id    BIGINT      NOT NULL REFERENCES project_groups(id) ON DELETE CASCADE,
    inviter_id  BIGINT      NOT NULL REFERENCES users(id),
    invitee_id  BIGINT      NOT NULL REFERENCES users(id),
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    message     TEXT,
    expires_at  TIMESTAMP,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE TABLE join_requests (
    id           BIGSERIAL PRIMARY KEY,
    group_id     BIGINT      NOT NULL REFERENCES project_groups(id) ON DELETE CASCADE,
    requester_id BIGINT      NOT NULL REFERENCES users(id),
    message      TEXT,
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_join_request UNIQUE (group_id, requester_id)
);

-- ─────────────────────── MODULE 2 — Job Matchmaker & ATS ─────

CREATE TABLE resumes (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    file_name      VARCHAR(255) NOT NULL,
    file_url       TEXT         NOT NULL,
    file_size      BIGINT,
    content_type   VARCHAR(100),
    extracted_text TEXT,
    is_primary     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE job_listings (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    company         VARCHAR(200) NOT NULL,
    description     TEXT,
    required_skills TEXT[],
    type            VARCHAR(50),
    location        VARCHAR(150),
    is_remote       BOOLEAN      NOT NULL DEFAULT FALSE,
    deadline        DATE,
    priority        VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM',
    posted_by       BIGINT       NOT NULL REFERENCES users(id),
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE ats_analysis (
    id               BIGSERIAL PRIMARY KEY,
    resume_id        BIGINT     NOT NULL REFERENCES resumes(id)       ON DELETE CASCADE,
    job_listing_id   BIGINT     REFERENCES job_listings(id)           ON DELETE SET NULL,
    ats_score        NUMERIC(5, 2) NOT NULL,
    keyword_matches  TEXT[],
    missing_keywords TEXT[],
    ai_feedback      TEXT,
    created_at       TIMESTAMP  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP  NOT NULL DEFAULT NOW()
);

CREATE TABLE cv_suggestions (
    id          BIGSERIAL PRIMARY KEY,
    resume_id   BIGINT    NOT NULL REFERENCES resumes(id) ON DELETE CASCADE,
    section     VARCHAR(100) NOT NULL,
    suggestion  TEXT         NOT NULL,
    priority    VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM',
    applied     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE job_matches (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT         NOT NULL REFERENCES users(id)        ON DELETE CASCADE,
    job_listing_id  BIGINT         NOT NULL REFERENCES job_listings(id) ON DELETE CASCADE,
    match_score     NUMERIC(5, 2)  NOT NULL,
    matched_skills  TEXT[],
    missing_skills  TEXT[],
    created_at      TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP      NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_job_match UNIQUE (user_id, job_listing_id)
);

CREATE TABLE job_applications (
    id             BIGSERIAL PRIMARY KEY,
    job_listing_id BIGINT      NOT NULL REFERENCES job_listings(id) ON DELETE CASCADE,
    user_id        BIGINT      NOT NULL REFERENCES users(id)        ON DELETE CASCADE,
    resume_id      BIGINT      REFERENCES resumes(id)               ON DELETE SET NULL,
    cover_letter   TEXT,
    status         VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    recruiter_notes TEXT,
    created_at     TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_job_application UNIQUE (job_listing_id, user_id)
);

-- ─────────────────────── MODULE 3 — Campus Engagement ─────────

CREATE TABLE campus_events (
    id            BIGSERIAL PRIMARY KEY,
    title         VARCHAR(200) NOT NULL,
    description   TEXT,
    event_date    TIMESTAMP    NOT NULL,
    venue         VARCHAR(200),
    is_online     BOOLEAN      NOT NULL DEFAULT FALSE,
    max_attendees INT,
    organizer_id  BIGINT       NOT NULL REFERENCES users(id),
    is_published  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE event_rsvps (
    id         BIGSERIAL PRIMARY KEY,
    event_id   BIGINT      NOT NULL REFERENCES campus_events(id) ON DELETE CASCADE,
    user_id    BIGINT      NOT NULL REFERENCES users(id)         ON DELETE CASCADE,
    status     VARCHAR(20) NOT NULL DEFAULT 'GOING',
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_event_rsvp UNIQUE (event_id, user_id)
);

CREATE TABLE project_milestones (
    id          BIGSERIAL PRIMARY KEY,
    group_id    BIGINT       NOT NULL REFERENCES project_groups(id) ON DELETE CASCADE,
    title       VARCHAR(200) NOT NULL,
    description TEXT,
    due_date    DATE,
    status      VARCHAR(30)  NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE milestone_tasks (
    id           BIGSERIAL PRIMARY KEY,
    milestone_id BIGINT      NOT NULL REFERENCES project_milestones(id) ON DELETE CASCADE,
    assignee_id  BIGINT      REFERENCES users(id)                       ON DELETE SET NULL,
    title        VARCHAR(200) NOT NULL,
    description  TEXT,
    due_date     DATE,
    status       VARCHAR(30)  NOT NULL DEFAULT 'TODO',
    priority     VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM',
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE group_chat_messages (
    id           BIGSERIAL PRIMARY KEY,
    group_id     BIGINT    NOT NULL REFERENCES project_groups(id) ON DELETE CASCADE,
    sender_id    BIGINT    NOT NULL REFERENCES users(id),
    content      TEXT      NOT NULL,
    message_type VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    file_url     TEXT,
    is_deleted   BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE shared_files (
    id           BIGSERIAL PRIMARY KEY,
    group_id     BIGINT       NOT NULL REFERENCES project_groups(id) ON DELETE CASCADE,
    uploaded_by  BIGINT       NOT NULL REFERENCES users(id),
    file_name    VARCHAR(255) NOT NULL,
    file_url     TEXT         NOT NULL,
    file_size    BIGINT,
    content_type VARCHAR(100),
    description  VARCHAR(200),
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE kanban_tasks (
    id           BIGSERIAL PRIMARY KEY,
    group_id     BIGINT       NOT NULL REFERENCES project_groups(id) ON DELETE CASCADE,
    assignee_id  BIGINT       REFERENCES users(id)                   ON DELETE SET NULL,
    title        VARCHAR(200) NOT NULL,
    description  TEXT,
    "column"     VARCHAR(30)  NOT NULL DEFAULT 'BACKLOG',
    priority     VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM',
    story_points INT,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE meetings (
    id               BIGSERIAL PRIMARY KEY,
    group_id         BIGINT       NOT NULL REFERENCES project_groups(id) ON DELETE CASCADE,
    title            VARCHAR(200) NOT NULL,
    agenda           TEXT,
    meeting_time     TIMESTAMP    NOT NULL,
    duration_minutes INT,
    meeting_link     TEXT,
    status           VARCHAR(20)  NOT NULL DEFAULT 'SCHEDULED',
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE meeting_availability (
    id             BIGSERIAL PRIMARY KEY,
    meeting_id     BIGINT    NOT NULL REFERENCES meetings(id) ON DELETE CASCADE,
    user_id        BIGINT    NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    available_from TIMESTAMP NOT NULL,
    available_to   TIMESTAMP NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ─────────────────────── MODULE 4 — AI Academic Assistant ─────

CREATE TABLE conversations (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title      VARCHAR(200) NOT NULL DEFAULT 'New Conversation',
    subject    VARCHAR(100),
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE chat_messages (
    id              BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT    NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    role            VARCHAR(20) NOT NULL,
    content         TEXT        NOT NULL,
    token_count     INT,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE TABLE study_resources (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    description TEXT,
    subject     VARCHAR(100) NOT NULL,
    type        VARCHAR(50),
    url         TEXT         NOT NULL,
    tags        TEXT[],
    added_by    BIGINT       REFERENCES users(id) ON DELETE SET NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE bookmarks (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT    NOT NULL REFERENCES users(id)          ON DELETE CASCADE,
    resource_id BIGINT    NOT NULL REFERENCES study_resources(id) ON DELETE CASCADE,
    note        TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_bookmark UNIQUE (user_id, resource_id)
);

-- ─────────────────────── NOTIFICATIONS ────────────────────────

CREATE TABLE notifications (
    id             BIGSERIAL PRIMARY KEY,
    recipient_id   BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title          VARCHAR(100) NOT NULL,
    message        TEXT,
    type           VARCHAR(50)  NOT NULL,
    reference_id   BIGINT,
    reference_type VARCHAR(50),
    is_read        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ─────────────────────── INDEXES ──────────────────────────────

CREATE INDEX idx_users_email            ON users(email);
CREATE INDEX idx_students_reg_number    ON students(registration_number);
CREATE INDEX idx_group_members_group    ON group_members(group_id);
CREATE INDEX idx_group_members_user     ON group_members(user_id);
CREATE INDEX idx_invitations_invitee    ON team_invitations(invitee_id);
CREATE INDEX idx_join_req_group         ON join_requests(group_id);
CREATE INDEX idx_resumes_user           ON resumes(user_id);
CREATE INDEX idx_job_listings_active    ON job_listings(is_active);
CREATE INDEX idx_ats_resume             ON ats_analysis(resume_id);
CREATE INDEX idx_job_matches_user       ON job_matches(user_id, match_score DESC);
CREATE INDEX idx_events_date            ON campus_events(event_date);
CREATE INDEX idx_chat_messages_conv     ON chat_messages(conversation_id);
CREATE INDEX idx_notifications_recip    ON notifications(recipient_id, is_read);
