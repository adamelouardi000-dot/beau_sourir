package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.mvc.dto.PrescriptionCreateRequest;
import ma.dentalTech.mvc.dto.PrescriptionDto;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo;
import ma.dentalTech.service.modules.dossierMedical.api.PrescriptionService;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepo prescriptionRepo;

    public PrescriptionServiceImpl(PrescriptionRepo prescriptionRepo) {
        this.prescriptionRepo = prescriptionRepo;
    }

    @Override
    public PrescriptionDto create(PrescriptionCreateRequest request) {
        if (request == null || request.ordonnanceId() == null) {
            throw new IllegalArgumentException("ordonnanceId obligatoire");
        }
        if (request.medicament() == null || request.medicament().isBlank()) {
            throw new IllegalArgumentException("medicament obligatoire");
        }

        Prescription p = new Prescription();

        // 1) Ordonnance (entity has Ordonnance ordonnance)
        Ordonnance o = new Ordonnance();
        setIdAny(o, request.ordonnanceId());
        p.setOrdonnance(o);

        // 2) Medicament (entity has Medicament medicament)
        Long medicamentId = getOrCreateMedicamentId(request.medicament().trim());
        Medicament m = new Medicament();
        setIdAny(m, medicamentId);
        p.setMedicament(m);

        // 3) Mapping DTO -> entity
        // DB expects "posologie" NOT NULL -> we store it in entity.frequence
        p.setFrequence(request.posologie());
        p.setDureeEnJours(request.duree());

        // (optionnel) quantite par défaut si tu veux
        if (p.getQuantite() == null) p.setQuantite(1);

        // BaseEntity
        p.setDateCreation(LocalDate.now());
        p.setDateDerniereModification(LocalDateTime.now());

        prescriptionRepo.create(p);

        return toDto(p, request.medicament().trim());
    }

    @Override
    public List<PrescriptionDto> getByOrdonnance(Long ordonnanceId) {
        return prescriptionRepo.findByOrdonnance(ordonnanceId).stream()
                .map(p -> toDto(p, null))
                .toList();
    }

    private PrescriptionDto toDto(Prescription p, String fallbackMedicamentName) {
        Long ordonnanceId = (p.getOrdonnance() != null) ? p.getOrdonnance().getId() : null;

        String medicamentName = fallbackMedicamentName;
        if (medicamentName == null && p.getMedicament() != null && p.getMedicament().getId() != null) {
            medicamentName = getMedicamentNameById(p.getMedicament().getId());
        }

        String posologie = p.getFrequence();                 // entity.frequence -> dto.posologie
        int duree = (p.getDureeEnJours() != null) ? p.getDureeEnJours() : 0;

        return new PrescriptionDto(
                p.getId(),
                ordonnanceId,
                medicamentName,
                posologie,
                duree
        );
    }

    // ---------------- JDBC Medicaments ----------------

    private Long getOrCreateMedicamentId(String nom) {
        String select = "SELECT id FROM Medicaments WHERE nom = ? LIMIT 1";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(select)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SELECT Medicaments: " + e.getMessage(), e);
        }

        String insert = """
            INSERT INTO Medicaments(nom, dateCreation, dateDerniereModification, creePar, modifiePar)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nom);
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, null);
            ps.setString(5, null);

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur INSERT Medicaments: " + e.getMessage(), e);
        }
    }

    private String getMedicamentNameById(Long id) {
        if (id == null) return null;
        String sql = "SELECT nom FROM Medicaments WHERE id=? LIMIT 1";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    // ----- helper setId (Long / long) -----
    private void setIdAny(Object obj, Long id) {
        try {
            obj.getClass().getMethod("setId", Long.class).invoke(obj, id);
            return;
        } catch (Exception ignored) {}
        try {
            obj.getClass().getMethod("setId", long.class).invoke(obj, id != null ? id : 0L);
        } catch (Exception ignored) {}
    }
}
