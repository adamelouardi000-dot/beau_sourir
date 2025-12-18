package ma.dentalTech.service.modules.auth.api;

import java.util.Base64;

public class PasswordEncoder {

    /**
     * Encode un mot de passe (Version simplifiée en Base64 pour le test technique).
     */
    public String encode(String rawPassword) {
        if (rawPassword == null) return null;
        return Base64.getEncoder().encodeToString(rawPassword.getBytes());
    }

    /**
     * Vérifie la correspondance entre le mot de passe brut et le mot de passe encodé.
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}