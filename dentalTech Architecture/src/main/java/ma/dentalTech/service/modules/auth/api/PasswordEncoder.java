package ma.dentalTech.service.modules.auth.api;

import lombok.Builder;
import lombok.Data;

public interface PasswordEncoder {

    /**
     * Encode un mot de passe brut (hash + salt).
     */
    String encode(CharSequence rawPassword);

    /**
     * Vérifie un mot de passe brut contre un hash stocké en base, et retourne un résultat détaillé.
     */
    VerifyResult verify(CharSequence rawPassword, String storedHash);

    /**
     * Indique si le hash stocké doit être recalculé (ex: changement cost/algorithme).
     */
    boolean needsRehash(String storedHash);

    @Data
    @Builder
    class VerifyResult {
        private boolean ok;            // true si mot de passe correct
        private String reason;         // ex: "OK", "EMPTY_INPUT", "HASH_INVALID", "MISMATCH"
        private boolean needsRehash;   // true si le hash doit être régénéré (upgrade)
    }
}
