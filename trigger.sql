-- PittSocial Triggers

-- Delete pendingFriends on insert to friends if they were previously pendingFriends
DROP TRIGGER IF EXISTS friend_delete_pendingFriend;
CREATE TRIGGER friend_delete_pendingFriend
    AFTER INSERT
    ON friend
    FOR EACH ROW
    EXECUTE PROCEDURE pendingFriend_check();

CREATE OR REPLACE FUNCTION pendingFriend_check()
    RETURNS trigger AS
$$
BEGIN
    IF EXISTS (SELECT FROM pendingFriend p 
                WHERE new.userID1=p.fromID
                    AND new.userID2=p.toID) THEN

        DELETE FROM pendingFriend p
            WHERE new.userID1=p.toID
            AND new.userID2=p.fromID;

    ELSE IF EXISTS (SELECT FROM pendingFriend p 
                    WHERE new.userID1=p.fromID
                    AND new.userID2=p.toID) THEN

        DELETE FROM pendingFriend p
            WHERE new.userID1=p.fromID
            AND new.userID2=p.toID;
    end if;
    return new;
END
$$ LANGUAGE 'plpgsql';

-- Make sure they aren't already friends. If they are, don't insert.
DROP TRIGGER IF EXISTS friend_already_friends;
CREATE TRIGGER friend_already_friends
    BEFORE INSERT
    ON friend
    FOR EACH ROW
    EXECUTE PROCEDURE friend_check();

CREATE OR REPLACE FUNCTION friend_check()
    RETURNS trigger AS
$$
BEGIN
    IF EXISTS (SELECT FROM friend f 
                WHERE new.userID1=f.userID2
                    AND new.userID2=f.userID1) THEN
        return null;
    ELSE 
        return new;
    END IF;
END
$$ LANGUAGE 'plpgsql';

-- Check that the newly pending friends aren't already friends
DROP TRIGGER IF EXISTS pendingFriend_already_friends;
CREATE TRIGGER pendingFriend_already_friends
    BEFORE INSERT
    ON pendingFriend
    FOR EACH ROW
    EXECUTE PROCEDURE friend_already_check();

CREATE OR REPLACE FUNCTION friend_already_check()
    RETURNS trigger AS
$$
BEGIN
    IF EXISTS (SELECT FROM friend f 
                WHERE f.userID1=new.fromID
                    AND f.userID2=new.toID
                     OR f.userID1=new.toID
                    AND f.userID2=new.fromID) THEN
        return null;
    ELSE 
        return new;
    end if;
END
$$ LANGUAGE 'plpgsql';


-- If a pendingFriend request comes in from the toID of another pendingFriend, 
-- make them friends
DROP TRIGGER IF EXISTS pendingFriend_make_friends;
CREATE TRIGGER pendingFriend_make_friends
    BEFORE INSERT
    ON pendingFriend
    FOR EACH ROW
    EXECUTE PROCEDURE makeFriend();

CREATE OR REPLACE FUNCTION makeFriend()
    RETURNS TRIGGER AS
$$
BEGIN
    IF EXISTS(
        SELECT FROM pendingFriend pf
        WHERE new.fromID = pf.toID
          AND new.toID = pf.fromID
    ) THEN
        INSERT INTO friend("userID1", "userID2", "JDate", "message")
            VALUES(new.fromID, new.toID, CURRENT_DATE, new.message);
        DELETE FROM pendingFriend p
            WHERE pf.fromID=p.toID
            AND pf.toID=p.fromID;
        return null;
    ELSE
        return new;
    END IF;
END
$$ LANGUAGE 'plpgsql';

-- When a message is made to a user, add it to the messageRecipient table
DROP TRIGGER IF EXISTS message_addRecipient_User;
CREATE TRIGGER message_addRecipient_User
    AFTER INSERT
    ON "message"
    WHERE toGroupID IS NULL
    FOR EACH ROW
    EXECUTE PROCEDURE addRecipient();

CREATE OR REPLACE FUNCTION addRecipient()
    RETURNS TRIGGER AS
$$
    INSERT INTO messageRecipient("msgID", "userID")
        VALUES(new.msgID, new.toUserID);
$$ LANGUAGE 'plpgsql';

-- When a pendingGroupMember is added, make sure they aren't already 
-- in the group
DROP TRIGGER IF EXISTS pendingGroup_already_member;
CREATE TRIGGER pendingGroup_already_member
    BEFORE INSERT
    on pendingGroupMember
    FOR EACH ROW
    EXECUTE PROCEDURE checkGroupMembers();

CREATE OR REPLACE FUNCTION addRecipient()
    RETURNS TRIGGER AS
$$
    IF EXISTS (
        SELECT FROM groupMember gm
        WHERE new.gID=gm.gID
          AND new.userID=gm.userID
    ) THEN
        return null;
    ELSE
        return new;
    END IF;
$$ LANGUAGE 'plpgsql';

-- When a groupMember is added, delete the pendingGroupMember if they
-- were pending
DROP TRIGGER IF EXISTS groupMember_delete_pending;
CREATE TRIGGER groupMember_delete_pending
    AFTER INSERT
    ON groupMember
    FOR EACH ROW
    EXECUTE PROCEDURE pendingGroupMember_check();

CREATE OR REPLACE FUNCTION pendingGroupMember_check()
    RETURNS trigger AS
$$
BEGIN
    IF EXISTS (SELECT FROM pendingGroupMember pgm 
                WHERE new.gID=pgm.gID
                AND new.userID=pgm.userID) THEN
        DELETE FROM pendingGroupMember pgm
            WHERE new.gID=pgm.gID
              AND new.userID=pgm.userID;
    end if;
    return new;
END
$$ LANGUAGE 'plpgsql';