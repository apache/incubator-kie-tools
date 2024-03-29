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

import { describe, test, expect } from "@jest/globals";
import { NavigationKeysUtils } from "@kie-tools/boxed-expression-component/dist/keysUtils/keyUtils";

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
      expect(NavigationKeysUtils.isEsc(key!)).toBe(expected);
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
      expect(NavigationKeysUtils.isAltGraph(key!)).toBe(expected);
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
      expect(NavigationKeysUtils.isArrowDown(key!)).toBe(expected);
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
      expect(NavigationKeysUtils.isArrowLeft(key!)).toBe(expected);
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
      expect(NavigationKeysUtils.isArrowRight(key!)).toBe(expected);
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
      expect(NavigationKeysUtils.isArrowUp(key!)).toBe(expected);
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
      expect(NavigationKeysUtils.isAnyArrow(key!)).toBe(expected);
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
      expect(NavigationKeysUtils.isEnter(key!)).toBe(expected);
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
      expect(NavigationKeysUtils.isTab(key!)).toBe(expected);
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
      expect(NavigationKeysUtils.isFunctionKey(key!)).toBe(expected);
    });
  });
});
