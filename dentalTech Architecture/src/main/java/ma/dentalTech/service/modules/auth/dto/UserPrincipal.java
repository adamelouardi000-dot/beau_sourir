package ma.dentalTech.service.modules.auth.dto;

import lombok.*;
import ma.dentalTech.entities.enums.RoleType;

import java.util.HashSet;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserPrincipal {
    private Long userId;
    private String login;
    private String email;

    @Builder.Default
    private Set<RoleType> roles = new HashSet<>();

    @Builder.Default
    private Set<String> privileges = new HashSet<>();
}
