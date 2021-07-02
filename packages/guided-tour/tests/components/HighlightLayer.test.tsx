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
import { useContextMock, renderedComponent, render, setupContainer, teardownContainer } from "../utils";

import { HighlightLayer } from "@kie-tooling-core/guided-tour/dist/components";

describe("HighlightLayer", () => {
  beforeEach(setupContainer);
  afterEach(teardownContainer);

  it("does not render when layer and the current highlight are disabled", () => {
    const ctx = {
      isHighlightLayerEnabled: false,
      currentStep: 0,
      currentTutorial: { steps: [{ highlightEnabled: false }] },
    };

    act(() => {
      useContextMock(ctx);
      render(<HighlightLayer />);
    });
    expect(renderedComponent()).toMatchSnapshot();
  });

  it("renders when layer is disabled, but the current step highlight is enabled", () => {
    const ctx = {
      isHighlightLayerEnabled: false,
      currentStep: 0,
      currentTutorial: { steps: [{ highlightEnabled: true }] },
    };

    act(() => {
      useContextMock(ctx);
      render(<HighlightLayer />);
    });
    expect(renderedComponent()).toMatchSnapshot();
  });

  it("renders when layer is enabled, but the current step highlight is disabled", () => {
    const ctx = {
      isHighlightLayerEnabled: true,
      currentStep: 0,
      currentTutorial: { steps: [{ highlightEnabled: false }] },
    };

    act(() => {
      useContextMock(ctx);
      render(<HighlightLayer />);
    });
    expect(renderedComponent()).toMatchSnapshot();
  });

  it("renders when layer is enabled, but current element position and curre step are not present", () => {
    const ctx = { isHighlightLayerEnabled: true };

    act(() => {
      useContextMock(ctx);
      render(<HighlightLayer />);
    });
    expect(renderedComponent()).toMatchSnapshot();
  });

  it("renders when layer with a square to highlight current element, when current element position is present", () => {
    const ctx = {
      isNegativeReinforcementStateEnabled: false,
      isHighlightLayerEnabled: true,
      currentRefElementPosition: {
        top: 100,
        left: 100,
        height: 200,
        width: 200,
      },
    };

    act(() => {
      useContextMock(ctx);
      render(<HighlightLayer />);
    });
    expect(renderedComponent()).toMatchSnapshot();
  });
});
