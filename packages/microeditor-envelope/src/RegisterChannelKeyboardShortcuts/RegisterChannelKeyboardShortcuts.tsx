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

import { StateControlCommand } from "@kogito-tooling/core-api";
import { KeyboardShortcutsApi } from "@kogito-tooling/keyboard-shortcuts";
import { useEffect } from "react";
import * as React from "react";
import { StateControlService } from "../api/stateControl";
import { EnvelopeBusInnerMessageHandler } from "../EnvelopeBusInnerMessageHandler";

interface Props {
  keyboardShortcuts: KeyboardShortcutsApi;
  stateControl: StateControlService;
  messageBus: EnvelopeBusInnerMessageHandler;
}

export function RegisterChannelKeyboardShortcuts(props: Props) {
  // Add the redo keyboard shortcut
  useEffect(() => {
    const id = props.keyboardShortcuts.registerKeyPress("shift+ctrl+z", "Edit | Redo last edit", async () => {
      props.stateControl.redo();
      props.messageBus.request_stateControlCommandUpdate(StateControlCommand.REDO);
    });
    return () => props.keyboardShortcuts.deregister(id);
  }, []);

  // Add the undo keyboard shortcut
  useEffect(() => {
    const id = props.keyboardShortcuts.registerKeyPress("ctrl+z", "Edit | Undo last edit", async () => {
      props.stateControl.undo();
      props.messageBus.request_stateControlCommandUpdate(StateControlCommand.UNDO);
    });
    return () => props.keyboardShortcuts.deregister(id);
  }, []);
  return <></>;
}
