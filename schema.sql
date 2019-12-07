-- The following creates tables w/ constraints for the 1555 project 

drop table if exists profile cascade;
drop table if exists friend cascade;
drop table if exists pendingFriend cascade;
drop table if exists messageInfo cascade;
drop table if exists messageRecipient cascade;
drop table if exists groupInfo cascade;
drop table if exists groupMember cascade;
drop table if exists pendingGroupMember cascade;

create table profile
(   -- Assumes all must be entered by the user (not null)
    userID          integer,
    name            varchar(50) NOT NULL
        check (name LIKE '%'),            -- Assumes they must enter a first and last name (now only first name 4 demo)
    email           varchar(50) NOT NULL
        check (email LIKE '%@%.___'),       -- Assumes they must enter an email with something@something.(3 extention)
    password        varchar(50) NOT NULL,
    date_of_birth   date NOT NULL
        check (date_of_birth >= '1/1/1901' AND date_of_birth <= current_date), -- Assumes they were not born before 1901
    lastlogin       timestamp NOT NULL,

    CONSTRAINT profile_pk Primary Key (userID),
    CONSTRAINT profile_uq Unique (email)
);

create table friend
(   -- Assumes all must be entered by the user (not null)
    userID1         integer NOT NULL,
    userID2         integer NOT NULL,
    JDate           date NOT NULL
        check (JDate <= current_date),      -- Assumes the date must be before the present time
    message         varchar(200) NOT NULL,

    CONSTRAINT friend_pk Primary Key (userID1, userID2),
    CONSTRAINT friend_fk foreign Key (userID1) references profile (userID),
    CONSTRAINT friend_fk2 foreign Key (userID2) references profile (userID)
);

create table pendingFriend
(   -- Assumes all must be entered by the user (not null)
    fromID          integer NOT NULL,
    toID            integer NOT NULL,
    message         varchar(200) NOT NULL,

    CONSTRAINT pendingFriend_pk Primary Key (fromID, toID),
    CONSTRAINT pendingFriend_fk foreign Key (fromID) references profile (userID),
    CONSTRAINT pendingFriend_fk2 foreign Key (toID) references profile (userID)
);

create table groupInfo
(   -- Assumes gID, name, and size must be entered by the user (not null)
    gID             integer NOT NULL,
    name            varchar(50) NOT NULL,
    size           integer NOT NULL,
    description     varchar(200) DEFAULT NULL,

    CONSTRAINT group_pk Primary Key (gID),
    CONSTRAINT group_uq Unique (name)
);

create table messageInfo
(   -- Assumes msgID, fromID, message, and timeSent must be entered by the user (not null)
    msgID           integer NOT NULL,
    fromID          integer NOT NULL,
    message         varchar(200) NOT NULL,
    toUserID        integer DEFAULT NULL,
    toGroupID       integer DEFAULT NULL,
    timeSent        timestamp NOT NULL
        check (timeSent <= current_timestamp), -- Assumes timeSent must be before the current time

    CONSTRAINT message_pk Primary Key (msgID),
    CONSTRAINT message_fk foreign Key (fromID) references profile (userID),
    CONSTRAINT message_fk2 foreign Key (toUserID) references profile (userID),
    CONSTRAINT message_fk3 foreign Key (toGroupID) references groupInfo (gID)
);

create table messageRecipient
(   -- Assumes all must be entered by the user (not null)
    msgID           integer NOT NULL,
    userID          integer NOT NULL,

    CONSTRAINT messageRecipient_pk Primary Key (msgID, userID),
    CONSTRAINT messageRecipient_fk foreign Key (msgID) references messageInfo(msgID),
    CONSTRAINT messageRecipient_fk2 foreign Key (userID) references profile(userID)
);

create table groupMember
(   -- Assumes all must be entered by the user (not null) 
    gID             integer NOT NULL,
    userID          integer NOT NULL,
    role            varchar(20) NOT NULL
        check (role = 'manager' OR role = 'member'), -- Assumes the only roles allowed are manager or member

    CONSTRAINT groupMember_pk Primary Key (gID, userID),
    CONSTRAINT groupMember_fk foreign Key (gID) references groupInfo (gID),
    CONSTRAINT groupMember_fk2 foreign Key (userID) references profile(userID)
);

create table pendingGroupMember
(   -- Assumes all must be entered by the user (not null)
    gID             integer NOT NULL,
    userID          integer NOT NULL,
    message         varchar(200) NOT NULL,

    CONSTRAINT pendingGroupMember_pk Primary Key (gID, userID),
    CONSTRAINT pendingGroupMember_fk foreign Key (gID) references groupInfo (gID),
    CONSTRAINT pendingGroupMember_fk2 foreign Key (userID) references profile(userID)
);