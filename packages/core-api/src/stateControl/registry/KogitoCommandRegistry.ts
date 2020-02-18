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
}

export class KogitoCommandRegistryImpl<T> implements KogitoCommandRegistry<T>{

  private maxStackSize: number = 200;
  private commands: Array<KogitoCommand<T>> = [];

  private onNewCommand: (kogitoCommand: KogitoCommand<T>) => void;

  constructor(onNewCommand: (kogitoCommand: KogitoCommand<T>) => void) {
    this.onNewCommand = onNewCommand;
  }

  public register(id: string, command: T): void {
    if (id && command) {
      if ((this.commands.length + 1) > this.maxStackSize) {
        this.commands.shift();
      }

      const kogitoCommand = new KogitoCommand(id, command);

      this.commands.push(kogitoCommand);
      this.onNewCommand(kogitoCommand);
    }
  }

  public peek(): T | null {
    if (this.commands?.length > 0) {
      return this.commands[this.commands.length -1].get();
    }
    return null;
  }

  public pop(): T | null {
    if (this.commands && this.commands.length > 0) {
      const command = this.commands.pop();

      if (command) {
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
  }

  public setMaxSize(size: number): void {
    this.maxStackSize = size;
  }
}