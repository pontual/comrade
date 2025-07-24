CREATE TRIGGER trg_control_set_timestamp
    BEFORE INSERT OR UPDATE ON sch_monitoring.control
    FOR EACH ROW
EXECUTE FUNCTION set_timestamp();
