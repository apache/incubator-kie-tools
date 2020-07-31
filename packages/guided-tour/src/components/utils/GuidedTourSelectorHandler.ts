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

import { useContext, useEffect, useMemo } from "react";

import { getCurrentStep } from ".";
import { KogitoGuidedTour } from "../..";
import { DEFAULT_RECT } from "@kogito-tooling/microeditor-envelope-protocol";
import { CurrentTutorialContext } from "../../contexts";

function querySelector(query: string) {
  try {
    return document.querySelector(query);
  } catch (err) {
    return undefined;
  }
}

export const useSelectorHandler = () => {
  const { currentStep, currentTutorial, latestUserInteraction, setCurrentRefElementPosition } = useContext(
    CurrentTutorialContext
  );
  const dialogStep = useMemo(() => getCurrentStep(currentStep, currentTutorial), [currentStep, currentTutorial]);
  const selector = dialogStep?.selector ?? "";

  useEffect(() => {
    if (selector.length === 0) {
      return;
    }

    const isCustomSelector = selector.indexOf(":::") !== -1;
    if (isCustomSelector) {
      KogitoGuidedTour.getInstance().triggerPositionProvider(selector);
      return;
    }

    const element = querySelector(selector);
    const isValidQuerySelector = !!element;
    if (isValidQuerySelector) {
      setCurrentRefElementPosition(element!.getBoundingClientRect());
      return;
    }

    setCurrentRefElementPosition(DEFAULT_RECT);
  }, [currentStep, latestUserInteraction]);
};
