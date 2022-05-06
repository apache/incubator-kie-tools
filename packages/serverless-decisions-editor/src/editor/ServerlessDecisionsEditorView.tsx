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
import * as React from "react";
import { ServerlessDecisionsEditorChannelApi } from "../api";
import { Notification } from "@kie-tools-core/notifications/dist/api";

export class ServerlessDecisionsEditorView implements Editor {
  private readonly editorRef: React.RefObject<EditorApi>;
  private readonly initArgs: EditorInitArgs;
  public af_componentId: "serverless-decisions-editor";
  public af_componentTitle: "YAML Rules Editor";
  public af_isReact = true;

  constructor(
    private readonly envelopeContext: KogitoEditorEnvelopeContextType<ServerlessDecisionsEditorChannelApi>,
    initArgs: EditorInitArgs
  ) {
    this.editorRef = React.createRef<EditorApi>();
    this.initArgs = initArgs;
  }

  public af_componentRoot() {
    return <div></div>;
  }

  public getContent(): Promise<string> {
    return this.editorRef.current!.getContent();
  }

  public async getElementPosition() {
    return DEFAULT_RECT;
  }

  public getPreview(): Promise<string | undefined> {
    return this.editorRef.current!.getPreview();
  }

  public async redo(): Promise<void> {
    return this.editorRef.current!.redo();
  }

  public setContent(path: string, content: string): Promise<void> {
    return this.editorRef.current!.setContent(path, content);
  }

  public async setTheme(theme: EditorTheme) {
    return this.editorRef.current!.setTheme(theme);
  }

  public async undo(): Promise<void> {
    return this.editorRef.current!.undo();
  }

  public async validate(): Promise<Notification[]> {
    return this.editorRef.current!.validate();
  }
}
