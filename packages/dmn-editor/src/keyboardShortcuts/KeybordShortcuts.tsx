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

import * as RF from "reactflow";
import * as React from "react";
import { useContext, useRef } from "react";
import {
  DMN_EDITOR_DIAGRAM_CLIPBOARD_MIME_TYPE,
  DmnEditorDiagramClipboard,
  buildClipboardFromDiagram,
  getClipboard,
} from "../clipboard/Clipboard";
import { getNewDmnIdRandomizer } from "../idRandomizer/dmnIdRandomizer";
import { NodeNature, nodeNatures } from "../mutations/NodeNature";
import { addOrGetDrd } from "../mutations/addOrGetDrd";
import { addStandaloneNode } from "../mutations/addStandaloneNode";
import { EdgeDeletionMode, deleteEdge } from "../mutations/deleteEdge";
import { NodeDeletionMode, canRemoveNodeFromDrdOnly, deleteNode } from "../mutations/deleteNode";
import { repopulateInputDataAndDecisionsOnAllDecisionServices } from "../mutations/repopulateInputDataAndDecisionsOnDecisionService";
import { useDmnEditorStoreApi } from "../store/StoreContext";
import { DmnDiagramEdgeData } from "../diagram/edges/Edges";
import { CONTAINER_NODES_DESIRABLE_PADDING, getBounds } from "../diagram/maths/DmnMaths";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { DmnDiagramNodeData } from "../diagram/nodes/Nodes";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { NodeType } from "../diagram/connections/graphStructure";
import { buildXmlHref, parseXmlHref } from "../xml/xmlHrefs";
import { DEFAULT_VIEWPORT } from "../diagram/Diagram";

export interface KeyboardShortcuts {
  hideFromDrd: () => Promise<void>;
  toggleHierarchyHighlight: () => Promise<void>;
  togglePropertiesPanel: () => Promise<void>;
  createGroup: () => Promise<void>;
  selectAll: () => Promise<void>;
  panDown: () => Promise<void>;
  panUp: () => Promise<void>;
  paste: () => Promise<void>;
  copy: () => Promise<void>;
  cut: () => Promise<void>;
  cancelAction: () => Promise<void>;
  focusOnBounds: () => Promise<void>;
  resetPosition: () => Promise<void>;
}

const KeyboardShortcutsContext = React.createContext<{
  keyboardShortcutsRef: React.MutableRefObject<KeyboardShortcuts | undefined>;
}>({} as any);

export function useKeyboardShortcuts() {
  return useContext(KeyboardShortcutsContext);
}

export function KeyboardShortcutsProvider(props: React.PropsWithChildren<{}>) {
  const keyboardShortcutsRef = useRef<KeyboardShortcuts>({
    hideFromDrd: async () => {},
    toggleHierarchyHighlight: async () => {},
    togglePropertiesPanel: async () => {},
    createGroup: async () => {},
    selectAll: async () => {},
    panDown: async () => {},
    panUp: async () => {},
    paste: async () => {},
    copy: async () => {},
    cut: async () => {},
    cancelAction: async () => {},
    focusOnBounds: async () => {},
    resetPosition: async () => {},
  });

  return (
    <KeyboardShortcutsContext.Provider value={{ keyboardShortcutsRef: keyboardShortcutsRef }}>
      {props.children}
    </KeyboardShortcutsContext.Provider>
  );
}
