package ma.dentalTech.mvc.dto;

import ma.dentalTech.entities.enums.RoleType;

import java.util.List;

public record RoleCreateRequest(
        String libelle,
        RoleType type,
        List<String> privileges
) {}
