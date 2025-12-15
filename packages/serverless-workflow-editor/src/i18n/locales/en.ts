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

import { en as en_common } from "@kie-tools/i18n-common-dictionary";
import { SwfEditorI18n } from "../SwfEditorI18n";

export const en: SwfEditorI18n = {
  ...en_common,
  autoLayout: "Autolayout (beta)",
  close: "Close",
  none: "None",
  cancel: "Cancel",
  nodes: {
    view: "View",
    edit: "Edit",
    output: "OUTPUT",
    doubleClickToName: "Double-click to name",
    addCompensationTransitionEdge: "Add Compensation Transition",
    addDataConditionTransitionEdge: "Add Data Condition Transition",
    addDefaultConditionTransitionEdge: "Add Default Transition",
    addErrorTransitionEdge: "Add Error Transition",
    addEventConditionTransitionEdge: "Add Event Condition Transition",
    addTransitionEdge: "Add Transition",
    addCallbackStateNode: "Add Callback State",
    addEventStateNode: "Add Event State",
    addForeachStateNode: "Add ForEach State",
    addInjectStateNode: "Add Inject State",
    addOperationStateNode: "Add Operation State",
    addParallelStateNode: "Add Parallel State",
    addSleepStateNode: "Add Sleep State",
    addSwitchStateNode: "Add Switch State",
    emptyDiagram: "Empty Diagram",
    swfDiagramEmpty: "This SWF Diagram is empty",
    diagramHasNodesOrOpenAnotherFile: "Make sure the workflow has nodes or try opening another file",
    startByDraggingNodes: "Start by dragging nodes from the Palette",
    overlays: "Overlays",
    nodesSelected: (selectedNodesCount: number) => `${selectedNodesCount} nodes selected`,
    edgesSelected: (selectedEdgesCount: number) => `${selectedEdgesCount} edges selected`,
    nodeSelected: (nodeCount: number) => `${nodeCount} node`,
    edgeSelected: (edgeCount: number) => `${edgeCount} edge`,
    nodes: (nodeCount: number) => `${nodeCount} nodes`,
    edges: (edgeCount: number) => `${edgeCount} edges`,
    selected: "selected",
    addingNodesMakingChanges: "Adding nodes or making changes to the Diagram will automatically create a DRD for you.",
    removeDrd: "Remove DRD",
    remove: "Remove",
    unknown: "unknown",
    empty: "<Empty>",
  },
  on: "on",
  off: "off",
  overlaysPanel: {
    snapping: "Snapping",
    horizontal: "Horizontal",
    vertical: "Vertical",
    highlightSelectedNode: "Highlight selected node(s) hierarchy",
  },
  swfEditor: {
    editor: "Editor",
    unexpectedErrorOccured: "An unexpected error happened",
    reportBug:
      "This is a bug. Please consider reporting it so the SWF Editor can continue improving. See the details below.",
    tryUndoingLastAction: "Try undoing last action",
    fileAnIssue: "File an issue...",
    copy: "Copy",
    copied: "Copied",
  },
};
