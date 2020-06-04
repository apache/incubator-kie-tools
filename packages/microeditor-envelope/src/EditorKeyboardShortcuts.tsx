import { useEffect } from "react";
import * as React from "react";
import { KeyboardShortcutsApi } from "./api/keyboardShortcuts";
import { StateControl } from "./api/stateControl";
import { EnvelopeBusInnerMessageHandler } from "./EnvelopeBusInnerMessageHandler";
import { EditorStateControlEvent } from "@kogito-tooling/embedded-editor";

interface Props {
  keyboardShortcuts: KeyboardShortcutsApi;
  stateControl: StateControl;
  busAPI: EnvelopeBusInnerMessageHandler;
}

export function RegisterEditorKeyboardShortcuts(props: Props) {
  useEffect(() => {
    const id = props.keyboardShortcuts.registerKeyPress(
      "ctrl+z",
      "Undo | Undo last edit",
      async () => {
        props.stateControl.undo();
        props.busAPI.notify_channelStateControl(EditorStateControlEvent.UNDO);
      },
      { element: window }
    );
    return () => props.keyboardShortcuts.deregister(id);
  }, []);

  useEffect(() => {
    const id = props.keyboardShortcuts.registerKeyPress(
      "shift+ctrl+z",
      "Redo | Redo last edit",
      async () => {
        props.stateControl.redo();
        props.busAPI.notify_channelStateControl(EditorStateControlEvent.REDO);
      },
      { element: window }
    );
    return () => props.keyboardShortcuts.deregister(id);
  }, []);

  return (<></>)
}
