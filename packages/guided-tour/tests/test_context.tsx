import * as React from "react";
import { CurrentTutorialContext, CurrentTutorialContextType } from "@kie-tooling-core/guided-tour/dist/contexts";
import { I18nDictionariesProvider, I18nDictionariesProviderProps } from "@kie-tooling-core/i18n/dist/react-components";
import {
  GuidedTourI18nContext,
  guidedTourI18nDefaults,
  guidedTourI18nDictionaries,
} from "@kie-tooling-core/guided-tour/dist/i18n";
import { GuidedTourI18n } from "@kie-tooling-core/guided-tour/dist/i18n";
import { DEFAULT_RECT, Rect, Tutorial } from "@kie-tooling-core/guided-tour/dist/api";

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
