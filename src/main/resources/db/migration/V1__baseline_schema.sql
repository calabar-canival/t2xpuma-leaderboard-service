create sequence leaderboard_seq start with 1 increment by 50;
create sequence users_seq start with 1 increment by 50;
create table leaderboard (rank integer, id bigint not null, last_synced_at timestamp(6) with time zone, points bigint, phone_number varchar(255), primary key (id));
create table users (prediction_subscription_status boolean not null, status smallint check ((status between 0 and 1)), id bigint not null, points bigint not null, phone_number varchar(255) not null, primary key (id));