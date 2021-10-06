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
import { fireEvent, render } from "@testing-library/react";
import { usingCurrentTutorialContext, usingTestingGuidedTourI18nContext } from "../test_context";
import { Dialog } from "@kie-tooling-core/guided-tour/dist/components";
import { KogitoGuidedTour } from "@kie-tooling-core/guided-tour/dist";
import { AutoMode, DemoMode, Tutorial } from "@kie-tooling-core/guided-tour/dist/api";

jest.useFakeTimers();

const tutorialLabel = "default tutorial";
function registeredTutorial(tutorial: Tutorial) {
  KogitoGuidedTour.getInstance().registerTutorial(tutorial);
  return tutorial;
}

beforeAll(() => {
  jest.resetAllMocks();
});

describe("Dialog", () => {
  describe("when the step loads", () => {
    it("renders react-based content", () => {
      const { container } = render(
        usingTestingGuidedTourI18nContext(
          usingCurrentTutorialContext(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />, {
            currentTutorial: registeredTutorial({
              label: tutorialLabel,
              steps: [
                {
                  mode: new DemoMode(),
                  content: <div>Something as JSX</div>,
                },
              ],
            }),
          }).wrapper
        ).wrapper
      );
      expect(container).toMatchSnapshot();
    });

    it("renders function-based content", () => {
      const { container } = render(
        usingTestingGuidedTourI18nContext(
          usingCurrentTutorialContext(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />, {
            currentTutorial: registeredTutorial({
              label: tutorialLabel,
              steps: [
                {
                  mode: new DemoMode(),
                  content: () => <div>Something as JSX-function</div>,
                },
              ],
            }),
          }).wrapper
        ).wrapper
      );
      expect(container).toMatchSnapshot();
    });

    it("renders string-based content", () => {
      const { container } = render(
        usingTestingGuidedTourI18nContext(
          usingCurrentTutorialContext(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />, {
            currentTutorial: registeredTutorial({
              label: tutorialLabel,
              steps: [
                {
                  mode: new DemoMode(),
                  content: "<div>Something as string</div>",
                },
              ],
            }),
          }).wrapper
        ).wrapper
      );
      expect(container).toMatchSnapshot();
    });

    it("renders a step on auto mode", () => {
      const { container } = render(
        usingTestingGuidedTourI18nContext(
          usingCurrentTutorialContext(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />, {
            currentTutorial: registeredTutorial({
              label: tutorialLabel,
              steps: [
                {
                  mode: new AutoMode(1000),
                  content: "<div>Something as string</div>",
                },
              ],
            }),
          }).wrapper
        ).wrapper
      );
      expect(container).toMatchSnapshot();
      expect(setTimeout).toHaveBeenLastCalledWith(expect.any(Function), 1000);
    });

    it("closes when users click on the close button", () => {
      const { container } = render(
        usingTestingGuidedTourI18nContext(
          usingCurrentTutorialContext(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />, {
            currentTutorial: registeredTutorial({
              label: tutorialLabel,
              steps: [
                {
                  mode: new DemoMode(),
                  content: <div>Something</div>,
                },
              ],
            }),
          }).wrapper
        ).wrapper
      );

      fireEvent.click(
        document.querySelector("[data-ouia-component-id='dmn-guided-tour'] button[aria-label='Close']")!,
        { bubbles: true }
      );
      expect(container).toMatchSnapshot();
    });
  });

  describe("when the step cannot be loaded", () => {
    it("renders empty state", () => {
      const { container } = render(
        usingTestingGuidedTourI18nContext(
          usingCurrentTutorialContext(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />, {
            currentStep: 1,
            currentTutorial: registeredTutorial({
              label: tutorialLabel,
              steps: [],
            }),
          }).wrapper
        ).wrapper
      );
      expect(container).toMatchSnapshot();
    });
  });

  describe("when negative reinforcement appears", () => {
    const negativeReinforcementCtx = {
      isNegativeReinforcementStateEnabled: true,
      currentTutorial: registeredTutorial({
        label: tutorialLabel,
        steps: [
          {
            mode: new DemoMode(),
            negativeReinforcementMessage: "Try to click there!",
          },
        ],
      }),
    };

    it("renders negative reinforcement message", () => {
      const { container } = render(
        usingTestingGuidedTourI18nContext(
          usingCurrentTutorialContext(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />, {
            ...negativeReinforcementCtx,
            isHighlightLayerEnabled: true,
          }).wrapper
        ).wrapper
      );
      expect(container).toMatchSnapshot();
    });

    it("renders negative reinforcement clue", () => {
      const { container } = render(
        usingTestingGuidedTourI18nContext(
          usingCurrentTutorialContext(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />, {
            ...negativeReinforcementCtx,
            isHighlightLayerEnabled: false,
          }).wrapper
        ).wrapper
      );
      expect(container).toMatchSnapshot();
    });

    it("sets 'isHighlightLayerEnabled' as 'false' when users press the 'Continue' button", () => {
      const { ctx, wrapper } = usingCurrentTutorialContext(
        usingTestingGuidedTourI18nContext(<Dialog isEnabled={true} tutorialLabel={tutorialLabel} />).wrapper,
        {
          ...negativeReinforcementCtx,
          isHighlightLayerEnabled: true,
        }
      );

      render(wrapper);
      fireEvent.click(document.querySelector("[data-kgt-continue]")!, { bubbles: true });
      expect(ctx.isHighlightLayerEnabled).toBeFalsy();
    });
  });
});
