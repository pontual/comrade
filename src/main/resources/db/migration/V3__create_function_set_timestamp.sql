CREATE OR REPLACE FUNCTION set_timestamp()
    RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        NEW.created_at := NOW();
        NEW.updated_at := NOW();
    ELSIF TG_OP = 'UPDATE' THEN
        NEW.updated_at := NOW();
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
