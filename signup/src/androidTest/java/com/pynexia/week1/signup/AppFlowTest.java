package com.pynexia.week1.signup;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.pm.ActivityInfo;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AppFlowTest {
    private void snapshot(String name) throws java.io.IOException {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        android.graphics.Bitmap bitmap = InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
        java.io.File folder = new java.io.File(InstrumentationRegistry.getInstrumentation().getTargetContext().getExternalFilesDir(null), "evidence");
        if (!folder.exists() && !folder.mkdirs()) throw new java.io.IOException("Cannot create screenshot folder");
        try (java.io.FileOutputStream stream = new java.io.FileOutputStream(new java.io.File(folder, name + ".png"))) {
            if (!bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)) throw new java.io.IOException("Cannot save screenshot");
        } finally { bitmap.recycle(); }
    }

    private Activity launch(Class<? extends Activity> type) {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), type);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return InstrumentationRegistry.getInstrumentation().startActivitySync(intent);
    }

    @Test public void registersValidatesAndRejectsDuplicate() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.getSharedPreferences("local_accounts", Context.MODE_PRIVATE).edit().clear().commit();
        launch(MainActivity.class);
        onView(withId(R.id.submit)).check(matches(not(isEnabled())));
        snapshot("signup-form");
        fill("not-an-email", "mismatch");
        onView(withId(R.id.submit)).check(matches(not(isEnabled())));
        onView(withId(R.id.email)).perform(scrollTo(), replaceText("learner@example.com"), closeSoftKeyboard());
        onView(withId(R.id.confirm)).perform(scrollTo(), replaceText("Learn@123"), closeSoftKeyboard());
        onView(withId(R.id.submit)).check(matches(isEnabled())).perform(scrollTo(), click());
        awaitAccountScreen();
        onView(withId(R.id.profile)).check(matches(withText("Test Learner\nlearner@example.com")));
        snapshot("signup-success");
        String stored = context.getSharedPreferences("local_accounts", Context.MODE_PRIVATE).getAll().toString();
        assertFalse(stored.contains("Learn@123"));
        assertTrue(stored.contains(".hash="));
        onView(withId(R.id.another)).perform(scrollTo(), click());
        fill("LEARNER@example.com", "Learn@123");
        onView(withId(R.id.submit)).perform(scrollTo(), click());
        Thread.sleep(1000);
        onView(withId(R.id.feedback)).check(matches(withText(R.string.duplicate)));
    }
    private void fill(String email, String confirmation) {
        onView(withId(R.id.name)).perform(scrollTo(), replaceText("Test Learner"), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(scrollTo(), replaceText(email), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(scrollTo(), replaceText("Learn@123"), closeSoftKeyboard());
        onView(withId(R.id.confirm)).perform(scrollTo(), replaceText(confirmation), closeSoftKeyboard());
    }
    @Test public void registrationSurvivesActivityRecreation() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.getSharedPreferences("local_accounts", Context.MODE_PRIVATE).edit().clear().commit();
        Activity activity = launch(MainActivity.class);
        fill("rotation@example.com", "Learn@123");
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            activity.findViewById(R.id.submit).performClick();
            activity.recreate();
        });
        awaitAccountScreen();
        onView(withId(R.id.profile)).check(matches(withText("Test Learner\nrotation@example.com")));
    }
    private void awaitAccountScreen() throws Exception {
        long deadline = System.currentTimeMillis() + 30000;
        while (System.currentTimeMillis() < deadline) {
            try { onView(withId(R.id.another)).check(matches(isDisplayed())); return; }
            catch (androidx.test.espresso.NoMatchingViewException notYet) { Thread.sleep(200); }
        }
        fail("Registration did not navigate to success within 30 seconds");
    }

}
