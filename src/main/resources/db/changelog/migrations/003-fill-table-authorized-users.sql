--liquibase formatted sql

--changeset simonian:3
insert into netology.authorized_users(login, password)
values ('tima', '$2a$12$d6HEMjwZ5Th2cwMZ58a2yeWLVD2a5VtNtm.9cLZTH/3UNd1.zJaP.'), --pass1
       ('sema', '$2a$12$ugk0MEJ6/Uw4S7CO/9HhAONaz4STEERz.9KjWnOmtMUiXTh/Q./t6'), --pass2
       ('rosa', '$2a$12$DFzcAN4aWt56gKPvHFL4zeUvY/vQA4VeVHsXn4Kwn2ihD/wKD2dH.'); --pass3