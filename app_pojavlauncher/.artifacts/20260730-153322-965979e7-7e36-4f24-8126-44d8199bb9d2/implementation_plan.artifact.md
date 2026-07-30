# Convert MainMenuFragment to Compose

This plan outlines the steps to convert the XML-based `MainMenuFragment` to a fully Jetpack Compose implementation.

## Proposed Changes

### PojavLauncher Themes and Components

#### [MainMenuFragmentCompose.kt](file:///C:/Users/ASHMEET/Documents/Hyper_Launcher_v3/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/screens/components/MainMenuFragmentCompose.kt)

- **NEW** Create `MainMenuFragmentCompose` which will contain:
    - `MenuButton`: A custom button matching `LauncherMenuButton` style.
    - `mcVersionSpinnerCompose`: A custom spinner matching `mcVersionSpinner` style.
    - `MineButtonCompose`: A custom button matching `MineButton` style.
    - `MainMenuContent`: The main layout optimized for landscape mode (horizontal).

#### [MainMenuFragment.java](file:///C:/Users/ASHMEET/Documents/Hyper_Launcher_v3/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/fragments/MainMenuFragment.java)

- Modify `onCreateView` to return a `ComposeView`.
- Update `onViewCreated` logic to interact with the Compose state if necessary, or move the logic into the Compose component.

---

### Implementation Details

#### MenuButton
- Will use `MaterialTheme.colorScheme.surfaceVariant` for background.
- Will support an icon (`painter`) and text.
- Standardized padding and text sizes from dimensions.

#### mcVersionSpinnerCompose
- Will load instances using `Instances.load()`.
- Will display the selected instance's name and icon.
- Will show a dropdown/dialog to select instances.

#### MineButtonCompose
- Will match the "Play" button style (Noto Sans Bold, specific background).

## Verification Plan

### Automated Tests
- No automated tests available for UI changes in this project.

### Manual Verification
- Deploy the app and verify the main menu layout in landscape mode.
- Test the version spinner (selecting different versions).
- Test all menu buttons (Wiki, Social Media, Custom Controls, etc.).
- Verify theme switching (if applicable, though Pojav uses a fixed dark theme).
