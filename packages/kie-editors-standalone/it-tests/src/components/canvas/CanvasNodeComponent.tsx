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
import { CanvasEditorApi } from "../../../../dist/jsdiagram/CanvasEditorApi";

export interface Props {
  nodeId: string;
  color: string;
  canvas: CanvasEditorApi | undefined;
}

export interface State {
  currentColor: string;
}

export class CanvasNodeComponent extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);

    this.state = {
      currentColor: this.props.color,
    };
    console.log(this.state.currentColor);
  }

  private handleSubmit = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    this.props.canvas?.setBackgroundColor(this.props.nodeId, this.state.currentColor);
  };

  private handleChange = async (e: React.ChangeEvent<HTMLInputElement>): Promise<void> => {
    this.setState({ currentColor: e.target.value });
  };

  render() {
    return (
      <form onSubmit={this.handleSubmit}>
        <label>
          Set color:
          <input type="text" value={this.state.currentColor} onChange={this.handleChange} />
        </label>
        <input type="submit" value="Submit" />
      </form>
    );
  }
}
