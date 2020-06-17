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
import { useContext } from "react";

import { getCurrentStep } from "./utils";
import { CurrentTutorialContext } from "../contexts";

import "./HighlightLayer.sass";

export const HighlightLayer = () => {
  const {
    currentTutorial,
    currentStep,
    currentRefElementPosition,
    isHighlightLayerEnabled,
    isNegativeReinforcementStateEnabled
  } = useContext(CurrentTutorialContext);
  const step = getCurrentStep(currentStep, currentTutorial);
  const highlightEnabled = step?.highlightEnabled || isHighlightLayerEnabled;

  let refElementX = 0;
  let refElementY = 0;
  let refElementWidth = 0;
  let refElementHeight = 0;

  if (!isNegativeReinforcementStateEnabled && currentRefElementPosition) {
    refElementX = currentRefElementPosition.left;
    refElementY = currentRefElementPosition.top;
    refElementWidth = currentRefElementPosition.width;
    refElementHeight = currentRefElementPosition.height;
  }

  const width = window.innerWidth;
  const height = window.innerHeight;
  const PADDING = 5;
  const rectX = refElementX - PADDING;
  const rectY = refElementY - PADDING;
  const rectWidth = refElementWidth + PADDING * 2;
  const reactHeight = refElementHeight + PADDING * 2;

  function internalRectPath() {
    if (highlightEnabled) {
      return `M0 0 H${width} V${height} H0Z 
              M${rectX} ${rectY} V${rectY + reactHeight} H${rectX + rectWidth} V${rectY}Z`;
    }
    return "";
  }

  return (
    <svg style={{ opacity: highlightEnabled ? 1 : 0 }} className="kgt-svg-layer">
      <path d={internalRectPath()} style={{ fill: "rgba(0, 0, 0, .5)" }} />
    </svg>
  );
};
