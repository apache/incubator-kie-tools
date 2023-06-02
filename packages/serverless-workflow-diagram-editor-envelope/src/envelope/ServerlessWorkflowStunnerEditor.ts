/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { ServerlessWorkflowDiagramEditorEnvelopeApi } from "../api";
import { Node } from "../api/StunnerEditorEnvelopeAPI";
import { SwfStunnerEditorAPI, SwfStunnerEditorCanvas, SwfStunnerEditoSession } from "../api/SwfStunnerEditorAPI";

export class SwfStunnerEditor implements SwfStunnerEditorAPI {
  session: SwfStunnerEditoSession;
  canvas: SwfStunnerEditorCanvas;

  constructor(envelopeApi: MessageBusClientApi<ServerlessWorkflowDiagramEditorEnvelopeApi>) {
    this.session = {
      getAllNodesUUID: () => {
        return envelopeApi.requests.editor_session_getAllNodesUUID();
      },
      getEdgeByUUID: (uuid: string) => {
        return envelopeApi.requests.editor_session_getEdgeByUUID(uuid);
      },
      getNodeByUUID: (uuid: string) => {
        return envelopeApi.requests.editor_session_getNodeByUUID(uuid);
      },
      getDefinitionByElementUUID: (uuid: string) => {
        return envelopeApi.requests.editor_session_getDefinitionByElementUUID(uuid);
      },
      getNodeByName: (name: string) => {
        return envelopeApi.requests.editor_session_getNodeByName(name);
      },
      getNodeName: (node: Node) => {
        return envelopeApi.requests.editor_session_getNodeName(node);
      },
      getSelectedElementUUID: () => {
        return envelopeApi.requests.editor_session_getSelectedElementUUID();
      },
      getSelectedNode: () => {
        return envelopeApi.requests.editor_session_getSelectedNode();
      },
      getSelectedEdge: () => {
        return envelopeApi.requests.editor_session_getSelectedEdge();
      },
      getSelectedDefinition: () => {
        return envelopeApi.requests.editor_session_getSelectedDefinition();
      },
      selectByUUID: (uuid: string) => {
        envelopeApi.requests.editor_session_selectByUUID(uuid);
        return Promise.resolve();
      },
      selectByName: (name: string) => {
        envelopeApi.requests.editor_session_selectByName(name);
        return Promise.resolve();
      },
      clearSelection: () => {
        envelopeApi.requests.editor_session_clearSelection();
        return Promise.resolve();
      },
    };
    this.canvas = {
      getShapeIds: () => {
        return envelopeApi.requests.editor_canvas_getShapeIds();
      },
      getBackgroundColor: (uuid: string) => {
        return envelopeApi.requests.editor_canvas_getBackgroundColor(uuid);
      },
      setBackgroundColor: (uuid: string, color: string) => {
        envelopeApi.requests.editor_canvas_setBackgroundColor(uuid, color);
        return Promise.resolve();
      },
      getBorderColor: (uuid: string) => {
        return envelopeApi.requests.editor_canvas_getBorderColor(uuid);
      },
      setBorderColor: (uuid: string, color: string) => {
        envelopeApi.requests.editor_canvas_setBorderColor(uuid, color);
        return Promise.resolve();
      },
      getLocation: (uuid: string) => {
        return envelopeApi.requests.editor_canvas_getLocation(uuid);
      },
      getAbsoluteLocation: (uuid: string) => {
        return envelopeApi.requests.editor_canvas_getAbsoluteLocation(uuid);
      },
      getDimensions: (uuid: string) => {
        return envelopeApi.requests.editor_canvas_getDimensions(uuid);
      },
      center: (uuid: string) => {
        envelopeApi.requests.editor_canvas_center(uuid);
        return Promise.resolve();
      },
      draw: () => {
        envelopeApi.requests.editor_canvas_draw();
        return Promise.resolve();
      },
    };
  }
}
