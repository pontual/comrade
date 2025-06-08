CREATE TRIGGER trg_person_set_timestamp
    BEFORE INSERT OR UPDATE ON sch_person.person
    FOR EACH ROW
EXECUTE FUNCTION set_timestamp();
