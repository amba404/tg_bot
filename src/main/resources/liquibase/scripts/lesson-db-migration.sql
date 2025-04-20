-- liquibase formatted sql

-- changeset Amba404:1
CREATE SEQUENCE IF NOT EXISTS notification_task_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS notification_task
(
    id           BIGINT NOT NULL,
    user_id      BIGINT,
    date_time    TIMESTAMP WITHOUT TIME ZONE,
    is_done      BOOLEAN,
    text_message VARCHAR(255),
    CONSTRAINT pk_notification_task PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS notification_task_datetime_is_done ON notification_task (date_time, is_done);

-- changeset Amba404:2
ALTER TABLE notification_task
    RENAME user_id to chat_id;