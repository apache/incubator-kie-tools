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

import { EditorFactory, KogitoEditorEnvelopeContextType } from "@kie-tools-core/editor/dist/api";
import { EditorEnvelopeViewApi, KogitoEditorEnvelopeApiImpl } from "@kie-tools-core/editor/dist/envelope";
import { EnvelopeApiFactoryArgs } from "@kie-tools-core/envelope";
import { ServerlessWorkflowDiagramEditorChannelApi, ServerlessWorkflowDiagramEditorEnvelopeApi } from "../api";
import { ServerlessWorkflowDiagramEditor } from "./ServerlessWorkflowDiagramEditor";

export type ServerlessWorkflowDiagramEnvelopeApiFactoryArgs = EnvelopeApiFactoryArgs<
  ServerlessWorkflowDiagramEditorEnvelopeApi,
  ServerlessWorkflowDiagramEditorChannelApi,
  EditorEnvelopeViewApi<ServerlessWorkflowDiagramEditor>,
  KogitoEditorEnvelopeContextType<ServerlessWorkflowDiagramEditorChannelApi>
>;

export class ServerlessWorkflowDiagramEditorEnvelopeApiImpl
  extends KogitoEditorEnvelopeApiImpl<
    ServerlessWorkflowDiagramEditor,
    ServerlessWorkflowDiagramEditorEnvelopeApi,
    ServerlessWorkflowDiagramEditorChannelApi
  >
  implements ServerlessWorkflowDiagramEditorEnvelopeApi
{
  constructor(
    private readonly serverlessWorkflowArgs: ServerlessWorkflowDiagramEnvelopeApiFactoryArgs,
    editorFactory: EditorFactory<ServerlessWorkflowDiagramEditor, ServerlessWorkflowDiagramEditorChannelApi>
  ) {
    super(serverlessWorkflowArgs, editorFactory);
  }

  private getEditorOrThrowError() {
    const editor = this.view().getEditor();
    if (!editor) {
      throw new Error("Editor not found.");
    }
    return editor;
  }

  public async canvas_getNodeIds() {
    return this.getEditorOrThrowError().getNodeIds();
  }

  public async canvas_getBackgroundColor(uuid: string) {
    return this.getEditorOrThrowError().getBackgroundColor(uuid);
  }

  public async canvas_setBackgroundColor(uuid: string, backgroundColor: string) {
    return this.getEditorOrThrowError().setBackgroundColor(uuid, backgroundColor);
  }

  public async canvas_getBorderColor(uuid: string) {
    return this.getEditorOrThrowError().getBorderColor(uuid);
  }

  public async canvas_setBorderColor(uuid: string, borderColor: string) {
    return this.getEditorOrThrowError().setBorderColor(uuid, borderColor);
  }

  public async canvas_getLocation(uuid: string) {
    return this.getEditorOrThrowError().getLocation(uuid);
  }

  public async canvas_getAbsoluteLocation(uuid: string) {
    return this.getEditorOrThrowError().getAbsoluteLocation(uuid);
  }

  public async canvas_getDimensions(uuid: string) {
    return this.getEditorOrThrowError().getDimensions(uuid);
  }

  public async canvas_applyState(uuid: string, state: string) {
    return this.getEditorOrThrowError().applyState(uuid, state);
  }

  public async canvas_centerNode(uuid: string) {
    return this.getEditorOrThrowError().centerNode(uuid);
  }

  public kogitoSwfDiagramEditor__highlightNode(args: { nodeName: string }) {
    return this.getEditorOrThrowError().selectStateByName(args.nodeName);
  }
}
