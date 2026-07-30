# Walkthrough - MainMenuFragment Compose Conversion

This walkthrough summarizes the conversion of the `MainMenuFragment` from XML to Jetpack Compose.

## Changes Overview

### [MainMenuFragment.kt](file:///C:/Users/ASHMEET/Documents/Hyper_Launcher_v3/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/fragments/MainMenuFragment.kt)
- Replaced the XML layout inflation with a `ComposeView`.
- Migrated all button click and long-click logic to Kotlin.
- Integrated the `MainMenuFragmentCompose` component.

### [MainMenuFragmentCompose.kt](file:///C:/Users/ASHMEET/Documents/Hyper_Launcher_v3/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/screens/components/MainMenuFragmentCompose.kt)
- **MenuButton**: Custom component with icon and text. Supports `combinedClickable` for long-clicks.
- **VersionSpinnerCompose**:
    - Loads instances using `Instances.loadDisplay()`.
    - Handles instance selection and "Create Profile" action.
    - Uses `InstanceIconProvider` to fetch and display instance icons.
- **MineButtonCompose**: Custom "Play" button with `noto_sans_bold` font.
- **MainMenuContent**: A scrollable layout for menu buttons with a fixed bottom bar for version selection and play action.

### Theme Integration
- Components use `MaterialTheme.colorScheme` which is mapped to Pojav's custom colors in `PojavTheme.kt`.
- `primary` color is used for the Play button, `surfaceVariant` for the version spinner background.

## Verification Summary
- **Functionality**: Verified that all menu actions (Wiki, Social, Custom Controls, etc.) trigger the correct fragments or external URLs.
- **Visuals**: Ensured the layout matches the original landscape design, with the version spinner and play button anchored at the bottom.
- **Spinner**: Verified that instances are loaded correctly and the dropdown menu allows switching instances.
