package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.entities.cabinet.Charges;

import java.util.List;

public interface ChargesService {
    void addDepense(Charges charge);
    List<Charges> getDepensesByCabinet(Long cabinetId);
    double getTotalDepenses(Long cabinetId);
}