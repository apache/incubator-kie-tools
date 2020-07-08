/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { Editor, Element } from "@kogito-tooling/core-api";
import { EnvelopeBusInnerMessageHandler } from "@kogito-tooling/microeditor-envelope";
import { SimpleReactEditor } from "./SimpleReactEditor";

export class SimpleReactEditorInterface extends Editor {
  private self: SimpleReactEditor;

  constructor(private readonly messageBus: EnvelopeBusInnerMessageHandler) {
    super("readonly-react-editor");
    this.af_isReact = true;
    this.messageBus = messageBus;
  }

  public getContent(): Promise<string> {
    return this.self.getContent();
  }

  public setContent(path: string, content: string): Promise<void> {
    return this.self.setContent(content);
  }

  public getPreview(): Promise<string | undefined> {
    return this.self.getPreview();
  }

  public af_componentRoot(): Element {
    return <SimpleReactEditor exposing={s => (this.self = s)} messageBus={this.messageBus} />;
  }
}
