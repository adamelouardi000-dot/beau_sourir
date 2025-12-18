-- ==========================================================
--  MODULE DOSSIER MEDICAL :
--   DossiersMedicaux / Consultations / Actes / Ordonnances /
--   Medicaments / Prescriptions / Certificats / Interventions
-- ==========================================================


-- ==========================================================
-- Table DossiersMedicaux
-- Lié à Patient (module patient) et Medecin (module users)
-- ==========================================================
CREATE TABLE IF NOT EXISTS DossiersMedicaux (
                                                id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                                patient_id BIGINT NOT NULL,
                                                medecin_id BIGINT NULL,

                                                dateOuverture DATE NOT NULL DEFAULT (CURRENT_DATE),
    noteGenerale TEXT NULL,

    -- Champs BaseEntity
    dateCreation             DATE        NOT NULL DEFAULT (CURRENT_DATE),
    dateDerniereModification DATETIME    DEFAULT CURRENT_TIMESTAMP,
    creePar                  VARCHAR(80),
    modifiePar               VARCHAR(80),

    CONSTRAINT fk_dm_patient
    FOREIGN KEY (patient_id) REFERENCES Patients(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

    -- Medecin = table Medecins(id) (module users)
    CONSTRAINT fk_dm_medecin
    FOREIGN KEY (medecin_id) REFERENCES Medecins(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE,

    KEY idx_dm_patient (patient_id),
    KEY idx_dm_medecin (medecin_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- ==========================================================
-- Table Consultations
-- Une consultation appartient à un DossierMedical
-- RDV optionnel (agenda) : on met rdv_id en nullable
-- ==========================================================
CREATE TABLE IF NOT EXISTS Consultations (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                             dossier_medical_id BIGINT NOT NULL,
                                             rdv_id BIGINT NULL,

                                             dateConsultation DATETIME NOT NULL,
                                             motif VARCHAR(255),
    diagnostic TEXT,
    noteMedecin TEXT,

    -- Champs BaseEntity
    dateCreation             DATE        NOT NULL DEFAULT (CURRENT_DATE),
    dateDerniereModification DATETIME    DEFAULT CURRENT_TIMESTAMP,
    creePar                  VARCHAR(80),
    modifiePar               VARCHAR(80),

    CONSTRAINT fk_cons_dm
    FOREIGN KEY (dossier_medical_id) REFERENCES DossiersMedicaux(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

    -- RDV : si ta table RDV existe déjà (module agenda)
    CONSTRAINT fk_cons_rdv
    FOREIGN KEY (rdv_id) REFERENCES RDV(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE,

    KEY idx_cons_dm_date (dossier_medical_id, dateConsultation),
    KEY idx_cons_rdv (rdv_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- ==========================================================
-- Table Actes
-- Un acte appartient à une consultation
-- (si vous avez déjà un module "actes", renomme cette table)
-- ==========================================================
CREATE TABLE IF NOT EXISTS ActesMedical (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                            consultation_id BIGINT NOT NULL,

                                            libelle VARCHAR(150) NOT NULL,
    prix DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    description TEXT NULL,

    dateActe DATETIME NOT NULL,

    -- Champs BaseEntity
    dateCreation             DATE        NOT NULL DEFAULT (CURRENT_DATE),
    dateDerniereModification DATETIME    DEFAULT CURRENT_TIMESTAMP,
    creePar                  VARCHAR(80),
    modifiePar               VARCHAR(80),

    CONSTRAINT fk_acte_cons
    FOREIGN KEY (consultation_id) REFERENCES Consultations(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

    KEY idx_acte_cons (consultation_id),
    KEY idx_acte_date (dateActe)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- ==========================================================
-- Table Ordonnances
-- Une ordonnance appartient à une consultation
-- ==========================================================
CREATE TABLE IF NOT EXISTS Ordonnances (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                           consultation_id BIGINT NOT NULL,

                                           dateOrdonnance DATETIME NOT NULL,
                                           remarque TEXT NULL,

    -- Champs BaseEntity
                                           dateCreation             DATE        NOT NULL DEFAULT (CURRENT_DATE),
    dateDerniereModification DATETIME    DEFAULT CURRENT_TIMESTAMP,
    creePar                  VARCHAR(80),
    modifiePar               VARCHAR(80),

    CONSTRAINT fk_ord_cons
    FOREIGN KEY (consultation_id) REFERENCES Consultations(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

    KEY idx_ord_cons (consultation_id),
    KEY idx_ord_date (dateOrdonnance)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- ==========================================================
-- Table Medicaments (référentiel)
-- ==========================================================
CREATE TABLE IF NOT EXISTS Medicaments (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                           nom VARCHAR(150) NOT NULL,
    forme VARCHAR(80) NULL,
    dosage VARCHAR(80) NULL,

    -- Champs BaseEntity
    dateCreation             DATE        NOT NULL DEFAULT (CURRENT_DATE),
    dateDerniereModification DATETIME    DEFAULT CURRENT_TIMESTAMP,
    creePar                  VARCHAR(80),
    modifiePar               VARCHAR(80),

    UNIQUE KEY uk_medic_nom_dosage (nom, dosage),
    KEY idx_medic_nom (nom)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- ==========================================================
-- Table Prescriptions
-- Association Ordonnance -> Medicament
-- ==========================================================
CREATE TABLE IF NOT EXISTS Prescriptions (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                             ordonnance_id BIGINT NOT NULL,
                                             medicament_id BIGINT NOT NULL,

                                             posologie VARCHAR(255) NOT NULL,
    duree VARCHAR(80) NULL,
    quantite INT NULL,

    -- Champs BaseEntity
    dateCreation             DATE        NOT NULL DEFAULT (CURRENT_DATE),
    dateDerniereModification DATETIME    DEFAULT CURRENT_TIMESTAMP,
    creePar                  VARCHAR(80),
    modifiePar               VARCHAR(80),

    CONSTRAINT fk_presc_ord
    FOREIGN KEY (ordonnance_id) REFERENCES Ordonnances(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

    CONSTRAINT fk_presc_medic
    FOREIGN KEY (medicament_id) REFERENCES Medicaments(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,

    KEY idx_presc_ord (ordonnance_id),
    KEY idx_presc_medic (medicament_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- ==========================================================
-- Table Certificats
-- Un certificat appartient à une consultation
-- ==========================================================
CREATE TABLE IF NOT EXISTS Certificats (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                           consultation_id BIGINT NOT NULL,

                                           type VARCHAR(80) NOT NULL,      -- (si vous avez un enum, on pourra le remplacer)
    contenu TEXT NOT NULL,
    dateCertificat DATE NOT NULL,

    -- Champs BaseEntity
    dateCreation             DATE        NOT NULL DEFAULT (CURRENT_DATE),
    dateDerniereModification DATETIME    DEFAULT CURRENT_TIMESTAMP,
    creePar                  VARCHAR(80),
    modifiePar               VARCHAR(80),

    CONSTRAINT fk_cert_cons
    FOREIGN KEY (consultation_id) REFERENCES Consultations(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

    KEY idx_cert_cons (consultation_id),
    KEY idx_cert_date (dateCertificat)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- ==========================================================
-- Table InterventionsMedecins
-- Pour tracer des interventions/actes faits par un médecin sur une consultation
-- ==========================================================
CREATE TABLE IF NOT EXISTS InterventionsMedecins (
                                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                                     consultation_id BIGINT NOT NULL,
                                                     medecin_id BIGINT NOT NULL,

                                                     roleIntervention VARCHAR(120) NULL,
    commentaire TEXT NULL,

    -- Champs BaseEntity
    dateCreation             DATE        NOT NULL DEFAULT (CURRENT_DATE),
    dateDerniereModification DATETIME    DEFAULT CURRENT_TIMESTAMP,
    creePar                  VARCHAR(80),
    modifiePar               VARCHAR(80),

    CONSTRAINT fk_interv_cons
    FOREIGN KEY (consultation_id) REFERENCES Consultations(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

    CONSTRAINT fk_interv_med
    FOREIGN KEY (medecin_id) REFERENCES Medecins(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

    KEY idx_interv_cons (consultation_id),
    KEY idx_interv_med (medecin_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
