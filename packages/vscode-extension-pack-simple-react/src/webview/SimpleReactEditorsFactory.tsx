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
import * as AppFormer from "appformer-js-core";
import * as MicroEditorEnvelope from "appformer-js-microeditor-envelope";
import { EnvelopeBusInnerMessageHandler } from "appformer-js-microeditor-envelope";
import { SimpleReactEditorsLanguageData } from "../common/SimpleReactEditorsLanguageData";

export class SimpleReactEditorsFactory implements MicroEditorEnvelope.EditorFactory<SimpleReactEditorsLanguageData> {
  public createEditor(
    languageData: SimpleReactEditorsLanguageData,
    messageBus: EnvelopeBusInnerMessageHandler
  ): Promise<AppFormer.Editor> {
    switch (languageData.type) {
      case "react":
        return Promise.resolve(new ReactReadonlyAppFormerEditor(messageBus));
      default:
        throw new Error("Only react editors are supported on this extension.");
    }
  }
}

class ReactReadonlyAppFormerEditor extends AppFormer.Editor {
  private readonly messageBus: EnvelopeBusInnerMessageHandler;

  private self: ReactReadonlyEditor;

  constructor(messageBus: EnvelopeBusInnerMessageHandler) {
    super("readonly-react-editor");
    this.af_isReact = true;
    this.messageBus = messageBus;
  }

  public getContent(): Promise<string> {
    return this.self.getContent();
  }

  public isDirty(): boolean {
    return this.self.isDirty();
  }

  public setContent(content: string): Promise<void> {
    return this.self.setContent(content);
  }

  public af_componentRoot(): AppFormer.Element {
    return <ReactReadonlyEditor exposing={s => (this.self = s)} messageBus={this.messageBus} />;
  }
}

interface Props {
  exposing: (s: ReactReadonlyEditor) => void;
  messageBus: EnvelopeBusInnerMessageHandler;
}

interface State {
  content: string;
  originalContent: string;
}

class ReactReadonlyEditor extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    props.exposing(this);
    this.state = {
      originalContent: "",
      content: ""
    };
  }

  public setContent(content: string) {
    return new Promise<void>(res =>
      this.setState({ originalContent: content }, () => {
        res();
      })
    ).then(() => this.updateContent(content));
  }

  private updateContent(content: string) {
    return new Promise<void>(res => {
      this.setState({ content: content }, () => {
        this.props.messageBus.notify_dirtyIndicatorChange(this.isDirty());
        res();
      });
    });
  }

  //saving triggers this method
  public getContent() {
    return this.setContent(this.state.content).then(() => this.state.content);
  }

  public isDirty() {
    return this.state.content !== this.state.originalContent;
  }

  public render() {
    return (
      <textarea
        style={{ width: "100%", height: "100%" }}
        value={this.state.content}
        onInput={(e: any) => this.updateContent(e.target.value)}
      />
    );
  }
}
