package ma.dentalTech.service.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.mvc.dto.PrescriptionCreateRequest;
import ma.dentalTech.mvc.dto.PrescriptionDto;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo;
import ma.dentalTech.service.modules.dossierMedical.api.PrescriptionService;

import java.util.List;
import java.util.stream.Collectors;

public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepo prescriptionRepo;

    public PrescriptionServiceImpl(PrescriptionRepo prescriptionRepo) {
        this.prescriptionRepo = prescriptionRepo;
    }

    @Override
    public PrescriptionDto create(PrescriptionCreateRequest request) {
        if (request == null || request.ordonnanceId() == null)
            throw new IllegalArgumentException("ordonnanceId obligatoire");

        Prescription p = new Prescription();
        p.setQuantite(1); // défaut
        p.setFrequence(request.posologie());
        p.setDureeEnJours(request.duree());

        Ordonnance o = new Ordonnance();
        o.setId(request.ordonnanceId());
        p.setOrdonnance(o);

        Medicament m = new Medicament();
        m.setNom(request.medicament());
        p.setMedicament(m);

        prescriptionRepo.create(p);

        return toDto(p);
    }

    @Override
    public List<PrescriptionDto> getByOrdonnance(Long ordonnanceId) {
        return prescriptionRepo.findByOrdonnance(ordonnanceId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private PrescriptionDto toDto(Prescription p) {
        String medNom = (p.getMedicament() != null ? p.getMedicament().getNom() : null);
        return new PrescriptionDto(
                p.getId(),
                medNom,
                p.getFrequence(),
                p.getDureeEnJours() != null ? p.getDureeEnJours() : 0
        );
    }
}
