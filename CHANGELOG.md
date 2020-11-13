# 0.8.0

We are happy to announce a fresh new Kogito Tooling release! 🎉

On this release, we did a lot of improvements and bug fixes.

**NOTE:** This release only works on VS Code 1.46.0 and later.


## Included on this release:

### New features:

#### Infrastructure
*   [[KOGITO-3449](https://issues.redhat.com/browse/KOGITO-3449)] - Add an option to update the current opened Gist on the Online Editor

### Fixed issues in Kogito:

#### Infrastructure
*   [[KOGITO-3709](https://issues.redhat.com/browse/KOGITO-3709)] - Creating GitHub gist with no token setup requires the users to click on "Gist it!" twice
*   [[KOGITO-3347](https://issues.redhat.com/browse/KOGITO-3347)] - The input field on Gist modal is being marked as invalid with a valid token
*   [[KOGITO-3705](https://issues.redhat.com/browse/KOGITO-3705)] - Cannot open files on private repos using the Chrome Extension
*   [[KOGITO-3820](https://issues.redhat.com/browse/KOGITO-3820)] - Fix open from source Gist URLs double encoded

#### Editors
*   [[KOGITO-1489](https://issues.redhat.com/browse/KOGITO-1489)] - Editor is broken after See as source during loading (Chrome Extension)
*   [[KOGITO-3192](https://issues.redhat.com/browse/KOGITO-3192)] - [DMN Designer] Multiple DRDs support - The undo/reado are lost when user changes between diagrams
*   [[KOGITO-3364](https://issues.redhat.com/browse/KOGITO-3364)] - [DMN Designer] Multiple DRDs support - When a node is deleted in the DRG, undo/redo operations do not work properly
*   [[KOGITO-3366](https://issues.redhat.com/browse/KOGITO-3366)] - [DMN Designer] Boxed Expression - Decision Tables - Users lost constraints when they change the focus
