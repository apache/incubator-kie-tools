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

import * as React from "react";
import { DefaultKeyboardShortcutsService } from "../DefaultKeyboardShortcutsService";
import { ChannelType, OperatingSystem } from "@kogito-tooling/editor-envelope-protocol";
import { fireEvent, render } from "@testing-library/react";

describe("DefaultKeyboardShortcutsService", () => {
  test("keyPress", async () => {
    const keyboardShortcutsService = new DefaultKeyboardShortcutsService({
      editorContext: { operatingSystem: OperatingSystem.LINUX, channel: ChannelType.ONLINE }
    });

    const [action] = getActionForKeyPress("ctrl+a", keyboardShortcutsService);
    expect(keyboardShortcutsService.registered().length).toStrictEqual(1);

    fire("keydown", { ctrlKey: true, code: "KeyA" });

    expect(action).toHaveBeenCalled();
    expect(keyboardShortcutsService.registered().length).toStrictEqual(1);
  });

  test("keyPress on ignored tag", async () => {
    const keyboardShortcutsService = new DefaultKeyboardShortcutsService({
      editorContext: { operatingSystem: OperatingSystem.LINUX, channel: ChannelType.ONLINE }
    });

    const input = render(<input data-testid={"an-input"}/>).getByTestId("an-input");
    const [actionOnInput] = getActionForKeyPress("ctrl+a", keyboardShortcutsService, { element: input });
    fireEvent(input, new KeyboardEvent("keydown", { ctrlKey: true, code: "KeyA" }));
    expect(actionOnInput).not.toHaveBeenCalled();

    const div = render(<div data-testid={"a-div"}/>).getByTestId("a-div");
    const [actionOnDiv] = getActionForKeyPress("ctrl+a", keyboardShortcutsService, { element: div });
    fireEvent(div, new KeyboardEvent("keydown", { ctrlKey: true, code: "KeyA" }));
    expect(actionOnDiv).toHaveBeenCalled();

    expect(keyboardShortcutsService.registered().length).toStrictEqual(2);
  });

  test("keyPress on macOS", async () => {
    const keyboardShortcutsService = new DefaultKeyboardShortcutsService({
      editorContext: { operatingSystem: OperatingSystem.MACOS, channel: ChannelType.ONLINE }
    });

    const [action] = getActionForKeyPress("ctrl+a", keyboardShortcutsService);
    expect(keyboardShortcutsService.registered().length).toStrictEqual(1);

    fire("keydown", { metaKey: true, code: "KeyA" });

    expect(action).toHaveBeenCalled();
    expect(keyboardShortcutsService.registered().length).toStrictEqual(1);
  });

  test("keyDown then keyUp", async () => {
    const keyboardShortcutsService = new DefaultKeyboardShortcutsService({
      editorContext: { operatingSystem: OperatingSystem.LINUX, channel: ChannelType.ONLINE }
    });

    const [actionDown, actionUp] = getActionsForKeyUpAndDown("ctrl+a", keyboardShortcutsService);
    expect(keyboardShortcutsService.registered().length).toStrictEqual(1);

    fire("keydown", { ctrlKey: true, code: "KeyA" });
    expect(actionDown).toHaveBeenCalled();

    fire("keyup", { code: "CtrlRight" });
    expect(actionUp).toHaveBeenCalled();

    expect(keyboardShortcutsService.registered().length).toStrictEqual(1);

    //

    const [actionDown2, actionUp2] = getActionsForKeyUpAndDown("ctrl+b", keyboardShortcutsService);
    expect(keyboardShortcutsService.registered().length).toStrictEqual(2);

    fire("keydown", { ctrlKey: true, code: "KeyB" });
    expect(actionDown2).toHaveBeenCalled();

    fire("keyup", { code: "KeyB" });
    expect(actionUp2).toHaveBeenCalled();

    expect(keyboardShortcutsService.registered().length).toStrictEqual(2);
  });

  test("keyPressOnce", async () => {
    const keyboardShortcutsService = new DefaultKeyboardShortcutsService({
      editorContext: { operatingSystem: OperatingSystem.LINUX, channel: ChannelType.ONLINE }
    });

    const [action] = getActionForKeyPressOnce("ctrl+c", keyboardShortcutsService);
    expect(keyboardShortcutsService.registered().length).toStrictEqual(1);

    fire("keydown", { ctrlKey: true, code: "KeyC" });

    expect(action).toHaveBeenCalled();
    expect(keyboardShortcutsService.registered().length).toStrictEqual(0);
  });

  test("deregister", async () => {
    const keyboardShortcutsService = new DefaultKeyboardShortcutsService({
      editorContext: { operatingSystem: OperatingSystem.LINUX, channel: ChannelType.ONLINE }
    });

    const [action, id] = getActionForKeyPress("ctrl+c", keyboardShortcutsService);
    expect(keyboardShortcutsService.registered().length).toStrictEqual(1);

    fire("keydown", { ctrlKey: true, code: "KeyC" });
    expect(action).toHaveBeenCalled();

    keyboardShortcutsService.deregister(id as number);
    expect(keyboardShortcutsService.registered().length).toStrictEqual(0);
  });
});

function fire(type: "keydown" | "keyup", opts: KeyboardEventInit) {
  fireEvent(window, new KeyboardEvent(type, opts));
}

function getActionForKeyPress(combination: string, api: DefaultKeyboardShortcutsService, opts: any = {}) {
  const fn = jest.fn();
  const id = api.registerKeyPress(
    combination,
    "Label",
    () => {
      fn();
      return Promise.resolve();
    },
    opts
  );

  return [fn, id];
}

function getActionForKeyPressOnce(combination: string, api: DefaultKeyboardShortcutsService) {
  const fn = jest.fn();
  const id = api.registerKeyPressOnce(
    combination,
    () => {
      fn();
      return Promise.resolve();
    },
    {}
  );

  return [fn, id];
}

function getActionsForKeyUpAndDown(combination: string, api: DefaultKeyboardShortcutsService) {
  const down = jest.fn();
  const up = jest.fn();
  const id = api.registerKeyDownThenUp(
    combination,
    "Label",
    () => {
      down();
      return Promise.resolve();
    },
    () => {
      up();
      return Promise.resolve();
    },
    {}
  );

  return [down, up, id];
}
