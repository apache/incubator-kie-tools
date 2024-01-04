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
import { Editor, EditorInitArgs, EditorTheme, KogitoEditorEnvelopeContextType } from "@kie-tools-core/editor/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import * as React from "react";
import { YardEditor } from "./YardEditor";
import { YardEditorApi, YardEditorChannelApi } from "../api";
import { Position } from "monaco-editor";
import { validationPromise } from "@kie-tools/yard-validator/dist/";

export class YardEditorView implements Editor {
  private readonly editorRef: React.RefObject<YardEditorApi>;
  private readonly initArgs: EditorInitArgs;
  public af_isReact = true;
  public af_componentId: "yard-editor";
  public af_componentTitle: "Yard Editor";
  private path: string;

  constructor(
    private readonly envelopeContext: KogitoEditorEnvelopeContextType<YardEditorChannelApi>,
    initArgs: EditorInitArgs
  ) {
    this.editorRef = React.createRef<YardEditorApi>();
    this.initArgs = initArgs;
  }
  public setContent(path: string, content: string): Promise<void> {
    this.path = path;
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
      <YardEditor
        ref={this.editorRef}
        channelType={this.initArgs.channel}
        onStateControlCommandUpdate={
          this.envelopeContext.channelApi.notifications.kogitoEditor_stateControlCommandUpdate.send
        }
        onNewEdit={this.envelopeContext.channelApi.notifications.kogitoWorkspace_newEdit.send}
        setNotifications={this.envelopeContext.channelApi.notifications.kogitoNotifications_setNotifications.send}
        isReadOnly={this.initArgs.isReadOnly}
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
    let result: Notification[] = [];
    return this.editorRef
      .current!.getContent()
      .then((value) => {
        return validationPromise(value);
      })
      .then((value) => {
        result = result.concat(value);
        return this.editorRef.current!.validate();
      })
      .then((value) => {
        result = result.concat(value);
        return new Promise<Notification[]>((resolve) => {
          resolve(result);
        });
      });
  }

  public async setTheme(theme: EditorTheme) {
    return this.editorRef.current!.setTheme(theme);
  }

  public moveCursorToPosition(position: Position): void {
    this.editorRef.current!.moveCursorToPosition(position);
  }
}
