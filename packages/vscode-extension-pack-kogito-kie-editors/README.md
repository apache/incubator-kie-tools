BPMN, DMN and Test Scenario Editors
--

## Release notes - 0.4.2 (alpha)

We are happy to announce a fresh new Kogito Tooling release! 🎉

On this release, we did a lot of improvements and bug fixes. The highlight of this release is the inclusion of the alpha version of our newest editor for Test Scenarios (`.scesim` extension).

**NOTE**: This release only works on VS Code 1.45.0 and later.

**NOTE**: The Undo/Redo and Dirty Files Detection mechanism are fixed on VS Code 1.45.0, with the caveat that Undo/Redo operations only work via keyboard shortcuts. We hope that this will be fixed on VS Code 1.46.0 release. See tracking of this issue [here](https://github.com/microsoft/vscode/issues/90110).


New features
---

VS Code

- [KOGITO-1863](https://issues.redhat.com/browse/KOGITO-1863) - Adapt to new VSCode API 1.45.0
- [KOGITO-1837](https://issues.redhat.com/browse/KOGITO-1837) - Expose viewType and getPreviewCommandId on vscode-extension index.ts
- [KOGITO-1970](https://issues.redhat.com/browse/KOGITO-1970) - Implement Undo/Redo
- [KOGITO-1971](https://issues.redhat.com/browse/KOGITO-1971) - Implement SaveAs
- [KOGITO-1972](https://issues.redhat.com/browse/KOGITO-1972) - Implement Revert
- [KOGITO-1973](https://issues.redhat.com/browse/KOGITO-1973) - Implement async Save
- [KOGITO-1974](https://issues.redhat.com/browse/KOGITO-1974) - Implement Backup

Editor

- [KOGITO-777](https://issues.redhat.com/browse/KOGITO-777) - Include SCESIM editor in Kogito Tools
- [KOGITO-1609](https://issues.redhat.com/browse/KOGITO-1609) - [SCESIM Editor] Remove Test Report & Coverage Report docks in Kogito
- [KOGITO-1650](https://issues.redhat.com/browse/KOGITO-1650) - Show warning if user creates SCESIM using malformed DMN
- [KOGITO-1713](https://issues.redhat.com/browse/KOGITO-1713) - [Scesim Editor] Allow user to distinguish between dmn files in different packages
- [KOGITO-2000](https://issues.redhat.com/browse/KOGITO-2000) - [Scesim Editor] Filter dmn files from target folder
- [KOGITO-657](https://issues.redhat.com/browse/KOGITO-657) - [DMN Designer] Client-side FEEL variable validation


Fixed issues
---
 
VS Code

- [KOGITO-1801](https://issues.redhat.com/browse/KOGITO-1801) - Dirty state of editor on consequent save operation
- [KOGITO-1835](https://issues.redhat.com/browse/KOGITO-1835) - State Control API stop working on VSCode 1.44.x

Editors

- [KOGITO-1566](https://issues.redhat.com/browse/KOGITO-1566) - [SCESIM Editor] DMN Type with nested objects doesn’t works correctly
- [KOGITO-1658](https://issues.redhat.com/browse/KOGITO-1658) - Broken layout of BPMN processes from examples repository
- [KOGITO-1684](https://issues.redhat.com/browse/KOGITO-1684) - Deleted file with unsaved changes is not closed
- [KOGITO-1687](https://issues.redhat.com/browse/KOGITO-1687) - DMN invalid data type names are not reported
- [KOGITO-1714](https://issues.redhat.com/browse/KOGITO-1714) - [Scesim Editor]Create test scenario dialog do not shows dmn files from subdirectories in VScode
- [KOGITO-1717](https://issues.redhat.com/browse/KOGITO-1717) - [Scesim Editor] The order in dropdown differs from the order in file explorer of VS code
