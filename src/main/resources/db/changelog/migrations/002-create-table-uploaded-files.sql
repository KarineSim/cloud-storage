--liquibase formatted sql

--changeset simonian:2
create table netology.uploaded_files(
    id bigint auto_increment primary key,
    file_name varchar(255) unique,
    size bigint not null,
    file_content mediumblob not null,
    upload_date timestamp not null,
    update_date timestamp,
    status varchar(255) not null,
    type varchar(255) not null,
    authorized_users_id bigint references authorized_users(id)
);

