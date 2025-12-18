-- ==========================================================
--  SEED - Module Dossier Medical
-- ==========================================================

-- Dossier médical pour patient 1, médecin 2
INSERT INTO DossiersMedicaux
(id, patient_id, medecin_id, dateOuverture, noteGenerale, dateCreation, creePar, modifiePar)
VALUES
    (1, 1, 2, '2025-10-01', 'Dossier initial patient 1', '2025-10-01', 'SYSTEM', 'SYSTEM');


-- Consultation 1 (sans RDV)
INSERT INTO Consultations
(id, dossier_medical_id, rdv_id, dateConsultation, motif, diagnostic, noteMedecin, dateCreation, creePar, modifiePar)
VALUES
    (1, 1, NULL, '2025-10-02 10:30:00', 'Douleur dentaire', 'Caries suspectée', 'Prévoir radio', '2025-10-02', 'SYSTEM', 'SYSTEM');


-- Actes
INSERT INTO ActesMedical
(id, consultation_id, libelle, prix, description, dateActe, dateCreation, creePar, modifiePar)
VALUES
    (1, 1, 'Consultation', 200.00, 'Consultation standard', '2025-10-02 10:40:00', '2025-10-02', 'SYSTEM', 'SYSTEM'),
    (2, 1, 'Détartrage', 350.00, 'Détartrage complet', '2025-10-02 10:55:00', '2025-10-02', 'SYSTEM', 'SYSTEM');


-- Ordonnance
INSERT INTO Ordonnances
(id, consultation_id, dateOrdonnance, remarque, dateCreation, creePar, modifiePar)
VALUES
    (1, 1, '2025-10-02 11:05:00', 'Prendre après repas', '2025-10-02', 'SYSTEM', 'SYSTEM');


-- Médicaments
INSERT INTO Medicaments
(id, nom, forme, dosage, dateCreation, creePar, modifiePar)
VALUES
    (1, 'Paracétamol', 'Comprimé', '500mg', '2025-10-02', 'SYSTEM', 'SYSTEM'),
    (2, 'Amoxicilline', 'Gélule', '1g', '2025-10-02', 'SYSTEM', 'SYSTEM');


-- Prescriptions
INSERT INTO Prescriptions
(id, ordonnance_id, medicament_id, posologie, duree, quantite, dateCreation, creePar, modifiePar)
VALUES
    (1, 1, 1, '1 cp / 8h', '3 jours', 9, '2025-10-02', 'SYSTEM', 'SYSTEM'),
    (2, 1, 2, '1 gélule / 12h', '5 jours', 10, '2025-10-02', 'SYSTEM', 'SYSTEM');


-- Certificat
INSERT INTO Certificats
(id, consultation_id, type, contenu, dateCertificat, dateCreation, creePar, modifiePar)
VALUES
    (1, 1, 'ARRET_TRAVAIL', 'Arrêt de travail 2 jours', '2025-10-02', '2025-10-02', 'SYSTEM', 'SYSTEM');


-- Intervention médecin (traçabilité)
INSERT INTO InterventionsMedecins
(id, consultation_id, medecin_id, roleIntervention, commentaire, dateCreation, creePar, modifiePar)
VALUES
    (1, 1, 2, 'MEDECIN_TRAITANT', 'Consultation et plan de traitement', '2025-10-02', 'SYSTEM', 'SYSTEM');
