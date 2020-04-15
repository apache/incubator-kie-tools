BPMN and DMN Editors
--

## Release notes - 0.3.1 (alpha)

We are happy to announce a fresh new Kogito Tooling release! On this release, we did a lot of improvements and bug fixes. 

**NOTE:** Due to some API changes on VSCode Custom Editor API, Undo/Redo and Dirty Indicator are broken on VS Code 1.44.x. We expect that this issue will be solved hopefully on the next VS Code release, in about a month from now. We are tracking the progress of this issue on [KOGITO-1835](https://issues.redhat.com/browse/KOGITO-1835).

Included on this release:
 
#### VS Code Improvements
- [KOGITO-1778](https://issues.redhat.com/browse/KOGITO-1778) - Adapt to new VSCode API (1.44.0)
 
#### BPMN Improvements/Bug Fixes
- [KOGITO-1354](https://issues.redhat.com/browse/KOGITO-1354) - Kogito Use realfile name provided by Appformer
- [KOGITO-988](https://issues.redhat.com/browse/KOGITO-988) - Stunner - [VSCode] Support for process metadata attributes
- [KOGITO-1020](https://issues.redhat.com/browse/KOGITO-1020) - VSCode - BPMN modeler support variables tagging
- [KOGITO-279](https://issues.redhat.com/browse/KOGITO-279) - Stunner - you should to deactivate property to save it's value
- [KOGITO-420](https://issues.redhat.com/browse/KOGITO-420) - Text in BPMN doesn't save when there are no changes in the diagram
- [KOGITO-987](https://issues.redhat.com/browse/KOGITO-987) - VsCode editor - process id not saved/updated
- [KOGITO-1183](https://issues.redhat.com/browse/KOGITO-1183) - BPMN2 Imports not working in Kogito
- [KOGITO-1426](https://issues.redhat.com/browse/KOGITO-1426) - Re-enable Resource Content API
- [KOGITO-1505](https://issues.redhat.com/browse/KOGITO-1505) - Stunner - CDATA block missing
- [KOGITO-1529](https://issues.redhat.com/browse/KOGITO-1529) - BPMN Editor - Cannot save a process once a service tasks' attributes are not properly set
- [KOGITO-1607](https://issues.redhat.com/browse/KOGITO-1607) - Mixed bpmn and bpmn2 files in kogito examples
- [KOGITO-1615](https://issues.redhat.com/browse/KOGITO-1615) - Unable to open BPMN examples in kogito bpmn designer
- [KOGITO-1692](https://issues.redhat.com/browse/KOGITO-1692) - New custom data type can not be added into onboarding-example
- [KOGITO-1693](https://issues.redhat.com/browse/KOGITO-1693) - Deprecated Float constructor in onboarding-example
- [KOGITO-1694](https://issues.redhat.com/browse/KOGITO-1694) - Inappropriate modules description - onboarding-example
- [KOGITO-1696](https://issues.redhat.com/browse/KOGITO-1696) - Port conflicts in onboarding-example
 
 #### DMN Improvements/Bug Fixes
- [KOGITO-1752](https://issues.redhat.com/browse/KOGITO-1752) - [DMN Designer] Data Types - Drag and Drop does work as expected on Kogito webapps
