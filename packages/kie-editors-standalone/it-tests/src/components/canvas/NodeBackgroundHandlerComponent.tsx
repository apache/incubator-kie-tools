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

import * as React from "react";
import { Color } from "vscode";
import { CanvasEditorApi } from "../../../../dist/jsdiagram/CanvasEditorApi";

interface State {
  color: string;
}

interface Props {
  canvas: CanvasEditorApi | undefined;
  nodeId: string;
}

export class NodeBackgroundHandlerComponent extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    let currentColor = "";
    props.canvas?.getBackgroundColor(props.nodeId).then((clr) => {
      currentColor = clr;
      console.log("COLOR: " + clr);
      this.state = {
        color: currentColor,
      };
    });

    this.state = {
      color: currentColor,
    };

    this.forceUpdate();

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event: any) {
    this.setState({ color: event.target.value });
  }
  handleSubmit(event: any) {
    event.preventDefault();
    this.props.canvas?.setBackgroundColor(this.props.nodeId, this.state.color);
  }

  render() {
    return (
      <div>
        <span>{this.state.color}</span>
        <form onSubmit={this.handleSubmit}>
          <label>
            Set color:
            <input type="text" value={this.state.color} onChange={this.handleChange} />
          </label>
          <input type="submit" value="Submit" />
        </form>
      </div>
    );
  }
}
