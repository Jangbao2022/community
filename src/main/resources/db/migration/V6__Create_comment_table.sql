create table comment
(
  id bigint auto_increment,
  parent_id bigint not null comment '父类ID',
  type int not null comment '父类类型',
  commentator int not null comment '评论人ID',
  gmt_create bigint null comment '创建时间',
  gmt_modified bigint null comment '更新时间',
  like_count bigint default 0 null comment '点赞数',
  constraint comment_pk
    primary key (id)
);

