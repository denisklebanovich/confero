CREATE SEQUENCE IF NOT EXISTS session_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE attachment
(
    id         VARCHAR(255) NOT NULL,
    name       VARCHAR(255),
    url        VARCHAR(255),
    session_id BIGINT,
    CONSTRAINT pk_attachment PRIMARY KEY (id)
);

CREATE TABLE keyword
(
    value VARCHAR(255) NOT NULL,
    CONSTRAINT pk_keyword PRIMARY KEY (value)
);

CREATE TABLE presenter
(
    orcid   VARCHAR(255) NOT NULL,
    name    VARCHAR(255),
    surname VARCHAR(255),
    CONSTRAINT pk_presenter PRIMARY KEY (orcid)
);

CREATE TABLE proposal
(
    id      VARCHAR(255) NOT NULL,
    title   VARCHAR(255),
    content VARCHAR(255),
    status  VARCHAR(255),
    type    VARCHAR(255),
    CONSTRAINT pk_proposal PRIMARY KEY (id)
);

CREATE TABLE proposal_keywords
(
    proposal_id    VARCHAR(255) NOT NULL,
    keywords_value VARCHAR(255) NOT NULL
);

CREATE TABLE proposal_presenters
(
    proposal_id      VARCHAR(255) NOT NULL,
    presenters_orcid VARCHAR(255) NOT NULL
);

CREATE TABLE session
(
    id              BIGINT NOT NULL,
    duration        INTEGER,
    title           VARCHAR(255),
    description     VARCHAR(255),
    presenter_orcid VARCHAR(255),
    start_time      TIMESTAMP WITHOUT TIME ZONE,
    end_time        TIMESTAMP WITHOUT TIME ZONE,
    stream_url      VARCHAR(255),
    CONSTRAINT pk_session PRIMARY KEY (id)
);

CREATE TABLE session_attachments
(
    session_id     BIGINT       NOT NULL,
    attachments_id VARCHAR(255) NOT NULL
);

ALTER TABLE session_attachments
    ADD CONSTRAINT uc_session_attachments_attachments UNIQUE (attachments_id);

ALTER TABLE attachment
    ADD CONSTRAINT FK_ATTACHMENT_ON_SESSION FOREIGN KEY (session_id) REFERENCES session (id);

ALTER TABLE session
    ADD CONSTRAINT FK_SESSION_ON_PRESENTER_ORCID FOREIGN KEY (presenter_orcid) REFERENCES presenter (orcid);

ALTER TABLE proposal_keywords
    ADD CONSTRAINT fk_prokey_on_keyword FOREIGN KEY (keywords_value) REFERENCES keyword (value);

ALTER TABLE proposal_keywords
    ADD CONSTRAINT fk_prokey_on_proposal FOREIGN KEY (proposal_id) REFERENCES proposal (id);

ALTER TABLE proposal_presenters
    ADD CONSTRAINT fk_propre_on_presenter FOREIGN KEY (presenters_orcid) REFERENCES presenter (orcid);

ALTER TABLE proposal_presenters
    ADD CONSTRAINT fk_propre_on_proposal FOREIGN KEY (proposal_id) REFERENCES proposal (id);

ALTER TABLE session_attachments
    ADD CONSTRAINT fk_sesatt_on_attachment FOREIGN KEY (attachments_id) REFERENCES attachment (id);

ALTER TABLE session_attachments
    ADD CONSTRAINT fk_sesatt_on_session FOREIGN KEY (session_id) REFERENCES session (id);