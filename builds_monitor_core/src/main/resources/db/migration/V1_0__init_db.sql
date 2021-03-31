CREATE TABLE builds_info (
    id                SERIAL    NOT NULL,
    product_code      TEXT      NOT NULL,
    build_full_number TEXT      NOT NULL,
    linux_repo_link   TEXT      NOT NULL,
    build_size        INT       NOT NULL,
    checksum_link     TEXT      NOT NULL,
    current_checksum  TEXT      NOT NULL,
    build_date        DATE      NOT NULL,
    status            TEXT      NOT NULL,
    bytes_loaded      INT       NOT NULL DEFAULT 0,
    last_update_time  TIMESTAMP NOT NULL DEFAULT now(),
    product_info      JSONB,
    CONSTRAINT builds_info__uk
        UNIQUE (product_code, build_full_number)
);

CREATE UNIQUE INDEX builds_info_checksum_link_uindex
    ON builds_info(checksum_link);

CREATE UNIQUE INDEX builds_info_id_uindex
    ON builds_info(id);

CREATE UNIQUE INDEX builds_info_linux_repo_link_uindex
    ON builds_info(linux_repo_link);

CREATE INDEX builds_info_product_code_build_full_number_index
    ON builds_info(product_code, build_full_number);

ALTER TABLE builds_info
    ADD CONSTRAINT builds_info_id_pk
        PRIMARY KEY (id);

