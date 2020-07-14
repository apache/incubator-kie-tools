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

/**
 * PUBLIC ENVELOPE API
 */
export interface KeyboardShortcutsApi {
  /**
   * Register a Keyboard Shortcuts for a keypress event.
   * @param combination The combination of keys that trigger 'onKeyDown' action. This is shown on the Keyboard Shortcuts panel.
   * @param label The label for this Keyboard Shortcut. This is shown on the Keyboard Shortcuts panel. Use a `|` character to separate its section from its description. (e.g. "Moving | Up")
   * @param onKeyPress The action to  be executed when 'combination' is pressed.
   * @param opts Options of this registration.
   *
   * @return An id representing this registration. This id can be used to 'deregister' the Keyboard Shortcut.
   */
  registerKeyPress(combination: string, label: string, onKeyPress: () => Thenable<void>, opts?: Opts): number;

  /**
   * Register a Keyboard Shortcuts for a keypress event.
   * @param combination The combination of keys that trigger 'onKeyDown' action when they're pressed and 'onKeyUp' when they're released. This is shown on the Keyboard Shortcuts panel.
   * @param label The label for this Keyboard Shortcut. This is shown on the Keyboard Shortcuts panel. Use a `|` character to separate its section from its description. (e.g. "Moving | Up")
   * @param onKeyDown The action to  be executed when 'combination' is pressed.
   * @param onKeyUp The action to  be executed when 'combination' is released.
   * @param opts Options of this registration.
   *
   * @return An id representing this registration. This id can be used to 'deregister' the Keyboard Shortcut.
   */
  registerKeyDownThenUp(
    combination: string,
    label: string,
    onKeyDown: () => Thenable<void>,
    onKeyUp: () => Thenable<void>,
    opts?: Opts
  ): number;

  /**
   * Deregister a Keyboard Shortcut.
   *
   * @param id The id obtained after registering the shortcut.
   */
  deregister(id: number): void;
}

/**
 * PUBLIC ENVELOPE API
 */
export interface Opts {
  hidden?: boolean;
  element?: EventTarget;
  repeat?: boolean;
}
