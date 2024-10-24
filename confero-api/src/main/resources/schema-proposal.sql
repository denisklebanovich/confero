CREATE TABLE IF NOT EXISTS users
(
    id    BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    role  VARCHAR(255) NOT NULL,
    CONSTRAINT users_role_check CHECK (role IN ('ADMIN', 'USER', 'PARTICIPANT'))
);

CREATE TABLE IF NOT EXISTS proposal
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255)                NOT NULL,
    type        VARCHAR(255)                NOT NULL,
    creator_id  BIGINT                      NOT NULL,
    tags        JSONB,
    description VARCHAR(40000)              NOT NULL,
    status      VARCHAR(255)                NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT proposal_type_check CHECK (type IN ('TALK', 'POSTER', 'WORKSHOP')),
    CONSTRAINT proposal_status_check CHECK (status IN ('PENDING', 'DRAFT', 'REJECTED', 'ACCEPTED', 'CHANGE_REQUESTED')),
    FOREIGN KEY (creator_id) REFERENCES users (id)
);


CREATE TABLE IF NOT EXISTS comment
(
    id          BIGSERIAL PRIMARY KEY,
    proposal_id BIGINT                      NOT NULL,
    user_id     BIGINT                      NOT NULL,
    content     VARCHAR(512)                NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    FOREIGN KEY (proposal_id) REFERENCES proposal (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);


CREATE TABLE IF NOT EXISTS proposal_presenters
(
    proposal_id  BIGINT       NOT NULL,
    presenter_id BIGINT       NOT NULL,
    orcid        VARCHAR(255) NOT NULL,
    PRIMARY KEY (proposal_id, presenter_id),
    FOREIGN KEY (proposal_id) REFERENCES proposal (id),
    FOREIGN KEY (presenter_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS session
(
    id          BIGSERIAL PRIMARY KEY       NOT NULL,
    proposal_id BIGINT                      NOT NULL,
    start_time  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    FOREIGN KEY (proposal_id) REFERENCES proposal (id)
);

CREATE TABLE IF NOT EXISTS timetable
(
    id           BIGSERIAL PRIMARY KEY       NOT NULL,
    session_id   BIGINT                      NOT NULL,
    presenter_id BIGINT                      NOT NULL,
    start_time   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    FOREIGN KEY (session_id) REFERENCES session (id),
    FOREIGN KEY (presenter_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS session_attachments
(
    id           BIGSERIAL PRIMARY KEY,
    session_id   BIGINT       NOT NULL,
    title        VARCHAR(255) NOT NULL,
    url          VARCHAR(255) NOT NULL,
    presenter_id BIGINT       NOT NULL,
    FOREIGN KEY (session_id) REFERENCES session (id),
    FOREIGN KEY (presenter_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS configuration
(
    key   VARCHAR(255) PRIMARY KEY NOT NULL,
    value VARCHAR(255)             NOT NULL,
    CONSTRAINT configuration_key_unique UNIQUE (key)
);
