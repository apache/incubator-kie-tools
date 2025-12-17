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

import { EditorInitArgs, EditorTheme, KogitoEditorEnvelopeContextType } from "@kie-tools-core/editor/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import * as React from "react";
import {
  ServerlessWorkflowCombinedEditorApi,
  ServerlessWorkflowCombinedEditorChannelApi,
  ServerlessWorkflowCombinedEditorEnvelopeApi,
} from "../api";
import { ServerlessWorkflowCombinedEditor } from "./ServerlessWorkflowCombinedEditor";
import { Position } from "monaco-editor";

export class ServerlessWorkflowCombinedEditorView implements ServerlessWorkflowCombinedEditorApi {
  private readonly editorRef: React.RefObject<ServerlessWorkflowCombinedEditorApi>;
  public af_isReact = true;
  public af_componentId: "serverless-workflow-combined-editor";
  public af_componentTitle: "Serverless Workflow Combined Editor";

  constructor(
    private readonly envelopeContext: KogitoEditorEnvelopeContextType<
      ServerlessWorkflowCombinedEditorEnvelopeApi,
      ServerlessWorkflowCombinedEditorChannelApi
    >,
    private readonly initArgs: EditorInitArgs
  ) {
    this.editorRef = React.createRef<ServerlessWorkflowCombinedEditorApi>();
  }

  public setContent(normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string): Promise<void> {
    return this.editorRef.current!.setContent(normalizedPosixPathRelativeToTheWorkspaceRoot, content);
  }

  public getContent(): Promise<string> {
    return this.editorRef.current!.getContent();
  }

  public getPreview(): Promise<string | undefined> {
    return this.editorRef.current!.getPreview();
  }

  public af_componentRoot() {
    return (
      <ServerlessWorkflowCombinedEditor
        ref={this.editorRef}
        locale={this.initArgs.initialLocale}
        isReadOnly={this.initArgs.isReadOnly}
        channelType={this.initArgs.channel}
        resourcesPathPrefix={this.initArgs.resourcesPathPrefix}
        onNewEdit={this.envelopeContext.channelApi.notifications.kogitoWorkspace_newEdit.send}
        onStateControlCommandUpdate={
          this.envelopeContext.channelApi.notifications.kogitoEditor_stateControlCommandUpdate.send
        }
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

  public colorNodes(nodeNames: string[], color: string, colorConnectedEnds: boolean): void {
    this.editorRef.current!.colorNodes(nodeNames, color, colorConnectedEnds);
  }

  public moveCursorToPosition(position: Position): void {
    this.editorRef.current!.moveCursorToPosition(position);
  }
}
