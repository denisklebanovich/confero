CREATE TABLE IF NOT EXISTS users
(
    email    VARCHAR(255) PRIMARY KEY,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS conference_edition
(
    id                        BIGSERIAL PRIMARY KEY,
    application_deadline_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at                TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS conference_invitee
(
    user_id    VARCHAR NOT NULL,
    edition_id BIGINT  NOT NULL,
    PRIMARY KEY (user_id, edition_id),

    CONSTRAINT fk_conference_invitee_user FOREIGN KEY (user_id) REFERENCES users (email),
    CONSTRAINT fk_conference_invitee_edition FOREIGN KEY (edition_id) REFERENCES conference_edition (id)
);


CREATE TABLE IF NOT EXISTS session
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255)                NOT NULL,
    type        VARCHAR(255)                NOT NULL,
    creator_id  VARCHAR                     NOT NULL,
    tags        JSONB,
    edition_id  BIGINT                      NOT NULL,
    description VARCHAR(40000)              NOT NULL,
    status      VARCHAR(255)                NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT session_type_check CHECK (type IN ('SESSION', 'WORKSHOP', 'TUTORIAL')),
    CONSTRAINT session_status_check CHECK (status IN ('PENDING', 'DRAFT', 'REJECTED', 'ACCEPTED', 'CHANGE_REQUESTED')),
    CONSTRAINT fk_session_creator FOREIGN KEY (creator_id) REFERENCES users (email),
    CONSTRAINT fk_session_edition FOREIGN KEY (edition_id) REFERENCES conference_edition (id)
);


CREATE TABLE IF NOT EXISTS application_comment
(
    id         BIGSERIAL PRIMARY KEY,
    session_id BIGINT                      NOT NULL,
    user_id    VARCHAR                     NOT NULL,
    content    TEXT                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_application_comment_session FOREIGN KEY (session_id) REFERENCES session (id),
    CONSTRAINT fk_application_comment_user FOREIGN KEY (user_id) REFERENCES users (email)
);


CREATE TABLE IF NOT EXISTS presenter
(
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL,
    presentation_id BIGINT       NOT NULL,
    session_id      BIGINT       NOT NULL,
    orcid           VARCHAR(255) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    surname         VARCHAR(255) NOT NULL,
    is_main         BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_presenter_user FOREIGN KEY (email) REFERENCES users (email),
    CONSTRAINT fk_presenter_presentation FOREIGN KEY (presentation_id) REFERENCES presentation (id)
);

-- partial unique index to ensure only one main presenter per presentation
CREATE UNIQUE INDEX IF NOT EXISTS unique_main_presenter_per_presentation
    ON presenter (presentation_id)
    WHERE is_main = TRUE;


CREATE TABLE IF NOT EXISTS presentation
(
    id          BIGSERIAL PRIMARY KEY,
    session_id  BIGINT                      NOT NULL,
    title       VARCHAR(255),
    description VARCHAR(12000),
    start_time  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_presentation_session FOREIGN KEY (session_id) REFERENCES session (id)
);


CREATE TABLE IF NOT EXISTS session_attachment
(
    id           BIGSERIAL PRIMARY KEY,
    session_id   BIGINT       NOT NULL,
    presenter_id BIGINT       NOT NULL,
    title        VARCHAR(255) NOT NULL,
    url          VARCHAR(255) NOT NULL,

    CONSTRAINT fk_session_attachment_session FOREIGN KEY (session_id) REFERENCES session (id),
    CONSTRAINT fk_session_attachment_presenter FOREIGN KEY (presenter_id) REFERENCES presenter (id)
);
