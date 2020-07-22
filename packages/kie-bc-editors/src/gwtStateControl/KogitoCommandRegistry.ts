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

import { GwtStateControlCommand } from "./GwtStateControlCommand";
import { KogitoEdit } from "@kogito-tooling/microeditor-envelope-protocol";
import { KogitoEditorChannelApi, MessageBusClient } from "@kogito-tooling/microeditor-envelope-protocol";

/**
 * PUBLIC GWT EDITORS API
 * Represents a command registry API to be used on command-based editors.
 */
export interface KogitoCommandRegistry<T> {
  register(id: string, command: T): void;
  peek(): T | null;
  pop(): T | null;
  isEmpty(): boolean;
  getCommands(): T[];
  clear(): void;
  setMaxSize(size: number): void;
}

export class DefaultKogitoCommandRegistry<T> implements KogitoCommandRegistry<T> {
  private maxStackSize = 200;
  private commands: Array<GwtStateControlCommand<T>> = [];
  private undoneCommands: string[] = [];

  constructor(private readonly messageBus: MessageBusClient<KogitoEditorChannelApi>) {}

  private onNewCommand(newCommand: GwtStateControlCommand<T>) {
    if (!this.undoneCommands.includes(newCommand.getId())) {
      // Only notifying if the command is a new command. Also clearing the removedCommands registry, since the undone
      // commands won't be redone
      this.messageBus.notify("receive_newEdit", new KogitoEdit(newCommand.getId()));
      this.undoneCommands = [];
    } else {
      // Removing the command from the removedCommands registry since it's been registered again (redo).
      this.undoneCommands.splice(this.undoneCommands.indexOf(newCommand.getId()), 1);
    }
  }

  public register(id: string, command: T): void {
    if (id && command) {
      if (this.commands.length + 1 > this.maxStackSize) {
        this.commands.shift();
      }

      const kogitoCommand = new GwtStateControlCommand(id, command);

      this.commands.push(kogitoCommand);
      this.onNewCommand(kogitoCommand);
    }
  }

  public peek(): T | null {
    if (this.commands?.length > 0) {
      return this.commands[this.commands.length - 1].get();
    }
    return null;
  }

  public pop(): T | null {
    if (this.commands && this.commands.length > 0) {
      const command = this.commands.pop();

      if (command) {
        // If a command is removed (by an undo) we are keeping it's id on a registry to avoid notifying if the command
        // is registered again (redo)
        this.undoneCommands.push(command.getId());
        return command.get();
      }
    }
    return null;
  }

  public isEmpty(): boolean {
    return this.commands.length === 0;
  }

  public getCommands(): T[] {
    return this.commands.map(command => command.get());
  }

  public clear(): void {
    this.commands = [];
    this.undoneCommands = [];
  }

  public setMaxSize(size: number): void {
    this.maxStackSize = size;
  }

  public getUndoneCommands(): string[] {
    return this.undoneCommands;
  }
}
