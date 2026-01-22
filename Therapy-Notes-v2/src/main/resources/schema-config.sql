-- Enable Foreign Key support for the session
PRAGMA foreign_keys = ON;

-----------------------------------------------------------
-- 1. LOOKUP TABLES
-----------------------------------------------------------
CREATE TABLE IF NOT EXISTS assessment_option_types (
    "type_key" TEXT PRIMARY KEY,
    "display_name" TEXT NOT NULL,
    "display_order" INTEGER,
    "insert_date" DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "assessment_options" (
  "id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "type" TEXT NOT NULL,
  "name" TEXT NOT NULL,
  "description" TEXT DEFAULT NULL,
  "inactive" INTEGER DEFAULT 0,
  "insert_date" DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY ("type") REFERENCES "assessment_option_types" ("type_key")
);

-----------------------------------------------------------
-- 2. MAIN ENTITIES
-----------------------------------------------------------

CREATE TABLE IF NOT EXISTS "clients" (
  "client_id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "client_code" TEXT UNIQUE NOT NULL,
  "first_name" TEXT DEFAULT NULL,
  "last_name" TEXT DEFAULT NULL,
  "email1" TEXT DEFAULT NULL,
  "email2" TEXT DEFAULT NULL,
  "email3" TEXT DEFAULT NULL,
  "phone1" TEXT DEFAULT NULL,
  "phone2" TEXT DEFAULT NULL,
  "phone3" TEXT DEFAULT NULL,
  "date_of_birth" DATETIME DEFAULT NULL,
  "insert_date" DATETIME DEFAULT CURRENT_TIMESTAMP,
  "update_date" DATETIME DEFAULT CURRENT_TIMESTAMP,
  "inactive" INTEGER DEFAULT 0
);

-- Initialize clients to start at ID 1000, only initialize client sequence if it does not exist already
INSERT INTO sqlite_sequence (name, seq)
SELECT 'clients', 999
WHERE NOT EXISTS (
    SELECT 1 FROM sqlite_sequence WHERE name = 'clients'
);

CREATE TABLE IF NOT EXISTS "notes" (
  "note_id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "client_id" INTEGER NOT NULL,
  "appt_date_time" DATETIME DEFAULT NULL,
  "virtual_appt" INTEGER DEFAULT 0, -- SQLite uses 0/1 for boolean/bit values
  "appt_note" TEXT DEFAULT NULL,
  "diagnosis" TEXT DEFAULT NULL,
  "session_number" INTEGER DEFAULT NULL,
  "session_length" TEXT DEFAULT NULL,
  "narrative" TEXT DEFAULT NULL,
  "appearance" INTEGER DEFAULT NULL,
  "appearance_comment" TEXT DEFAULT NULL,
  "speech" INTEGER DEFAULT NULL,
  "speech_comment" TEXT DEFAULT NULL,
  "affect" INTEGER DEFAULT NULL,
  "affect_comment" TEXT DEFAULT NULL,
  "eye_contact" INTEGER DEFAULT NULL,
  "eye_contact_comment" TEXT DEFAULT NULL,
  "next_appt" INTEGER DEFAULT NULL,
  "next_appt_comment" TEXT DEFAULT NULL,
  "referral_comment" TEXT DEFAULT NULL,
  "collateral_contact_comment" TEXT DEFAULT NULL,
  "certified" DATETIME DEFAULT NULL,
  "insert_date" DATETIME DEFAULT CURRENT_TIMESTAMP,
  "update_date" DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY ("client_id") REFERENCES "clients" ("client_id"),
  FOREIGN KEY ("appearance") REFERENCES "assessment_options" ("id"),
  FOREIGN KEY ("speech") REFERENCES "assessment_options" ("id"),
  FOREIGN KEY ("affect") REFERENCES "assessment_options" ("id"),
  FOREIGN KEY ("eye_contact") REFERENCES "assessment_options" ("id"),
  FOREIGN KEY ("next_appt") REFERENCES "assessment_options" ("id")
);

-----------------------------------------------------------
-- 3. JUNCTION & RELATED TABLES
-----------------------------------------------------------

CREATE TABLE IF NOT EXISTS "contacts" (
  "contact_id" INTEGER PRIMARY KEY AUTOINCREMENT,
  "linked_client" INTEGER DEFAULT NULL,
  "first_name" TEXT DEFAULT NULL,
  "last_name" TEXT DEFAULT NULL,
  "email1" TEXT DEFAULT NULL,
  "email2" TEXT DEFAULT NULL,
  "email3" TEXT DEFAULT NULL,
  "phone1" TEXT DEFAULT NULL,
  "phone2" TEXT DEFAULT NULL,
  "phone3" TEXT DEFAULT NULL,
  "emergency_contact" INTEGER DEFAULT 0, -- BIT(1) replacement
  "insert_date" DATETIME DEFAULT CURRENT_TIMESTAMP,
  "update_date" DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY ("linked_client") REFERENCES "clients" ("client_id") 
    ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS "referrals" (
  "note_id" INTEGER NOT NULL,
  "referral_id" INTEGER NOT NULL,
  "insert_date" DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY ("note_id", "referral_id"),
  FOREIGN KEY ("note_id") REFERENCES "notes" ("note_id") 
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY ("referral_id") REFERENCES "assessment_options" ("id") 
    ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS "symptoms" (
  "note_id" INTEGER NOT NULL,
  "symptom_id" INTEGER NOT NULL,
  "insert_date" DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY ("note_id", "symptom_id"),
  FOREIGN KEY ("note_id") REFERENCES "notes" ("note_id") 
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY ("symptom_id") REFERENCES "assessment_options" ("id") 
    ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS "collateral_contacts" (
  "note_id" INTEGER NOT NULL,
  "collateral_contact_type_id" INTEGER NOT NULL,
  "insert_date" DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY ("note_id", "collateral_contact_type_id"),
  FOREIGN KEY ("note_id") REFERENCES "notes" ("note_id") 
    ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY ("collateral_contact_type_id") REFERENCES "assessment_options" ("id") 
    ON DELETE RESTRICT ON UPDATE CASCADE
);

-----------------------------------------------------------
-- 4. INDICES (for performance)
-----------------------------------------------------------

CREATE INDEX IF NOT EXISTS "assessment_options_type_idx" ON "assessment_options" ("type_key");
CREATE INDEX IF NOT EXISTS "client_code_idx" ON "clients" ("client_code", "first_name", "last_name", "client_id", "inactive");
CREATE INDEX IF NOT EXISTS "appt_search_idx" ON "notes" ("appt_date_time", "virtual_appt");
CREATE INDEX IF NOT EXISTS "appearance_fk_idx" ON "notes" ("appearance");
CREATE INDEX IF NOT EXISTS "speech_fk_idx" ON "notes" ("speech");
CREATE INDEX IF NOT EXISTS "affect_fk_idx" ON "notes" ("affect");
CREATE INDEX IF NOT EXISTS "eye_contact_fk_idx" ON "notes" ("eye_contact");
CREATE INDEX IF NOT EXISTS "next_appt_fk_idx" ON "notes" ("next_appt");
CREATE INDEX IF NOT EXISTS "symptom_type_idx" ON "symptoms" ("symptom_id");
CREATE INDEX IF NOT EXISTS "collateral_contact_fk_idx" ON "collateral_contacts" ("collateral_contact_type_id");
CREATE INDEX IF NOT EXISTS "referral_type_fk_idx" ON "referrals" ("referral_id");
CREATE INDEX IF NOT EXISTS "contact_client_fk_idx" ON "contacts" ("linked_client");

-----------------------------------------------------------
-- 5. MAINTENANCE
-----------------------------------------------------------

CREATE TABLE IF NOT EXISTS "user_preferences" (
  "preference_key" TEXT PRIMARY KEY,
  "preference_value" TEXT,
  "preference_type" TEXT,  -- 'BOOLEAN', 'INTEGER', 'STRING'
  "display_name" TEXT,
  "description" TEXT,
  "default_value" TEXT,
  "category" TEXT,
  "update_date" DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS "idx_preferences_category" ON "user_preferences"("category");