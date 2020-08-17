# 0.6.1 (alpha)

## New features
VS Code
*   [KOGITO-3089](https://issues.redhat.com/browse/KOGITO-3089) - Add button on VS Code to generate SVG for BPMN and DMN editors

Editors
*   No new features

## Fixed issues
VS Code
*   [KOGITO-2579](https://issues.redhat.com/browse/KOGITO-2579) - Custom Editor VS Code issues on 1.46 version (probably fixed on 1.47)
*   [KOGITO-1689](https://issues.redhat.com/browse/KOGITO-1689) - Save when multiple assets opened
*   [KOGITO-2134](https://issues.redhat.com/browse/KOGITO-2134) - [VSCode] Allow custom editors to hook into Edit menu actions
*   [KOGITO-2135](https://issues.redhat.com/browse/KOGITO-2135) - [VSCode] Custom editor does not open properly
*   [KOGITO-2619](https://issues.redhat.com/browse/KOGITO-2619) - scrolling a nested Decision Table in VSCode DMN Editor messed up the editor screen
*   [KOGITO-1980](https://issues.redhat.com/browse/KOGITO-1980) - Update labels from `VSCode` to `VS Code`

Editors
*   [KOGITO-1883](https://issues.redhat.com/browse/KOGITO-1883) - [Test Scenario Editor] Search DMN files over subfolders doesn't work in Windows Environment
*   [KOGITO-2953](https://issues.redhat.com/browse/KOGITO-2953) - Fix Guided Tour styles

# 0.6.0 (alpha)

## New features
VS Code
- [KOGITO-2076](https://issues.redhat.com/browse/KOGITO-2076) Open File API
- [KOGITO-2180](https://issues.redhat.com/browse/KOGITO-2180) Use KeyboardShortcutsAPI with StateControlAPI (Undo/Redo)
- [KOGITO-2542](https://issues.redhat.com/browse/KOGITO-2542) Update Hub to install VS Code extension from Marketplace
- [KOGITO-766](https://issues.redhat.com/browse/KOGITO-766) Keyboard Shortcuts API
- [KOGITO-1373](https://issues.redhat.com/browse/KOGITO-1373) Integrate the new StateControl API (Undo/Redo/Is Dirty) on the different channels
- [KOGITO-2132](https://issues.redhat.com/browse/KOGITO-2132) Adapt to new VS Code 1.46

Editors
- [KOGITO-1776](https://issues.redhat.com/browse/KOGITO-1776) [DMN Designer] Create a "Load Projects From Client" button
- [KOGITO-2615](https://issues.redhat.com/browse/KOGITO-2615) [Guided Tour - DMN] All anchors in the DMN guide tour must be target="_blank"
- [KOGITO-2137](https://issues.redhat.com/browse/KOGITO-2137) [Scesim Editor] Integrate SceSim with State Control API
- [KOGITO-2337](https://issues.redhat.com/browse/KOGITO-2337) [SCESIM Editor] Enable UNDO/REDO
- [KOGITO-697](https://issues.redhat.com/browse/KOGITO-697) [DMN Designer] Support Included Models on Kogito

## Fixed issues
VS Code
- [KOGITO-745](https://issues.redhat.com/browse/KOGITO-745) Command Y to undo isn't mapped
- [KOGITO-2490](https://issues.redhat.com/browse/KOGITO-2490) Shortcuts should not be triggered when typing on text inputs
- [KOGITO-2493](https://issues.redhat.com/browse/KOGITO-2493) Update and use @types/vscode
- [KOGITO-2580](https://issues.redhat.com/browse/KOGITO-2580) Add noImplicitAny property back into tsconfig.json
- [KOGITO-2581](https://issues.redhat.com/browse/KOGITO-2581) Delete isDirty method on core-api

Editors
- [KOGITO-1552](https://issues.redhat.com/browse/KOGITO-1552) [DMN Designer] Open a DMN file with an included model - DRG Elements
- [KOGITO-1553](https://issues.redhat.com/browse/KOGITO-1553) [DMN Designer] Open a DMN file with an included model - Included Models tab
