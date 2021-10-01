create table gift_certificate
(
    id               BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(50),
    description      VARCHAR(100),
    price            BIGINT,
    duration         BIGINT,
    create_date      VARCHAR(30),
    last_update_date VARCHAR(30)
);

create table tag
(
    id   BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50)
);

create table gifts_and_tags
(
    certificate_id BIGINT NOT NULL,
    tag_id         BIGINT NOT NULL,
    PRIMARY KEY (certificate_id, tag_id),
    FOREIGN KEY (certificate_id) REFERENCES gift_certificate (id),
    FOREIGN KEY (tag_id) REFERENCES tag (id)
);