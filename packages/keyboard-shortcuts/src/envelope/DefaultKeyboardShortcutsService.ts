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

import { OperatingSystem } from "@kogito-tooling/channel-common-api";
import { ChannelKeyboardEvent } from "../api";
import { KeyboardShortcutRegisterOpts } from "./KeyboardShortcutRegisterOpts";

export interface KeyBinding {
  combination: string;
  label: string;
  opts?: KeyboardShortcutRegisterOpts;
  listener: (e: KeyboardEvent) => boolean;
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

const IGNORED_TAGS = ["INPUT", "TEXTAREA", "SELECT", "OPTION"];

export class DefaultKeyboardShortcutsService {
  private eventIdentifiers = 1;

  private readonly keyBindings = new Map<number, KeyBinding>();

  constructor(private readonly args: { os?: OperatingSystem }) {}

  public registerKeyDownThenUp(
    combination: string,
    label: string,
    onKeyDown: (target: EventTarget | null) => Thenable<void>,
    onKeyUp: (target: EventTarget | null) => Thenable<void>,
    opts?: KeyboardShortcutRegisterOpts
  ) {
    console.debug(`Registering shortcut (down/up) for ${combination} - ${label}: ${opts?.repeat}`);

    const keyBinding = {
      combination,
      label,
      listener: (event: KeyboardEvent | CustomEvent<ChannelKeyboardEvent>) => {
        const keyboardEvent = getProcessableKeyboardEvent(combination, event, opts);
        if (!keyboardEvent) {
          return true;
        }

        if (keyboardEvent.type === "keydown") {
          if (setsEqual(this.combinationKeySet(combination), this.pressedKeySet(keyboardEvent))) {
            console.debug(`Fired (down) [${combination}]!`);
            onKeyDown(keyboardEvent.target);
            return false;
          }
        } else if (keyboardEvent.type === "keyup") {
          if (
            this.combinationKeySet(combination).has(MODIFIER_KEY_NAMES.get(keyboardEvent.code) ?? "") ||
            this.combinationKeySet(combination).has(keyboardEvent.code)
          ) {
            console.debug(`Fired (up) [${combination}]!`);
            onKeyUp(keyboardEvent.target);
            return false;
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
    onKeyPress: (target: EventTarget | null) => Thenable<void>,
    opts?: KeyboardShortcutRegisterOpts
  ) {
    console.debug(`Registering shortcut (press) for ${combination} - ${label}: ${opts?.repeat}`);

    const keyBinding = {
      combination,
      label,
      listener: (event: KeyboardEvent | CustomEvent<ChannelKeyboardEvent>) => {
        const keyboardEvent = getProcessableKeyboardEvent(combination, event, opts);
        if (!keyboardEvent) {
          return true;
        }

        if (setsEqual(this.combinationKeySet(combination), this.pressedKeySet(keyboardEvent))) {
          console.debug(`Fired (press) [${combination}]!`);
          onKeyPress(keyboardEvent.target);
          return false;
        }

        return true;
      },
      opts
    };

    this.keyBindings.set(this.eventIdentifiers, keyBinding);

    this.keyBindingElement(keyBinding).addEventListener("keydown", keyBinding.listener);

    return this.eventIdentifiers++;
  }

  public registerKeyPressOnce(
    combination: string,
    onKeyPress: (target: EventTarget | null) => Thenable<void>,
    opts?: KeyboardShortcutRegisterOpts
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

  private keyBindingElement(keyBinding?: KeyBinding) {
    return keyBinding?.opts?.element ?? window;
  }

  private combinationKeySet(combination: string) {
    const keys = combination
      .split("+")
      .map(k => k.toLowerCase())
      .map(k => KEY_CODES.get(k) ?? k);

    if (this.args.os === OperatingSystem.MACOS) {
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

function getProcessableKeyboardEvent(
  combination: string,
  event: KeyboardEvent | CustomEvent<ChannelKeyboardEvent>,
  opts?: KeyboardShortcutRegisterOpts
): KeyboardEvent | null {
  if (event instanceof CustomEvent && IGNORED_TAGS.includes(event.detail.channelOriginalTargetTagName!)) {
    console.debug(`Ignoring execution (${combination}) because target is ${event.detail.channelOriginalTargetTagName}`);
    return null;
  }

  const keyboardEvent = event instanceof CustomEvent ? new KeyboardEvent(event.detail.type, event.detail) : event;
  if (keyboardEvent.target instanceof Element && IGNORED_TAGS.includes(keyboardEvent.target.tagName)) {
    console.debug(`Ignoring execution (${combination}) because target is ${keyboardEvent.target.tagName}`);
    return null;
  }

  if (keyboardEvent.repeat && !opts?.repeat) {
    return null;
  }

  return keyboardEvent;
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
