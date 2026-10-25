# Sign-Up

A native Android registration form with live validation, a dynamically enabled submit button, and a local account confirmation screen.

Pynexia Android training / Week 1. Java 17 + XML.

[Hosted showcase](https://engeyad.github.io/pynexia-week1-signup/) · [Report PDF](https://engeyad.github.io/pynexia-week1-signup/downloads/Sign-Up-App-Report.pdf) · [APK](https://engeyad.github.io/pynexia-week1-signup/downloads/Sign-Up-App.apk) · [Submission ZIP](https://engeyad.github.io/pynexia-week1-signup/downloads/Sign-Up-App.zip)

## Run

Open this repository root in Android Studio. Install SDK 35 / Build Tools 35.0.0; use JDK 17 or 21. Sync Gradle and run `signup` on Android 8.0 / API 26 or later. Initial sync needs internet. AGP 8.9.1; Gradle Wrapper 8.11.1.

```powershell
.\gradlew.bat :signup:assembleDebug :signup:testDebugUnitTest :signup:lintDebug
.\gradlew.bat :signup:connectedDebugAndroidTest
```

The second command needs an emulator/device. On macOS/Linux, use `chmod +x gradlew` then `./gradlew`. APK output: `signup/build/outputs/apk/debug/signup-debug.apk`.

## Implementation

### Required fields

Name, email, password and confirmation use XML inputs. Name: 2-80 characters. Password: 8-128 characters with at least one letter and one digit.

### Dynamic validation

TextWatcher updates the submit state. Only valid fields and matching passwords enable Create account. Invalid fields show feedback on focus loss.

### Local registration

App-private storage rejects duplicate emails without case sensitivity. Salted PBKDF2-HMAC-SHA256 derivation runs off the UI thread; the pending task survives Activity recreation.

## Review walkthrough

1. Try an invalid email and mismatched passwords; Create account stays disabled.
2. Use name Alex Learner, email alex@example.com, password Practice123 and matching confirmation.
3. Tap Create account and confirm the profile confirmation screen appears.
4. Return and reuse the same email to check duplicate handling. Clear app data to reset local accounts.

## Verification

3 JVM tests and 2 device tests passed, with zero failures, errors or skips. Lint: 0 errors and 7 warnings, without suppression. Three JVM tests cover validation rules. Two device tests cover submit states, local account creation without plaintext password storage, duplicate emails and registration across Activity recreation.

Tested 25 October 2026 on a Pixel 8 emulator, Android 17 / API 37 (37.1 image), x86_64. No physical-device or API 26 run. See `verification/` for executed reports; local filesystem prefixes are redacted. Debug APK signature verified.

## Scope

Local-only learning exercise: no backend, email verification, recovery or shared login account. Passwords are not saved across screen recreation. Autofill is disabled for this practice form.

APKs are debug-signed builds for assessment. The hosted page is a project showcase with actual screenshots and downloads, not an in-browser Android runtime. Publication does not submit the assignment to Pynexia.

## شرح عربي

يتحقق التمرين من الاسم والبريد الإلكتروني وكلمة المرور وتأكيدها أثناء الكتابة. لا يتفعّل زر إنشاء الحساب إلا عند صحة جميع الحقول. تُحفظ الحسابات داخل التطبيق محليًا، وتُرفض عناوين البريد المكررة. تُشتق كلمات المرور مع ملح عشوائي بدل حفظها كنص واضح، وتُنفذ العملية خارج مسار واجهة المستخدم.

للتجربة: أدخل بيانات صحيحة وكلمة مرور من ثمانية أحرف على الأقل تتضمن حرفًا ورقمًا، ثم كرر البريد نفسه للتحقق من منع التكرار. هذا التطبيق مستقل عن تمرين تسجيل الدخول ولا يرسل البيانات إلى خادم.

## Submission

Copy `SUBMISSION-NOTES.txt` into the notes field. Repository URL: https://github.com/ENGEyad/pynexia-week1-signup

Hosted URL: https://engeyad.github.io/pynexia-week1-signup/

Attach the ZIP or PDF. The APK is inside the ZIP, since the supplied form does not accept direct APK attachments.

Reference: https://developer.android.com/develop/ui/views/touch-and-input
