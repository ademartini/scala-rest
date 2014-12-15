# --- !Ups

create table uploaded_files (
    id bigint primary key auto_increment not null,
    name varchar(255) not null,
    word_count int not null
);

create table counts (
    id bigint primary key auto_increment not null,
    file_id bigint not null,
    foreign key (file_id) references uploaded_files(id),
    word clob not null,
    count int not null
);

# --- !Downs

drop table counts;
drop table uploaded_files;
