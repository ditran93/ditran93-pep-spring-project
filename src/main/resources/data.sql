drop table if exists message;
drop table if exists account;
create table account (
    accountId int primary key auto_increment,
    username varchar(255) not null unique,
    password varchar(255)
);
create table message (
    messageId int primary key auto_increment,
    postedBy int,
    messageText varchar(255),
    timePostedEpoch bigint,
    foreign key (postedBy) references account(accountId)
);

-- Starting test values with ids of 9999 to avoid test issues
insert into account values (9999, 'testuser1', 'password');
insert into account values (9998, 'testuser2', 'password');
insert into account values (9997, 'testuser3', 'password');
insert into account values (9996, 'testuser4', 'password');

insert into message values (9999, 9999,'test message 1',1669947792);
insert into message values (9997, 9997,'test message 2',1669947792);
insert into message values (9996, 9996,'test message 3',1669947792);

