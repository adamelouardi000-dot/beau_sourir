-- ==========================================================
--  MODULE CAISSE : SituationFinanciere / Factures
--  Aligné avec enums:
--   StatutSituationFinanciere { EN_REGLE, EN_CREDIT, EN_PROMO }
--   StatutFacture { EN_ATTENTE, PAYEE, PARTIELLEMENT_PAYEE, ANNULEE, IMPAYEE }
-- ==========================================================

-- Table SituationFinancieres
CREATE TABLE IF NOT EXISTS SituationFinancieres (
                                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                                    totaleDesActes  DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    totalePaye      DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    credit          DECIMAL(12,2) NOT NULL DEFAULT 0.00,

    statut ENUM('EN_REGLE','EN_CREDIT','EN_PROMO') NOT NULL,
    enPromo BOOLEAN NOT NULL DEFAULT FALSE,

    -- Liaison vers dossierMedical (sans FK pour l’instant)
    dossier_medical_id BIGINT NULL,

    -- Champs BaseEntity
    dateCreation             DATE        NOT NULL DEFAULT (CURRENT_DATE),
    dateDerniereModification DATETIME    DEFAULT CURRENT_TIMESTAMP,
    creePar                  VARCHAR(80),
    modifiePar               VARCHAR(80),

    KEY idx_sf_statut (statut),
    KEY idx_sf_dossier (dossier_medical_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- Table Factures
CREATE TABLE IF NOT EXISTS Factures (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                        totaleFacture DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    totalePaye    DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    reste         DECIMAL(12,2) NOT NULL DEFAULT 0.00,

    statut ENUM('EN_ATTENTE','PAYEE','PARTIELLEMENT_PAYEE','ANNULEE','IMPAYEE') NOT NULL,
    dateFacture DATETIME NOT NULL,

    situation_financiere_id BIGINT NULL,

    -- Champs BaseEntity
    dateCreation             DATE        NOT NULL DEFAULT (CURRENT_DATE),
    dateDerniereModification DATETIME    DEFAULT CURRENT_TIMESTAMP,
    creePar                  VARCHAR(80),
    modifiePar               VARCHAR(80),

    CONSTRAINT fk_facture_sf
    FOREIGN KEY (situation_financiere_id) REFERENCES SituationFinancieres(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE,

    KEY idx_facture_date (dateFacture),
    KEY idx_facture_statut (statut),
    KEY idx_facture_sf (situation_financiere_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
