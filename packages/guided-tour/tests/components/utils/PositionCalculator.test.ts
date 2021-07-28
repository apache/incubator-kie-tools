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

import { calculatePositionStyle } from "@kie-tooling-core/guided-tour/dist/components/utils";
import { Rect } from "@kie-tooling-core/guided-tour/dist/api";

describe("PositionCalculator", () => {
  describe("calculatePositionStyle", () => {
    const rect: Rect = {
      bottom: 60,
      height: 50,
      left: 20,
      right: 220,
      top: 10,
      width: 200,
      x: 20,
      y: 10,
    };

    it("returns position for when the position is 'right'", () => {
      expect(calculatePositionStyle("right", rect)).toEqual({
        left: 240,
        top: 10,
        transform: "rotate3d(0, 0, 0, 0deg)",
      });
    });

    it("returns position for when the position is 'left'", () => {
      expect(calculatePositionStyle("left", rect)).toEqual({
        left: 0,
        top: 10,
        transform: "translate(-100%, 0%)",
      });
    });

    it("returns position for when the position is 'bottom'", () => {
      expect(calculatePositionStyle("bottom", rect)).toEqual({
        left: 20,
        top: 80,
        transform: "rotate3d(0, 0, 0, 0deg)",
      });
    });

    it("returns position for when the position is a non-identified position", () => {
      expect(calculatePositionStyle("center", rect)).toEqual({
        left: "",
        top: "",
        transform: "translate(-50%, -50%)",
      });
    });

    it("returns position for when the rect is 'undefined'", () => {
      expect(calculatePositionStyle("center", undefined)).toEqual({
        left: "",
        top: "",
        transform: "translate(-50%, -50%)",
      });
    });
  });
});
