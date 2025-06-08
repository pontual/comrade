CREATE TRIGGER trg_account_user_set_timestamp
    BEFORE INSERT OR UPDATE ON sch_user.account_user
    FOR EACH ROW
EXECUTE FUNCTION set_timestamp();