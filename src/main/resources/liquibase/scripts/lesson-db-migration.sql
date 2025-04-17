-- liquibase formatted sql

-- changeset jonathan:1
CREATE TABLE IF NOT EXISTS public.notification_task
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT    not NULL,
    date_time    TIMESTAMP NOT NULL,
    is_done      BOOLEAN   NOT NULL,
    text_message TEXT      NOT NULL
);

CREATE INDEX notification_task_datetime_is_done ON public.notification_task (date_time, is_done);