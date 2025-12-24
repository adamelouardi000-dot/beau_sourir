USE beau_sourir;

INSERT INTO Patients
(nom, prenom, adresse, telephone, email, dateNaissance, sexe, assurance, creePar)
VALUES
    ('EL AMRANI', 'Youssef', 'Rabat', '0611111111', 'y.elamrani@dentaltech.ma', '1995-03-12', 'Homme', 'CNSS', 'seed'),
    ('BENALI', 'Sara', 'Casablanca', '0622222222', 's.benali@dentaltech.ma', '1998-07-22', 'Femme', 'CNOPS', 'seed'),
    ('AIT LAHCEN', 'Omar', 'Agadir', '0633333333', 'o.aitlahcen@dentaltech.ma', '1989-11-05', 'Homme', 'Aucune', 'seed'),
    ('EL FASSI', 'Imane', 'Fes', '0644444444', 'i.elfassi@dentaltech.ma', '2001-01-18', 'Femme', 'Autre', 'seed'),
    ('TEST', 'PATIENT', 'Rabat', '0600000000', 'test.patient@dentaltech.ma', '2000-01-01', 'Homme', 'Aucune', 'seed');
INSERT INTO Antecedents
(nom, categorie, niveauRisque, creePar)
VALUES
    ('Allergie à la pénicilline', 'ALLERGIE', 'ELEVE', 'seed'),
    ('Hypertension', 'MALADIE_CHRONIQUE', 'MODERE', 'seed'),
    ('Diabète type 2', 'MALADIE_CHRONIQUE', 'CRITIQUE', 'seed'),
    ('Tabagisme', 'HABITUDE_DE_VIE', 'MODERE', 'seed'),
    ('Extraction dentaire ancienne', 'ANTECEDENT_DENTAIRE', 'FAIBLE', 'seed');
-- Youssef EL AMRANI
INSERT INTO Patient_Antecedents (patient_id, antecedent_id)
VALUES (1, 1), (1, 2);

-- Sara BENALI
INSERT INTO Patient_Antecedents (patient_id, antecedent_id)
VALUES (2, 3);

-- Omar AIT LAHCEN
INSERT INTO Patient_Antecedents (patient_id, antecedent_id)
VALUES (3, 4);

-- TEST PATIENT
INSERT INTO Patient_Antecedents (patient_id, antecedent_id)
VALUES (5, 5);
