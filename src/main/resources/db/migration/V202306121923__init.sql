

create table users(
    id bigserial primary key,
    username text unique not null,
    email text not null,
    password text not null,
    role text not null default 'USER'
);

create table friend2friend(
    user_id bigint,
    friend_id bigint
);

create table posts(
    id bigserial primary key,
    text text not null,
    title text not null,
    user_id bigint references users
);



create table friend_requests(
    id bigserial primary key,
    sender_id bigint references users,
    receiver_id bigint references users,
    status text not null default 'PENDING'
);

