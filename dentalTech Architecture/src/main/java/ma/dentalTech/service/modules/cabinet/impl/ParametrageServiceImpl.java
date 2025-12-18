package ma.dentalTech.service.modules.cabinet.impl;

import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.users.Staff;
import ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository;
import ma.dentalTech.service.modules.cabinet.api.ParametrageService;

import java.util.List;
import java.util.Optional;

public class ParametrageServiceImpl implements ParametrageService {

    private final CabinetMedicalRepository cabinetRepository;

    // Constructor Injection
    public ParametrageServiceImpl(CabinetMedicalRepository cabinetRepository) {
        this.cabinetRepository = cabinetRepository;
    }

    @Override
    public CabinetMedical createCabinet(CabinetMedical cabinet) {
        if (cabinet == null) throw new IllegalArgumentException("Cabinet cannot be null");
        // Validation: Check if email is already used
        if (cabinet.getEmail() != null && cabinetRepository.findByEmail(cabinet.getEmail()).isPresent()) {
            throw new RuntimeException("Cabinet with this email already exists.");
        }
        cabinetRepository.create(cabinet);
        return cabinet;
    }

    @Override
    public void updateCabinetInfo(CabinetMedical cabinet) {
        cabinetRepository.update(cabinet);
    }

    @Override
    public Optional<CabinetMedical> getCabinetById(Long id) {
        return Optional.ofNullable(cabinetRepository.findById(id));
    }

    @Override
    public void deleteCabinet(Long id) {
        cabinetRepository.deleteById(id);
    }

    @Override
    public void addStaffToCabinet(Long cabinetId, Long staffId) {
        cabinetRepository.addStaffToCabinet(cabinetId, staffId);
    }

    @Override
    public void removeStaffFromCabinet(Long cabinetId, Long staffId) {
        cabinetRepository.removeStaffFromCabinet(cabinetId, staffId);
    }

    @Override
    public List<Staff> getCabinetStaff(Long cabinetId) {
        return cabinetRepository.getStaffOfCabinet(cabinetId);
    }
}