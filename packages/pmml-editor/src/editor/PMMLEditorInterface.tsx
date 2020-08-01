/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { Editor, KogitoEditorEnvelopeContextType } from "@kogito-tooling/editor-api";
import { DEFAULT_RECT } from "@kogito-tooling/editor-envelope-protocol";
import * as React from "react";
import { PMMLEditor } from "./PMMLEditor";

export class PMMLEditorInterface implements Editor {
  private self: PMMLEditor;
  public af_isReact = true;
  public af_componentId: "pmml-editor";
  public af_componentTitle: "PMML Editor";

  constructor(private readonly envelopeContext: KogitoEditorEnvelopeContextType) {}

  public async getElementPosition() {
    return DEFAULT_RECT;
  }

  public setContent(path: string, content: string): Promise<void> {
    return this.self.setContent(path, content);
  }

  public getContent(): Promise<string> {
    return this.self.getContent();
  }

  public getPreview(): Promise<string | undefined> {
    return Promise.resolve(undefined);
  }

  public af_componentRoot() {
    return <PMMLEditor exposing={s => (this.self = s)} messageBusClient={this.envelopeContext.channelApi} />;
  }

  public async undo(): Promise<void> {
    //Place holder until StateControl is added for React based components.
  }

  public async redo(): Promise<void> {
    //Place holder until StateControl is added for React based components.
  }
}
