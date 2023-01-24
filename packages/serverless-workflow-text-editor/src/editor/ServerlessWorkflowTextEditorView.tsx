/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { Editor, EditorInitArgs, EditorTheme, KogitoEditorEnvelopeContextType } from "@kie-tools-core/editor/dist/api";
import { DEFAULT_RECT } from "@kie-tools-core/guided-tour/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { Position } from "monaco-editor";
import * as React from "react";
import { ServerlessWorkflowTextEditorChannelApi } from "../api";
import { ServerlessWorkflowTextEditor } from "./ServerlessWorkflowTextEditor";

export interface ServerlessWorkflowTextEditorApi extends Editor {
  moveCursorToNode(nodeName: string): void;
  moveCursorToPosition(position: Position): void;
}

export class ServerlessWorkflowTextEditorView implements ServerlessWorkflowTextEditorApi {
  private readonly editorRef: React.RefObject<ServerlessWorkflowTextEditorApi>;
  public af_isReact = true;
  public af_componentId: "serverless-workflow-text-editor";
  public af_componentTitle: "Serverless Workflow Text Editor";

  constructor(
    private readonly envelopeContext: KogitoEditorEnvelopeContextType<ServerlessWorkflowTextEditorChannelApi>,
    private readonly initArgs: EditorInitArgs
  ) {
    this.editorRef = React.createRef<ServerlessWorkflowTextEditorApi>();
  }

  public async getElementPosition() {
    return DEFAULT_RECT;
  }

  public setContent(path: string, content: string): Promise<void> {
    return this.editorRef.current!.setContent(path, content);
  }

  public getContent(): Promise<string> {
    return this.editorRef.current!.getContent();
  }

  public getPreview(): Promise<string | undefined> {
    return this.editorRef.current!.getPreview();
  }

  public af_componentRoot() {
    return (
      <ServerlessWorkflowTextEditor
        ref={this.editorRef}
        isReadOnly={this.initArgs.isReadOnly}
        channelType={this.initArgs.channel}
        onNewEdit={this.envelopeContext.channelApi.notifications.kogitoWorkspace_newEdit.send}
        onStateControlCommandUpdate={
          this.envelopeContext.channelApi.notifications.kogitoEditor_stateControlCommandUpdate.send
        }
        setNotifications={this.envelopeContext.channelApi.notifications.kogitoNotifications_setNotifications.send}
      />
    );
  }

  public async undo(): Promise<void> {
    return this.editorRef.current!.undo();
  }

  public async redo(): Promise<void> {
    return this.editorRef.current!.redo();
  }

  public async validate(): Promise<Notification[]> {
    return this.editorRef.current!.validate();
  }

  public async setTheme(theme: EditorTheme) {
    return this.editorRef.current!.setTheme(theme);
  }

  public moveCursorToNode(nodeName: string): void {
    this.editorRef.current?.moveCursorToNode(nodeName);
  }

  public moveCursorToPosition(position: Position): void {
    this.editorRef.current?.moveCursorToPosition(position);
  }
}
