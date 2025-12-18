package ma.dentalTech.service.modules.patient.api;

import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;

import java.util.List;
import java.util.Optional;

public interface AntecedentService {

    // CRUD
    List<Antecedent> findAll();
    Antecedent findById(Long id);
    void create(Antecedent antecedent);
    void update(Antecedent antecedent);
    void deleteById(Long id);

    // extras
    Optional<Antecedent> findByNom(String nom);
    List<Antecedent> findByCategorie(CategorieAntecedent categorie);
    List<Antecedent> findByNiveauRisque(NiveauRisque niveau);

    List<Patient> getPatientsHavingAntecedent(Long antecedentId);

    long count();
    List<Antecedent> findPage(int limit, int offset);
}
