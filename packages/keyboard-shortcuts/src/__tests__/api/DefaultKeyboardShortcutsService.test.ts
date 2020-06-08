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

import { DefaultKeyboardShortcutsService, KeyboardShortcutsApi } from "../../api";
import { ChannelType, OperatingSystem } from "@kogito-tooling/core-api";
import { fireEvent } from "@testing-library/react";

describe("DefaultKeyboardShortcutsService", () => {
  test("keyPress", async () => {
    const keyboardShortcutsApi = new DefaultKeyboardShortcutsService({
      operatingSystem: OperatingSystem.LINUX,
      channel: ChannelType.ONLINE
    });

    const [wasFired] = resolveWhenKeyPressed("ctrl+a", keyboardShortcutsApi);
    keyboardShortcutsApi.executeDelayedShortcutsRegistration();
    expect(keyboardShortcutsApi.registered().length).toStrictEqual(1);

    fire("keydown", { ctrlKey: true, code: "KeyA" });
    await wasFired;
    expect(keyboardShortcutsApi.registered().length).toStrictEqual(1);
  });

  test("keyPress on macOS", async () => {
    const keyboardShortcutsApi = new DefaultKeyboardShortcutsService({
      operatingSystem: OperatingSystem.MACOS,
      channel: ChannelType.ONLINE
    });

    const [wasFired] = resolveWhenKeyPressed("ctrl+a", keyboardShortcutsApi);
    keyboardShortcutsApi.executeDelayedShortcutsRegistration();
    expect(keyboardShortcutsApi.registered().length).toStrictEqual(1);
    fire("keydown", { metaKey: true, code: "KeyA" });
    await wasFired;

    expect(keyboardShortcutsApi.registered().length).toStrictEqual(1);
  });

  test("keyDown then keyUp", async () => {
    const keyboardShortcutsApi = new DefaultKeyboardShortcutsService({
      operatingSystem: OperatingSystem.LINUX,
      channel: ChannelType.ONLINE
    });

    const [wasFiredDownA, wasFiredUpA] = resolveWhenKeyDownThenUp("ctrl+a", keyboardShortcutsApi);
    keyboardShortcutsApi.executeDelayedShortcutsRegistration();
    expect(keyboardShortcutsApi.registered().length).toStrictEqual(1);
    fire("keydown", { ctrlKey: true, code: "KeyA" });
    await wasFiredDownA;

    fire("keyup", { code: "CtrlRight" });
    await wasFiredUpA;

    expect(keyboardShortcutsApi.registered().length).toStrictEqual(1);

    //

    const [wasFiredDownB, wasFiredUpB] = resolveWhenKeyDownThenUp("ctrl+b", keyboardShortcutsApi);
    keyboardShortcutsApi.executeDelayedShortcutsRegistration();
    expect(keyboardShortcutsApi.registered().length).toStrictEqual(2);
    fire("keydown", { ctrlKey: true, code: "KeyB" });
    await wasFiredDownB;

    fire("keyup", { code: "KeyB" });
    await wasFiredUpB;

    expect(keyboardShortcutsApi.registered().length).toStrictEqual(2);
  });

  test("keyPressOnce", async () => {
    const keyboardShortcutsApi = new DefaultKeyboardShortcutsService({
      operatingSystem: OperatingSystem.LINUX,
      channel: ChannelType.ONLINE
    });

    const [wasFired] = resolveWhenKeyPressedOnce("ctrl+c", keyboardShortcutsApi);
    keyboardShortcutsApi.executeDelayedShortcutsRegistration();
    expect(keyboardShortcutsApi.registered().length).toStrictEqual(1);

    fire("keydown", { ctrlKey: true, code: "KeyC" });
    await wasFired;

    expect(keyboardShortcutsApi.registered().length).toStrictEqual(0);
  });

  test("deregister", async () => {
    const keyboardShortcutsApi = new DefaultKeyboardShortcutsService({
      operatingSystem: OperatingSystem.LINUX,
      channel: ChannelType.ONLINE
    });

    const [wasFired, id] = resolveWhenKeyPressed("ctrl+c", keyboardShortcutsApi);
    keyboardShortcutsApi.executeDelayedShortcutsRegistration();
    expect(keyboardShortcutsApi.registered().length).toStrictEqual(1);

    fire("keydown", { ctrlKey: true, code: "KeyC" });
    await wasFired;

    keyboardShortcutsApi.deregister(id);
    expect(keyboardShortcutsApi.registered().length).toStrictEqual(0);
  });
});

function fire(type: "keydown" | "keyup", opts) {
  fireEvent(window, new KeyboardEvent(type, opts));
}

function resolveWhenKeyPressed(combination: string, api: KeyboardShortcutsApi) {
  let id;
  const p = new Promise(res => {
    id = api.registerKeyPress(
      combination,
      "Label",
      () => {
        res();
        return Promise.resolve();
      },
      {}
    );
  });

  return [p, id];
}

function resolveWhenKeyPressedOnce(combination: string, api: KeyboardShortcutsApi) {
  let id;
  const p = new Promise(res => {
    id = api.registerKeyPressOnce(
      combination,
      () => {
        res();
        return Promise.resolve();
      },
      {}
    );
  });

  return [p, id];
}

function resolveWhenKeyDownThenUp(combination: string, api: KeyboardShortcutsApi) {
  let down;
  let up;
  let id;

  down = new Promise(resDown => {
    up = new Promise(resUp => {
      id = api.registerKeyDownThenUp(
        combination,
        "Label",
        () => {
          resDown();
          return Promise.resolve();
        },
        () => {
          resUp();
          return Promise.resolve();
        },
        {}
      );
    });
  });

  return [down, up, id];
}
