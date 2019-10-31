-- PittSocial Triggers

-- Check that the new friends were previously pendingfriends
CREATE TRIGGER friends_from_pending_check
    BEFORE INSERT
    ON friend
    FOR EACH ROW
    EXECUTE PROCEDURE pendingFriend_check();

CREATE OR REPLACE FUNCTION pendingFriend_check()
    RETURNS trigger AS
$$
BEGIN
    IF EXISTS (SELECT FROM pendingFriend p 
                WHERE new.userID1=p.fromID
                    AND new.userID2=p.toID
                     OR new.userID1=p.toID
                    AND new.userID2=p.fromID) THEN
        return new;
    ELSE
        return null;
    end if;
END
$$ LANGUAGE 'plpgsql';

-- Check that the newly pending friends aren't already friends
CREATE TRIGGER already_friends
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

