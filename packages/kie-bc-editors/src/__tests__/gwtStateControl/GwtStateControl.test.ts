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

import { GwtStateControlService, GwtStateControlApi } from "../../gwtStateControl";

const innerMessageHandler = jest.fn();

let messageBus;

let stateControlService: GwtStateControlService;
let stateControlApi: GwtStateControlApi;

describe("StateControl", () => {
  beforeEach(() => {
    stateControlService = new GwtStateControlService();
    messageBus = new innerMessageHandler();
    stateControlApi = stateControlService.exposeApi(messageBus);
  });

  test("test undo redo without commands", () => {
    expect(() => stateControlService.undo()).not.toThrow();
    expect(() => stateControlService.redo()).not.toThrow();
  });

  test("test undo redo with commands", () => {
    const undoCommand = jest.fn();
    const redoCommand = jest.fn();

    stateControlApi.setUndoCommand(undoCommand);
    stateControlApi.setRedoCommand(redoCommand);

    stateControlService.undo();

    expect(undoCommand).toBeCalled();

    stateControlService.redo();

    expect(redoCommand).toBeCalled();
  });
});
