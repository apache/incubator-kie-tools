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
import { StateControl } from "../../api/stateControl";
import { EnvelopeBusInnerMessageHandler } from "../../EnvelopeBusInnerMessageHandler";
import { KeyboardShortcutRegistration } from "../../api/keyboardShortcuts";

export function undoShortcut(): KeyboardShortcutRegistration {
  return {
    combination: "ctrl+z",
    label: "Edit | Undo last edit",
  };
}

export function redoShortcut(): KeyboardShortcutRegistration {
  return {
    combination: "shift+ctrl+z",
    label: "Edit | Redo last edit",
  };
}
