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
import * as Core from "@kogito-tooling/microeditor-envelope-protocol";
import { ChannelType, StateControlCommand } from "@kogito-tooling/microeditor-envelope-protocol";
import { Editor } from "@kogito-tooling/editor-api";
import { LoadingScreen } from "./LoadingScreen";
import { DefaultKeyboardShortcutsService } from "@kogito-tooling/keyboard-shortcuts";
import "@patternfly/patternfly/patternfly-variables.css";
import "@patternfly/patternfly/patternfly-addons.css";
import "@patternfly/patternfly/patternfly.css";
import { KogitoEnvelopeBus } from "./KogitoEnvelopeBus";
import { KeyBindingsHelpOverlay } from "./KeyBindingsHelpOverlay";

interface Props {
  exposing: (self: EditorEnvelopeView) => void;
  loadingScreenContainer: HTMLElement;
  keyboardShortcutsService: DefaultKeyboardShortcutsService;
  context: Core.EditorContext;
  messageBus: KogitoEnvelopeBus;
}

interface State {
  editor?: Editor;
  loading: boolean;
}

export class EditorEnvelopeView extends React.Component<Props, State> {
  private redoShortcutKeybindingId: number;
  private undoShortcutKeybindingId: number;

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

  public componentDidMount() {
    if (this.props.context.channel !== ChannelType.VSCODE) {
      this.registerRedoShortcut();
      this.registerUndoShortcut();
    }
  }

  public componentWillUnmount() {
    if (this.props.context.channel !== ChannelType.VSCODE) {
      this.props.keyboardShortcutsService.deregister(this.redoShortcutKeybindingId);
      this.props.keyboardShortcutsService.deregister(this.undoShortcutKeybindingId);
    }
  }

  private registerRedoShortcut() {
    this.redoShortcutKeybindingId = this.props.keyboardShortcutsService.registerKeyPress(
      "shift+ctrl+z",
      "Edit | Redo last edit",
      async () => {
        this.getEditor()!.redo();
        this.props.messageBus.client.notify("receive_stateControlCommandUpdate", StateControlCommand.REDO);
      }
    );
  }

  private registerUndoShortcut() {
    this.undoShortcutKeybindingId = this.props.keyboardShortcutsService.registerKeyPress(
      "ctrl+z",
      "Edit | Undo last edit",
      async () => {
        this.getEditor()!.undo();
        this.props.messageBus.client.notify("receive_stateControlCommandUpdate", StateControlCommand.UNDO);
      }
    );
  }

  public render() {
    return (
      <>
        {!this.state.loading && <KeyBindingsHelpOverlay />}
        {ReactDOM.createPortal(<LoadingScreen visible={this.state.loading} />, this.props.loadingScreenContainer!)}
        {this.state.editor && this.state.editor.af_isReact && this.state.editor.af_componentRoot()}
      </>
    );
  }
}
