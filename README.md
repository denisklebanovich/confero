# Confero - Conference Management System

## Database Schema

```mermaid
erDiagram
    USERS {
        VARCHAR email PK "Primary Key, email as unique identifier"
        BOOLEAN is_admin "Not Null, Default FALSE"
    }

    CONFERENCE_EDITION {
        BIGSERIAL id PK "Primary Key"
        TIMESTAMP application_deadline_time "Not Null"
    }

    CONFERENCE_INVITEE {
        BIGINT user_id FK "Foreign Key to USERS (email)"
        BIGINT edition_id FK "Foreign Key to CONFERENCE_EDITION (id)"
    }

    SESSION {
        BIGSERIAL id PK "Primary Key"
        VARCHAR title "Not Null"
        VARCHAR type "Not Null, Enum('SESSION', 'WORKSHOP', 'TUTORIAL')"
        VARCHAR creator_id FK "References USERS (email)"
        JSONB tags "Optional, stores tags as JSON array"
        BIGINT edition_id FK "Foreign Key to CONFERENCE_EDITION (id)"
        VARCHAR description "Not Null, Max Length 40000"
        VARCHAR status "Not Null, Enum('PENDING', 'DRAFT', 'REJECTED', 'ACCEPTED', 'CHANGE_REQUESTED')"
        TIMESTAMP created_at "Not Null"
    }

    PROPOSAL_COMMENT {
        BIGSERIAL id PK "Primary Key"
        BIGINT session_id FK "References SESSION (id)"
        VARCHAR user_id FK "References USERS (email)"
        VARCHAR content "Not Null, Max Length 512"
        TIMESTAMP created_at "Not Null"
    }

    PRESENTER {
        BIGSERIAL id PK "Primary Key"
        VARCHAR email FK "References USERS (email)"
        BIGINT presentation_id FK "References PRESENTATION (id)"
        VARCHAR orcid "Not Null"
        VARCHAR name "Not Null"
        VARCHAR surname "Not Null"
        BOOLEAN is_main "Not Null, Default FALSE"
    }

    PRESENTATION {
        BIGINT presenter_id PK "Primary Key, Foreign Key to PRESENTER (id)"
        VARCHAR title "Not Null"
        VARCHAR description "Max Length 12000"
        TIMESTAMP start_time 
        TIMESTAMP end_time 
        BIGINT session_id FK "References SESSION (id)"
    }

    ATTACHMENT {
        BIGSERIAL id PK "Primary Key"
        BIGINT session_id FK "References SESSION (id)"
        BIGINT presentation_id FK "References PRESENTATION (id)"
        VARCHAR title "Not Null"
        VARCHAR url "Not Null"
        TIMESTAMP created_at "Not Null"
    }

    USERS ||--o{ SESSION: "creates"
    USERS ||--o{ PROPOSAL_COMMENT: "comments on"
    USERS ||--o{ PRESENTER: "is associated with"
    SESSION ||--o{ PROPOSAL_COMMENT: "has comments"
    SESSION ||--o{ PRESENTATION: "has presentations"
    PRESENTATION ||--o{ ATTACHMENT: "has attachments"
    PRESENTATION ||--o| PRESENTER: "organized by"
    USERS ||--o{ CONFERENCE_INVITEE: "is invited"
    CONFERENCE_EDITION ||--o{ CONFERENCE_INVITEE: "includes invitees"
    CONFERENCE_EDITION ||--o{ SESSION: "hosts"
```

### OpenAPI code generation

The same OpenAPI specification file is used to generate type-safe client and server API code for both `ui` and `api`. The file is located at `confero-api/src/main/resources/openapi.yaml`.

Swagger UI is available at `api/swagger`.