package ma.dentalTech.mvc.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String motDePasse;
}
