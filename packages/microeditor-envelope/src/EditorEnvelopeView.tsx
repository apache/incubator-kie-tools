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
import * as ReactDOM from "react-dom";
import * as AppFormer from "@kogito-tooling/core-api";
import { LoadingScreen } from "./LoadingScreen";
import { KeyBindingsHelpOverlay } from "@kogito-tooling/keyboard-shortcuts";
import { KeyboardShortcutsApi } from "../../keyboard-shortcuts/src/api";
import { RegisterChannelKeyboardShortcuts } from "./RegisterChannelKeyboardShortcuts";
import "@patternfly/patternfly/patternfly-variables.css";
import "@patternfly/patternfly/patternfly-addons.css";
import "@patternfly/patternfly/patternfly.css";
import { StateControl } from "./api/stateControl";
import { EnvelopeBusInnerMessageHandler } from "./EnvelopeBusInnerMessageHandler";

interface Props {
  exposing: (self: EditorEnvelopeView) => void;
  loadingScreenContainer: HTMLElement;
  keyboardShortcuts: KeyboardShortcutsApi;
  context: AppFormer.EditorContext;
  stateControl: StateControl;
  messageBus: EnvelopeBusInnerMessageHandler;
}

interface State {
  editor?: AppFormer.Editor;
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

  public setEditor(editor: AppFormer.Editor) {
    return new Promise(res => this.setState({ editor: editor }, res));
  }

  public setLoadingFinished() {
    return new Promise(res => this.setState({ loading: false }, res));
  }

  public setLoading() {
    return this.setState({ loading: true });
  }

  private LoadingScreenPortal() {
    return ReactDOM.createPortal(<LoadingScreen visible={this.state.loading} />, this.props.loadingScreenContainer!);
  }

  public render() {
    return (
      <>
        {!this.state.loading && (
          <>
            <RegisterChannelKeyboardShortcuts
              keyboardShortcuts={this.props.keyboardShortcuts}
              stateControl={this.props.stateControl}
              messageBus={this.props.messageBus}
            />
            <KeyBindingsHelpOverlay keyboardShortcuts={this.props.keyboardShortcuts} context={this.props.context} />
          </>
        )}
        {this.LoadingScreenPortal()}
        {this.state.editor && this.state.editor.af_isReact && this.state.editor.af_componentRoot()}
      </>
    );
  }
}
