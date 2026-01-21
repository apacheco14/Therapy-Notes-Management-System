CREATE TRIGGER IF NOT EXISTS "trig_clients_update_date"
AFTER UPDATE ON "clients"
FOR EACH ROW
BEGIN
    UPDATE "clients" 
    SET "update_date" = CURRENT_TIMESTAMP 
    WHERE "client_id" = OLD."client_id";
END;
//

CREATE TRIGGER IF NOT EXISTS "trig_notes_update_date"
AFTER UPDATE ON "notes"
FOR EACH ROW
BEGIN
    UPDATE "notes" 
    SET "update_date" = CURRENT_TIMESTAMP 
    WHERE "note_id" = OLD."note_id";
END;
//

CREATE TRIGGER IF NOT EXISTS "trig_contacts_update_date"
AFTER UPDATE ON "contacts"
FOR EACH ROW
BEGIN
    UPDATE "contacts" 
    SET "update_date" = CURRENT_TIMESTAMP 
    WHERE "contact_id" = OLD."contact_id";
END;
//

CREATE TRIGGER IF NOT EXISTS "trig_preferences_update_date"
AFTER UPDATE ON "user_preferences"
FOR EACH ROW
BEGIN
    UPDATE "user_preferences" 
    SET "update_date" = CURRENT_TIMESTAMP 
    WHERE "preference_key" = OLD."preference_key";
END;
//
