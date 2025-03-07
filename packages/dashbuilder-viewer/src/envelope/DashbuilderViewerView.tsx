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
import { DashbuilderWrapper } from "../dashbuilder/DashbuilderWrapper";
import {
  Editor,
  EditorApi,
  EditorInitArgs,
  EditorTheme,
  KogitoEditorEnvelopeApi,
  KogitoEditorEnvelopeContextType,
} from "@kie-tools-core/editor/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import * as React from "react";
import { DashbuilderViewerChannelApi } from "./DashbuilderViewerChannelApi";

export class DashbuilderViewerView implements Editor {
  private readonly editorRef: React.RefObject<EditorApi>;
  public dashbuilderWrapper: DashbuilderWrapper;
  public af_isReact = true;
  public af_componentId: "dashbuilder-editor";
  public af_componentTitle: "Dashbuilder Editor";

  constructor(
    private readonly envelopeContext: KogitoEditorEnvelopeContextType<
      KogitoEditorEnvelopeApi,
      DashbuilderViewerChannelApi
    >,
    private readonly initArgs: EditorInitArgs
  ) {
    this.editorRef = React.createRef<EditorApi>();
    this.dashbuilderWrapper = new DashbuilderWrapper(() => {
      envelopeContext.channelApi.requests.getComponentsServerUrl().then((componentServerUrl) => {
        if (componentServerUrl) {
          this.dashbuilderWrapper.setComponentServerUrl(componentServerUrl);
        }
      });
      envelopeContext.channelApi.notifications.kogitoEditor_ready.send();
    });
  }

  public setContent(normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string): Promise<void> {
    this.dashbuilderWrapper.setContent(content);
    return Promise.resolve();
  }

  public getContent(): Promise<string> {
    return Promise.resolve(this.dashbuilderWrapper.getContent());
  }

  public getPreview(): Promise<string | undefined> {
    return this.editorRef.current!.getPreview();
  }
  public af_componentRoot() {
    return <></>;
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
