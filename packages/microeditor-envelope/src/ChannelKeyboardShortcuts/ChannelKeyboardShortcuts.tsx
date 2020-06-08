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

import { ChannelStateControlEvent } from "@kogito-tooling/core-api";
import { KeyboardShortcutsApi, undoShortcut, redoShortcut } from "@kogito-tooling/keyboard-shortcuts";
import { useEffect } from "react";
import * as React from "react";
import { EnvelopeBusInnerMessageHandler } from "../EnvelopeBusInnerMessageHandler";
import { StateControl } from "../api/stateControl";

interface Props {
  keyboardShortcuts: KeyboardShortcutsApi;
  stateControl: StateControl;
  busApi: EnvelopeBusInnerMessageHandler;
}

export function ChannelKeyboardShortcuts(props: Props) {
  // Add the redo keyboard shortcut
  useEffect(() => {
    const { combination, label } = redoShortcut();
    const id = props.keyboardShortcuts.registerKeyPress(combination, label, async () => {
      props.stateControl.redo();
      props.busApi.notify_channelStateControl(ChannelStateControlEvent.REDO);
    });
    return () => props.keyboardShortcuts.deregister(id);
  }, []);

  // Add the undo keyboard shortcut
  useEffect(() => {
    const { combination, label } = undoShortcut();
    const id = props.keyboardShortcuts.registerKeyPress(combination, label, async () => {
      props.stateControl.undo();
      props.busApi.notify_channelStateControl(ChannelStateControlEvent.UNDO);
    });
    return () => props.keyboardShortcuts.deregister(id);
  }, []);
  return <></>;
}
