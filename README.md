# Hans Wehr

An Android app containing the scanned pages of the Hans Wehr *Dictionary of Modern
Written Arabic* (4th edition), with search by root letters. The entire dictionary is
bundled inside the app, so it works completely offline and needs no permissions.

Version 4.0 is a modernised, ad-free rebuild of the original app by Uwais Iqbal.

## Install

1. Download the latest `HansWehr-<version>.apk` from the
   [Releases](https://github.com/amjadodeh/Hans-Wehr-App/releases) page.
2. Open the file on your phone and allow installs from your browser or file manager
   when asked.

Requirements: Android 6.0 (API 23) or newer. The APK is universal (it contains no
native code), so it runs on every CPU architecture.

This build uses a new package name (`com.amjadodeh.hanswehr`), so it installs
alongside the old Play Store version instead of replacing it.

## Usage

- Tap the search icon, or tap once on any page, and type the root letters of a word, in Arabic (`كتب`) or in
  Roman Arabic (`ktb`). The dictionary opens on the page where that root begins.
- `th`, `kh`, `sh`, `gh` and `dh` each stand for one Arabic letter (ث خ ش غ ذ). When two
  such letters are genuinely separate, put a hyphen or space between them: `s-h-r` is
  س‑ه‑ر, while `shr` is ش‑ر. The full letter table is in the app's side menu.
- Swipe left and right to turn pages. Pinch or double-tap to zoom; drag to pan.
- The side menu has the verb-form table, the Roman-letter mapping, a tutorial, About,
  Settings, Help and Feedback (opens the project's issue tracker) and Rate Us (opens the
  Releases page). The app always opens on page 1, as the original did.

## What changed in 4.0

- Builds with current tooling: Gradle 9.6, Android Gradle Plugin 9.5, AndroidX,
  `compileSdk` 37, `targetSdk` 36, `minSdk` 23.
- Dictionary pages are shipped inside the APK instead of being downloaded from a
  third-party Firebase project. No internet access, no storage permission.
- Ads, in-app purchases, Firebase analytics and search logging are gone.
- Handles edge-to-edge display on Android 15+ and the Android 11+ scoped-storage rules.
  Keeps the original dark look.

## Building

```sh
./gradlew assembleRelease
```

The signed release APK is written to `app/build/outputs/apk/release/app-release.apk`.
Signing reads a properties file outside the repository, by default
`~/keystores/hanswehr-release.properties` (override with the
`HANSWEHR_KEYSTORE_PROPERTIES` environment variable):

```properties
storeFile=/absolute/path/to/hanswehr-release.jks
storePassword=...
keyAlias=hanswehr
keyPassword=...
```

If the file is absent the release build is produced unsigned.

## Credits

- Original app: [Uwais Iqbal](https://github.com/uwaisiqbal/Hans-Wehr-App).
- Dictionary: Hans Wehr, *A Dictionary of Modern Written Arabic*, 4th edition,
  edited by J Milton Cowan.

## Privacy

See [privacy_policy.md](privacy_policy.md). In short: the app collects nothing and
never connects to the internet.
