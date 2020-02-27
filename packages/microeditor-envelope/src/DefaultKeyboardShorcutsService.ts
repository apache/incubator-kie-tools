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

import { KeyboardShortcutsApi } from "./KeyboardShorcutsApi";

export interface KeyBinding {
  combination: string;
  label: string;
  opts?: KeyBindingServiceOpts;
  listener: (e: KeyboardEvent) => boolean;
}

export interface KeyBindingServiceOpts {
  hidden?: boolean;
  element?: EventTarget;
  repeat?: boolean;
}

export enum ModKeys {
  CTRL = "ctrl",
  META = "meta",
  ALT = "alt",
  SHIFT = "shift"
}

const MODIFIER_KEY_NAMES = new Map<string, string>([
  ["AltLeft", "alt"],
  ["AltRight", "alt"],
  ["CtrlLeft", "ctrl"],
  ["CtrlRight", "ctrl"],
  ["MetaLeft", "meta"],
  ["MetaRight", "meta"],
  ["ShiftLeft", "shift"],
  ["ShiftRight", "shift"]
]);

const KEY_CODES = new Map<string, string>([
  ["/", "Slash"],
  ["esc", "Escape"],
  ["delete", "Delete"],
  ["backspace", "Backspace"],
  ["right", "ArrowRight"],
  ["left", "ArrowLeft"],
  ["up", "ArrowUp"],
  ["down", "ArrowDown"],
  ["a", "KeyA"],
  ["b", "KeyB"],
  ["c", "KeyC"],
  ["d", "KeyD"],
  ["e", "KeyE"],
  ["f", "KeyF"],
  ["g", "KeyG"],
  ["h", "KeyH"],
  ["i", "KeyI"],
  ["j", "KeyJ"],
  ["k", "KeyK"],
  ["l", "KeyL"],
  ["m", "KeyM"],
  ["n", "KeyN"],
  ["o", "KeyO"],
  ["p", "KeyP"],
  ["q", "KeyQ"],
  ["r", "KeyR"],
  ["s", "KeyS"],
  ["t", "KeyT"],
  ["u", "KeyU"],
  ["v", "KeyV"],
  ["w", "KeyW"],
  ["x", "KeyX"],
  ["y", "KeyY"],
  ["z", "KeyZ"]
]);

export class DefaultKeyboardShorcutsService implements KeyboardShortcutsApi {
  private eventIdentifiers = 1;

  private readonly keyBindings = new Map<number, KeyBinding>();

  public registerKeyDownThenUp(
    combination: string,
    label: string,
    onKeyDown: () => Thenable<void>,
    onKeyUp: () => Thenable<void>,
    opts?: KeyBindingServiceOpts
  ) {
    console.debug(`Registering shortcut (down/up) for ${combination} - ${label}: ${opts?.repeat}`);

    const keyBinding = {
      combination,
      label,
      listener: (e: KeyboardEvent) => {
        if (e.repeat && !opts?.repeat) {
          return true;
        }

        if (e.type === "keydown") {
          if (setsEqual(this.combinationKeySet(combination), this.pressedKeySet(e))) {
            console.debug(`Fired (down) [${combination}]!`);
            onKeyDown();
          }
        } else if (e.type === "keyup") {
          if (setsEqual(this.combinationKeySet(combination), new Set([MODIFIER_KEY_NAMES.get(e.code)]))) {
            console.debug(`Fired (up) [${combination}]!`);
            onKeyUp();
          }
        }

        return true;
      },
      opts
    };

    this.keyBindings.set(this.eventIdentifiers, keyBinding);

    this.keyBindingElement(keyBinding).addEventListener("keydown", keyBinding.listener);
    this.keyBindingElement(keyBinding).addEventListener("keyup", keyBinding.listener);

    return this.eventIdentifiers++;
  }

