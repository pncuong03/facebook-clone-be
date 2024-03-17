CREATE TABLE tbl_user
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR,
    password VARCHAR
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
ALTER TABLE tbl_chat
    ADD COLUMN newest_message VARCHAR;
ALTER TABLE tbl_event_notification
    ADD COLUMN message VARCHAR;
ALTER TABLE tbl_event_notification
    ADD COLUMN created_at TIMESTAMP;

// đã gửi kết bạn chưa (api lấy danh sách người dùng)




