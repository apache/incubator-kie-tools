# 0.9.1 (alpha)

## New features:

Infrastructure
* [KOGITO-4914](https://issues.redhat.com/browse/KOGITO-4914) - The SVG icon is broken on vscode-insiders

Editors

--

## Fixed issues:

Editors
* [KOGITO-2197](https://issues.redhat.com/browse/KOGITO-2197) - [Scesim Editor] Bottom scroll bar getting hide
* [KOGITO-3192](https://issues.redhat.com/browse/KOGITO-3192) - [DMN Designer] Multiple DRDs support - The undo/redo are lost when user changes between diagrams
* [KOGITO-4916](https://issues.redhat.com/browse/KOGITO-4916) - [DMN Designer] Error during the save/marshaller of specific diagrams


# 0.9.0 (alpha)

## New features:
Infrastructure
* [KOGITO-4349](https://issues.redhat.com/browse/KOGITO-4349) - Publish VS Code Extensions to OpenVSX registry

Editors
* [KOGITO-4190](https://issues.redhat.com/browse/KOGITO-4190) - SceSim runner does not display reason for failure

## Fixed issues:

Infrastructure

--

Editors
* [KOGITO-4266](https://issues.redhat.com/browse/KOGITO-4266) - [DMN Designer] Decision Service is missing inputData element in model with multiple DRDs

# 0.8.6 (alpha)

--

# 0.8.5 (alpha)

## New features:

Infrastructure
* [KOGITO-4666](https://issues.redhat.com/browse/KOGITO-4666) - Converge the CSS to avoid conflicts between PF3 and PF4


Editors

--

## Fixed issues:

Infrastructure

--

Editors
* [KOGITO-4257](https://issues.redhat.com/browse/KOGITO-4257) - Importing and modeling decision models is too slow for productive modeling
* [KOGITO-4265](https://issues.redhat.com/browse/KOGITO-4265) - [DMN Designer] Decision Services - The parameters order in the properties panel is not correct
* [KOGITO-4368](https://issues.redhat.com/browse/KOGITO-4368) - DMN Editor wrong edge arrow tip connection on reopen
* [KOGITO-4500](https://issues.redhat.com/browse/KOGITO-4500) - [DMN Designer] DMN schema/model validation errors when model has AUTO-SOURCE or AUTO-TARGET connections
* [KOGITO-4533](https://issues.redhat.com/browse/KOGITO-4533) - Scesim assets are broken in VS Code extension
* [KOGITO-4539](https://issues.redhat.com/browse/KOGITO-4539) - [DMN Designer] DMN takes too long to open models with too many nodes

# 0.8.4 (alpha)

--

# 0.8.3 (alpha)

## New features:

Editors
*   [[KOGITO-4122](https://issues.redhat.com/browse/KOGITO-4122)] - [Test Scenario Editor] Improve Test Scenario creation UX
*   [[KOGITO-3302](https://issues.redhat.com/browse/KOGITO-3302)] - Allow to select any DMN asset in Wizard

### Fixed issues:

VS Code
*   [[KOGITO-3718](https://issues.redhat.com/browse/KOGITO-3718)] - Importing DMN as Included Model in VS Code on Windows

Editors
*   [[KOGITO-4049](https://issues.redhat.com/browse/KOGITO-4122)] - Cannot open Violation Scenarios.scesim in VScode
*   [[KOGITO-4197](https://issues.redhat.com/browse/KOGITO-4197)] - [DMN Designer] DMN 1.1 model can not be fixed to proper DMN 1.2

# 0.8.2 (alpha)

--

# 0.8.1 (alpha)

## New features

Editors
*   [[KOGITO-3805](https://issues.redhat.com/browse/KOGITO-3805)] - [DMN Designer] Convert DMN 1.1/1.3 models to version 1.2

## Fixed issues
VS Code
*   [[KOGITO-2629](https://issues.redhat.com/browse/KOGITO-2629)] - [VSCode] Undo/redo command don't fire for webviews if used from command palette
*   [[KOGITO-3808](https://issues.redhat.com/browse/KOGITO-3808)] - Fix Keyboard Shortcuts modal title on VS Code dark theme
*   [[KOGITO-3884](https://issues.redhat.com/browse/KOGITO-3884)] - Save SVG file using the kieserver naming convention
*   [[KOGITO-3901](https://issues.redhat.com/browse/KOGITO-3901)] - Cannot Include DMNs on VS Code- Kogito Bundle on Windows
*   [[KOGITO-3348](https://issues.redhat.com/browse/KOGITO-3348)] - Fix Open SVG popup to not open SVG when clicking the X button

Editors
*   [[KOGITO-2770](https://issues.redhat.com/browse/KOGITO-2770)] - [Test Scenario Editor] Changes on Setting Docks must activate isDirty status
*   [[KOGITO-3696](https://issues.redhat.com/browse/KOGITO-3696)] - DMN Editor regression failing display edge when xml missing DMNDI

# 0.8.0 (alpha)

## Fixed issues

Editors
*   [[KOGITO-3192](https://issues.redhat.com/browse/KOGITO-3192)] - [DMN Designer] Multiple DRDs support - The undo/reado are lost when user changes between diagrams
*   [[KOGITO-3364](https://issues.redhat.com/browse/KOGITO-3364)] - [DMN Designer] Multiple DRDs support - When a node is deleted in the DRG, undo/redo operations do not work properly
*   [[KOGITO-3366](https://issues.redhat.com/browse/KOGITO-3366)] - [DMN Designer] Boxed Expression - Decision Tables - Users lost constraints when they change the focus

# 0.7.1 (alpha)

## New features

Editors
*   [[KOGITO-2664](https://issues.redhat.com/browse/KOGITO-2664)] - [DMN Designer] Multiple DRDs support

## Fixed issues

Editors
*   [[KOGITO-2348](https://issues.redhat.com/browse/KOGITO-2348)] - Scenario fails if model includes another one
*   [[KOGITO-2773](https://issues.redhat.com/browse/KOGITO-2773)] - [Test Scenario Editor] BC - Kogito generated scesim file are in same cases not compatible
*   [[KOGITO-3152](https://issues.redhat.com/browse/KOGITO-3152)] - [DMN Designer] PMML support - function parameters generation
*   [[KOGITO-3571](https://issues.redhat.com/browse/KOGITO-3571)] - [DMN Designer] Multiple DRDs support - Information requirements are duplicated into the DMN XML


# 0.7.0 (alpha)

## New features

VS Code
*   [[KOGITO-1517](https://issues.redhat.com/browse/KOGITO-1517)] - Improve accessibility on file with unsupported extension error
*   [[KOGITO-2210](https://issues.redhat.com/browse/KOGITO-2210)] - Define Integration API for Java Backend Services
*   [[KOGITO-2863](https://issues.redhat.com/browse/KOGITO-2863)] - Think about removing a default Envelope from `embedded-editor`
*   [[KOGITO-2887](https://issues.redhat.com/browse/KOGITO-2887)] - Implement a sample service
*   [[KOGITO-3042](https://issues.redhat.com/browse/KOGITO-3042)] - Replace MessageBusClient with proxified version of ApiToConsume
*   [[KOGITO-3043](https://issues.redhat.com/browse/KOGITO-3043)] - Provide locale information in a way that GWT can read
*   [[KOGITO-3056](https://issues.redhat.com/browse/KOGITO-3056)] - Move I18nService and and its Envelope/Channel APIs to its own module
*   [[KOGITO-3057](https://issues.redhat.com/browse/KOGITO-3057)] - Add a initialLocale prop on I18nDictionariesProvider
*   [[KOGITO-3096](https://issues.redhat.com/browse/KOGITO-3096)] - CI for kogito-tooling-java
*   [[KOGITO-3100](https://issues.redhat.com/browse/KOGITO-3100)] - Documentation for backend services
*   [[KOGITO-2984](https://issues.redhat.com/browse/KOGITO-2984)] - Use i18n dictionaries on VS Code extension backend
*   [[KOGITO-3205](https://issues.redhat.com/browse/KOGITO-3205)] - Create test runner service running on the backend infra

Editors
*   [[KOGITO-543](https://issues.redhat.com/browse/KOGITO-543)] - DMN - Read Only mode
*   [[KOGITO-1716](https://issues.redhat.com/browse/KOGITO-1716)] - [Test Scenario Editor] Show warning if user opens scesim file for rule based test scenario in VSCode
*   [[KOGITO-2674](https://issues.redhat.com/browse/KOGITO-2674)] - [DMN Designer] Multiple DRDs support - Context menu component
*   [[KOGITO-2675](https://issues.redhat.com/browse/KOGITO-2675)] - [DMN Designer] Multiple DRDs support - Context menu - Show the DRD clickable icon when node is selected
*   [[KOGITO-2761](https://issues.redhat.com/browse/KOGITO-2761)] - [DMN Designer] PMML support - Resource content API integration
*   [[KOGITO-2895](https://issues.redhat.com/browse/KOGITO-2895)] - [DMN Designer] PMML support - PMML Marshaller integration
*   [[KOGITO-2896](https://issues.redhat.com/browse/KOGITO-2896)] - [DMN Designer] PMML support - Using a linked PMML inside the editor
*   [[KOGITO-3022](https://issues.redhat.com/browse/KOGITO-3022)] - [DMN Designer] Multiple DRDs support - Context menu - When multiple nodes are selected, showing context menu with right click

## Fixed issues

VS Code
*   [[KOGITO-3313](https://issues.redhat.com/browse/KOGITO-3313)] - Fix on filename change
*   [[KOGITO-3326](https://issues.redhat.com/browse/KOGITO-3326)] - Enable EmbeddedEditor to support an update on the StateControl instance
*   [[KOGITO-3417](https://issues.redhat.com/browse/KOGITO-3417)] - EditorEnvelopeView reference should be get with a function to be updated
*   [[KOGITO-3314](https://issues.redhat.com/browse/KOGITO-3314)] - Broken link on popup due to circular dependency issue

Editors
*   [[KOGITO-2060](https://issues.redhat.com/browse/KOGITO-2060)] - [Test Scenario Editor] Order of facts of Test Tools in Kogito and BC is different
*   [[KOGITO-3153](https://issues.redhat.com/browse/KOGITO-3153)] - [DMN Designer] PMML support - run via quarkus
*   [[KOGITO-3155](https://issues.redhat.com/browse/KOGITO-3155)] - [SCESIM] Test DMN model including PMML
*   [[KOGITO-3504](https://issues.redhat.com/browse/KOGITO-3504)] - Interrogation mark cannot be used on new inline editor

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
