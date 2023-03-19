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

import * as React from "react";
import { CurrentTutorialContext, CurrentTutorialContextType } from "@kie-tools-core/guided-tour/dist/contexts";
import { I18nDictionariesProvider, I18nDictionariesProviderProps } from "@kie-tools-core/i18n/dist/react-components";
import {
  GuidedTourI18nContext,
  guidedTourI18nDefaults,
  guidedTourI18nDictionaries,
} from "@kie-tools-core/guided-tour/dist/i18n";
import { GuidedTourI18n } from "@kie-tools-core/guided-tour/dist/i18n";
import { DEFAULT_RECT, Rect, Tutorial } from "@kie-tools-core/guided-tour/dist/api";

export function usingCurrentTutorialContext(children: React.ReactElement, ctx?: Partial<CurrentTutorialContextType>) {
  const currentTutorialContext: CurrentTutorialContextType = {
    currentStep: 0,
    completedStep: 0,
    currentTutorial: new Tutorial("default tutorial", []),
    isHighlightLayerEnabled: false,
    isNegativeReinforcementStateEnabled: false,
    currentRefElementPosition: DEFAULT_RECT,
    setCompletedStep: (index: number) => (currentTutorialContext.completedStep = index),
    setCurrentStep: (index: number) => (currentTutorialContext.currentStep = index),
    setCurrentTutorial: (tutorial: Tutorial) => (currentTutorialContext.currentTutorial = tutorial),
    setCurrentRefElementPosition: (rect: Rect) => (currentTutorialContext.currentRefElementPosition = rect),
    setIsHighlightLayerEnabled: (isEnabled: boolean) => (currentTutorialContext.isHighlightLayerEnabled = isEnabled),
    setIsNegativeReinforcementStateEnabled: (isEnabled: boolean) =>
      (currentTutorialContext.isNegativeReinforcementStateEnabled = isEnabled),
    setLatestUserInteraction: () => null,
    ...ctx,
  };

  return {
    ctx: currentTutorialContext,
    wrapper: (
      <CurrentTutorialContext.Provider key={""} value={currentTutorialContext}>
        {children}
      </CurrentTutorialContext.Provider>
    ),
  };
}

export function usingTestingGuidedTourI18nContext(
  children: React.ReactElement,
  ctx?: Partial<I18nDictionariesProviderProps<GuidedTourI18n>>
) {
  const usedCtx: I18nDictionariesProviderProps<GuidedTourI18n> = {
    defaults: guidedTourI18nDefaults,
    dictionaries: guidedTourI18nDictionaries,
    ctx: GuidedTourI18nContext,
    children,
    ...ctx,
  };
  return {
    ctx: usedCtx,
    wrapper: (
      <I18nDictionariesProvider defaults={usedCtx.defaults} dictionaries={usedCtx.dictionaries} ctx={usedCtx.ctx}>
        {usedCtx.children}
      </I18nDictionariesProvider>
    ),
  };
}
