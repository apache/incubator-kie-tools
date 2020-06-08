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

import { EditorContext, OperatingSystem } from "@kogito-tooling/core-api";
import { KeyBinding, KeyBindingServiceOpts, KeyboardShortcutsApi } from "./KeyboardShorcutsApi";

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

enum ShortCutsType {
  KeyDownThenUp,
  KeyDown
}

export interface DelayedRegisterKeyBinding{
  type: ShortCutsType;
  binding: KeyBinding;
}

export class DefaultKeyboardShortcutsService implements KeyboardShortcutsApi {
  private readonly editorContext: EditorContext;

  private eventIdentifiers = 1;
  private readonly keyBindings = new Map<number, KeyBinding>();
  private delayedKeyBindings = new Array<DelayedRegisterKeyBinding>();

  constructor(editorContext: EditorContext) {
    this.editorContext = editorContext;
  }

  public registerKeyDownThenUp(
    combination: string,
    label: string,
    onKeyDown: (target: EventTarget | null) => Thenable<void>,
    onKeyUp: (target: EventTarget | null) => Thenable<void>,
    opts?: KeyBindingServiceOpts
  ) {
    console.debug(`Registering shortcut (down/up) for ${combination} - ${label}: ${opts?.repeat}`);
    console.debug(opts);

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
            onKeyDown(e.target);
          }
        } else if (e.type === "keyup") {
          if (
            this.combinationKeySet(combination).has(MODIFIER_KEY_NAMES.get(e.code) ?? "") ||
            this.combinationKeySet(combination).has(e.code)
          ) {
            console.debug(`Fired (up) [${combination}]!`);
            onKeyUp(e.target);
          }
        }

        return true;
      },
      opts
    };

    this.keyBindings.set(this.eventIdentifiers, keyBinding);

    if (keyBinding?.opts?.element || document.querySelector(".session-container")) {
      this.keyBindingElement(keyBinding).addEventListener("keydown", keyBinding.listener);
      this.keyBindingElement(keyBinding).addEventListener("keyup", keyBinding.listener); 
    }
    else {
      const delayedShorcut = {
        type: ShortCutsType.KeyDownThenUp,
        binding: keyBinding,
      };
      this.delayedKeyBindings.push(delayedShorcut);
    }
    return this.eventIdentifiers++;
  }

  private keyBindingElement(keyBinding?: KeyBinding) {
    return keyBinding?.opts?.element ?? document.querySelector(".session-container") ?? window;
  }


  public registerKeyPress(
    combination: string,
    label: string,
    onKeyPress: (target: EventTarget | null) => Thenable<void>,
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
          onKeyPress(e.target);
        }
        return true;
      },
      opts
    };

    this.keyBindings.set(this.eventIdentifiers, keyBinding);

    if (keyBinding?.opts?.element || document.querySelector(".session-container")) {
      this.keyBindingElement(keyBinding).addEventListener("keydown", keyBinding.listener);
    }
    else {
      const delayedShorcut = {
        type: ShortCutsType.KeyDown,
        binding: keyBinding,
      };
      this.delayedKeyBindings.push(delayedShorcut);
    }

    return this.eventIdentifiers++;
  }

  public executeDelayedShortcutsRegistration() {
    this.delayedKeyBindings.forEach(delayedKeyBindings => {
    
      if (delayedKeyBindings.type === ShortCutsType.KeyDown) {
        this.keyBindingElement(delayedKeyBindings.binding).addEventListener("keydown", delayedKeyBindings.binding.listener);
      }
      else {
        this.keyBindingElement(delayedKeyBindings.binding).addEventListener("keydown", delayedKeyBindings.binding.listener);
        this.keyBindingElement(delayedKeyBindings.binding).addEventListener("keyup", delayedKeyBindings.binding.listener); 
      }
    }); 
    this.delayedKeyBindings = []; 
  }

  public registerKeyPressOnce(
    combination: string,
    onKeyPress: (target: EventTarget | null) => Thenable<void>,
    opts?: KeyBindingServiceOpts
  ) {
    const id = this.registerKeyPress(
      combination,
      "",
      async target => {
        onKeyPress(target);
        this.deregister(id);
      },
      opts ? { ...opts!, hidden: true } : opts
    );

    return id;
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

    if (this.editorContext.operatingSystem === OperatingSystem.MACOS) {
      return new Set(keys.map(k => (k === ModKeys.CTRL ? ModKeys.META : k)));
    } else {
      return new Set(keys);
    }
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
    return Array.from(this.keyBindings.values());
  }
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
