package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;
import ma.dentalTech.service.modules.patient.api.AntecedentService;

import java.util.List;
import java.util.Optional;

public class AntecedentServiceImpl implements AntecedentService {

    private final AntecedentRepository antecedentRepo;

    public AntecedentServiceImpl(AntecedentRepository antecedentRepo) {
        this.antecedentRepo = antecedentRepo;
    }

    @Override
    public List<Antecedent> findAll() {
        return antecedentRepo.findAll();
    }

    @Override
    public Antecedent findById(Long id) {
        if (id == null) throw new IllegalArgumentException("id ne doit pas être null");
        Antecedent a = antecedentRepo.findById(id);
        if (a == null) throw new IllegalArgumentException("Antecedent introuvable (id=" + id + ")");
        return a;
    }

    @Override
    public void create(Antecedent antecedent) {
        if (antecedent == null) throw new IllegalArgumentException("antecedent ne doit pas être null");
        if (isBlank(antecedent.getNom())) throw new IllegalArgumentException("nom obligatoire");
        if (antecedent.getCategorie() == null) throw new IllegalArgumentException("categorie obligatoire");
        if (antecedent.getNiveauRisque() == null) throw new IllegalArgumentException("niveauRisque obligatoire");
        antecedentRepo.create(antecedent);
    }

    @Override
    public void update(Antecedent antecedent) {
        if (antecedent == null) throw new IllegalArgumentException("antecedent ne doit pas être null");
        if (antecedent.getId() == null) throw new IllegalArgumentException("id obligatoire pour update");
        if (!antecedentRepo.existsById(antecedent.getId()))
            throw new IllegalArgumentException("Antecedent introuvable (id=" + antecedent.getId() + ")");
        antecedentRepo.update(antecedent);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("id ne doit pas être null");
        antecedentRepo.deleteById(id);
    }

    @Override
    public Optional<Antecedent> findByNom(String nom) {
        if (isBlank(nom)) throw new IllegalArgumentException("nom ne doit pas être vide");
        return antecedentRepo.findByNom(nom);
    }

    @Override
    public List<Antecedent> findByCategorie(CategorieAntecedent categorie) {
        if (categorie == null) throw new IllegalArgumentException("categorie ne doit pas être null");
        return antecedentRepo.findByCategorie(categorie);
    }

    @Override
    public List<Antecedent> findByNiveauRisque(NiveauRisque niveau) {
        if (niveau == null) throw new IllegalArgumentException("niveau ne doit pas être null");
        return antecedentRepo.findByNiveauRisque(niveau);
    }

    @Override
    public List<Patient> getPatientsHavingAntecedent(Long antecedentId) {
        if (antecedentId == null) throw new IllegalArgumentException("antecedentId ne doit pas être null");
        return antecedentRepo.getPatientsHavingAntecedent(antecedentId);
    }

    @Override
    public long count() {
        return antecedentRepo.count();
    }

    @Override
    public List<Antecedent> findPage(int limit, int offset) {
        if (limit <= 0) throw new IllegalArgumentException("limit doit être > 0");
        if (offset < 0) throw new IllegalArgumentException("offset doit être >= 0");
        return antecedentRepo.findPage(limit, offset);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
