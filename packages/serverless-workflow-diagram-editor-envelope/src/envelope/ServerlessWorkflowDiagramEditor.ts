/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { GwtEditorWrapper } from "@kie-tools/kie-bc-editors/dist/common";
import { StunnerCanvas, StunnerEditor, StunnerNode, StunnerSession } from "../api/StunnerAPI";

interface CustomWindow extends Window {
  canvas: StunnerCanvas;
  editor: StunnerEditor;
}

declare let window: CustomWindow;

export interface ServerlessWorkflowDiagramEditor extends GwtEditorWrapper, StunnerCanvas, StunnerSession {
  myServerlessWorkflowDiagramMethod(): string;
}

export class ServerlessWorkflowDiagramEditorImpl extends GwtEditorWrapper implements ServerlessWorkflowDiagramEditor {
  public myServerlessWorkflowDiagramMethod() {
    return "serverless-workflow-diagram-specific--configured";
  }

  public getGraph() {
    return window.editor.session.getGraph();
  }

  public getEdgeByUUID(uuid: string) {
    return window.editor.session.getEdgeByUUID(uuid);
  }

  public getNodeByUUID(uuid: string) {
    return window.editor.session.getNodeByUUID(uuid);
  }

  public getDefinitionByElementUUID(uuid: string) {
    return window.editor.session.getDefinitionByElementUUID(uuid);
  }

  public getNodeByName(name: string) {
    return window.editor.session.getNodeByName(name);
  }

  public getNodeName(node: StunnerNode) {
    return window.editor.session.getNodeName(node);
  }

  public getDefinitionId(bean: Object) {
    return window.editor.session.getDefinitionId(bean);
  }

  public getDefinitionName(bean: Object) {
    return window.editor.session.getDefinitionName(bean);
  }

  public getSelectedElementUUID() {
    return window.editor.session.getSelectedElementUUID();
  }

  public getSelectedNode() {
    return window.editor.session.getSelectedNode();
  }

  public getSelectedEdge() {
    return window.editor.session.getSelectedEdge();
  }

  public getSelectedDefinition() {
    return window.editor.session.getSelectedDefinition();
  }

  public selectByUUID(uuid: string) {
    window.editor.session.selectByUUID(uuid);
  }

  public selectByName(name: string) {
    window.editor.session.selectByName(name);
  }

  public clearSelection() {
    window.editor.session.clearSelection();
  }

  public getShapeIds() {
    return window.editor.canvas.getShapeIds();
  }

  public getBackgroundColor(uuid: string) {
    return window.editor.canvas.getBackgroundColor(uuid);
  }

  public setBackgroundColor(uuid: string, backgroundColor: string) {
    window.editor.canvas.setBackgroundColor(uuid, backgroundColor);
  }

  public getBorderColor(uuid: string) {
    return window.editor.canvas.getBorderColor(uuid);
  }

  public setBorderColor(uuid: string, borderColor: string) {
    window.editor.canvas.setBorderColor(uuid, borderColor);
  }

  public getLocation(uuid: string) {
    return window.editor.canvas.getLocation(uuid);
  }

  public getAbsoluteLocation(uuid: string) {
    return window.editor.canvas.getAbsoluteLocation(uuid);
  }

  public getDimensions(uuid: string) {
    return window.editor.canvas.getDimensions(uuid);
  }

  public center(uuid: string) {
    window.editor.canvas.center(uuid);
  }

  public draw() {
    window.editor.canvas.draw();
  }
}
