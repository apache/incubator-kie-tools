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


import { StateControl, StateControlApi } from "../../api/stateControl";

const innerMessageHandler = jest.fn();

let messageBus;

let stateControl:StateControl;
let stateControlApi:StateControlApi;

describe("StateControl", () => {

  beforeEach(() => {
    stateControl = new StateControl();
    messageBus = new innerMessageHandler();
    stateControlApi = stateControl.exposeApi(messageBus);
  });

  test("test undo redo without commands", () => {
    expect(() => stateControl.undo()).not.toThrow();
    expect(() => stateControl.redo()).not.toThrow();
  });

  test("test undo redo with commands", () => {

    const undoCommand = jest.fn();
    const redoCommand = jest.fn();

    stateControlApi.setUndoCommand(undoCommand);
    stateControlApi.setRedoCommand(redoCommand);

    stateControl.undo();

    expect(undoCommand).toBeCalled();

    stateControl.redo();

    expect(redoCommand).toBeCalled();
  });
});