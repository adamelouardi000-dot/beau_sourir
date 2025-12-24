package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.service.modules.auth.api.PasswordEncoder;
import org.mindrot.jbcrypt.BCrypt;

public class DefaultPasswordEncoder implements PasswordEncoder {

    private static final int COST = 12; // niveau de sécurité (pro)

    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null)
            throw new IllegalArgumentException("Mot de passe null.");

        return BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt(COST));
    }

    @Override
    public VerifyResult verify(CharSequence rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null || storedHash.isBlank()) {
            return VerifyResult.builder()
                    .ok(false)
                    .reason("EMPTY_INPUT")
                    .needsRehash(false)
                    .build();
        }

        try {
            boolean match = BCrypt.checkpw(rawPassword.toString(), storedHash);

            return VerifyResult.builder()
                    .ok(match)
                    .reason(match ? "OK" : "MISMATCH")
                    .needsRehash(match && needsRehash(storedHash))
                    .build();

        } catch (Exception e) {
            return VerifyResult.builder()
                    .ok(false)
                    .reason("HASH_INVALID")
                    .needsRehash(false)
                    .build();
        }
    }

    @Override
    public boolean needsRehash(String storedHash) {
        if (storedHash == null || !storedHash.startsWith("$2"))
            return true;

        int currentCost = Integer.parseInt(storedHash.substring(4, 6));
        return currentCost < COST;
    }
}
