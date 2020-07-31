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

import { useUserInteractions } from "../../../components/utils";
import { BlockMode, DemoMode, UserInteraction } from "@kogito-tooling/microeditor-envelope-protocol";

describe("GuidedTourUserInteractions", () => {
  describe("useUserInteractions", () => {
    it("enables negative reinforcement when it's allowed", () => {
      useContext({
        latestUserInteraction: new UserInteraction("CLICK", ".data-types-tab")
      });

      act(() => useUserInteractions());

      expect(ctx.currentStep).toBe(0);
      expect(ctx.isNegativeReinforcementStateEnabled).toBeTruthy();
      expect(ctx.isHighlightLayerEnabled).toBeTruthy();
    });

    it("does not enable negative reinforcement when user already performed the required action", () => {
      useContext({
        completedStep: 1, // the user has already explored the next step
        latestUserInteraction: new UserInteraction("CLICK", ".data-types-tab")
      });

      act(() => useUserInteractions());

      expect(ctx.currentStep).toBe(0);
      expect(ctx.isNegativeReinforcementStateEnabled).toBeFalsy();
      expect(ctx.isHighlightLayerEnabled).toBeFalsy();
    });

    it("does not enable negative reinforcement when reinforcement message is not present", () => {
      useContext({
        latestUserInteraction: new UserInteraction("CLICK", ".data-types-tab"),
        currentTutorial: {
          steps: [
            {
              negativeReinforcementMessage: null,
              mode: new BlockMode({ action: "CREATED", target: "Node" }, [".palette", "canvas"])
            }
          ]
        }
      });

      act(() => useUserInteractions());

      expect(ctx.currentStep).toBe(0);
      expect(ctx.isNegativeReinforcementStateEnabled).toBeFalsy();
      expect(ctx.isHighlightLayerEnabled).toBeFalsy();
    });

    it("does not enable negative reinforcement when it's already enabled", () => {
      useContext({
        isNegativeReinforcementStateEnabled: true,
        isHighlightLayerEnabled: false,
        latestUserInteraction: new UserInteraction("CLICK", ".data-types-tab")
      });

      act(() => useUserInteractions());

      expect(ctx.currentStep).toBe(0);
      expect(ctx.isNegativeReinforcementStateEnabled).toBeTruthy();
      expect(ctx.isHighlightLayerEnabled).toBeFalsy();
    });

    it("does not enable negative reinforcement when the current step mode is not block mode", () => {
      useContext({
        latestUserInteraction: new UserInteraction("CLICK", ".data-types-tab"),
        currentTutorial: {
          steps: [{ mode: new DemoMode() }]
        }
      });

      act(() => useUserInteractions());

      expect(ctx.currentStep).toBe(0);
      expect(ctx.isNegativeReinforcementStateEnabled).toBeFalsy();
      expect(ctx.isHighlightLayerEnabled).toBeFalsy();
    });

    it("goes to the next step when the latest user interaction matches with the current step user interaction", () => {
      useContext({
        latestUserInteraction: new UserInteraction("CREATED", "Node")
      });

      act(() => useUserInteractions());

      expect(ctx.currentStep).toBe(1);
      expect(ctx.isNegativeReinforcementStateEnabled).toBeFalsy();
      expect(ctx.isHighlightLayerEnabled).toBeFalsy();
    });
  });
});

jest.mock("react", () => {
  const ActualReact = require.requireActual("react");
  return {
    ...ActualReact,
    useContext: () => ctx,
    useEffect: (fn: any) => fn(),
    useMemo: (fn: any, _deps: any) => fn()
  };
});

const ctx: any = {
  setCurrentStep: (currentStepIndex: number) => (ctx.currentStep = currentStepIndex),
  setIsNegativeReinforcementStateEnabled: (isEnabled: boolean) => (ctx.isNegativeReinforcementStateEnabled = isEnabled),
  setIsHighlightLayerEnabled: (isEnabled: boolean) => (ctx.isHighlightLayerEnabled = isEnabled)
};

function useContext(currentCtx: any) {
  ctx.currentStep = currentCtx.currentStep ?? 0;
  ctx.completedStep = currentCtx.completedStep ?? 0;
  ctx.currentTutorial = currentCtx.currentTutorial ?? {
    steps: [
      {
        negativeReinforcementMessage: "Click on 'Node' to continue...",
        mode: new BlockMode({ action: "CREATED", target: "Node" }, [".palette", "canvas"])
      }
    ]
  };
  ctx.isHighlightLayerEnabled = currentCtx.isHighlightLayerEnabled ?? false;
  ctx.isNegativeReinforcementStateEnabled = currentCtx.isNegativeReinforcementStateEnabled ?? false;
  ctx.latestUserInteraction = currentCtx.latestUserInteraction ?? new UserInteraction("", "");
}
