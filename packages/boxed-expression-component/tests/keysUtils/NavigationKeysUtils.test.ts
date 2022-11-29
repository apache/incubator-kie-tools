/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { NavigationKeysUtils } from "@kie-tools/boxed-expression-component/dist/keysUtils";

const notAKey = "NotAKey";
const testDescription = "With input key: '%s', should return '%s'";

describe("NavigationKeysUtils", () => {
  describe("isEscape", () => {
    test.each([
      [undefined, false],
      ["", false],
      [notAKey, false],
      ["escapex", false],
      ["escape", true],
      ["Escape", true],
      ["ESCAPE", true],
    ])(testDescription, (key, expected) => {
      // @ts-ignore
      expect(NavigationKeysUtils.isEscape(key)).toBe(expected);
    });
  });

  describe("isAltGraph", () => {
    test.each([
      [undefined, false],
      ["", false],
      [notAKey, false],
      ["altgraphx", false],
      ["altgraph", true],
      ["AltGraph", true],
      ["ALTGRAPH", true],
    ])(testDescription, (key, expected) => {
      // @ts-ignore
      expect(NavigationKeysUtils.isAltGraph(key)).toBe(expected);
    });
  });

  describe("isArrowDown", () => {
    test.each([
      [undefined, false],
      ["", false],
      [notAKey, false],
      ["arrowdownx", false],
      ["arrowdown", true],
      ["ArrowDown", true],
      ["ARROWDOWN", true],
    ])(testDescription, (key, expected) => {
      // @ts-ignore
      expect(NavigationKeysUtils.isArrowDown(key)).toBe(expected);
    });
  });

  describe("isArrowLeft", () => {
    test.each([
      [undefined, false],
      ["", false],
      [notAKey, false],
      ["arrowleftx", false],
      ["arrowleft", true],
      ["ArrowLeft", true],
      ["ARROWLEFT", true],
    ])(testDescription, (key, expected) => {
      // @ts-ignore
      expect(NavigationKeysUtils.isArrowLeft(key)).toBe(expected);
    });
  });

  describe("isArrowRight", () => {
    test.each([
      [undefined, false],
      ["", false],
      [notAKey, false],
      ["arrowrightx", false],
      ["arrowright", true],
      ["ArrowRight", true],
      ["ARROWRIGHT", true],
    ])(testDescription, (key, expected) => {
      // @ts-ignore
      expect(NavigationKeysUtils.isArrowRight(key)).toBe(expected);
    });
  });

  describe("isArrowUp", () => {
    test.each([
      [undefined, false],
      ["", false],
      [notAKey, false],
      ["arrowupx", false],
      ["arrowup", true],
      ["ArrowUp", true],
      ["ARROWUP", true],
    ])(testDescription, (key, expected) => {
      // @ts-ignore
      expect(NavigationKeysUtils.isArrowUp(key)).toBe(expected);
    });
  });

  describe("isAnyArrow", () => {
    test.each([
      [undefined, false],
      ["", false],
      [notAKey, false],
      ["enter", false],
      ["arrowupx", false],
      ["arrowup", true],
      ["arrowleft", true],
      ["ArrowUp", true],
      ["ARROWRIGHT", true],
    ])(testDescription, (key, expected) => {
      // @ts-ignore
      expect(NavigationKeysUtils.isAnyArrow(key)).toBe(expected);
    });
  });

  describe("isEnter", () => {
    test.each([
      [undefined, false],
      ["", false],
      [notAKey, false],
      ["enterx", false],
      ["enter", true],
      ["Enter", true],
      ["ENTER", true],
    ])(testDescription, (key, expected) => {
      // @ts-ignore
      expect(NavigationKeysUtils.isEnter(key)).toBe(expected);
    });
  });

  describe("isTab", () => {
    test.each([
      [undefined, false],
      ["", false],
      [notAKey, false],
      ["tabx", false],
      ["tab", true],
      ["Tab", true],
      ["TAB", true],
    ])(testDescription, (key, expected) => {
      // @ts-ignore
      expect(NavigationKeysUtils.isTab(key)).toBe(expected);
    });
  });

  describe("isFX", () => {
    test.each([
      [undefined, false],
      ["", false],
      [notAKey, false],
      ["F1x", false],
      ["f1", true],
      ["F1", true],
      ["F12", true],
      ["F120", false],
    ])(testDescription, (key, expected) => {
      // @ts-ignore
      expect(NavigationKeysUtils.isFX(key)).toBe(expected);
    });
  });

  describe("isTypingKey", () => {
    test.each([
      [undefined, false],
      ["", false],
      [notAKey, false],
      ["as", false],
      ["F1", false],
      ["f1", false],
      ["tab", false],
      ["capslock", false],
      ["enter", true],
      ["space", true],
      ["a", true],
      ["D", true],
      ["1", true],
      ["!", true],
    ])(testDescription, (key, expected) => {
      // @ts-ignore
      expect(NavigationKeysUtils.isTypingKey(key)).toBe(expected);
    });
  });
});
