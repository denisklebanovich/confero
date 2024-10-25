CREATE TABLE IF NOT EXISTS users
(
    email VARCHAR(255) PRIMARY KEY,
    role  VARCHAR(255) NOT NULL,
    CONSTRAINT users_role_check CHECK (role IN ('ADMIN', 'USER', 'PARTICIPANT'))
);

CREATE TABLE IF NOT EXISTS session
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255)                NOT NULL,
    type        VARCHAR(255)                NOT NULL,
    creator_id  BIGINT                      NOT NULL,
    tags        JSONB,
    description VARCHAR(40000)              NOT NULL,
    status      VARCHAR(255)                NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT session_type_check CHECK (type IN ('SESSION', 'WORKSHOP', 'TUTORIAL')),
    CONSTRAINT session_status_check CHECK (status IN ('PENDING', 'DRAFT', 'REJECTED', 'ACCEPTED', 'CHANGE_REQUESTED')),
    FOREIGN KEY (creator_id) REFERENCES users (email)
);


CREATE TABLE IF NOT EXISTS proposal_comment
(
    id         BIGSERIAL PRIMARY KEY,
    session_id BIGINT                      NOT NULL,
    user_id    BIGINT                      NOT NULL,
    content    VARCHAR(512)                NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    FOREIGN KEY (session_id) REFERENCES session (id),
    FOREIGN KEY (user_id) REFERENCES users (email)
);


CREATE TABLE IF NOT EXISTS presenter
(
    id         BIGSERIAL PRIMARY KEY,
    email      BIGINT       NOT NULL,
    session_id BIGINT       NOT NULL,
    orcid      VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    surname    VARCHAR(255) NOT NULL,
    FOREIGN KEY (session_id) REFERENCES session (id),
    FOREIGN KEY (email) REFERENCES users (email)
);

CREATE TABLE IF NOT EXISTS presentation
(
    presenter_id BIGINT PRIMARY KEY,
    start_time   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    FOREIGN KEY (presenter_id) REFERENCES presenter (id)
);

CREATE TABLE IF NOT EXISTS presenter_attachment
(
    id           BIGSERIAL PRIMARY KEY,
    presenter_id BIGINT       NOT NULL,
    title        VARCHAR(255) NOT NULL,
    url          VARCHAR(255) NOT NULL,
    FOREIGN KEY (presenter_id) REFERENCES presenter (id)
);

CREATE TABLE IF NOT EXISTS configuration
(
    key   VARCHAR(255) PRIMARY KEY NOT NULL,
    value JSONB                    NOT NULL,
    CONSTRAINT configuration_key_unique UNIQUE (key)
);
