package com.pynexia.week1.signup;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Locale;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class AccountStore {
    private final SharedPreferences accounts;
    public AccountStore(Context context) { accounts = context.getSharedPreferences("local_accounts", Context.MODE_PRIVATE); }

    public synchronized boolean create(String name, String email, char[] password) throws GeneralSecurityException {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        String key = Base64.encodeToString(MessageDigest.getInstance("SHA-256").digest(normalizedEmail.getBytes(StandardCharsets.UTF_8)), Base64.NO_WRAP);
        if (accounts.contains(key + ".hash")) return false;
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        PBEKeySpec spec = new PBEKeySpec(password, salt, 120000, 256);
        byte[] hash;
        try { hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        finally { spec.clearPassword(); }
        try {
            boolean saved = accounts.edit().putString(key + ".name", name.trim())
                    .putString(key + ".email", normalizedEmail)
                    .putString(key + ".salt", Base64.encodeToString(salt, Base64.NO_WRAP))
                    .putString(key + ".hash", Base64.encodeToString(hash, Base64.NO_WRAP))
                    .putInt(key + ".iterations", 120000).commit();
            if (!saved) throw new IllegalStateException("Account could not be saved");
            return true;
        } finally { Arrays.fill(hash, (byte) 0); }
    }
}
