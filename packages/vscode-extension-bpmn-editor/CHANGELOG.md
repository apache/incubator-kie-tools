# 0.8.2 (alpha)

### New features:

#### Editors
*   [[KOGITO-3853](https://issues.redhat.com/browse/KOGITO-3853)] -  [DMN editor] Move the "structure" option to top of the Data Type drop-down
*   [[KOGITO-3885](https://issues.redhat.com/browse/KOGITO-3885)] -  [DMN editor] Show parameters list for Decision Services

### Fixed issues:

#### Editors
* [[KOGITO-4104](https://issues.redhat.com/browse/KOGITO-4104)] -  [DMN editor] Rename "Dismiss" to "Skip tour"
* [[KOGITO-1429](https://issues.redhat.com/browse/KOGITO-1429)]  - DefaultXmlFormatter: Should return plain text if formatting fails
* [[KOGITO-1677](https://issues.redhat.com/browse/KOGITO-1677)]  - [DMN editor] User cannot open DMN editor if it was previously saved in workspace
* [[KOGITO-2187](https://issues.redhat.com/browse/KOGITO-2187)]  - DMN editor when moving DS output decision becomes encapsulated
* [[KOGITO-2515](https://issues.redhat.com/browse/KOGITO-2515)]  - DMN Editor decision service wrong layout
* [[KOGITO-2712](https://issues.redhat.com/browse/KOGITO-2712)] - [DMN editor] VS Code Included models self reference
* [[KOGITO-3151](https://issues.redhat.com/browse/KOGITO-3151)]  - [DMN editor] Copied value is pasted twice
* [[KOGITO-3476](https://issues.redhat.com/browse/KOGITO-3476)]  - DMN decision table "Unable to resolve type reference 'UNDEFINED'" in simple data types
* [[KOGITO-4124](https://issues.redhat.com/browse/KOGITO-4124)]  - Error when adding constraints to a data type
* [[KOGITO-4165](https://issues.redhat.com/browse/KOGITO-4165)]  - [DMN editor] 'continue' vs 'Continue'


# 0.8.1 (alpha)

## Fixed issues
VS Code
*   [[KOGITO-2629](https://issues.redhat.com/browse/KOGITO-2629)] - [VSCode] Undo/redo command don't fire for webviews if used from command palette
*   [[KOGITO-3808](https://issues.redhat.com/browse/KOGITO-3808)] - Fix Keyboard Shortcuts modal title on VS Code dark theme
*   [[KOGITO-3884](https://issues.redhat.com/browse/KOGITO-3884)] - Save SVG file using the kieserver naming convention
*   [[KOGITO-3901](https://issues.redhat.com/browse/KOGITO-3901)] - Cannot Include DMNs on VS Code- Kogito Bundle on Windows
*   [[KOGITO-3348](https://issues.redhat.com/browse/KOGITO-3348)] - Fix Open SVG popup to not open SVG when clicking the X button

# 0.8.0 (alpha)

No changes.

# 0.7.1 (alpha)

## New features

Editors
*   [[KOGITO-1886](https://issues.redhat.com/browse/KOGITO-1886)] - [BPMN] Reuse Data Types across the process


# 0.7.0 (alpha)

## New features

VS Code
*   [[KOGITO-1508](https://issues.redhat.com/browse/KOGITO-1508)] - Implement selenium test for kogito-bpmn in community
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

## Fixed issues

VS Code
*   [[KOGITO-3313](https://issues.redhat.com/browse/KOGITO-3313)] - Fix on filename change
*   [[KOGITO-3326](https://issues.redhat.com/browse/KOGITO-3326)] - Enable EmbeddedEditor to support an update on the StateControl instance
*   [[KOGITO-3417](https://issues.redhat.com/browse/KOGITO-3417)] - EditorEnvelopeView reference should be get with a function to be updated
*   [[KOGITO-3314](https://issues.redhat.com/browse/KOGITO-3314)] - Broken link on popup due to circular dependency issue

# 0.6.1 (alpha)

## New features
VS Code
*   [KOGITO-3089](https://issues.redhat.com/browse/KOGITO-3089) - Add button on VS Code to generate SVG for BPMN and DMN editors

Editors
*   [KOGITO-2644](https://issues.redhat.com/browse/KOGITO-2644) - Add auto start property for adhoc subprocess

## Fixed issues
VS Code
*   [KOGITO-2579](https://issues.redhat.com/browse/KOGITO-2579) - Custom Editor VS Code issues on 1.46 version (probably fixed on 1.47)
*   [KOGITO-1689](https://issues.redhat.com/browse/KOGITO-1689) - Save when multiple assets opened
*   [KOGITO-2134](https://issues.redhat.com/browse/KOGITO-2134) - [VSCode] Allow custom editors to hook into Edit menu actions
*   [KOGITO-2135](https://issues.redhat.com/browse/KOGITO-2135) - [VSCode] Custom editor does not open properly
*   [KOGITO-1980](https://issues.redhat.com/browse/KOGITO-1980) - Update labels from `VSCode` to `VS Code`

Editors
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

## Fixed issues
VS Code
- [KOGITO-745](https://issues.redhat.com/browse/KOGITO-745) Command Y to undo isn't mapped
- [KOGITO-2490](https://issues.redhat.com/browse/KOGITO-2490) Shortcuts should not be triggered when typing on text inputs
- [KOGITO-2493](https://issues.redhat.com/browse/KOGITO-2493) Update and use @types/vscode
- [KOGITO-2580](https://issues.redhat.com/browse/KOGITO-2580) Add noImplicitAny property back into tsconfig.json
- [KOGITO-2581](https://issues.redhat.com/browse/KOGITO-2581) Delete isDirty method on core-api

Editors
- [KOGITO-1196](https://issues.redhat.com/browse/KOGITO-1196) Editor goes back horizontally when diagram is bigger than default
- [KOGITO-1661](https://issues.redhat.com/browse/KOGITO-1661) BPMN Examples have 'null' in process type property
- [KOGITO-1997](https://issues.redhat.com/browse/KOGITO-1997) Stunner - Process metadata attribute value should be a free string
- [KOGITO-2166](https://issues.redhat.com/browse/KOGITO-2166) VS Code editor - Package property: default is not a valid Java package name
