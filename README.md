<div align="center">
    <img src="assets/icon_round.png" width="128" height="128">
</div>

<div align="center">
# 🏳️ Flags 🌍

[![Android](https://img.shields.io/badge/Android-Platform?label=Platform&color=62a900)](https://www.android.com/)
[![Min API: 24](https://img.shields.io/badge/24-minSdkVersion?label=minSdkVersion&color=62a900)](https://developer.android.com/tools/releases/platforms#7.0)
[![GitHub Releases](https://img.shields.io/badge/GitHub_Releases-Download?label=Download&color=62a900)](https://github.com/aftly/Flags/releases)
[![Latest release version](https://img.shields.io/github/v/release/aftly/Flags?include_prereleases&color=dc5d18)](https://github.com/aftly/Flags/releases)
[![License: MPL 2.0](https://img.shields.io/badge/license-MPL%202.0-blue.svg)](https://github.com/aftly/Flags/blob/main/LICENSE)
</div>

## ❔ About
- **Flags** is a free and open source Android app for displaying various flags of the world with categorisation for descriptions and user sorting and a flag guessing game.

- *This app is currently in alpha and is feature incomplete. Feedback is welcome, please submit feature requests and issues via [GitHub Issues](https://github.com/aftly/Flags/issues).*
<br>

## 📸 Screenshots
- **TODO**
<br>

## 📱 Installation
[<img src="assets/badge_github.png" 
    alt="Get it on GitHub" 
    height="80">](https://github.com/aftly/Flags/releases)

**Verification info**:
- Package & APK Signing Certificate hash (SHA-256):
  - *(such as for use with [AppVerifier](https://github.com/soupslurpr/AppVerifier))*
```
dev.aftly.flags 2E:F2:C1:C7:6B:7F:BB:06:F7:AD:55:07:C6:6B:7D:12:4B:F3:2F:95:CB:01:CD:62:C8:DD:E2:F5:5F:3B:71:6C
```
<br>

## ⭐ Features
- List flags
- View flag and description of it's entity from it's categories
- Search screen
- Flag game
- Button/menu for filtering flags by category(s)
<br>

 **📌 TODO for version 1.0:**
- [ ] Add more flags (to less populated categories)
- [x] Add scroll to top button on list and search screens
- [ ] Full screen view option on flag image
- [ ] Implement regime type categories for political flags from OurWorldInData
- [ ] Add Python scripts to repo
- [ ] Make searches filterable
- [ ] Move search function into list flags screen
- [ ] Add ability to select multiple individual categories in the filter menu
- [ ] Persistent data features
- [ ] Game features: Timers, score history, exporting scores (for sharing)
- [ ] Settings screen: Themes, strictness of user guesses for game, disable animations for older devices
- [ ] Info screen: For information about the app and links to app resources
- [ ] Add ability to save flags to custom list
- [ ] Optimise app for different screen resolutions and aspect ratios
- [ ] Implement tests and production optimizations
- [ ] Language translations
- [ ] Release app on the Google Play Store
<br>

## 🛠 Design
- Built with Jetpack Compose in Kotlin, following architecture best practises described on Android Developers, such as UDF, SSOT, ViewModels for managing state and business logic. (UI-only state is kept in local scope.)

- Flag info uses local data, originally sourced from Wikipedia via a collection of Python scripts for getting flag images, common names, official names and alternate names.
Flag category info is derived more manually, by filtering from broader categories such as on [countries by system of government](https://en.wikipedia.org/wiki/List_of_countries_by_system_of_government) and other Wikipedia lists.
<br>

## ❤️ Acknowledgements 
 - Flag information sourced from [Wikipedia](https://en.wikipedia.org/wiki/Main_Page)
<br>

## 🔖 License 
```
Licensed under the Mozilla Public License, Version 2.0.
```
