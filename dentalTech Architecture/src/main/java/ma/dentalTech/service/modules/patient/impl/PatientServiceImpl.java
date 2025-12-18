package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.patient.api.PatientService;

import java.util.List;

public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepo;
    private final AntecedentRepository antecedentRepo;

    public PatientServiceImpl(PatientRepository patientRepo, AntecedentRepository antecedentRepo) {
        this.patientRepo = patientRepo;
        this.antecedentRepo = antecedentRepo;
    }

    @Override
    public List<Patient> findAll() {
        return patientRepo.findAll();
    }

    @Override
    public Patient findById(Long id) {
        if (id == null) throw new IllegalArgumentException("id ne doit pas être null");
        Patient p = patientRepo.findById(id);
        if (p == null) throw new IllegalArgumentException("Patient introuvable (id=" + id + ")");
        return p;
    }

    @Override
    public void create(Patient patient) {
        if (patient == null) throw new IllegalArgumentException("patient ne doit pas être null");
        if (isBlank(patient.getNom())) throw new IllegalArgumentException("nom obligatoire");
        if (isBlank(patient.getPrenom())) throw new IllegalArgumentException("prenom obligatoire");
        if (patient.getSexe() == null) throw new IllegalArgumentException("sexe obligatoire");
        if (patient.getAssurance() == null) throw new IllegalArgumentException("assurance obligatoire");

        // email souvent unique en DB: si le repo a findByEmail, tu peux le vérifier ici.
        patientRepo.create(patient);
    }

    @Override
    public void update(Patient patient) {
        if (patient == null) throw new IllegalArgumentException("patient ne doit pas être null");
        if (patient.getId() == null) throw new IllegalArgumentException("id obligatoire pour update");

        Patient old = patientRepo.findById(patient.getId());
        if (old == null) throw new IllegalArgumentException("Patient introuvable (id=" + patient.getId() + ")");

        patientRepo.update(patient);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("id ne doit pas être null");
        patientRepo.deleteById(id);
    }

    @Override
    public void addAntecedentToPatient(Long patientId, Long antecedentId) {
        if (patientId == null || antecedentId == null)
            throw new IllegalArgumentException("patientId/antecedentId ne doivent pas être null");

        // validations via repos
        if (patientRepo.findById(patientId) == null)
            throw new IllegalArgumentException("Patient introuvable (id=" + patientId + ")");
        if (!antecedentRepo.existsById(antecedentId))
            throw new IllegalArgumentException("Antecedent introuvable (id=" + antecedentId + ")");

        patientRepo.addAntecedentToPatient(patientId, antecedentId);
    }

    @Override
    public void removeAntecedentFromPatient(Long patientId, Long antecedentId) {
        if (patientId == null || antecedentId == null)
            throw new IllegalArgumentException("patientId/antecedentId ne doivent pas être null");
        patientRepo.removeAntecedentFromPatient(patientId, antecedentId);
    }

    @Override
    public List<Antecedent> getAntecedentsOfPatient(Long patientId) {
        if (patientId == null) throw new IllegalArgumentException("patientId ne doit pas être null");
        return patientRepo.getAntecedentsOfPatient(patientId);
    }

    @Override
    public long count() {
        return patientRepo.count();
    }

    @Override
    public List<Patient> findPage(int limit, int offset) {
        if (limit <= 0) throw new IllegalArgumentException("limit doit être > 0");
        if (offset < 0) throw new IllegalArgumentException("offset doit être >= 0");
        return patientRepo.findPage(limit, offset);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
