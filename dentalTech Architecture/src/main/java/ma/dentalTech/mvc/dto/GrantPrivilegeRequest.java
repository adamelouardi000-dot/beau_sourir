package ma.dentalTech.mvc.dto;

public record GrantPrivilegeRequest(
        Long roleId,
        String privilege
) {}
