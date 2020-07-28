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

import { useLayoutEffect } from "react";

import { Rect, UserInteraction } from "@kogito-tooling/editor-envelope-protocol";
import { EventLabel } from "../../core";

export const useStartTutorialListener = (onStartTutorial: (tutorialLabel: string) => void) => {
  const type = "GuidedTour.startTutorial";
  useGuidedTourBusEffect(type, event => onStartTutorial(event.detail));
};

export const useUserInteractionListener = (onUserInteraction: (userInteraction: UserInteraction) => void) => {
  const type = "GuidedTour.userInteraction";
  useGuidedTourBusEffect(type, event => onUserInteraction(event.detail));
};

export const usePositionListener = (onPositionReceived: (rect: Rect) => void) => {
  const type = "GuidedTour.newPosition";
  useGuidedTourBusEffect(type, event => onPositionReceived(event.detail));
};

function useGuidedTourBusEffect(eventLabel: EventLabel, consumer: (customEvent: CustomEvent) => void) {
  useLayoutEffect(createEffect(eventLabel, consumer), []);
}

function createEffect(eventLabel: EventLabel, consumer: (customEvent: CustomEvent) => void) {
  return () => {
    function listener(e: any) {
      consumer(e as CustomEvent);
    }

    document.addEventListener(eventLabel, listener);
    return () => {
      document.removeEventListener(eventLabel, listener);
    };
  };
}
