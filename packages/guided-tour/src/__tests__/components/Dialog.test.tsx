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
import { act } from "react-dom/test-utils";
import { render, renderedComponent, setupContainer, teardownContainer, triggerClick, useContextMock } from "../utils";

import { Dialog } from "../../components";
import { KogitoGuidedTour } from "../..";
import { AutoMode, DEFAULT_RECT, DemoMode, Rect, Tutorial } from "@kogito-tooling/microeditor-envelope-protocol";

jest.useFakeTimers();

const tutorialLabel = "default tutorial";
const ctx = {
  currentStep: 0,
  completedStep: 0,
  currentTutorial: new Tutorial(tutorialLabel, []),
  isHighlightLayerEnabled: false,
  isNegativeReinforcementStateEnabled: false,
  currentRefElementPosition: DEFAULT_RECT,
  setCompletedStep: (index: number) => (ctx.completedStep = index),
  setCurrentStep: (index: number) => (ctx.currentStep = index),
  setCurrentTutorial: (tutorial: Tutorial) => (ctx.currentTutorial = tutorial),
  setCurrentRefElementPosition: (rect: Rect) => (ctx.currentRefElementPosition = rect),
  setIsHighlightLayerEnabled: (isEnabled: boolean) => (ctx.isHighlightLayerEnabled = isEnabled),
  setIsNegativeReinforcementStateEnabled: (isEnabled: boolean) => (ctx.isNegativeReinforcementStateEnabled = isEnabled)
};

function registeredTutorial(tutorial: Tutorial) {
  KogitoGuidedTour.getInstance().registerTutorial(tutorial);
  return tutorial;
}

describe("Dialog", () => {
  beforeEach(setupContainer);
  afterEach(teardownContainer);

  describe("when the step loads", () => {
    it("renders react-based content", () => {
      act(() => {
        useContextMock({
          ...ctx,
          currentTutorial: registeredTutorial({
            label: tutorialLabel,
            steps: [
              {
                mode: new DemoMode(),
                content: <div>Something as JSX</div>
              }
            ]
          })
        });

        render(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />);
      });
      expect(renderedComponent()).toMatchSnapshot();
    });

    it("renders function-based content", () => {
      act(() => {
        useContextMock({
          ...ctx,
          currentTutorial: registeredTutorial({
            label: tutorialLabel,
            steps: [
              {
                mode: new DemoMode(),
                content: () => <div>Something as JSX-function</div>
              }
            ]
          })
        });

        render(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />);
      });
      expect(renderedComponent()).toMatchSnapshot();
    });

    it("renders string-based content", () => {
      act(() => {
        useContextMock({
          ...ctx,
          currentTutorial: registeredTutorial({
            label: tutorialLabel,
            steps: [
              {
                mode: new DemoMode(),
                content: "<div>Something as string</div>"
              }
            ]
          })
        });

        render(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />);
      });
      expect(renderedComponent()).toMatchSnapshot();
    });

    it("renders a step on auto mode", () => {
      act(() => {
        useContextMock({
          ...ctx,
          currentTutorial: registeredTutorial({
            label: tutorialLabel,
            steps: [
              {
                mode: new AutoMode(1000),
                content: "<div>Something as string</div>"
              }
            ]
          })
        });

        render(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />);
      });
      expect(renderedComponent()).toMatchSnapshot();
      expect(setTimeout).toHaveBeenLastCalledWith(expect.any(Function), 1000);
    });

    it("closes when users click on the close button", () => {
      act(() => {
        useContextMock({
          ...ctx,
          currentTutorial: registeredTutorial({
            label: tutorialLabel,
            steps: [
              {
                mode: new DemoMode(),
                content: <div>Something</div>
              }
            ]
          })
        });

        render(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />);

        triggerClick("[data-kgt-close]");
      });
      expect(renderedComponent()).toMatchSnapshot();
    });
  });

  describe("when the step cannot be loaded", () => {
    const emptyStateCtx = {
      ...ctx,
      currentStep: 1,
      currentTutorial: registeredTutorial({
        label: tutorialLabel,
        steps: []
      })
    };

    it("renders empty state", () => {
      act(() => {
        useContextMock(emptyStateCtx);
        render(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />);
      });
      expect(renderedComponent()).toMatchSnapshot();
    });
  });

  describe("when negative reinforcement appears", () => {
    const negativeReinforcementCtx = {
      ...ctx,
      isNegativeReinforcementStateEnabled: true,
      currentTutorial: registeredTutorial({
        label: tutorialLabel,
        steps: [
          {
            mode: new DemoMode(),
            negativeReinforcementMessage: "Try to click there!"
          }
        ]
      })
    };

    it("renders negative reinforcement message", () => {
      act(() => {
        useContextMock({ ...negativeReinforcementCtx, isHighlightLayerEnabled: true });
        render(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />);
      });

      expect(renderedComponent()).toMatchSnapshot();
    });

    it("renders negative reinforcement clue", () => {
      act(() => {
        useContextMock({ ...negativeReinforcementCtx, isHighlightLayerEnabled: false });
        render(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />);
      });
      expect(renderedComponent()).toMatchSnapshot();
    });

    it("sets 'isHighlightLayerEnabled' as 'false' when users press the 'Continue' button", () => {
      act(() => {
        useContextMock({ ...negativeReinforcementCtx, isHighlightLayerEnabled: true });
        render(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />);
        triggerClick("[data-kgt-continue]");
      });
      expect(ctx.isHighlightLayerEnabled).toBeFalsy();
    });
  });
});
