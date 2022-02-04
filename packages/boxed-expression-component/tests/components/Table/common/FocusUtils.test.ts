/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import {
  focusPrevCell,
  focusNextCell,
  focusUpperCell,
  focusLowerCell,
  focusInsideCell,
  getParentCell,
  focusParentCell,
} from "@kie-tools/boxed-expression-component/dist/components/Table/common";
import { cellFocus } from "../../../../src/components/Table/common";

describe("FocusUtils tests", () => {
  describe("getParentCell tests", () => {
    it("should fail", () => {
      // @ts-ignore
      expect(getParentCell()).toBeNull();
      expect(getParentCell(null)).toBeNull();
    });

    it("should return the input", () => {
      const element = document.createElement("td");
      element.closest = () => "parent";
      expect(getParentCell(element)).toBe(element);
    });

    it("should return the parent", () => {
      const element = document.createElement("div");
      element.closest = () => "parent";
      expect(getParentCell(element)).toBe("parent");
    });
  });
  // /* TODO: test cellFocus */
  describe("cellFocus tests", () => {
    it("should fail", () => {
      // @ts-ignore
      expect(() => cellFocus()).not.toThrowError();
      expect(() => cellFocus(null)).not.toThrowError();
    });

    it("should focus the input", () => {
      const element = document.createElement("td");
      const mockFocus = jest.spyOn(element, "focus");
      cellFocus(element);
      expect(mockFocus).toHaveBeenCalled();
    });
  });
  // /* TODO: test focusCurrentCell */
  // describe("focusCurrentCell tests", () => {
  // });
  // /* TODO: test focusNextCell */
  // describe("focusNextCell tests", () => {
  // });
  // /* TODO: test focusPrevCell */
  // describe("focusPrevCell tests", () => {
  // });
  // /* TODO: test focusUpperCell */
  // describe("focusUpperCell tests", () => {
  // });
  // /* TODO: test focusLowerCell */
  // describe("focusLowerCell tests", () => {
  // });
  // /* TODO: test focusInsideCell */
  // describe("focusInsideCell tests", () => {
  // });
  // /* TODO: test focusParentCell */
  // describe("focusParentCell tests", () => {
  // });
});
