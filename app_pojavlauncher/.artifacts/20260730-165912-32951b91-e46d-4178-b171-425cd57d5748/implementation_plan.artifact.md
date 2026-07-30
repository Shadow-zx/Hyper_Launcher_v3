# Instance Selection Screen Implementation Plan

This plan outlines the creation of a new instance selection screen using Jetpack Compose, featuring category filtering, radio-button selection, and a sidebar for actions like "New Profile", "Refresh", and "Import Modpack".

## User Review Required

- **Action Placement**: The "Create New", "Refresh", and "Import Modpack" buttons will be placed in a left-side column (sidebar) as requested.
- **Filtering Logic**: I'll categorize instances into "All", "Vanilla", and "Modded" based on their `versionId`.
- **Navigation**: I will create a new Fragment `InstanceSelectionFragment` to host this Compose screen, triggered from the Main Menu.

## Proposed Changes

### Core Components

#### [NEW] [InstanceSelectionScreen.kt](file:///C:/Users/ASHMEET/Documents/Hyper_Launcher_v3/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/screens/InstanceSelectionScreen.kt)

- Main Compose screen for instance selection.
- Features:
    - **Sidebar**: Buttons for "Create New", "Refresh", and "Import Modpack".
    - **Top Tabs**: Filter categories (All, Vanilla, Modded).
    - **Grid/List**: Rounded cards for each instance with a radio button for selection.
    - **Background**: Modern glassmorphic or dark-themed cards as per theme.

#### [NEW] [InstanceSelectionFragment.kt](file:///C:/Users/ASHMEET/Documents/Hyper_Launcher_v3/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/fragments/InstanceSelectionFragment.kt)

- Fragment host for `InstanceSelectionScreen`.
- Handles navigation to `ProfileTypeSelectFragment`, `SearchModFragment`, and `InstanceEditorFragment`.
- Uses `ExtraCore` to trigger game launch or refresh.

---

### Main Menu Integration

#### [MainMenuFragmentCompose.kt](file:///C:/Users/ASHMEET/Documents/Hyper_Launcher_v3/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/screens/components/MainMenuFragmentCompose.kt)

- Replace the current `DropdownMenu` with a call to `onVersionSpinnerClick` (which will navigate to the new fragment).
- Remove the `allInstances` loading logic from the main menu to optimize performance.

#### [MainMenuFragment.kt](file:///C:/Users/ASHMEET/Documents/Hyper_Launcher_v3/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/fragments/MainMenuFragment.kt)

- Update `onVersionSpinnerClick` to navigate to `InstanceSelectionFragment`.

---

### Utils

#### [InstanceIconProvider.java](file:///C:/Users/ASHMEET/Documents/Hyper_Launcher_v3/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/instances/InstanceIconProvider.java)

- (Optional) Ensure `fetchIcon` works efficiently with many instances in a list.

## Verification Plan

### Automated Tests
- N/A (Compose UI testing requires device).

### Manual Verification
- **Navigation**: Click instance name in Main Menu -> New Selection Screen opens.
- **Filtering**: Verify tabs filter instances correctly (Vanilla vs Modded).
- **Selection**: Select an instance -> Radio button updates -> Returning to Main Menu shows new selection.
- **Actions**:
    - Click "Create New" -> Navigates to `ProfileTypeSelectFragment`.
    - Click "Import Modpack" -> Navigates to `SearchModFragment`.
    - Click "Refresh" -> List reloads.
