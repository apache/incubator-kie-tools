/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeContextType,
} from "@kie-tooling-core/editor/dist/api";
import { DEFAULT_RECT } from "@kie-tooling-core/guided-tour/dist/api";
import { Notification } from "@kie-tooling-core/notifications/dist/api";
import * as React from "react";
import { ServerlessWorkflowEditor } from "./ServerlessWorkflowEditor";

export class ServerlessWorkflowEditorInterface implements Editor {
  private editorRef: React.RefObject<EditorApi>;
  public af_isReact = true;
  public af_componentId: "serverless-workflow-editor";
  public af_componentTitle: "Serverless Workflow Editor";

  constructor(private readonly envelopeContext: KogitoEditorEnvelopeContextType<KogitoEditorChannelApi>) {
    this.editorRef = React.createRef<EditorApi>();
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
    return Promise.resolve(undefined);
  }

  public af_componentRoot() {
    return (
      <ServerlessWorkflowEditor
        ref={this.editorRef}
        ready={() => this.envelopeContext.channelApi.notifications.kogitoEditor_ready()}
        newEdit={(edit) => this.envelopeContext.channelApi.notifications.kogitoWorkspace_newEdit(edit)}
        setNotifications={(path, notifications) =>
          this.envelopeContext.channelApi.notifications.kogitoNotifications_setNotifications(path, notifications)
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
    return Promise.resolve(this.editorRef.current!.validate());
  }
}
