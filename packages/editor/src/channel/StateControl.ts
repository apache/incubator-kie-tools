/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

interface Command {
  id: string;
  undo?: () => void;
  redo?: () => void;
}

export class StateControl {
  private commandStack: Command[];
  private currentCommand?: Command;
  private savedCommand?: Command;
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
    } else {
      console.error("Can't unsubscribe callback because it wasn't subscribed.");
    }
  }

  public getSavedCommand() {
    return this.savedCommand;
  }

  public getCurrentCommand() {
    return this.currentCommand;
  }

  public getCommandStack() {
    return this.commandStack;
  }

  public getRegisteredCallbacks() {
    return this.registeredCallbacks;
  }

  public setSavedCommand() {
    this.savedCommand = this.currentCommand;
    const isDirty = this.isDirty();
    this.registeredCallbacks.forEach((callback) => callback(isDirty));
  }

  private setCurrentCommand(command: Command | undefined) {
    this.currentCommand = command;
    const isDirty = this.isDirty();
    this.registeredCallbacks.forEach((callback) => callback(isDirty));
  }

  public isDirty() {
    return this.currentCommand !== this.savedCommand;
  }

  public undo() {
    const indexOfCommandToUndo = this.commandStack.indexOf(this.currentCommand!);

    let nextCurrentCommandAfterUndo: Command | undefined;
    if (this.commandStack[indexOfCommandToUndo - 1]) {
      nextCurrentCommandAfterUndo = this.commandStack[indexOfCommandToUndo - 1];
    }

    this.currentCommand?.undo?.();
    this.setCurrentCommand(nextCurrentCommandAfterUndo);
  }

  public redo() {
    const indexOfCurrentCommand = this.commandStack.indexOf(this.currentCommand!);
    if (this.commandStack[indexOfCurrentCommand + 1]) {
      const commandRedone = this.commandStack[indexOfCurrentCommand + 1];
      commandRedone?.redo?.();
      this.setCurrentCommand(commandRedone);
    }
  }

  private eraseRedoCommands() {
    return this.commandStack.slice(0, this.commandStack.indexOf(this.currentCommand!) + 1);
  }

  public updateCommandStack(command: Command) {
    this.commandStack = this.eraseRedoCommands();

    if (command.id !== this.currentCommand?.id) {
      this.setCurrentCommand(command);
      this.commandStack = this.commandStack.concat(command);
    }
  }
}
