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
import {
  Editor,
  EditorApi,
  EditorInitArgs,
  EditorTheme,
  KogitoEditorEnvelopeContextType,
} from "@kie-tools-core/editor/dist/api";
import { DEFAULT_RECT } from "@kie-tools-core/guided-tour/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import * as React from "react";
import { YardEditor } from "./YardEditor";
import { YardEditorChannelApi } from "../api";

export class YardEditorView implements Editor {
  private readonly editorRef: React.RefObject<EditorApi>;
  private readonly initArgs: EditorInitArgs;
  public af_isReact = true;
  public af_componentId: "yard-editor";
  public af_componentTitle: "Yard Editor";

  constructor(
    private readonly envelopeContext: KogitoEditorEnvelopeContextType<YardEditorChannelApi>,
    initArgs: EditorInitArgs
  ) {
    this.editorRef = React.createRef<EditorApi>();
    this.initArgs = initArgs;
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
    return this.editorRef.current!.validate();
  }

  public async setTheme(theme: EditorTheme) {
    return this.editorRef.current!.setTheme(theme);
  }
}
