-----------------------------------------------------------
-- Assessment Option Types
-----------------------------------------------------------

INSERT INTO assessment_option_types (type_key, display_name, display_order) VALUES
('symptoms', 'Symptoms', 1),
('appearance', 'Appearance', 2),
('speech', 'Speech', 3),
('affect', 'Affect', 4),
('eyeContact', 'Eye Contact', 5),
('referrals', 'Referrals', 6),
('collateralContacts', 'Collateral Contacts', 7),
('nextAppt', 'Next Appointment', 8);

-----------------------------------------------------------
-- Symptom Options
-----------------------------------------------------------

INSERT INTO assessment_options (type, name) VALUES
("symptoms", "Depressed mood"),
("symptoms", "Decreased energy"),
("symptoms", "Hopelessness"),
("symptoms", "Worthlessness"),
("symptoms", "Grief"),
("symptoms", "Guilt"),
("symptoms", "Anxiety"),
("symptoms", "Panic attacks"),
("symptoms", "Obsessive thinking/behavior"),
("symptoms", "Relationship difficulty"),
("symptoms", "Irritability"),
("symptoms", "Impulsivity"),
("symptoms", "Attention deficit"),
("symptoms", "Hyperactivity"),
("symptoms", "Difficulty with sleep"),
("symptoms", "Delusions"),
("symptoms", "Hallucinations"),
("symptoms", "Paranoia"),
("symptoms", "Dissociative states"),
("symptoms", "Re-experiencing of trauma"),
("symptoms", "Oppositionalism"),
("symptoms", "Somatic complaints"),
("symptoms", "Concomitant medical condition"),
("symptoms", "Hospitalization"),
("symptoms", "Self injury"),
("symptoms", "Suicidal ideation"),
("symptoms", "Homicidal ideation"),
("symptoms", "Body image concerns"),
("symptoms", "Grandiosity"),
("symptoms", "Substance abuse (active)"),
("symptoms", "Substance abuse (early partial remission)"),
("symptoms", "Substance abuse (early full remission)");

-----------------------------------------------------------
-- Appearance Options
-----------------------------------------------------------

INSERT INTO assessment_options (type, name, description) VALUES
("appearance", "Neat", ""),
("appearance", "Casual", ""),
("appearance", "Disheveled", ""),
("appearance", "Other", "");

-----------------------------------------------------------
-- Speech Options
-----------------------------------------------------------

INSERT INTO assessment_options (type, name, description) VALUES
("speech", "Normal", ""),
("speech", "Rapid", ""),
("speech", "Slow", ""),
("speech", "Slurred", ""),
("speech", "Other", "");

-----------------------------------------------------------
-- Affect Options
-----------------------------------------------------------

INSERT INTO assessment_options (type, name, description) VALUES
("affect", "Normal", ""),
("affect", "Blunted/Flat", ""),
("affect", "Hyper/Manic", ""),
("affect", "Other", "");

-----------------------------------------------------------
-- Eye Contact Options
-----------------------------------------------------------

INSERT INTO assessment_options (type, name, description) VALUES
("eyeContact", "Normal", ""),
("eyeContact", "Poor", ""),
("eyeContact", "Intense", ""),
("eyeContact", "Other", "");

-----------------------------------------------------------
-- Referral Options
-----------------------------------------------------------

INSERT INTO assessment_options (type, name, description) VALUES
("referrals", "PCP", ""),
("referrals", "Psychiatrist", ""),
("referrals", "Other", "");

-----------------------------------------------------------
-- Collateral Contact Options
-----------------------------------------------------------

INSERT INTO assessment_options (type, name, description) VALUES
("collateralContacts", "PCP", ""),
("collateralContacts", "Parent", ""),
("collateralContacts", "School", ""),
("collateralContacts", "Other", "");

-----------------------------------------------------------
-- Next Appointment Options
-----------------------------------------------------------

INSERT INTO assessment_options (type, name, description) VALUES
("nextAppt", "1 Week", ""),
("nextAppt", "2 Weeks", ""),
("nextAppt", "Other", "");