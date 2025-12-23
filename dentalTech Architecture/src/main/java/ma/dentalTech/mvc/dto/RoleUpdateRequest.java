package ma.dentalTech.mvc.dto;

import ma.dentalTech.entities.enums.RoleType;

import java.util.List;

public record RoleUpdateRequest(
        Long id,
        String libelle,
        RoleType type,
        List<String> privileges
) {}
