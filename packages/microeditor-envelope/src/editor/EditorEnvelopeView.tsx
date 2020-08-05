/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { Editor } from "@kogito-tooling/editor-api";
import { LoadingScreen } from "./LoadingScreen";
import "@patternfly/patternfly/base/patternfly-variables.css";
import "@patternfly/patternfly/patternfly-addons.scss";
import "@patternfly/patternfly/patternfly.scss";
import { KeyBindingsHelpOverlay } from "./KeyBindingsHelpOverlay";

interface Props {
  exposing: (self: EditorEnvelopeView) => void;
}

interface State {
  editor?: Editor;
  loading: boolean;
}

export class EditorEnvelopeView extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { editor: undefined, loading: true };
    this.props.exposing(this);
  }

  public getEditor() {
    return this.state.editor;
  }

  public setEditor(editor: Editor) {
    return new Promise(res => this.setState({ editor: editor }, res));
  }

  public setLoadingFinished() {
    return new Promise(res => this.setState({ loading: false }, res));
  }

  public setLoading() {
    return this.setState({ loading: true });
  }

  public render() {
    return (
      <>
        {!this.state.loading && <KeyBindingsHelpOverlay />}
        <div id="loading-screen" style={{ zIndex: 100, position: "relative" }}>
          <LoadingScreen visible={this.state.loading} />
        </div>
        {this.state.editor && this.state.editor.af_isReact && this.state.editor.af_componentRoot()}
      </>
    );
  }
}
