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
import { KogitoEditorChannelApi } from "@kogito-tooling/editor/dist/api";
import { MessageBusClientApi } from "@kogito-tooling/envelope-bus/dist/api"
import * as React from "react";

export interface Props {
  exposing: (s: PMMLEditor) => void;
  channelApi: MessageBusClientApi<KogitoEditorChannelApi>;
}

export interface State {
  path: string;
  content: string;
  originalContent: string;
}

export class PMMLEditor extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    props.exposing(this);
    this.state = {
      path: "",
      content: "",
      originalContent: ""
    };
  }

  public componentDidMount(): void {
    this.props.channelApi.notifications.receive_ready();
  }

  public setContent(path: string, content: string): Promise<void> {
    return new Promise<void>(res => this.setState({ originalContent: content }, res));
  }

  public getContent(): Promise<string> {
    return new Promise<string>(res => this.state.content);
  }

  public render() {
    return (
      <textarea
        style={{ width: "100%", height: "100%", outline: 0, boxSizing: "border-box", border: 0 }}
        value={this.state.content}
        onInput={(e: any) => this.setState(e.target.value)}
        onChange={(e: any) => this.setState(e.target.value)}
      />
    );
  }
}
