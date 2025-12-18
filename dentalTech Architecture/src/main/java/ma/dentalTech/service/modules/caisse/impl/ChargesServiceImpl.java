package ma.dentalTech.service.modules.caisse.impl;

import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.repository.modules.cabinet.api.ChargesRepository;
import ma.dentalTech.service.modules.caisse.api.ChargesService;

import java.util.List;

public class ChargesServiceImpl implements ChargesService {

    private final ChargesRepository chargesRepository;

    public ChargesServiceImpl(ChargesRepository chargesRepository) {
        this.chargesRepository = chargesRepository;
    }

    @Override
    public void addDepense(Charges charge) {
        chargesRepository.create(charge);
    }

    @Override
    public List<Charges> getDepensesByCabinet(Long cabinetId) {
        return chargesRepository.findByCabinet(cabinetId);
    }

    @Override
    public double getTotalDepenses(Long cabinetId) {
        return getDepensesByCabinet(cabinetId).stream()
                .mapToDouble(Charges::getMontant)
                .sum();
    }
}