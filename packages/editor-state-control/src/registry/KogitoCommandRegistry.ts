/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { KogitoCommand } from "../Command";

export interface KogitoCommandRegistry<T> {
  register(id: string, command: T): void;
  peek(): T | null;
  pop(): T | null;
  isEmpty(): boolean;
  getCommands(): T[];
  clear(): void;
  setOnNewCommand(onNewCommand: OnNewCommand): void;
  setRegistryChangeListener(registryChangeListener: () => void): void;
}

export class KogitoCommandRegistryImpl<T> implements KogitoCommandRegistry<T>{

  private maxStackSize: number = 200;
  private commands: Array<KogitoCommand<T>> = [];

  private onNewCommand: OnNewCommand;
  private changeListener: () => void;

  public register(id: string, command: T): void {
    if (id && command) {
      if ((this.commands.length + 1) > this.maxStackSize) {
        this.commands.shift();
      }

      const kogitoCommand = new KogitoCommand(id, command);

      this.commands.push(kogitoCommand);

      this.notifyNewCommand(kogitoCommand);
      this.notifyRegistryChange();
    }
  }

  public peek(): T | null {
    if (this.commands && this.commands.length > 0) {
      return this.commands[this.commands.length -1].get();
    }
    return null;
  }

  public pop(): T | null {
    if (this.commands && this.commands.length > 0) {
      const command = this.commands.pop();

      if (command) {
        this.notifyRegistryChange();
        return command.get();
      }
    }
    return null;
  }

  public isEmpty(): boolean {
    return this.commands.length === 0;
  }

  public getCommands(): T[] {
    return this.commands.map((command) => command.get());
  }

  public clear(): void {
    this.commands = [];
    this.notifyRegistryChange();
  }

  public setMaxSize(size: number): void {
    this.maxStackSize = size;
  }

  public setOnNewCommand(onNewCommand: OnNewCommand): void {
    this.onNewCommand = onNewCommand;
  };

  public setRegistryChangeListener(changeListener: () => void): void {
    this.changeListener = changeListener;
  }

  private notifyNewCommand(newCommand: KogitoCommand<T>): void {
    if (this.onNewCommand) {
      this.onNewCommand.notifyNewCommand(newCommand);
    }
  }

  private notifyRegistryChange() {
    if (this.changeListener != null) {
      this.changeListener();
    }
  }
}

export interface OnNewCommand {
  notifyNewCommand(newCommand: KogitoCommand<any>): void;
}