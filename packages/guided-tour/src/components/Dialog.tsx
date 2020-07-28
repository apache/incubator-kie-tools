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
import { useState, useContext, useEffect, useMemo } from "react";

import {
  calculatePositionStyle,
  getCurrentStep,
  EmptyDialog,
  NegativeReinforcementDialog,
  StepDialog,
  usePositionListener,
  useSelectorHandler,
  useStartTutorialListener,
  useUserInteractionListener,
  useUserInteractions
} from "./utils";
import { KogitoGuidedTour } from "..";
import { CurrentTutorialContext } from "../contexts";
import { DEFAULT_RECT, AutoMode, DemoMode } from "@kogito-tooling/editor-envelope-protocol";

import "./Dialog.sass";

export const Dialog = (props: { isEnabled: boolean; tutorialLabel: string }) => {
  // Local state
  const [isEnabled, setIsEnabled] = useState(props.isEnabled);
  const [currentTutorialLabel, setCurrentTutorialLabel] = useState(props.tutorialLabel);

  // Global state
  const {
    currentTutorial,
    currentStep,
    completedStep,
    currentRefElementPosition,
    isNegativeReinforcementStateEnabled,
    isHighlightLayerEnabled,
    setCurrentTutorial,
    setCurrentStep,
    setCompletedStep,
    setCurrentRefElementPosition,
    setLatestUserInteraction,
    setIsNegativeReinforcementStateEnabled,
    setIsHighlightLayerEnabled
  } = useContext(CurrentTutorialContext);
  const guidedTour = KogitoGuidedTour.getInstance();
  const registeredTutorials = guidedTour.getRegisteredTutorials();

  // Aliases
  const dialogClass = "pf-c-modal-box kgt-dialog kgt-dialog" + (isEnabled ? "--enabled" : "--disabled");
  const dialogStep = useMemo(() => getCurrentStep(currentStep, currentTutorial), [currentStep, currentTutorial]);
  const dialogContent = dialogStep?.content ?? "";
  const dialogPosition = dialogStep?.position ?? "center";
  const dialogMode = dialogStep?.mode ?? new DemoMode();
  const dialogRefElement = isNegativeReinforcementStateEnabled ? DEFAULT_RECT : currentRefElementPosition;

  // Post processing
  const dialogStyle = calculatePositionStyle(dialogPosition, dialogRefElement);
  const emptyTemplate = useMemo(EmptyDialog(closeDialog), []);
  const regularTemplate = useMemo(StepDialog(dialogContent, closeDialog), [
    currentStep,
    currentTutorial,
    registeredTutorials,
    isNegativeReinforcementStateEnabled
  ]);
  const negativeReinforcementTemplate = useMemo(NegativeReinforcementDialog(dialogStep, closeDialog), [
    currentStep,
    isHighlightLayerEnabled,
    isNegativeReinforcementStateEnabled
  ]);

  useStartTutorialListener(tutorialLabel => setCurrentTutorialLabel(tutorialLabel));
  usePositionListener(rect => setCurrentRefElementPosition(rect));
  useUserInteractionListener(userInteraction => setLatestUserInteraction(userInteraction));
  useUserInteractions();
  useSelectorHandler();

  function closeDialog() {
    setIsEnabled(false);
    guidedTour.teardown();
  }

  function getDialogTemplate() {
    if (isNegativeReinforcementStateEnabled && dialogStep) {
      return negativeReinforcementTemplate;
    } else if (dialogStep) {
      return regularTemplate;
    } else {
      return emptyTemplate;
    }
  }

  function handleAutoMode() {
    if ("delay" in dialogMode) {
      const autoMode = dialogMode as AutoMode;
      setTimeout(() => setCurrentStep(currentStep + 1), autoMode.delay);
    }
  }

  useEffect(() => {
    if (currentStep > completedStep) {
      setCompletedStep(currentStep);
    }
  }, [currentStep]);

  useEffect(() => {
    const newCurrentTutorial = registeredTutorials.find(tutorial => tutorial.label === currentTutorialLabel);

    if (newCurrentTutorial) {
      setCurrentTutorial(newCurrentTutorial);
      setIsEnabled(true);
      setCurrentStep(0);
      setCompletedStep(0);
      setCurrentRefElementPosition(DEFAULT_RECT);
      setIsNegativeReinforcementStateEnabled(false);
      setIsHighlightLayerEnabled(false);
    }
  }, [registeredTutorials, currentTutorialLabel]);

  handleAutoMode();

  return (
    <div style={dialogStyle} className={dialogClass}>
      {getDialogTemplate()}
    </div>
  );
};
