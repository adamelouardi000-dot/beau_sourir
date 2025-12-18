package ma.dentalTech.service.modules.cabinet.api;

import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.users.Staff;

import java.util.List;
import java.util.Optional;

public interface ParametrageService {

    // --- Cabinet Setup ---
    CabinetMedical createCabinet(CabinetMedical cabinet);
    void updateCabinetInfo(CabinetMedical cabinet);
    Optional<CabinetMedical> getCabinetById(Long id);
    void deleteCabinet(Long id);

    // --- Staff Management ---
    void addStaffToCabinet(Long cabinetId, Long staffId);
    void removeStaffFromCabinet(Long cabinetId, Long staffId);
    List<Staff> getCabinetStaff(Long cabinetId);
}