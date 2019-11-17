-- PittSocial Triggers
--
-- TRIGGER 1:
-- Delete pendingFriends on insert to friends if they were previously pendingFriends
-- ASSUMPTION: once friends, you can no longer be pending friendship
CREATE OR REPLACE FUNCTION pendingFriend_check()
    RETURNS trigger AS
$$
BEGIN
    IF EXISTS (SELECT FROM pendingFriend p
                WHERE new.userID1=p.toID
                    AND new.userID2=p.fromID) THEN

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
--
-- TRIGGER 2
-- Make sure they aren't already friends. If they are, don't insert. 
-- ASSUMPTION: We do not want to keep track of duplicate (reversed) friendships
CREATE OR REPLACE FUNCTION friend_check()
    RETURNS trigger AS
$$
BEGIN
    IF EXISTS (SELECT FROM friend f
                WHERE new.userID1=f.userID2
                    AND new.userID2=f.userID1) THEN
        RAISE EXCEPTION 'users are already friends';
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
--
-- TRIGGER 3
-- Check that the newly pending friends aren't already friends
-- ASSUMPTION: you cannot attempt to friend someone twice
CREATE OR REPLACE FUNCTION friend_already_check()
    RETURNS trigger AS
$$
BEGIN
    IF EXISTS (SELECT FROM friend f
                WHERE f.userID1=new.fromID
                    AND f.userID2=new.toID
                     OR f.userID1=new.toID
                    AND f.userID2=new.fromID) THEN
        RAISE EXCEPTION 'users are already friends';
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
--
-- TRIGGER 4
-- If a pendingFriend request comes in from the toID of another pendingFriend, make them friends
-- ASSUMPTION: If both want to be friends, it makes them a friend
CREATE OR REPLACE FUNCTION makeFriend()
    RETURNS TRIGGER AS
$$
BEGIN
    IF EXISTS(
        SELECT FROM pendingFriend pf
        WHERE new.fromID = pf.toID
          AND new.toID = pf.fromID
    ) THEN
        INSERT INTO friend("userid1", "userid2", "jdate", "message")
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
--
-- TRIGGER 5:
-- When a message is made to a user, add it to the messageRecipient table
-- ASSUMPTION: we want to automatically keep track of all message recipients when a message is inserted
CREATE OR REPLACE FUNCTION addRecipient()
    RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO messageRecipient
        VALUES(new.msgID, new.toUserID);
    RETURN new;
END;
$$ LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS message_addRecipient_User on messageInfo;
CREATE TRIGGER message_addRecipient_User
    AFTER INSERT
    ON messageInfo
    FOR EACH ROW
    WHEN (NEW.toGroupID IS NULL)
    EXECUTE PROCEDURE addRecipient();
--
-- TRIGGER 6:
-- When a pendingGroupMember is added, make sure they aren't already in the group
-- ASSUMPTION: If already in a group, they cannot request to be in it again
CREATE OR REPLACE FUNCTION checkGroupMembers()
    RETURNS TRIGGER AS
$$
BEGIN
    IF EXISTS (
        SELECT FROM groupMember gm
        WHERE new.gID=gm.gID
          AND new.userID=gm.userID
    ) THEN
        RAISE EXCEPTION 'user already in group';
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
--
-- TRIGGER 7:
-- When a groupMember is added, delete the pendingGroupMember if they were pending
-- ASSUMPTION: We don't want to keep track of past pending members if they are already in a group
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
--
-- TRIGGER 8:
-- On the creation of a message, make sure toUserID or toGroupID is null, but not both
-- ASSUMPTION: A message can only be sent to either a user or group
CREATE OR REPLACE FUNCTION message_Null_ID_Check()
    RETURNS trigger AS
$$
BEGIN
    IF NEW.toUserID IS NULL AND NEW.toGroupID IS NOT NULL THEN
        return NEW;
    ELSIF NEW.toUserID IS NOT NULL AND NEW.toGroupID IS NULL THEN
        return NEW;
    ELSE
        RAISE EXCEPTION 'message must have a recipient';
        RETURN NULL;
    END IF;
