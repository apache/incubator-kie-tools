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

import { StateControl } from "../../stateControl/StateControl";

describe("StateControl", () => {
  let stateControl: StateControl;

  beforeEach(() => {
    stateControl = new StateControl();
  });

  describe("commandStack", () => {
    test("should be empty", () => {
      expect(stateControl.getCommandStack()).toEqual([]);
    });

    test("should set new commands on the stack", () => {
      const commands = ["1", "2", "3", "4"];
      stateControl.setCommandStack(commands);
      expect(stateControl.getCommandStack()).toEqual(commands);
    });

    test("should set new commands on the stack", () => {
      ["1", "2", "3", "4"].forEach((command, index, commands) => {
        stateControl.updateCommandStack(command);
        expect(stateControl.getCommandStack()).toEqual(commands.slice(0, index + 1));
      });
    });
  });

  describe("currentCommand", () => {
    test("should be undefined", () => {
      expect(stateControl.getCurrentCommand()).toBeUndefined();
    });

    test("should be the last added command on the stack", () => {
      ["1", "2", "3", "4"].forEach(command => {
        stateControl.updateCommandStack(command);
        expect(stateControl.getCurrentCommand()).toEqual(command);
      });
    });
  });

  describe("savedCommand", () => {
    test("should be undefined", () => {
      expect(stateControl.getSavedCommand()).toBeUndefined();
      stateControl.getSavedCommand();
      expect(stateControl.getSavedCommand()).toBeUndefined();

      ["1", "2", "3", "4"].forEach(command => {
        stateControl.updateCommandStack(command);
        expect(stateControl.getSavedCommand()).toBeUndefined();
      });
    });

    test("should be the last command added", () => {
      ["1", "2", "3", "4"].forEach(command => {
        stateControl.updateCommandStack(command);
        stateControl.setSavedCommand();
        expect(stateControl.getSavedCommand()).toEqual(command);
      });
    });

    test("should be the saved command", () => {
      const specialCommand = "special";
      stateControl.updateCommandStack(specialCommand);
      stateControl.setSavedCommand();

      ["1", "2", "3", "4"].forEach(command => {
        stateControl.updateCommandStack(command);
        expect(stateControl.getSavedCommand()).toEqual(specialCommand);
      });
    });
  });

  describe("isDirty::StateControl", () => {
    test("should be false", () => {
      expect(stateControl.isDirty()).toBeFalsy();

      ["1", "2", "3", "4"].forEach(command => {
        stateControl.updateCommandStack(command);
        stateControl.setSavedCommand();
        expect(stateControl.isDirty()).toBeFalsy();
      });
    });

    test("should be true", () => {
      ["1", "2", "3", "4"].forEach(command => {
        stateControl.updateCommandStack(command);
        expect(stateControl.isDirty()).toBeTruthy();
      });
    });

    test("should be true", () => {
      const specialCommand = "special";
      stateControl.updateCommandStack(specialCommand);
      stateControl.setSavedCommand();
      expect(stateControl.isDirty()).toBeFalsy();

      ["1", "2", "3", "4"].forEach(command => {
        stateControl.updateCommandStack(command);
        expect(stateControl.isDirty()).toBeTruthy();
      });
      stateControl.setSavedCommand();
      expect(stateControl.isDirty()).toBeFalsy();
    });
  });

  describe("updateCommandStack::StateControl", () => {
    test("shouldn't erase commands", () => {
      stateControl.updateCommandStack("1");
      expect(stateControl.getCommandStack()).toEqual(["1"]);

      const commands = ["1", "2", "3", "4"];
      commands.forEach(command => stateControl.updateCommandStack(command));

      expect(stateControl.getCommandStack()).toEqual(commands);
    });

    test("should erase", () => {
      const commands = ["1", "2", "3", "4"];
      commands.forEach(command => stateControl.updateCommandStack(command));
      stateControl.setCurrentCommand("2");
      stateControl.updateCommandStack("5");

      expect(stateControl.getCommandStack()).toEqual(["1", "2", "5"]);
    });
  });

  describe("undo::StateControl", () => {
    test("shouldn't undo an empty command stack", () => {
      stateControl.undo();
      expect(stateControl.getCurrentCommand()).toBeUndefined();
    });

    test("should undo to previous command and maintain command stack", () => {
      const commands = ["1", "2", "3", "4"];
      commands.forEach(command => stateControl.updateCommandStack(command));

      ["4", "3", "2", "1"].forEach(command => {
        expect(stateControl.getCurrentCommand()).toEqual(command);
        stateControl.undo();
      });
      expect(stateControl.getCurrentCommand()).toBeUndefined();
      expect(stateControl.getCommandStack()).toEqual(commands);
    });
  });

  describe("redo::StateControl", () => {
    test("shouldn't redo an empty command stack", () => {
      stateControl.redo();
      expect(stateControl.getCurrentCommand()).toBeUndefined();
    });

    test("should redo to the next possible command on the command stack", () => {
      const commands = ["1", "2", "3", "4"];
      commands.forEach(command => stateControl.updateCommandStack(command));

      commands.forEach(command => stateControl.undo());
      expect(stateControl.getCurrentCommand()).toBeUndefined();

      commands.forEach(command => {
        stateControl.redo();
        expect(stateControl.getCurrentCommand()).toEqual(command);
      });

      stateControl.redo();
      expect(stateControl.getCurrentCommand()).toEqual(commands.pop());
    });
  });

  describe("subscribe::StateControl", () => {
    let isDirty = false;
    const setIsDirty = (newState: boolean) => {
      isDirty = newState;
    };

    test("should update the state when a change occurs", () => {
      stateControl.subscribe(setIsDirty);
      expect(stateControl.isDirty()).toBeFalsy();

      stateControl.updateCommandStack("1");
      expect(stateControl.isDirty()).toBeTruthy();
      expect(isDirty).toBeTruthy();

      stateControl.setSavedCommand();
      expect(stateControl.isDirty()).toBeFalsy();
      expect(isDirty).toBeFalsy();
    });
  });

  describe("unsubscribe::StateControl", () => {
    let isDirty = false;
    const setIsDirty = (newState: boolean) => {
      isDirty = newState;
    };

    test("should unsubscribe and make inconsistent data", () => {
      stateControl.subscribe(setIsDirty);
      stateControl.updateCommandStack("1");
      stateControl.unsubscribe(setIsDirty);
      stateControl.setSavedCommand();

      expect(stateControl.isDirty()).toBeFalsy();
      expect(isDirty).toBeTruthy();
    });
  });

  describe("complete workflow", () => {
    test("shouldn't redo an empty command stack", () => {
      stateControl.updateCommandStack("1");
      stateControl.updateCommandStack("2");
      expect(stateControl.isDirty()).toBeTruthy();
      stateControl.setSavedCommand();
      expect(stateControl.isDirty()).toBeFalsy();

      stateControl.undo();
      expect(stateControl.isDirty()).toBeTruthy();
      stateControl.redo();
      expect(stateControl.isDirty()).toBeFalsy();

      expect(stateControl.getCommandStack()).toEqual(["1", "2"]);
      stateControl.undo();
      expect(stateControl.getCurrentCommand()).toEqual("1");
      stateControl.updateCommandStack("3");
      expect(stateControl.getCommandStack()).toEqual(["1", "3"]);
      stateControl.redo();
      expect(stateControl.getCurrentCommand()).toEqual("3");
    });
  });
});
