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
import { useState } from "react";
import { Dialog, HighlightLayer } from ".";
import { UserInteraction, Tutorial, Rect } from "../api";
import { CurrentTutorialContext } from "../contexts";
import { I18nDictionariesProvider } from "@kie-tooling-core/i18n/dist/react-components";
import { GuidedTourI18nContext, guidedTourI18nDefaults, guidedTourI18nDictionaries } from "../i18n";

export const GuidedTour = () => {
  const [currentTutorial, setCurrentTutorial] = useState<Tutorial>();
  const [currentRefElementPosition, setCurrentRefElementPosition] = useState<Rect>();
  const [isNegativeReinforcementStateEnabled, setIsNegativeReinforcementStateEnabled] = useState(false);
  const [latestUserInteraction, setLatestUserInteraction] = useState<UserInteraction>();
  const [isHighlightLayerEnabled, setIsHighlightLayerEnabled] = useState(false);
  const [currentStep, setCurrentStep] = useState(0);
  const [completedStep, setCompletedStep] = useState(0);

  return (
    <I18nDictionariesProvider
      defaults={guidedTourI18nDefaults}
      dictionaries={guidedTourI18nDictionaries}
      initialLocale={navigator.language}
      ctx={GuidedTourI18nContext}
    >
      <CurrentTutorialContext.Provider
        value={{
          currentTutorial,
          currentStep,
          completedStep,
          currentRefElementPosition,
          isNegativeReinforcementStateEnabled,
          isHighlightLayerEnabled,
          latestUserInteraction,
          setCurrentStep,
          setCompletedStep,
          setCurrentTutorial,
          setCurrentRefElementPosition,
          setIsNegativeReinforcementStateEnabled,
          setIsHighlightLayerEnabled,
          setLatestUserInteraction,
        }}
      >
        <HighlightLayer />
        <Dialog isEnabled={false} tutorialLabel="" />
      </CurrentTutorialContext.Provider>
    </I18nDictionariesProvider>
  );
};