  public registerKeyPress(
    combination: string,
    label: string,
    onKeyPress: () => Thenable<void>,
    opts?: KeyBindingServiceOpts
  ) {
    console.debug(`Registering shortcut (press) for ${combination} - ${label}: ${opts?.repeat}`);

    const keyBinding = {
      combination,
      label,
      listener: (e: KeyboardEvent) => {
        if (e.repeat && !opts?.repeat) {
          return true;
        }

        if (setsEqual(this.combinationKeySet(combination), this.pressedKeySet(e))) {
          console.debug(`Fired (press) [${combination}]!`);
          onKeyPress();
        }

        return true;
      },
      opts
    };

    this.keyBindings.set(this.eventIdentifiers, keyBinding);
    this.keyBindingElement(keyBinding).addEventListener("keydown", keyBinding.listener);
    return this.eventIdentifiers++;
  }

  public registerKeyPressOnce(combination: string, onKeyPress: () => Thenable<void>, opts?: KeyBindingServiceOpts) {
    const id = this.registerKeyPress(
      combination,
      "",
      async () => {
        onKeyPress();
        this.deregister(id);
      },
      opts ? { ...opts!, hidden: true } : opts
    );

    return id;
  }

  private keyBindingElement(keyBinding?: KeyBinding) {
    return keyBinding?.opts?.element ?? document.querySelector(".session-container") ?? window;
  }

  public deregister(id: number): void {
    const keyBinding = this.keyBindings.get(id);
    this.keyBindingElement(keyBinding).removeEventListener("keypress", keyBinding?.listener!);
    this.keyBindingElement(keyBinding).removeEventListener("keydown", keyBinding?.listener!);
    this.keyBindingElement(keyBinding).removeEventListener("keyup", keyBinding?.listener!);
    this.keyBindings.delete(id);
  }

  private combinationKeySet(combination: string) {
    const keys = combination
      .split("+")
      .map(k => k.toLowerCase())
      .map(k => KEY_CODES.get(k) ?? k);

    if (this.osName() === "macOS") {
      return new Set(keys.map(k => (k === ModKeys.CTRL ? ModKeys.META : k)));
    } else {
      return new Set(keys);
    }
  }

  private osName() {
    let osName = "unknown";

    if (navigator.appVersion.indexOf("Win") !== -1) {
      osName = "Windows";
    } else if (navigator.appVersion.indexOf("Mac") !== -1) {
      osName = "macOS";
    } else if (navigator.appVersion.indexOf("X11") !== -1) {
      osName = "UNIX";
    } else if (navigator.appVersion.indexOf("Linux") !== -1) {
      osName = "Linux";
    }

    return osName;
  }

  private pressedKeySet(e: KeyboardEvent) {
    const pressedKeySet = new Set();
    if (e.ctrlKey) {
      pressedKeySet.add(ModKeys.CTRL);
    }
    if (e.metaKey) {
      pressedKeySet.add(ModKeys.META);
    }
    if (e.altKey) {
      pressedKeySet.add(ModKeys.ALT);
    }
    if (e.shiftKey) {
      pressedKeySet.add(ModKeys.SHIFT);
    }
    if (Array.from(MODIFIER_KEY_NAMES.keys()).indexOf(e.code) === -1) {
      pressedKeySet.add(e.code);
    }
    return pressedKeySet;
  }

  public registered() {
    return removeDuplicates(
      Array.from(this.keyBindings.values()).filter(k => !k.opts?.hidden),
      "combination"
    );
  }
}

function removeDuplicates<T>(myArr: T[], prop: keyof T) {
  return myArr.filter((obj, pos, arr) => {
    return arr.map(mapObj => mapObj[prop]).indexOf(obj[prop]) === pos;
  });
}

function setsEqual(lhs: Set<unknown>, rhs: Set<unknown>) {
  if (lhs.size !== rhs.size) {
    return false;
  }

  for (const a of lhs) {
    if (!rhs.has(a)) {
      return false;
    }
  }

  return true;
}
