create table user
(id   bigint primary key not null auto_increment,
username varchar(100) unique not null,
password varchar(100),
avatar varchar(100),
created_at datetime,
updated_at datetime
)