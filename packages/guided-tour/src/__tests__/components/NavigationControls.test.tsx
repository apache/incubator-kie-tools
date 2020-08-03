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

import { NavigationControls } from "../../components";
import { KogitoGuidedTour } from "../..";
import { DemoMode } from "../../api";

describe("NavigationControls", () => {
  beforeEach(setupContainer);
  afterEach(teardownContainer);

  it("works when users go to the next step", () => {
    const ctx = {
      currentStep: 2,
      setCurrentStep: (index: number) => {
        ctx.currentStep = index;
      },
      currentTutorial: { steps: [{}, {}, {}, {}] }
    };

    act(() => {
      useContextMock(ctx);
      render(<NavigationControls />);

      triggerClick("[data-kgt-next]");
    });

    expect(renderedComponent()).toMatchSnapshot();
    expect(ctx.currentStep).toBe(3);
  });

  it("works when users go to the previous step", () => {
    const ctx = {
      currentStep: 2,
      setCurrentStep: (index: number) => {
        ctx.currentStep = index;
      },
      currentTutorial: { steps: [{}, {}, {}, {}] }
    };

    act(() => {
      useContextMock(ctx);
      render(<NavigationControls />);

      triggerClick("[data-kgt-prev]");
    });

    expect(renderedComponent()).toMatchSnapshot();
    expect(ctx.currentStep).toBe(1);
  });

  it("renders with a tutorial and a step", () => {
    act(() => {
      useContextMock({
        currentStep: 2,
        currentTutorial: {
          steps: [{}, {}, {}]
        }
      });
      render(<NavigationControls />);
    });
    expect(renderedComponent()).toMatchSnapshot();
  });

  it("renders without a tutorial, but with a step", () => {
    act(() => {
      useContextMock({ currentStep: 2, currentTutorial: undefined });
      render(<NavigationControls />);
    });
    expect(renderedComponent()).toMatchSnapshot();
  });

  it("renders without a tutorial and without a step", () => {
    act(() => {
      useContextMock({ currentStep: undefined, currentTutorial: undefined });
      render(<NavigationControls />);
    });
    expect(renderedComponent()).toMatchSnapshot();
  });

  it("renders with buttons when navigator is enabled", () => {
    const ctx = {
      currentStep: 0,
      setCurrentStep: (index: number) => {
        ctx.currentStep = index;
      },
      currentTutorial: {
        label: "Tutorial",
        steps: [
          {
            mode: new DemoMode(),
            navigatorEnabled: true
          }
        ]
      }
    };

    act(() => {
      KogitoGuidedTour.getInstance().registerTutorial(ctx.currentTutorial);
      useContextMock(ctx);
      render(<NavigationControls />);
    });

    expect(renderedComponent()).toMatchSnapshot();
  });
});
