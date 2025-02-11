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

import { DmnBuiltInDataType } from "../api";
import { DEFAULT_MIN_WIDTH } from "./WidthConstants";

/**
 * Uses canvas.measureText to compute and return the width of the given text of given font in pixels.
 *
 * @param {String} text The text to be rendered.
 * @param {String} font The css font descriptor that text is to be rendered with (e.g. "bold 14px verdana").
 *
 * @see https://stackoverflow.com/questions/118241/calculate-text-width-with-javascript/21015393#21015393
 */

const canvas = document.createElement("canvas");

export function getTextWidth(text: string, font: string) {
  // re-use canvas object for better performance
  const context = canvas.getContext("2d");
  context!.font = font;
  const metrics = context!.measureText(text);
  return Math.ceil(metrics.width);
}

function getCssStyle(element: Element, prop: string) {
  return window.getComputedStyle(element, null).getPropertyValue(prop);
}

export function getCanvasFont(el: Element) {
  const fontWeight = getCssStyle(el, "font-weight") || "normal";
  const fontSize = getCssStyle(el, "font-size") || "16px";
  const fontFamily = getCssStyle(el, "font-family") || "Times New Roman";

  return `${fontWeight} ${fontSize} ${fontFamily}`;
}

export function getDefaultColumnWidth({ name, typeRef }: { name: string; typeRef: string | undefined }): number {
  return (
    8 * 2 + // Copied from ContextEntry variable `getWidthToFit`
    2 + // Copied from ContextEntry variable `getWidthToFit`
    Math.max(
      DEFAULT_MIN_WIDTH,
      getTextWidth(name, "700 11.2px Menlo, monospace"),
      getTextWidth(
        `(${typeRef ?? DmnBuiltInDataType.Undefined})`,
        "700 11.6667px RedHatText, Overpass, overpass, helvetica, arial, sans-serif"
      )
    )
  );
}
