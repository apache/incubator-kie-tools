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

import { useSelectorHandler } from "../../../components/utils";
import { KogitoGuidedTour } from "../../..";
import { DEFAULT_RECT, Rect } from "../../../api";

describe("useSelectorHandler", () => {
  beforeEach(() => {
    document.querySelector = mockQuerySelector;
  });
  afterEach(() => {
    document.querySelector = realQuerySelector;
  });

  it("triggers a request position provider when step has a custom selector", () => {
    const selector = "CUSTOM:::Decision-1";
    ctx.currentTutorial.steps[0].selector = selector;
    act(() => {
      useSelectorHandler();
    });
    expect(triggerPositionProvider).toBeCalledWith(selector);
  });

  it("sets the current reference element position when step has a regular query selector", () => {
    ctx.currentTutorial.steps[0].selector = "div#my-element";
    act(() => {
      useSelectorHandler();
    });

    expect(ctx.currentRefElementPosition).toBe(elementPosition);
  });

  it("sets the default position when step does not have valid query selector", () => {
    ctx.currentTutorial.steps[0].selector = "2077";
    act(() => {
      useSelectorHandler();
    });

    expect(ctx.currentRefElementPosition).toBe(DEFAULT_RECT);
  });
});

jest.mock("react", () => {
  const ActualReact = jest.requireActual("react");
  return {
    ...ActualReact,
    useContext: () => ctx,
    useEffect: (fn: any) => fn(),
    useMemo: (fn: any, _deps: any) => fn(),
  };
});

const triggerPositionProvider = jest.fn();
const guidedTour = KogitoGuidedTour.getInstance();
guidedTour.triggerPositionProvider = triggerPositionProvider;

const ctx: any = {
  currentStep: 0,
  currentRefElementPosition: DEFAULT_RECT,
  currentTutorial: {
    steps: [
      {
        selector: "",
      },
    ],
  },
  setCurrentRefElementPosition: (rect: Rect) => (ctx.currentRefElementPosition = rect),
};

const elementPosition: Rect = {
  bottom: 110,
  height: 100,
  left: 10,
  right: 110,
  top: 10,
  width: 100,
  x: 10,
  y: 10,
};

const realQuerySelector = document.querySelector;
const mockQuerySelector = (selector: string) => {
  return {
    getBoundingClientRect: () => {
      return selector === "div#my-element" ? elementPosition : DEFAULT_RECT;
    },
  };
};
