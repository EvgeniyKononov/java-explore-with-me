--drop table stats;

CREATE TABLE IF NOT EXISTS stats
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app       VARCHAR(256)                            NOT NULL,
    uri       VARCHAR(128)                            NOT NULL,
    ip        VARCHAR(32)                             NOT NULL,
    timestamp TIMESTAMP                               NOT NULL,
    CONSTRAINT stats_pk PRIMARY KEY (id)
);