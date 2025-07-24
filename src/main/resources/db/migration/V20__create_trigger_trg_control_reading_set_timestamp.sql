CREATE TRIGGER trg_control_reading_set_timestamp
    BEFORE INSERT OR UPDATE ON sch_monitoring.control_reading
    FOR EACH ROW
EXECUTE FUNCTION set_timestamp();
