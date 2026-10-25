package com.pynexia.week1.signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.autofill.AutofillManager;

public class MainActivity extends BaseActivity {
    private EditText name, email, password, confirm;
    private Button submit;
    private TextView feedback;
    private boolean busy;
    private RegistrationTask registration;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        showLayout(R.layout.activity_main);
        name = findViewById(R.id.name); email = findViewById(R.id.email);
        password = findViewById(R.id.password); confirm = findViewById(R.id.confirm);
        submit = findViewById(R.id.submit); feedback = findViewById(R.id.feedback);
        TextWatcher watcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { updateForm(); }
            public void afterTextChanged(Editable s) {}
        };
        for (EditText field : new EditText[]{name, email, password, confirm}) {
            field.addTextChangedListener(watcher);
            field.setOnFocusChangeListener((view, focused) -> { if (!focused) validateField(field); });
        }
        submit.setOnClickListener(view -> register());
        Object retained = getLastNonConfigurationInstance();
        if (retained instanceof RegistrationTask task) {
            registration = task;
            name.setText(task.name);
            email.setText(task.email);
            setBusy(true);
        } else {
            updateForm();
        }
    }

    private String value(EditText field) { return field.getText().toString(); }
    private boolean valid() {
        return FormRules.validName(value(name)) && FormRules.validEmail(value(email))
                && FormRules.validPassword(value(password)) && value(password).equals(value(confirm));
    }

    private void updateForm() {
        if (busy) return;
        submit.setEnabled(valid());
        feedback.setText(valid() ? R.string.ready : R.string.guidance);
        for (EditText field : new EditText[]{name, email, password, confirm}) if (field.getError() != null) validateField(field);
    }

    private void validateField(EditText field) {
        int error = 0;
        if (field == name && !FormRules.validName(value(name))) error = R.string.name_error;
        if (field == email && !FormRules.validEmail(value(email))) error = R.string.email_error;
        if (field == password && !FormRules.validPassword(value(password))) error = R.string.password_error;
        if (field == confirm && !value(password).equals(value(confirm))) error = R.string.confirm_error;
        field.setError(error == 0 ? null : getString(error));
    }

    private void register() {
        if (busy || !valid()) return;
        AutofillManager autofill = getSystemService(AutofillManager.class);
        if (autofill != null) autofill.cancel();
        String fullName = value(name).trim(), address = value(email).trim();
        char[] secret = value(password).toCharArray();
        setBusy(true);
        registration = new RegistrationTask(this, fullName, address, secret);
        registration.attach(this::onRegistered);
    }

    private void setBusy(boolean value) {
        busy = value;
        submit.setEnabled(!value && valid());
        submit.setText(value ? R.string.creating : R.string.submit);
        for (EditText field : new EditText[]{name, email, password, confirm}) field.setEnabled(!value);
    }

    private void onRegistered(int error) {
        if (isFinishing()) return;
        String fullName = registration.name, address = registration.email;
        registration.detach();
        registration = null;
        setBusy(false);
        if (error != 0) { updateForm(); feedback.setText(error); return; }
        password.setText(""); confirm.setText("");
        Intent intent = new Intent(this, SuccessActivity.class);
        intent.putExtra("name", fullName); intent.putExtra("email", address);
        startActivity(intent);
        finish();
    }

    @Override protected void onStart() {
        super.onStart();
        if (registration != null) registration.attach(this::onRegistered);
    }

    @Override protected void onStop() {
        if (registration != null) registration.detach();
        super.onStop();
    }

    @Override public Object onRetainNonConfigurationInstance() { return registration; }
}
