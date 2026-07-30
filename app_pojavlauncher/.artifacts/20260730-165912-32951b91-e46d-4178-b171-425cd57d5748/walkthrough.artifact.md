# Instance Selection Screen Revamp Walkthrough

I have implemented a dedicated instance selection screen and modernized the main menu interaction as requested.

## Key Changes

### 1. Refined Instance Selection UI
Created a comprehensive selection screen in [InstanceSelectionScreen.kt](file:///C:/Users/ASHMEET/Documents/Hyper_Launcher_v3/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/screens/InstanceSelectionScreen.kt).
- **Navigation Rail Sidebar**: Replaced the custom sidebar with a standard Material 3 `NavigationRail` for actions like "New Profile", "Refresh", and "Import".
- **Top-Aligned Tabs**: The `TabRow` is now positioned at the very top of the main card, and the "Select Instance" header text has been removed for a cleaner look.
- **Single-Column List**: Instances are displayed in a vertical, single-column list (`LazyColumn`) for better readability.
- **List Item Style**: Each instance is presented as a horizontal list item with the icon on the left, matching the standard list style.
- **Material 3 Tabs**: Modern filter tabs (All, Vanilla, Modded) following M3 guidelines.
- **Sidebar Animations**: Tactical scale-down feedback when clicking actions in the rail.
- **Rounded Content Area**: The entire selection interface is contained within a modern rounded card.

### 2. Modern Instance List Items
- **Radio-Button Selection**: Clear visual indication of the selected instance.
- **Horizontal Layout**: Consistent list item design with centralized icons.
- **Safety Fallbacks**: Instances without names are now clearly labeled as **"UNNAMED"**.

### 3. Navigation & Backend
- **Fragment Host**: [InstanceSelectionFragment.kt](file:///C:/Users/ASHMEET/Documents/Hyper_Launcher_v3/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/fragments/InstanceSelectionFragment.kt) manages navigation flows.
- **2D Skin Rendering**: Updated [SkinHeadRenderer.java](file:///C:/Users/ASHMEET/Documents/Hyper_Launcher_v3/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/authenticator/accounts/SkinHeadRenderer.java) for a cleaner isometric look in the main menu sidebar.

## Verification Summary
- **UI Consistency**: Verified that the layout remains stable in landscape orientation and follows the one-column list request.
- **Filtering Logic**: Implemented `isVanilla` helper to correctly separate versions.
- **Interactive Feedback**: Verified that button animations and radio button states update correctly.
