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

import * as React from "react";
import { Editor, EditorTheme } from "@kie-tools-core/editor/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { GwtEditor } from "@kie-tools/kie-bc-editors/dist/common/consumedInteropApi/GwtAppFormerConsumedInteropApi";

export class DummyEditor implements Editor, GwtEditor {
  private ref: DummyEditorComponent;
  public readonly af_componentTitle = "Dummy Editor";
  public readonly af_componentId = "dummy-editor";
  public readonly af_isReact = true;

  public af_componentRoot() {
    return <DummyEditorComponent exposing={(self) => (this.ref = self)} />;
  }

  public getContent() {
    return this.ref!.getContent();
  }

  public undo() {
    return Promise.resolve();
  }

  public redo() {
    return Promise.resolve();
  }

  public setContent(content: string) {
    return this.ref!.setContent(content);
  }

  public getPreview(): Promise<string | undefined> {
    return Promise.resolve(undefined);
  }

  public validate(): Promise<Notification[]> {
    return Promise.resolve([]);
  }

  public setTheme(_theme: EditorTheme): Promise<void> {
    return Promise.resolve();
  }

  public applyTheme(_name: string | null): Promise<void> {
    return Promise.resolve();
  }

  public selectStateByName(_name: string | null): Promise<void> {
    return Promise.resolve();
  }
}

interface Props {
  exposing: (self: DummyEditorComponent) => void;
}

interface State {
  content: string;
}

class DummyEditorComponent extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { content: "" };
    this.props.exposing(this);
  }

  public getContent() {
    return Promise.resolve(this.state.content);
  }

  public setContent(content: string) {
    return new Promise<void>((res) => this.setState({ content: content }, res));
  }

  public render() {
    return (
      <div>
        {"Here's the dummy content: "}
        {this.state.content}
      </div>
    );
  }
}
