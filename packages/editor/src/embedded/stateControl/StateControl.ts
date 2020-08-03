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

type Command = undefined | string;

export class StateControl {
  private commandStack: string[];
  private currentCommand: Command;
  private savedCommand: Command;
  private registeredCallbacks: Array<(isDirty: boolean) => void>;

  constructor() {
    this.commandStack = [];
    this.registeredCallbacks = [];
  }

  public subscribe(callback: (isDirty: boolean) => void) {
    this.registeredCallbacks.push(callback);
    return callback;
  }

  public unsubscribe(callback: (isDirty: boolean) => void) {
    const index = this.registeredCallbacks.indexOf(callback);
    if (index > -1) {
      this.registeredCallbacks.splice(index, 1);
    }
  }

  public getSavedCommand() {
    return this.savedCommand;
  }

  public setSavedCommand() {
    this.savedCommand = this.currentCommand;
    this.registeredCallbacks.forEach(setIsDirty => setIsDirty(this.isDirty()));
  }

  public getCurrentCommand() {
    return this.currentCommand;
  }

  public setCurrentCommand(command: Command) {
    this.currentCommand = command;
    this.registeredCallbacks.forEach(setIsDirty => setIsDirty(this.isDirty()));
  }

  public getCommandStack() {
    return this.commandStack;
  }

  public setCommandStack(commandStack: string[]) {
    this.commandStack = commandStack;
  }

  public getRegisteredCallbacks() {
    return this.registeredCallbacks;
  }

  public isDirty() {
    return this.currentCommand !== this.savedCommand;
  }

  public undo() {
    const indexOfCurrentCommand = this.commandStack.indexOf(this.currentCommand!);

    let commandUndone: Command;
    if (this.commandStack[indexOfCurrentCommand - 1]) {
      commandUndone = this.commandStack[indexOfCurrentCommand - 1];
    }
    this.setCurrentCommand(commandUndone);
  }

  public redo() {
    const indexOfCurrentCommand = this.commandStack.indexOf(this.currentCommand!);
    if (this.commandStack[indexOfCurrentCommand + 1]) {
      const commandRedone = this.commandStack[indexOfCurrentCommand + 1];
      this.setCurrentCommand(commandRedone);
    }
  }

  private eraseRedoCommands() {
    return this.commandStack.slice(0, this.commandStack.indexOf(this.currentCommand!) + 1);
  }

  public updateCommandStack(command: string) {
    this.commandStack = this.eraseRedoCommands().concat(command);
    this.setCurrentCommand(command);
  }
}
