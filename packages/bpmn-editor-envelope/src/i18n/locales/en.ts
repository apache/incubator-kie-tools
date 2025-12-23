/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * License); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing;
 * software distributed under the License is distributed on an
 * AS IS BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { BpmnEditorEnvelopeI18n } from "..";
import { en as en_common } from "@kie-tools/i18n-common-dictionary";

export const en: BpmnEditorEnvelopeI18n = {
  ...en_common,
  unselect: "Unselect",
  deleteSelection: "Delete selection",
  selectDeselectAll: "Select/Deselect all",
  createGroupWrappingSelection: "Create group wrapping selection",
  hideFromDrd: "Hide from DRD",
  copyNodes: "Copy nodes",
  cutNodes: "Cut nodes",
  pasteNodes: "Paste nodes",
  openClosePropertiesPanel: "Open/Close properties panel",
  toggleHierarchyHighlights: "Toggle hierarchy highlights",
  selectionUp: "Move selection up",
  selectionDown: "Move selection down",
  selectionRight: "Move selection right",
  selectionLeft: "Move selection left",
  selectionUpBigDistance: "Move selection up a big distance",
  selectionDownBigDistance: "Move selection down a big distance",
  selectionLeftBigDistance: "Move selection left a big distance",
  selectionRightBigDistance: "Move selection right a big distance",
  focusOnSelection: "Focus on selection",
  resetPositionToOrigin: "Reset position to origin",
  rightMouseButton: "Right Mouse Button",
  holdAndDragtoPan: "Hold and drag to Pan",
  holdAndScrollToZoomInOut: "Hold and scroll to zoom in/out",
  holdAndScrollToNavigateHorizontally: "Hold and scroll to navigate horizontally",
  appendTasknode: "Append Task node",
  appendGatewayNode: "Append Gateway node",
  appendIntermediateCatchEventNode: "Append Intermediate Catch event node",
  appendIntermediateThrowEventNode: "Append Intermediate Throw event node",
  appendEndEventNode: "Append End event node",
  appendTextAnnotationNode: "Append Text Annotation node",
  milestone: "Milestone",
  flexibleProcesses: "Flexible processes",
  unableToOpenFile: "Unable to open file.",
  errorMessage: "Error details: ",
};
