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
- List flags by categories
- View flags and description of it's entity from it's categories
- Search for flags
- View & Search supports (politically) related flags
- Flag game
- Menu for selecting single or multiple categories
<br>

 **📌 TODO:**
- [x] ~~Create app icon~~
- [x] ~~Add scroll to top button on list and search screens~~
- [x] ~~Fullscreen flag image view~~
- [x] ~~Fullscreen view orientation relative to image aspect ratio & button for unlocking screen orientation~~
- [x] ~~Searches return politically related flags~~
- [x] ~~Search result exact match sorts item to first position in list~~
- [x] ~~Filter searches~~
- [x] ~~Category multi-selection in filter menu~~
- [x] ~~Show and select politically related flags on flag view screen (with button in top bar)~~
- [x] ~~Full screen carousel for flags in current list & flag name as header~~
- [x] ~~UI Counter to show number of flags in current list on View/List screen~~
- [x] ~~Move search function into list flags screen~~
- [x] ~~Add open in Wikipedia button to Flag screen~~
- [x] ~~**Game**: Button to show correct answer~~
- [x] ~~**Game**: Open card from Game Over dialog to show score overview & lists of guessed, skipped, shown and remainder flags~~
- [x] ~~**Game**: Timer modes: Standard and Time Trial~~
- [x] ~~Settings screen for Theme customisation~~, other settings and about app info - **(Partially Complete)**
- [x] Optimize app for different system Font sizes - **(Partially Complete)**
- [ ] **Persistent Game features**: Score history & exporting scores *(for sharing)*
- [ ] **Persistent Settings features**: Themes, strictness of user guesses for game, disable animations for older devices
- [ ] Save flags to custom list
- [ ] Deselect keyboard and text fields when tap off
- [ ] Add more flags *(to less populated categories and more popular related flags)*
- [ ] Update start menu buttons with (colourful) patterned backgrounds
- [ ] Add screenshots of app to **README**
- [ ] Add Python scripts to external repository
- [ ] Add regime type categories (for political flags) from **OurWorldInData**
- [ ] Download flag image to local files feature
- [ ] Pop up warning messages when invalid/not allowed user action
- [ ] Add flag ISO codes
- [ ] Optimize app for different screen resolutions and aspect ratios
- [ ] Implement tests and production optimizations
- [ ] Language translations
- [ ] **Release app on the Google Play Store**
<br>


## 🛠 Design
- Built with Jetpack Compose in Kotlin, following architecture best practises described on Android Developers such as UDF, SSOT, separation of concerns, and ViewModels for managing business state and logic.

- Flag info uses local data, originally sourced from Wikipedia via a collection of Python scripts for getting flag images, common names, official names and alternate names.
Flag category info is derived more manually, by filtering from broader categories such as on [countries by system of government](https://en.wikipedia.org/wiki/List_of_countries_by_system_of_government) and other Wikipedia lists.
<br>

## ❤️ Acknowledgements 
 - Flag information sourced from [Wikipedia](https://en.wikipedia.org/wiki/Main_Page)
<br>

## 🔖 [License](https://github.com/aftly/Flags/blob/main/LICENSE)
```
Licensed under the Mozilla Public License, Version 2.0.
```
