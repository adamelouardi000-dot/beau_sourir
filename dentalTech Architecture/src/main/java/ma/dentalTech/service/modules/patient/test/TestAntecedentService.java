package ma.dentalTech.service.modules.patient.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;

import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.patient.Antecedent;

import ma.dentalTech.service.modules.patient.api.AntecedentService;

public class TestAntecedentService {

    public static void main(String[] args) {
        AntecedentService service = ApplicationContext.getBean(AntecedentService.class);

        Long id = null;

        try {
            Antecedent a = Antecedent.builder()
                    .nom("Test antecedent")
                    .categorie(CategorieAntecedent.ALLERGIE)
                    .niveauRisque(NiveauRisque.FAIBLE)
                    .build();

            service.create(a);
            id = a.getId();
            System.out.println("✅ create antecedent id=" + id);

            a.setNiveauRisque(NiveauRisque.MODERE);
            service.update(a);
            System.out.println("✅ update antecedent");

            System.out.println("count=" + service.count());
            System.out.println("findById=" + service.findById(id));

            service.deleteById(id);
            System.out.println("✅ delete antecedent");
            id = null;

        } finally {
            if (id != null) {
                try { service.deleteById(id); } catch (Exception ignored) {}
            }
            SessionFactory.getInstance().closeConnection();
        }
    }
}
