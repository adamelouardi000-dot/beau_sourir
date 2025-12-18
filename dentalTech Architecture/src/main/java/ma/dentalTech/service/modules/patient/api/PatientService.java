package ma.dentalTech.service.modules.patient.api;

import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;

import java.util.List;

public interface PatientService {

    // CRUD
    List<Patient> findAll();
    Patient findById(Long id);
    void create(Patient patient);
    void update(Patient patient);
    void deleteById(Long id);

    // extras (liens N-N avec Antecedent)
    void addAntecedentToPatient(Long patientId, Long antecedentId);
    void removeAntecedentFromPatient(Long patientId, Long antecedentId);
    List<Antecedent> getAntecedentsOfPatient(Long patientId);

    // pagination / comptage si tu l'as dans repo
    long count();
    List<Patient> findPage(int limit, int offset);
}
