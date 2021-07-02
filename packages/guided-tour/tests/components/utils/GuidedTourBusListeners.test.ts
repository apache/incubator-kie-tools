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

import { act } from "react-dom/test-utils";

import {
  usePositionListener,
  useStartTutorialListener,
  useUserInteractionListener,
} from "@kie-tooling-core/guided-tour/dist/components/utils";
import { EventLabel } from "@kie-tooling-core/guided-tour/dist/core";

testGuidedTourListener("useStartTutorialListener", "GuidedTour.startTutorial", useStartTutorialListener);
testGuidedTourListener("useUserInteractionListener", "GuidedTour.userInteraction", useUserInteractionListener);
testGuidedTourListener("usePositionListener", "GuidedTour.newPosition", usePositionListener);

const addedListeners: any = {};
const removedListeners: any = {};

const realAddEventListener = document.addEventListener;
const realRemoveEventListener = document.removeEventListener;
const mockAddEventListener = jest.fn((event, fn) => (addedListeners[event] = fn));
const mockRemoveEventListener = jest.fn((event, fn) => (removedListeners[event] = fn));

const listener = (_: string): any => undefined;

function testGuidedTourListener(
  methodName: string,
  eventName: EventLabel,
  guidedTourlistener: (callback: (detail: any) => void) => void
) {
  describe(methodName, () => {
    beforeAll(() => {
      document.addEventListener = mockAddEventListener;
      document.removeEventListener = mockRemoveEventListener;
    });

    afterAll(() => {
      document.addEventListener = realAddEventListener;
      document.removeEventListener = realRemoveEventListener;
    });

    it("properly adds the event listener", () => {
      act(() => guidedTourlistener(listener));
      expect(addedListeners[eventName]).not.toBeUndefined();
    });

    it("properly removes the event listener", () => {
      act(() => guidedTourlistener(listener));
      expect(removedListeners[eventName]).not.toBeUndefined();
    });
  });
}

jest.mock("react", () => {
  const ActualReact = jest.requireActual("react");
  return {
    ...ActualReact,
    useLayoutEffect: (fn: any) => fn()(),
  };
});
