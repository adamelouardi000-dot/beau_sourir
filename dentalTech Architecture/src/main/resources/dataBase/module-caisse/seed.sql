-- ==========================================================
--  SEED - Module Caisse
-- ==========================================================

-- Situations financières
INSERT INTO SituationFinancieres
(id, totaleDesActes, totalePaye, credit, statut, enPromo, dossier_medical_id,
 dateCreation, dateDerniereModification, creePar, modifiePar)
VALUES
    (1, 1500.00, 1500.00, 0.00, 'EN_REGLE',  FALSE, NULL, '2025-10-01', '2025-10-01 10:00:00', 'SYSTEM', 'SYSTEM'),
    (2, 3000.00, 1000.00, 2000.00, 'EN_CREDIT', FALSE, NULL, '2025-10-05', '2025-10-05 11:00:00', 'SYSTEM', 'SYSTEM'),
    (3, 2000.00, 1200.00, 800.00,  'EN_PROMO',  TRUE,  NULL, '2025-10-10', '2025-10-10 12:00:00', 'SYSTEM', 'SYSTEM');


-- Factures (liées aux situations)
INSERT INTO Factures
(id, totaleFacture, totalePaye, reste, statut, dateFacture, situation_financiere_id,
 dateCreation, dateDerniereModification, creePar, modifiePar)
VALUES
    (1, 1500.00, 1500.00, 0.00,   'PAYEE',               '2025-10-01 10:10:00', 1, '2025-10-01', '2025-10-01 10:10:00', 'SYSTEM', 'SYSTEM'),
    (2, 2000.00,  500.00, 1500.00,'PARTIELLEMENT_PAYEE', '2025-10-06 09:30:00', 2, '2025-10-06', '2025-10-06 09:30:00', 'SYSTEM', 'SYSTEM'),
    (3, 1000.00,  500.00, 500.00, 'EN_ATTENTE',          '2025-10-07 15:00:00', 2, '2025-10-07', '2025-10-07 15:00:00', 'SYSTEM', 'SYSTEM'),
    (4, 2000.00, 1200.00, 800.00, 'IMPAYEE',             '2025-10-12 18:20:00', 3, '2025-10-12', '2025-10-12 18:20:00', 'SYSTEM', 'SYSTEM');
