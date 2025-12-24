package ma.dentalTech.mvc.dto;

import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private Long userId;
    private String nomComplet;
    private String email;

    private Set<String> roles;
    private Set<String> privileges;

    private String message; // "AUTH_OK", "LOGIN_SUCCESS", etc.
}
