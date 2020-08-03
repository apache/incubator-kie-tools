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

import { DEFAULT_RECT, Rect } from "../../api";

const DEFAULT_STYLE = {
  left: "",
  top: "",
  transform: "translate(-50%, -50%)"
};

export const calculatePositionStyle = (position: string, rect?: Rect) => {
  const { left, top, width, height } = rect ?? {};

  if (!(left && top && width && height) ?? rect === DEFAULT_RECT) {
    return DEFAULT_STYLE;
  }

  const MARGIN = 20;

  switch (position) {
    case "right":
      return {
        left: left + width + MARGIN,
        top: top,
        transform: "rotate3d(0, 0, 0, 0deg)"
      };
    case "left":
      return {
        left: left - MARGIN,
        top: top,
        transform: "translate(-100%, 0%)"
      };
    case "bottom":
      return {
        left: left,
        top: top + height + MARGIN,
        transform: "rotate3d(0, 0, 0, 0deg)"
      };
    default:
      return DEFAULT_STYLE;
  }
};
