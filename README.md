<p align="center">
  <a href="https://github.com/aftly/Flags">
    <img src="assets/icon_round.png" width="128" height="128">
    <h1 align="center">🏳️ Flags 🌍</h1>
  </a>
</p>
<p align="center">
  <a href="https://github.com/aftly/Flags" style="text-decoration:none" area-label="Android">
    <img src="https://img.shields.io/badge/Android-Platform?label=Platform&color=62a900">
  </a>
  <a href="https://github.com/aftly/Flags" style="text-decoration:none" area-label="Min API: 21">
    <img src="https://img.shields.io/badge/24-minSdkVersion?label=minSdkVersion&color=62a900">
  </a>
  <a href="https://github.com/aftly/Flags/releases" style="text-decoration:none" area-label="GitHub Releases">
    <img src="https://img.shields.io/badge/GitHub_Releases-Download?label=Download&color=62a900">
  </a>
  <a href="https://github.com/aftly/Flags/releases" style="text-decoration:none" area-label="Latest release version">
    <img src="https://img.shields.io/github/v/release/aftly/Flags?include_prereleases&color=dc5d18">
  </a>
  <a href="https://github.com/aftly/Flags/blob/main/LICENSE" style="text-decoration:none" area-label="License: MPL 2.0">
    <img src="https://img.shields.io/badge/license-MPL%202.0-blue.svg">
  </a>
</p>

## About
- **Flags** is a free and open source Android app for displaying various flags of the world with categorisation for descriptions and user sorting, and a flag guessing game.

- *This app is currently in alpha and is feature incomplete. Feedback is welcome, please submit feature requests and issues via [GitHub Issues](https://github.com/aftly/Flags/issues).*
<br>

## Features
- List flags
- Search page
- Flag Game
- Button/menu for filtering flags by category(s)
<br>

**TODO:**
- [ ] Add more flags
- [ ] Full screen view option on flag screen
- [ ] Implement regime type categories for political flags from OurWorldInData
- [ ] Add Python scripts to repo
- [ ] Make searches filterable
- [ ] Move search function into list flags screen
- [ ] Implement ability to select multiple individual categories in the filter menu
- [ ] Persistent data features
- [ ] Settings screen: Themes, strictness of user guesses for game, disable animations for older devices
- [ ] Info screen: For information about the app and links to app resources
- [ ] Game features: Timers, score history, exporting scores (for sharing)
- [ ] Optimise app for different screen resolutions and aspect ratios
- [ ] Language translations
- [ ] Release app on the Google Play Store
<br>

## 📱 Installation
[<img src="assets/badge_github.png" 
    alt="Get it on GitHub" 
    height="80">](https://github.com/aftly/Flags/releases)

**Verification info**:
- Package & APK Signing Certificate hash (SHA-256) *(such as for use with [AppVerifier](https://github.com/soupslurpr/AppVerifier))*: 
```
dev.aftly.flags 2E:F2:C1:C7:6B:7F:BB:06:F7:AD:55:07:C6:6B:7D:12:4B:F3:2F:95:CB:01:CD:62:C8:DD:E2:F5:5F:3B:71:6C
```
<br>

## 🛠 Design
- Built with Jetpack Compose in Kotlin, following architecture best practises described on Android Developers, such as SSOT, UDF, ViewModels for managing state (except for UI-only state which is kept in it's local Composable scope).

- Flag info uses local data, originally sourced from Wikipedia via a collection of Python scripts for getting flag images, common names, official names and alternate names.
Flag category info is derived more manually, by filtering from broader categories such as on [countries by system of government](https://en.wikipedia.org/wiki/List_of_countries_by_system_of_government) and other Wikipedia lists.
<br>

## 📱 Screenshots
- **TODO**
<br>

## ❤️ Acknowledgements 
 - Flag information sourced from [Wikipedia](https://en.wikipedia.org/wiki/Main_Page)
<br>

# 🔖 License 
```
Licensed under the Mozilla Public License, Version 2.0.
```
