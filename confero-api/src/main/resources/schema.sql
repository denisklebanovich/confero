CREATE TABLE IF NOT EXISTS users
(
    id           BIGSERIAL PRIMARY KEY,
    orcid        VARCHAR(255),
    access_token VARCHAR(255),
    avatar_url   TEXT,
    is_admin     BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS unique_user_orcid ON users (orcid);
CREATE UNIQUE INDEX IF NOT EXISTS unique_user_access_token ON users (access_token);

CREATE TABLE IF NOT EXISTS user_emails
(
    email              VARCHAR(255) PRIMARY KEY,
    confirmed          BOOLEAN NOT NULL DEFAULT FALSE,
    verification_token VARCHAR(255),
    user_id            BIGINT  NOT NULL,
    CONSTRAINT fk_user_emails_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS conference_edition
(
    id                        BIGSERIAL PRIMARY KEY,
    application_deadline_time TIMESTAMP NOT NULL,
    created_at                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS conference_invitee
(
    email      VARCHAR(255) NOT NULL,
    edition_id BIGINT       NOT NULL,
    PRIMARY KEY (email, edition_id),
    CONSTRAINT fk_conference_invitee_edition FOREIGN KEY (edition_id) REFERENCES conference_edition (id)
);

CREATE TABLE IF NOT EXISTS session
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    type        VARCHAR(255) NOT NULL CHECK (type IN ('SESSION', 'WORKSHOP', 'TUTORIAL')),
    creator_id  BIGINT       NOT NULL,
    tags        JSONB                 DEFAULT '[]',
    edition_id  BIGINT       NOT NULL,
    description TEXT         NOT NULL,
    status      VARCHAR(255) NOT NULL CHECK (status IN
                                             ('PENDING', 'DRAFT', 'REJECTED', 'ACCEPTED', 'CHANGE_REQUESTED')),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_session_creator FOREIGN KEY (creator_id) REFERENCES users (id),
    CONSTRAINT fk_session_edition FOREIGN KEY (edition_id) REFERENCES conference_edition (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS agenda
(
    user_id    BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    CONSTRAINT pk_agenda PRIMARY KEY (user_id, session_id),
    CONSTRAINT fk_agenda_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_agenda_session FOREIGN KEY (session_id) REFERENCES session (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS application_comment
(
    id         BIGSERIAL PRIMARY KEY,
    session_id BIGINT    NOT NULL,
    content    TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_application_comment_session FOREIGN KEY (session_id) REFERENCES session (id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS presentation
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    session_id  BIGINT       NOT NULL,
    start_time  TIMESTAMP,
    end_time    TIMESTAMP,
    CONSTRAINT fk_presentation_session FOREIGN KEY (session_id) REFERENCES session (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS presenter
(
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL,
    orcid           VARCHAR(255) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    surname         VARCHAR(255) NOT NULL,
    title           VARCHAR(255),
    organization    VARCHAR(255),
    is_speaker      BOOLEAN      NOT NULL DEFAULT FALSE,
    presentation_id BIGINT       NOT NULL,
    CONSTRAINT fk_presenter_presentation FOREIGN KEY (presentation_id) REFERENCES presentation (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS attachment
(
    id         BIGSERIAL PRIMARY KEY,
    creator_id BIGINT       NOT NULL,
    name      VARCHAR(255) NOT NULL,
    url        VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attachment_presenter FOREIGN KEY (creator_id) REFERENCES presenter (id) ON DELETE CASCADE
);
