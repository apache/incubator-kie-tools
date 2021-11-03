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
  canvas: CanvasEditorApi | undefined;
}

export interface State {
  currentColor: string;
  currentBorderColor: string;
  location: number[];
  absoluteLocation: number[];
}

export class CanvasNodeComponent extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);

    this.state = {
      currentColor: "",
      currentBorderColor: "",
      location: [-1, -1],
      absoluteLocation: [-1, -1],
    };
  }

  componentDidMount() {
    this.props.canvas?.getBackgroundColor(this.props.nodeId).then((backgroundColor) => {
      this.setState({ currentColor: backgroundColor });
    });

    this.props.canvas?.getBorderColor(this.props.nodeId).then((borderColor) => {
      this.setState({ currentBorderColor: borderColor });
    });

    this.props.canvas?.getLocation(this.props.nodeId).then((nodeLocation) => {
      this.setState({ location: nodeLocation });
    });

    this.props.canvas?.getLocation(this.props.nodeId).then((nodeAbsoluteLocation) => {
      this.setState({ absoluteLocation: nodeAbsoluteLocation });
    });
  }

  private handleBackgroundColorSubmit = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    this.props.canvas?.setBackgroundColor(this.props.nodeId, this.state.currentColor);
  };

  private handleBackgroundColorChange = async (e: React.ChangeEvent<HTMLInputElement>): Promise<void> => {
    this.setState({ currentColor: e.target.value });
  };

  private handleBorderColorSubmit = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    this.props.canvas?.setBorderColor(this.props.nodeId, this.state.currentColor);
  };

  private handleBorderColorChange = async (e: React.ChangeEvent<HTMLInputElement>): Promise<void> => {
    this.setState({ currentBorderColor: e.target.value });
  };

  render() {
    return (
      <div id="canvasNodeComponent">
        <form onSubmit={this.handleBackgroundColorSubmit}>
          <label>
            <span>Current background color:</span>
            <input type="text" value={this.state.currentColor} onChange={this.handleBackgroundColorChange} />
          </label>
          <input type="submit" value="Set background color" />
        </form>
        <form onSubmit={this.handleBorderColorSubmit}>
          <label>
            <span>Current border color:</span>
            <input type="text" value={this.state.currentBorderColor} onChange={this.handleBorderColorChange} />
          </label>
          <input type="submit" value="Set border color" />
        </form>
        <br />
        <span>Node location: [ x={this.state.location.toString()}=y ]</span>
        <br />
        <span>Node absolute location: [ x={this.state.absoluteLocation.toString()}=y ]</span>
      </div>
    );
  }
}
