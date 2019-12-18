create table user
(
  id           int auto_increment
    primary key,
  name         varchar(100) null,
  account_id   varchar(50)  null,
  token        varchar(36)  null,
  gmt_create   bigint(255)  null,
  gmt_modified bigint(255)  null
)