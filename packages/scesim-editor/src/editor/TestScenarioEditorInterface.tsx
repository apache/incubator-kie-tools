/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import {
  Editor,
  EditorTheme,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeContextType,
} from "@kie-tools-core/editor/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { TestScenarioEditor } from "./TestScenarioEditor";

export class TestScenarioEditorInterface implements Editor {
  private self: TestScenarioEditor;
  af_isReact: boolean;
  public af_componentId: "scesim-editor";
  public af_componentTitle: "Test Scenario Editor";

  constructor(private readonly envelopeContext: KogitoEditorEnvelopeContextType<KogitoEditorChannelApi>) {}

  af_componentRoot() {
    return (
      <TestScenarioEditor
        exposing={(s) => (this.self = s)}
        ready={() => this.envelopeContext.channelApi.notifications.kogitoEditor_ready.send()}
        newEdit={(edit) => this.envelopeContext.channelApi.notifications.kogitoWorkspace_newEdit.send(edit)}
        setNotifications={(path, notifications) =>
          this.envelopeContext.channelApi.notifications.kogitoNotifications_setNotifications.send(path, notifications)
        }
      />
    );
  }

  setContent(path: string, content: string): Promise<void> {
    return this.self.setContent(path, content);
  }

  getContent(): Promise<string> {
    return this.self.getContent();
  }

  getPreview(): Promise<string | undefined> {
    // Preview not available
    return Promise.resolve(undefined);
  }

  undo(): Promise<void> {
    return this.self.undo();
  }

  redo(): Promise<void> {
    return this.self.redo();
  }

  validate(): Promise<Notification[]> {
    return Promise.resolve(this.self.validate());
  }

  setTheme(theme: EditorTheme): Promise<void> {
    // Only default theme is supported
    return Promise.resolve();
  }
}
