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

import { GuidedTourDomUtils } from "@kie-tooling-core/guided-tour/dist/core";
import { act } from "react-dom/test-utils";

describe("GuidedTourDomUtils", () => {
  const domUtils = new GuidedTourDomUtils();

  describe("getGuidedTourHTMLElement", () => {
    it("creates the guided tour element when it's not present once", () => {
      act(() => {
        domUtils.getGuidedTourHTMLElement();
        domUtils.getGuidedTourHTMLElement();
      });
      expect(document.body).toMatchSnapshot();
    });

    it("always returns the same element", () => {
      const result1 = domUtils.getGuidedTourHTMLElement();
      const result2 = domUtils.getGuidedTourHTMLElement();
      expect(result1).toEqual(result2);
    });
  });

  describe("getGuidedTourHTMLElement", () => {
    it("removes the guided tour element when it exists", () => {
      act(() => {
        domUtils.getGuidedTourHTMLElement();
        domUtils.removeGuidedTourHTMLElement();
      });
      expect(document.body).toMatchSnapshot();
    });

    it("does not raise any error when the guided tour element does not exist", () => {
      act(() => {
        domUtils.removeGuidedTourHTMLElement();
      });
      expect(document.body).toMatchSnapshot();
    });
  });
});
