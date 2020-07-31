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
import { BlockMode, DemoMode } from "@kogito-tooling/microeditor-envelope-protocol";
import { CurrentTutorialContext } from "../../contexts";

export const useUserInteractions = () => {
  // Globals
  const {
    currentStep,
    completedStep,
    currentTutorial,
    isNegativeReinforcementStateEnabled,
    latestUserInteraction,
    setCurrentStep,
    setIsNegativeReinforcementStateEnabled,
    setIsHighlightLayerEnabled
  } = useContext(CurrentTutorialContext);

  // Aliases
  const dialogStep = useMemo(() => getCurrentStep(currentStep, currentTutorial), [currentStep, currentTutorial]);
  const mode = dialogStep?.mode ?? new DemoMode();

  function handleBlockMode(blockMode: BlockMode) {
    const { userInteraction, allowedSelectors } = blockMode;
    const targetSelector = latestUserInteraction?.target ?? "";

    function isAllowedInteraction() {
      return [...allowedSelectors, userInteraction.target].indexOf(targetSelector) !== -1;
    }

    function isNegativeReinforcementMessagePresent() {
      const message = dialogStep?.negativeReinforcementMessage ?? "";
      return message.length > 0;
    }

    function alreadyPerformedAction() {
      return completedStep > currentStep;
    }

    function isNegativeReinforcementAllowed() {
      return (
        !isAllowedInteraction() &&
        !alreadyPerformedAction() &&
        isNegativeReinforcementMessagePresent() &&
        !isNegativeReinforcementStateEnabled
      );
    }

    if (isNegativeReinforcementAllowed()) {
      setIsNegativeReinforcementStateEnabled(true);
      setIsHighlightLayerEnabled(true);
    }

    const expectedAction = userInteraction.action ?? "";
    const expectedTarget = userInteraction.target ?? "";

    const actualAction = latestUserInteraction?.action ?? "";
    const actualTarget = latestUserInteraction?.target ?? "";

    const isExpectedAction = actualAction.startsWith(expectedAction);
    const isExpectedTarget = actualTarget.startsWith(expectedTarget);

    if (isExpectedAction && isExpectedTarget) {
      setCurrentStep(currentStep + 1);
      setIsNegativeReinforcementStateEnabled(false);
      setIsHighlightLayerEnabled(false);
    }
  }

  useEffect(() => {
    if ("userInteraction" in mode) {
      handleBlockMode(mode as BlockMode);
    }
  }, [latestUserInteraction]);
};
