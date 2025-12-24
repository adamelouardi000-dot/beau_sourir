package ma.dentalTech.mvc.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    private boolean actif;
    private LocalDate lastLoginDate;
}
