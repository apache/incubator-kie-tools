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
import { EnvelopeBusInnerMessageHandler } from "@kogito-tooling/microeditor-envelope";

export interface Props {
  exposing: (s: SimpleReactEditor) => void;
  messageBus: EnvelopeBusInnerMessageHandler;
}

export interface State {
  content: string;
}

export class SimpleReactEditor extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    props.exposing(this);
    this.state = {
      content: ""
    };
  }

  public componentDidMount(): void {
    this.props.messageBus.notify_ready();
  }

  public async setContent(content: string): Promise<void> {
    this.setState({ content: content });
  }

  private async updateContent(content: string): Promise<void> {
    // The updateContent method is also called when users perform undo/redo actions
    // but, ideally, messageBus.notify_newEdit shouldn't be called in this cases.
    this.props.messageBus.notify_newEdit({ id: new Date().getTime().toString() });
    this.setContent(content);
  }

  public async getContent(): Promise<string> {
    return this.state.content;
  }

  public getPreview(): Promise<string | undefined> {
    throw new Error("Method not implemented.");
  }

  public render() {
    return (
      <textarea
        style={{
          width: "100%",
          height: "100%",
          outline: 0,
          boxSizing: "border-box",
          border: 0,
          color: "black"
        }}
        value={this.state.content}
        onChange={(e: any) => this.updateContent(e.target.value)}
      />
    );
  }
}
