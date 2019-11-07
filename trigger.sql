-- PittSocial Triggers

-- Delete pendingFriends on insert to friends if they were previously pendingFriends
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

    ELSIF EXISTS (SELECT FROM pendingFriend p
                    WHERE new.userID1=p.fromID
                    AND new.userID2=p.toID) THEN

        DELETE FROM pendingFriend p
            WHERE new.userID1=p.fromID
            AND new.userID2=p.toID;
    end if;
    return new;
END;
$$ LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS friend_delete_pendingFriend on friend;
CREATE TRIGGER friend_delete_pendingFriend
    AFTER INSERT
    ON friend
    FOR EACH ROW
    EXECUTE PROCEDURE pendingFriend_check();

-- WORKS --
-- Make sure they aren't already friends. If they are, don't insert.
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
END;
$$ LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS friend_already_friends on friend;
CREATE TRIGGER friend_already_friends
    BEFORE INSERT
    ON friend
    FOR EACH ROW
    EXECUTE PROCEDURE friend_check();

-- WORKS --
-- Check that the newly pending friends aren't already friends
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
END;
$$ LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS pendingFriend_already_friends on pendingFriend;
CREATE TRIGGER pendingFriend_already_friends
    BEFORE INSERT
    ON pendingFriend
    FOR EACH ROW
    EXECUTE PROCEDURE friend_already_check();

-- WORKS --
-- If a pendingFriend request comes in from the toID of another pendingFriend, 
-- make them friends
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
END;
$$ LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS pendingFriend_make_friends on pendingFriend;
CREATE TRIGGER pendingFriend_make_friends
    BEFORE INSERT
    ON pendingFriend
    FOR EACH ROW
    EXECUTE PROCEDURE makeFriend();

-- WORKING --
-- When a message is made to a user, add it to the messageRecipient table
CREATE OR REPLACE FUNCTION addRecipient()
    RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO messageRecipient
        VALUES(new.msgID, new.toUserID);
    RETURN new;
END;
$$ LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS message_addRecipient_User on "message";
CREATE TRIGGER message_addRecipient_User
    AFTER INSERT
    ON "message"
    FOR EACH ROW
    WHEN (NEW.toGroupID IS NULL)
    EXECUTE PROCEDURE addRecipient();

-- WORKS --
-- When a pendingGroupMember is added, make sure they aren't already 
-- in the group
CREATE OR REPLACE FUNCTION checkGroupMembers()
    RETURNS TRIGGER AS
$$
BEGIN
    IF EXISTS (
        SELECT FROM groupMember gm
        WHERE new.gID=gm.gID
          AND new.userID=gm.userID
    ) THEN
        return null;
    ELSE
        return new;
    END IF;
END;
$$ LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS pendingGroup_already_member on pendingGroupMember;
CREATE TRIGGER pendingGroup_already_member
    BEFORE INSERT
    on pendingGroupMember
    FOR EACH ROW
    EXECUTE PROCEDURE checkGroupMembers();

-- WORKS --
-- When a groupMember is added, delete the pendingGroupMember if they
-- were pending
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
END;
$$ LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS groupMember_delete_pending on groupMember;
CREATE TRIGGER groupMember_delete_pending
    AFTER INSERT
    ON groupMember
    FOR EACH ROW
    EXECUTE PROCEDURE pendingGroupMember_check();

-- On the creation of a message, make sure toUserID or toGroupID is null, but not both
CREATE OR REPLACE FUNCTION message_Null_ID_Check()
    RETURNS trigger AS
$$
BEGIN
    IF NEW.toUserID IS NULL AND NEW.toGroupID IS NOT NULL THEN
        return NEW;
    ELSIF NEW.toUserID IS NOT NULL AND NEW.toGroupID IS NULL THEN
        return NEW;
    ELSE
        RETURN NULL;
    END IF;
END;
$$ LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS message_check_NULL_ID on "message";
CREATE TRIGGER message_check_NULL_ID
    BEFORE INSERT
    ON "message"
    FOR EACH ROW
    EXECUTE PROCEDURE message_Null_ID_Check();

-- Don't permit an insert on groupMembers if it would violate the group limit
CREATE OR REPLACE FUNCTION groupMember_over_limit()
    RETURNS trigger AS
$$
DECLARE
    ilimit integer := (SELECT "limit"
                        FROM "group" g
                        WHERE NEW.gID=g.gID);
    imembers integer := (SELECT count(*)
                            FROM groupMember gm
                            WHERE NEW.gID=gm.gID);
BEGIN
    IF imembers < ilimit THEN
        return NEW;
    ELSE
        return NULL;
    END IF;
END;
$$ LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS groupMember_Limit_Check on groupMember;
CREATE TRIGGER groupMember_Limit_Check
    BEFORE INSERT
    ON groupMember
    FOR EACH ROW
    EXECUTE PROCEDURE groupMember_over_limit();