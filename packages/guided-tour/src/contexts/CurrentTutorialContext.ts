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

import { createContext } from "react";

import { UserInteraction, Tutorial, Rect } from "@kogito-tooling/microeditor-envelope-protocol";

export interface CurrentTutorialContextType {
  currentTutorial?: Tutorial;
  currentStep: number;
  completedStep: number;
  currentRefElementPosition?: Rect;
  isNegativeReinforcementStateEnabled: boolean;
  isHighlightLayerEnabled: boolean;
  latestUserInteraction?: UserInteraction;
  setCurrentTutorial: (tutorial?: Tutorial) => void;
  setCurrentStep: (index: number) => void;
  setCompletedStep: (index: number) => void;
  setCurrentRefElementPosition: (rect?: Rect) => void;
  setIsNegativeReinforcementStateEnabled: (isEnabled: boolean) => void;
  setIsHighlightLayerEnabled: (isEnabled: boolean) => void;
  setLatestUserInteraction: (userInteraction: UserInteraction) => void;
}

export const CurrentTutorialContext = createContext<CurrentTutorialContextType>({} as any);
