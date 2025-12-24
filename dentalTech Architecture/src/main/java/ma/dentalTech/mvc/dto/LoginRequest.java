package ma.dentalTech.mvc.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    private String login;       // ou email
    private String motDePasse;
}
