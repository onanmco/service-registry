create table if not exists services
(
    id bigint not null auto_increment,
    `name` varchar(255) not null,
    version varchar(255) not null,
    ip varchar(255) not null,
    port varchar(255) not null,
    `timestamp` bigint not null,
    `count` bigint not null default 0,
    constraint pk_services primary key (id),
    constraint un_services unique(`name`,version,ip,port)
) engine=InnoDB;