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
import {
  Editor,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
  KogitoEditorEnvelopeContextType,
} from "@kie-tools-core/editor/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import * as React from "react";
import { PMMLEditor } from "./PMMLEditor";

export class PMMLEditorInterface implements Editor {
  private self: PMMLEditor;
  public af_isReact = true;
  public af_componentId: "pmml-editor";
  public af_componentTitle: "PMML Editor";

  constructor(
    private readonly envelopeContext: KogitoEditorEnvelopeContextType<KogitoEditorEnvelopeApi, KogitoEditorChannelApi>
  ) {}

  public setContent(normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string): Promise<void> {
    return this.self.setContent(normalizedPosixPathRelativeToTheWorkspaceRoot, content);
  }

  public getContent(): Promise<string> {
    return this.self.getContent();
  }

  public getPreview(): Promise<string | undefined> {
    return Promise.resolve(undefined);
  }

  public af_componentRoot() {
    return (
      <PMMLEditor
        exposing={(s) => (this.self = s)}
        ready={() => this.envelopeContext.channelApi.notifications.kogitoEditor_ready.send()}
        newEdit={(edit) => this.envelopeContext.channelApi.notifications.kogitoWorkspace_newEdit.send(edit)}
        setNotifications={(path, notifications) =>
          this.envelopeContext.channelApi.notifications.kogitoNotifications_setNotifications.send(path, notifications)
        }
      />
    );
  }

  public async undo(): Promise<void> {
    return this.self.undo();
  }

  public async redo(): Promise<void> {
    return this.self.redo();
  }

  public async validate(): Promise<Notification[]> {
    return Promise.resolve(this.self.validate());
  }

  public async setTheme(): Promise<void> {
    // Only default theme is supported
    return Promise.resolve();
  }
}
