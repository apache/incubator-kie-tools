import * as React from "react";
import { CurrentTutorialContext, CurrentTutorialContextType } from "../contexts";
import { DEFAULT_RECT, Rect, Tutorial } from "../api";
import { I18nDictionariesProvider } from "@kogito-tooling/i18n";
import { guidedTourI18nDefaults, guidedTourI18nDictionaries } from "../i18n/locales";

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
    ...ctx
  };

  return {
    ctx: currentTutorialContext,
    wrapper: (
      <I18nDictionariesProvider defaults={guidedTourI18nDefaults} dictionaries={guidedTourI18nDictionaries}>
        <CurrentTutorialContext.Provider key={""} value={currentTutorialContext}>
          {children}
        </CurrentTutorialContext.Provider>
      </I18nDictionariesProvider>
    )
  };
};
