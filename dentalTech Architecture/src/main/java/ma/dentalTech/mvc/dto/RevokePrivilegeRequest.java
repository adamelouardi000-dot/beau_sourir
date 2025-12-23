package ma.dentalTech.mvc.dto;

public record RevokePrivilegeRequest(
        Long roleId,
        String privilege
) {}
