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

import { KeyBinding, KeyBindingServiceOpts } from "./DefaultKeyboardShorcutsService";

/**
 * PUBLIC ENVELOPE API
 */
export interface KeyboardShortcutsApi {
  registerKeyPress(
    combination: string,
    label: string,
    onKeyPress: () => Thenable<void>,
    opts?: KeyBindingServiceOpts
  ): number;

  registerKeyDownThenUp(
    combination: string,
    label: string,
    onKeyDown: () => Thenable<void>,
    onKeyUp: () => Thenable<void>,
    opts?: KeyBindingServiceOpts
  ): number;

  registerKeyPressOnce(combination: string, action: () => Thenable<void>, opts?: KeyBindingServiceOpts): number;

  deregister(id: number): void;

  registered(): KeyBinding[];
}
