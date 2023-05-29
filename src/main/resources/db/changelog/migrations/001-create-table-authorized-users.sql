--liquibase formatted sql

--changeset simonian:1
create table netology.authorized_users(
    id bigint auto_increment primary key,
    login varchar(255) unique,
    password varchar(255) not null
);

