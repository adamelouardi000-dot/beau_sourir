package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.entities.cabinet.Revenues;

import java.util.List;

public interface RevenusService {
    void addRecette(Revenues revenue);
    List<Revenues> getRecettesByCabinet(Long cabinetId);
    double getTotalRecettes(Long cabinetId);
}