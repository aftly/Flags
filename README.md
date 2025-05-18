<div align="center">
    <img src="assets/icon_round.png" width="128" height="128">
</div>

<div align="center">

# 🏳️ Flags 🌍

[![Android](https://img.shields.io/badge/Android-Platform?label=Platform&color=62a900)](https://www.android.com/)
[![Min API: 24](https://img.shields.io/badge/24-minSdkVersion?label=minSdkVersion&color=62a900)](https://developer.android.com/tools/releases/platforms#7.0)
[![GitLab Releases](https://img.shields.io/badge/GitLab_Releases-Download?label=Download&color=62a900)](https://gitlab.com/aftly/flags/-/releases)
[![Latest release version](https://img.shields.io/gitlab/v/release/aftly/flags?include_prereleases&color=dc5d18)](https://gitlab.com/aftly/flags/-/releases)
[![License: MPL 2.0](https://img.shields.io/badge/license-MPL%202.0-blue.svg)](https://gitlab.com/aftly/flags/-/blob/main/LICENSE)

</div>

## ❔ About
- **Flags** is a free and open source Android app for displaying various flags of the world with categorisation for descriptions and user sorting and a flag guessing game.

- *This app is currently in alpha and is feature incomplete. Feedback is welcome, please submit feature requests and issues via [GitLab Issues](https://gitlab.com/aftly/flags/-/issues).*
<br>

## 📸 Screenshots
- **TODO**
<br>

## 📱 Installation
- [**GitLab Releases**](https://gitlab.com/aftly/flags/-/releases)

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
- [x] Optimize app for different system Font sizes - **(Partially Complete)**
- [ ] Add more flags *(to less populated categories and more popular related flags)*
- [ ] Add open in Wikipedia button to Flag screen
- [ ] Add regime type categories (for political flags) from **OurWorldInData**
- [ ] Add Python scripts to repository
- [ ] Deselect keyboard and text fields when tap off
- [ ] Move search function into list flags screen
- [ ] **Game**: Button to show correct answer & list in game over details
- [ ] **Game**: Timer feature/mode
- [ ] **Persistent Game features**: Score history & exporting scores *(for sharing)*
- [ ] **Persistent Settings features**: Themes, strictness of user guesses for game, disable animations for older devices
- [ ] **Info screen**: For information about the app and links to app resources
- [ ] Save flags to custom list
- [ ] Add screenshots of app to **README**
- [ ] Download flag image to local files feature
- [ ] Pop up warning messages when invalid/not allowed user action
- [ ] Add flag ISO codes
- [ ] Optimize app for different screen resolutions and aspect ratios
- [ ] Implement tests and production optimizations
- [ ] Language translations
- [ ] Release app on the Google Play Store
<br>


## 🛠 Design
- Built with Jetpack Compose in Kotlin, following architecture best practises described on Android Developers such as UDF, SSOT and ViewModels for managing business state and logic.

- Flag info uses local data, originally sourced from Wikipedia via a collection of Python scripts for getting flag images, common names, official names and alternate names.
Flag category info is derived more manually, by filtering from broader categories such as on [countries by system of government](https://en.wikipedia.org/wiki/List_of_countries_by_system_of_government) and other Wikipedia lists.
<br>

## ❤️ Acknowledgements 
 - Flag information sourced from [Wikipedia](https://en.wikipedia.org/wiki/Main_Page)
<br>

## 🔖 [License](https://gitlab.com/aftly/flags/-/blob/main/LICENSE)
```
Licensed under the Mozilla Public License, Version 2.0.
```
