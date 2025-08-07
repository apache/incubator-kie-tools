/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { DmnEditorEnvelopeI18n } from "..";
import { en as en_common } from "@kie-tools/i18n-common-dictionary";

export const en: DmnEditorEnvelopeI18n = {
  ...en_common,
  keyboardShortCuts: {
    ctrlC: "Ctrl+C",
    ctrlX: "Ctrl+X",
    ctrlV: "Ctrl+V",
    ctrl: "Ctrl",
    escape: "Escape",
    backspace: "Backspace",
    delete: "Delete",
    up: "Up",
    down: "Down",
    left: "Left",
    right: "Right",
    a: "A",
    x: "X",
    g: "G",
    i: "I",
    h: "H",
    b: "B",
    shiftUp: "Shift + Up",
    shiftDown: "Shift + Down",
    shiftLeft: "Shift + Left",
    shiftRight: "Shift + Right",
    space: "Space",
    shift: "Shift",
  },
  dmnEditor: "DMN Editor",
  editOrUnselect: "Edit | Unselect",
  editOrDeleteSelection: "Edit | Delete selection",
  editOrSelectOrDeselectAll: "Edit | Select/Deselect all",
  editCreateGroupWrappingSelection: "Edit | Create group wrapping selection",
  editOrHideGroup: "Edit | Hide from DRD",
  editOrCopyNodes: "Edit | Copy nodes",
  editOrCutNodes: "Edit | Cut nodes",
  editOrPasteNodes: "Edit | Paste nodes",
  miscOpenClosePropertiesPanel: "Misc | Open/Close properties panel",
  miscToggleHierarchyHighlights: "Misc | Toggle hierarchy highlights",
  moveSelectionUp: "Move | Move selection up",
  moveSelectionDown: "Move | Move selection down",
  moveSelectionRight: "Move | Move selection right",
  moveSelectionLeft: "Move | Move selection left",
  moveSelectionUpBigDistance: "Move | Move selection up a big distance",
  moveSelectionDownBigDistance: "Move | Move selection down a big distance",
  moveSelectionLeftBigDistance: "Move | Move selection left a big distance",
  moveSelectionRightBigDistance: "Move | Move selection right a big distance",
  navigateFocusOnSelection: "Navigate | Focus on selection",
  navigateResetPositionToOrigin: "Navigate | Reset position to origin",
  rightMouseButton: "Right Mouse Button",
  navigateHoldAndDragtoPan: "Navigate | Hold and drag to Pan",
  navigateHoldAndScrollToZoomInOut: "Navigate | Hold and scroll to zoom in/out",
  navigateHoldAndScrollToNavigateHorizontally: "Navigate | Hold and scroll to navigate horizontally",
};
