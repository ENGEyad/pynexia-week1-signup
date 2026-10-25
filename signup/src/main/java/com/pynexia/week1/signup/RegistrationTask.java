package com.pynexia.week1.signup;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.IntConsumer;

final class RegistrationTask {
    final String name;
    final String email;
    private final Handler main = new Handler(Looper.getMainLooper());
    private IntConsumer listener;
    private boolean completed;
    private boolean delivered;
    private int error;

    RegistrationTask(Context context, String name, String email, char[] password) {
        this.name = name;
        this.email = email;
        Context application = context.getApplicationContext();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            int outcome = 0;
            try {
                if (!new AccountStore(application).create(name, email, password)) outcome = R.string.duplicate;
            } catch (GeneralSecurityException | IllegalStateException failure) {
                outcome = R.string.save_error;
            } finally {
                Arrays.fill(password, '\0');
            }
            int result = outcome;
            main.post(() -> { error = result; completed = true; deliver(); });
        });
        executor.shutdown();
    }

    void attach(IntConsumer listener) {
        this.listener = listener;
        main.post(this::deliver);
    }

    void detach() { listener = null; }

    private void deliver() {
        if (completed && !delivered && listener != null) {
            delivered = true;
            listener.accept(error);
        }
    }
}
