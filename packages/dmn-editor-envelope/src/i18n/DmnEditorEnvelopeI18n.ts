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

import { ReferenceDictionary } from "../../../i18n/dist/core";
import { CommonI18n } from "@kie-tools/i18n-common-dictionary";

interface DmnEditorEnvelopeDictionary extends ReferenceDictionary {
  dmnEditor: string;
  keyboardShortCuts: {
    ctrlC: string;
    ctrlX: string;
    ctrlV: string;
    ctrl: string;
    escape: string;
    backspace: string;
    delete: string;
    up: string;
    down: string;
    left: string;
    right: string;
    a: string;
    x: string;
    g: string;
    i: string;
    h: string;
    b: string;
    shiftUp: string;
    shiftDown: string;
    shiftLeft: string;
    shiftRight: string;
    space: string;
    shift: string;
  };
  editOrUnselect: string;
  editOrDeleteSelection: string;
  editOrSelectOrDeselectAll: string;
  editCreateGroupWrappingSelection: string;
  editOrHideGroup: string;
  editOrCopyNodes: string;
  editOrCutNodes: string;
  editOrPasteNodes: string;
  miscOpenClosePropertiesPanel: string;
  miscToggleHierarchyHighlights: string;
  moveSelectionUp: string;
  moveSelectionDown: string;
  moveSelectionLeft: string;
  moveSelectionRight: string;
  moveSelectionUpBigDistance: string;
  moveSelectionDownBigDistance: string;
  moveSelectionLeftBigDistance: string;
  moveSelectionRightBigDistance: string;
  navigateFocusOnSelection: string;
  navigateResetPositionToOrigin: string;
  rightMouseButton: string;
  navigateHoldAndDragtoPan: string;
  navigateHoldAndScrollToZoomInOut: string;
  navigateHoldAndScrollToNavigateHorizontally: string;
}

export interface DmnEditorEnvelopeI18n extends DmnEditorEnvelopeDictionary, CommonI18n {}
