/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { KeyBinding } from "./DefaultKeyboardShortcutsService";
import { KeyboardShortcutRegisterOpts } from "./KeyboardShortcutRegisterOpts";
import { KeyboardShortcutsService } from "./KeyboardShortcutsService";

export class NoOpKeyboardShortcutsService implements KeyboardShortcutsService {
  public registerKeyDownThenUp(
    combination: string,
    label: string,
    onKeyDown: (target: EventTarget | null) => Promise<void>,
    onKeyUp: (target: EventTarget | null) => Promise<void>,
    opts?: KeyboardShortcutRegisterOpts
  ): number {
    return 0;
  }

  public registerKeyPress(
    combination: string,
    label: string,
    onKeyPress: (target: EventTarget | null) => Promise<void>,
    opts?: KeyboardShortcutRegisterOpts
  ): number {
    return 0;
  }

  public registerKeyPressOnce(
    combination: string,
    onKeyPress: (target: EventTarget | null) => Promise<void>,
    opts?: KeyboardShortcutRegisterOpts
  ): number {
    return 0;
  }

  public deregister(id: number): void {
    // No-op
  }

  public registered(): KeyBinding[] {
    return [];
  }

  public isEnabled(): boolean {
    return false;
  }
}