END;
$$ LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS message_check_NULL_ID on messageInfo;
CREATE TRIGGER message_check_NULL_ID
    BEFORE INSERT
    ON messageInfo
    FOR EACH ROW
    EXECUTE PROCEDURE message_Null_ID_Check();
--
-- TRIGGER 9:
-- Don't permit an insert on groupMembers if it would violate the group limit
-- ASSUMPTION: We must enforce the group limits
CREATE OR REPLACE FUNCTION groupMember_over_limit()
    RETURNS trigger AS
$$
DECLARE
    ilimit integer := (SELECT size
                        FROM groupInfo g
                        WHERE NEW.gID=g.gID);
    imembers integer := (SELECT count(*)
                            FROM groupMember gm
                            WHERE NEW.gID=gm.gID);
BEGIN
    IF imembers < ilimit THEN
        return NEW;
    ELSE
        RAISE EXCEPTION 'group is already at limit';
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

-- TRIGGER 10:
-- When a message is sent to a group, add a messageRecipient for each groupMember
-- ASSUMPTION: Each group member should get a message sent to a group.
CREATE OR REPLACE FUNCTION addGroupRecipient()
    RETURNS TRIGGER AS
$$
DECLARE
    cur_groupMembers CURSOR(grID INTEGER) FOR SELECT userID
                                FROM groupMember gm
                                WHERE gm.gID=grID;
    rec_groupMember RECORD;
BEGIN
    OPEN cur_groupMembers(NEW.toGroupID);
    LOOP
        FETCH cur_groupMembers INTO rec_groupMember;
        EXIT WHEN NOT FOUND;

        INSERT INTO messageRecipient VALUES(NEW.msgID, rec_groupMember.userID);
    END LOOP;
    CLOSE cur_groupMembers;
    RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS message_addGroup_Recipient on messageInfo;
CREATE TRIGGER message_addGroup_Recipient
    AFTER INSERT
    ON messageInfo
    FOR EACH ROW
    WHEN (NEW.toUserID IS NULL)
    EXECUTE PROCEDURE addRecipient();

-- TRIGGER 11:
-- When a User is deleted, they should be removed from all of their groups.
-- ASSUMPTION: A User is no longer in a group if their account is deleted.
CREATE OR REPLACE FUNCTION removeUserFromGroups()
    RETURNS TRIGGER AS
$$
BEGIN
    DELETE FROM groupMember gm
    WHERE NEW.userID=gm.userID;
    RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS user_remove_from_groups on profile;
CREATE TRIGGER user_remove_from_groups
    BEFORE DELETE
    ON profile
    FOR EACH ROW
    EXECUTE PROCEDURE removeUserFromGroups();

-- TRIGGER 12: [In progress]
-- When a User is deleted, delete all messages where the from and to users are deleted.
-- ASSUMPTION: If neither user is on the system anymore, no one can view the messages, so they should be removed.
CREATE OR REPLACE FUNCTION removeUserFromMessages()
    RETURNS TRIGGER AS
$$
BEGIN
    DELETE FROM messageInfo mi
    WHERE NEW.userID=mi.fromID or NEW.userID=mi.toUserID;
    RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS user_remove_from_messages on profile;
CREATE TRIGGER user_remove_from_messages
    BEFORE DELETE
    ON profile
    FOR EACH ROW
    EXECUTE PROCEDURE removeUserFromMessages();


-- TRIGGER 13: [In progress ]
-- When a User is deleted, delete their entries in the messageRecipient table.
-- ASSUMPTION: If a user isn't on the system anymore, they can't view their messages
CREATE OR REPLACE FUNCTION removeUserFromMessageRecipient()
    RETURNS TRIGGER AS
$$
BEGIN
    DELETE FROM messagemessageRecipient mr
    WHERE NEW.userID=mr.userID;
    RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

DROP TRIGGER IF EXISTS user_remove_from_message_recipient on profile;
CREATE TRIGGER user_remove_from_message_recipient
    BEFORE DELETE
    ON profile
    FOR EACH ROW
    EXECUTE PROCEDURE removeUserFromMessageRecipient();

-- TRIGGER 14: [TODO]
-- When a User sends a friend request to themself, deny it.
-- ASSUMPTION: A user can't be friends with themselves.
