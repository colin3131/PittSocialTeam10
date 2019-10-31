drop table if exists profile cascade;
drop table if exists friend cascade;
drop table if exists pendingFriend cascade;
drop table if exists message cascade;
drop table if exists messageRecipient cascade;
drop table if exists "group" cascade;
drop table if exists groupMember cascade;
drop table if exists pendingGroupMember cascade;

create table profile
(
    userID          integer,
    name            varchar(50) NOT NULL
        check (name LIKE '% %'),
    email           varchar(50) NOT NULL
        check (email LIKE '%@%.___'),
    password        varchar(50) NOT NULL,
    date_of_birth   date NOT NULL
        check (date_of_birth >= '1/1/1901' AND date_of_birth <= current_date),
    lastlogin       timestamp NOT NULL,

    CONSTRAINT profile_pk Primary Key (userID),
    CONSTRAINT profile_uq Unique (email)
);

create table friend
(
    userID1         integer NOT NULL,
    userID2         integer NOT NULL,
    JDate           date NOT NULL
        check (JDate <= current_date),
    message         varchar(200) NOT NULL,

    CONSTRAINT friend_pk Primary Key (userID1, userID2),
    CONSTRAINT friend_fk foreign Key (userID1) references profile (userID),
    CONSTRAINT friend_fk2 foreign Key (userID2) references profile (userID)
);

create table pendingFriend
(
    fromID          integer NOT NULL,
    toID            integer NOT NULL,
    message         varchar(200) NOT NULL,

    CONSTRAINT pendingFriend_pk Primary Key (fromID, toID),
    CONSTRAINT pendingFriend_fk foreign Key (fromID) references profile (userID),
    CONSTRAINT pendingFriend_fk2 foreign Key (toID) references profile (userID)
);

create table "group"
(
    gID             integer NOT NULL,
    name            varchar(50) NOT NULL,
    "limit"           integer NOT NULL,
    description     varchar(200) DEFAULT NULL,

    CONSTRAINT group_pk Primary Key (gID),
    CONSTRAINT group_uq Unique (name)
);

-- DO A COUPLE CONSTAINT CHECKS STILL
create table message
(
    msgID           integer NOT NULL,
    fromID          integer NOT NULL,
    message         varchar(200) NOT NULL,
    toUserID        integer DEFAULT NULL,
    toGroupID       integer DEFAULT NULL,
    timeSent        timestamp NOT NULL
        check (timeSent <= current_timestamp),

    CONSTRAINT message_pk Primary Key (msgID),
    CONSTRAINT message_fk foreign Key (fromID) references profile (userID),
    CONSTRAINT message_fk2 foreign Key (toUserID) references profile (userID),
    CONSTRAINT message_fk3 foreign Key (toGroupID) references "group" (gID)
);

create table messageRecipient
(
    msgID           integer NOT NULL,
    userID          integer NOT NULL,

    CONSTRAINT messageRecipient_pk Primary Key (msgID),
    CONSTRAINT messageRecipient_fk foreign Key (msgID) references message(msgID),
    CONSTRAINT messageRecipient_fk2 foreign Key (userID) references profile(userID)
);

create table groupMember
(
    gID             integer NOT NULL,
    userID          integer NOT NULL,
    role            varchar(20) NOT NULL
        check (role = 'manager' OR role = 'member'),

    CONSTRAINT groupMember_pk Primary Key (gID, userID),
    CONSTRAINT groupMember_fk foreign Key (gID) references "group" (gID),
    CONSTRAINT groupMember_fk2 foreign Key (userID) references profile(userID)
);

create table pendingGroupMember
(
    gID             integer NOT NULL,
    userID          integer NOT NULL,
    message         varchar(200) NOT NULL,

    CONSTRAINT pendingGroupMember_pk Primary Key (gID, userID),
    CONSTRAINT pendingGroupMember_fk foreign Key (gID) references "group" (gID),
    CONSTRAINT pendingGroupMember_fk2 foreign Key (userID) references profile(userID)
);