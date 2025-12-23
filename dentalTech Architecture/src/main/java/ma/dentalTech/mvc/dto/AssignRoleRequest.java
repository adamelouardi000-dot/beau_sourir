package ma.dentalTech.mvc.dto;

public record AssignRoleRequest(
        Long utilisateurId,
        Long roleId
) {}
