package ma.dentalTech.mvc.dto;

import ma.dentalTech.entities.enums.Sexe;

import java.time.LocalDate;

public record UserCreateRequest(
        String nom,
        String email,
        String adresse,
        String cin,
        String tel,
        Sexe sexe,
        String login,
        String motDePasse,
        LocalDate dateNaissance
) {}
