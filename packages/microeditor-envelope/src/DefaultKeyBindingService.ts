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

export interface KeyBinding {
  combination: string;
  label: string;
  opts?: KeyBindingServiceOpts;
  listener: (e: KeyboardEvent) => boolean;
}

export interface KeyBindingServiceOpts {
  hidden?: boolean;
  element?: HTMLElement;
  event: "keypress" | "keyup" | "keydown";
}

export interface KeyBindingService {
  register(combination: string, label: string, action: () => Thenable<void>, opts?: KeyBindingServiceOpts): number;
  registerOnce(combination: string, action: () => Thenable<void>, opts?: KeyBindingServiceOpts): number;
  deregister(id: number): void;
  registered(): KeyBinding[];
}

export class DefaultKeyBindingService implements KeyBindingService {
  private eventIdentifiers = 1;

  private readonly keyBindings = new Map<number, KeyBinding>();
  private readonly keyCodes = new Map<string, string>([
    ["/", "Slash"],
    ["esc", "Escape"],
    ["c", "KeyC"],
    ["v", "KeyV"],
    ["s", "KeyS"],
    ["x", "KeyX"]
  ]);

  public registerOnce(combination: string, action: () => Thenable<void>, opts?: KeyBindingServiceOpts) {
    const id = this.register(
      combination,
      "",
      async () => {
        action();
        this.deregister(id);
      },
      opts ? { ...opts!, hidden: true } : opts
    );

    return id;
  }

  public register(combination: string, label: string, action: () => Thenable<void>, opts?: KeyBindingServiceOpts) {
    console.info(`registering shortcut for ${combination} - ${label}`);

    // if (Array.from(this.keyBindings.values()).filter(k => k.combination === combination).length > 0) {
    //   console.info("cannot register two combinations twice");
    //   return -1;
    // }

    const listener = (e: KeyboardEvent) => {
      // e.preventDefault();
      e.stopPropagation();

      if (e.repeat) {
        return false;
      }

      console.info(this.combinationKeySet(combination), this.pressedKeySet(e));

      if (setsEqual(this.combinationKeySet(combination), this.pressedKeySet(e))) {
        console.debug(`Fired [${combination}]!`);
        action();
      }

      return false;
    };

    (opts?.element ?? window).addEventListener(opts?.event ?? "keydown", listener);

    this.keyBindings.set(this.eventIdentifiers, { combination, label, listener, opts });
    return this.eventIdentifiers++;
  }

  public deregister(id: number): void {
    const keyBinding = this.keyBindings.get(id);
    (keyBinding?.opts?.element ?? window).removeEventListener(
      keyBinding?.opts?.event ?? "keydown",
      keyBinding?.listener!
    );
    this.keyBindings.delete(id);
  }

  private combinationKeySet(combination: string) {
    return new Set(
      combination
        .split("+")
        .map(k => k.toLowerCase())
        .map(k => this.keyCodes.get(k) ?? k)
    );
  }

  private pressedKeySet(e: KeyboardEvent) {
    const pressedKeySet = new Set();
    if (e.ctrlKey) {
      pressedKeySet.add("ctrl");
    }
    if (e.metaKey) {
      pressedKeySet.add("meta");
    }
    if (e.altKey) {
      pressedKeySet.add("alt");
    }
    if (e.shiftKey) {
      pressedKeySet.add("shift");
    }
    pressedKeySet.add(e.code);
    return pressedKeySet;
  }

  public registered() {
    return Array.from(this.keyBindings.values()).filter(k => !k.opts?.hidden);
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
