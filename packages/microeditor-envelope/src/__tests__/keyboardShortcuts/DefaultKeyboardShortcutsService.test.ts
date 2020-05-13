import { DefaultKeyboardShortcutsService, KeyboardShortcutsApi } from "../../api/keyboardShortcuts";
import { ChannelType, OperatingSystem } from "@kogito-tooling/core-api";
import { fireEvent } from "@testing-library/react";

describe("DefaultKeyboardShortcutsService", () => {
  test("keyPress", async () => {
    const keyboardShortcutsApi = new DefaultKeyboardShortcutsService({
      operatingSystem: OperatingSystem.LINUX,
      channel: ChannelType.ONLINE
    });

    const [wasFired] = resolveWhenKeyPressed("ctrl+a", keyboardShortcutsApi);
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
    expect(keyboardShortcutsApi.registered().length).toStrictEqual(1);
    fire("keydown", { ctrlKey: true, code: "KeyA" });
    await wasFiredDownA;

    fire("keyup", { code: "CtrlRight" });
    await wasFiredUpA;

    expect(keyboardShortcutsApi.registered().length).toStrictEqual(1);

    //

    const [wasFiredDownB, wasFiredUpB] = resolveWhenKeyDownThenUp("ctrl+b", keyboardShortcutsApi);
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
