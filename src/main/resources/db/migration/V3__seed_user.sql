insert into _users (created_at, email, password, updated_at, username)
VALUES (now(), 'user@gmail.com', '$2a$10$DHsoy5vHhlnYq3Zl4AhVBOXy7suwuQRuQ0thGS.RJCHAVJeZ4pEqG', now(), 'user');

insert into _users (created_at, email, password, updated_at, username)
VALUES (now(), 'admin@gmail.com', '$2a$10$DHsoy5vHhlnYq3Zl4AhVBOXy7suwuQRuQ0thGS.RJCHAVJeZ4pEqG', now(), 'admin');

insert into _users (created_at, email, password, updated_at, username)
VALUES (now(), 'mod@gmail.com', '$2a$10$DHsoy5vHhlnYq3Zl4AhVBOXy7suwuQRuQ0thGS.RJCHAVJeZ4pEqG', now(), 'mod');

-- user -> ROLE_USER
INSERT INTO user_roles (user_id, role_id)
VALUES (
           (SELECT id FROM _users WHERE username = 'user'),
           (SELECT id FROM roles WHERE name = 'ROLE_USER')
       );

-- admin -> ROLE_ADMIN
INSERT INTO user_roles (user_id, role_id)
VALUES (
           (SELECT id FROM _users WHERE username = 'admin'),
           (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
       );

-- mod -> ROLE_MODERATOR
INSERT INTO user_roles (user_id, role_id)
VALUES (
           (SELECT id FROM _users WHERE username = 'mod'),
           (SELECT id FROM roles WHERE name = 'ROLE_MODERATOR')
       );

insert into user_roles (user_id, role_id)
values (
        (select id from _users where username='mod'),
        (select id from roles where name='ROLE_USER')
       )
