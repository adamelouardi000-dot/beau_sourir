package ma.dentalTech.service.modules.auth.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResult {
    private UserPrincipal principal;
    private String message; // ex: AUTH_OK
}
