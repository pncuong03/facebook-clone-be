CREATE TABLE tbl_user
(
    id        BIGSERIAL PRIMARY KEY,
    username  VARCHAR,
    password  VARCHAR,
    full_name VARCHAR,
    image_url VARCHAR,
    birthday  TIMESTAMP,
    gender    VARCHAR
);

CREATE TABLE tbl_post
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT REFERENCES tbl_user (id),
    content       VARCHAR,
    image_urls    VARCHAR,
    like_count    INTEGER,
    comment_count INTEGER,
    share_count   INTEGER,
    share_id      BIGINT REFERENCES tbl_post (id),
    state         VARCHAR,
    created_at    TIMESTAMP
);

CREATE TABLE tbl_chat
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR,
    manager_id       BIGINT REFERENCES tbl_user (id),
    chat_type        VARCHAR,
    newest_chat_time TIMESTAMP,
    user_id1         BIGINT REFERENCES tbl_user (id),
    user_id2         BIGINT REFERENCES tbl_user (id),
    newest_user_id   BIGINT REFERENCES tbl_user (id),
    newest_message   VARCHAR
);

CREATE TABLE tbl_user_chat_map
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES tbl_user (id),
    chat_id BIGINT REFERENCES tbl_chat (id)
);

CREATE TABLE tbl_message
(
    id            BIGSERIAL PRIMARY KEY,
    sender_id     BIGINT REFERENCES tbl_user (id),
    chat_id1      BIGINT REFERENCES tbl_chat (id),
    chat_id2      BIGINT REFERENCES tbl_chat (id),
    group_chat_id BIGINT REFERENCES tbl_chat (id),
    message       VARCHAR,
    created_at    TIMESTAMP
);

CREATE TABLE tbl_event_notification
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT REFERENCES tbl_user (id),
    event_type VARCHAR,
    state      VARCHAR,
    chat_id    BIGINT REFERENCES tbl_chat (id)
);

CREATE TABLE tbl_like_map
(
    id      BIGSERIAL PRIMARY KEY,
    post_id BIGINT REFERENCES tbl_post (id),
    user_id BIGINT REFERENCES tbl_user (id)
);

CREATE TABLE tbl_comment_map
(
    id         BIGSERIAL PRIMARY KEY,
    post_id    BIGINT REFERENCES tbl_post (id),
    user_id    BIGINT REFERENCES tbl_user (id),
    comment    VARCHAR,
    created_at TIMESTAMP
);

CREATE TABLE tbl_share_map
(
    id      BIGSERIAL PRIMARY KEY,
    post_id BIGINT REFERENCES tbl_post (id),
    user_id BIGINT REFERENCES tbl_user (id)
);

ALTER TABLE tbl_chat
    ADD COLUMN is_me BOOLEAN;
ALTER TABLE tbl_event_notification
    ADD COLUMN message VARCHAR;
ALTER TABLE tbl_event_notification
    ADD COLUMN created_at TIMESTAMP;

CREATE TABLE tbl_friend_request
(
    id          BIGSERIAL PRIMARY KEY,
    sender_id   BIGINT REFERENCES tbl_user (id),
    receiver_id BIGINT REFERENCES tbl_user (id),
    created_at  TIMESTAMP
);

CREATE TABLE tbl_friend_map
(
    id        BIGSERIAL PRIMARY KEY,
    user_id_1 BIGINT REFERENCES tbl_user (id),
    user_id_2 BIGINT REFERENCES tbl_user (id)
);

CREATE TABLE tbl_notification
(
    id            BIGSERIAL PRIMARY KEY,
    type          VARCHAR,
    user_id       BIGINT REFERENCES tbl_user (id),
    interact_id   BIGINT REFERENCES tbl_user (id),
    group_id      BIGINT,
    interact_type VARCHAR,
    post_id       BIGINT,
    has_seen      BOOLEAN,
    created_at    TIMESTAMP
);

ALTER TABLE tbl_chat
    ADD COLUMN image_url VARCHAR;

create table tbl_tag
(
    id   bigserial primary key,
    name varchar
)

create table tbl_group
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGSERIAL,
    name         VARCHAR,
    member_count INTEGER,
    role         VARCHAR
)
create table tbl_group_tag_map
(
    id       bigserial primary key,
    group_id bigserial references tbl_group (id),
    tag_id   bigserial references tbl_tag (id)
)
create table tbl_user_group_map(
                                   id bigserial primary key,
                                   user_id bigserial references tbl_user(id),
                                   group_id bigserial references tbl_group(id),
                                   role varchar
)