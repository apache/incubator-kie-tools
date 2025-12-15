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

import { ReferenceDictionary } from "@kie-tools-core/i18n/dist/core";
import { CommonI18n } from "@kie-tools/i18n-common-dictionary";

interface SwfEditorDictionary
  extends ReferenceDictionary<{
    autoLayout: string;
    close: string;
    none: string;
    cancel: string;
    nodes: {
      view: string;
      edit: string;
      output: string;
      doubleClickToName: string;
      addCompensationTransitionEdge: string;
      addDataConditionTransitionEdge: string;
      addDefaultConditionTransitionEdge: string;
      addErrorTransitionEdge: string;
      addEventConditionTransitionEdge: string;
      addTransitionEdge: string;
      addCallbackStateNode: string;
      addEventStateNode: string;
      addForeachStateNode: string;
      addInjectStateNode: string;
      addOperationStateNode: string;
      addParallelStateNode: string;
      addSleepStateNode: string;
      addSwitchStateNode: string;
      emptyDiagram: string;
      swfDiagramEmpty: string;
      diagramHasNodesOrOpenAnotherFile: string;
      startByDraggingNodes: string;
      overlays: string;
      nodesSelected: (selectedNodesCount: number) => string;
      edgesSelected: (selectedEdgesCount: number) => string;
      oneNode: string;
      oneEdge: string;
      nodes: (nodeCount: number) => string;
      edges: (edgeCount: number) => string;
      selected: string;
      addingNodesMakingChanges: string;
      remove: string;
      unknown: string;
      empty: string;
    };
    on: string;
    off: string;
    overlaysPanel: {
      snapping: string;
      horizontal: string;
      vertical: string;
      highlightSelectedNode: string;
    };
    swfEditor: {
      editor: string;
      unexpectedErrorOccured: string;
      reportBug: string;
      tryUndoingLastAction: string;
      fileAnIssue: string;
      copy: string;
      copied: string;
    };
  }> {}

export interface SwfEditorI18n extends SwfEditorDictionary, CommonI18n {}
