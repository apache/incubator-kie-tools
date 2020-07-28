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

import { getCurrentStep, getSteps } from "../../../components/utils";
import { KogitoGuidedTour } from "../../..";
import { DemoMode, Mode, Step, SubTutorialMode } from "@kogito-tooling/editor-envelope-protocol";

describe("GuidedTourStepsHelper", () => {
  const currentStep = 3;
  const guidedTour = KogitoGuidedTour.getInstance();
  const step0: Step = makeStep("#selector-0");
  const step1: Step = makeStep("#selector-1");
  const step2: Step = makeStep("#selector-2", new SubTutorialMode("tutorial 2"));
  const step3: Step = makeStep("#selector-3");
  const tutorial1 = {
    label: "tutorial 1",
    steps: [step0, step1, step2, step3]
  };
  const step2_0: Step = makeStep("#selector-2_0");
  const step2_1: Step = makeStep("#selector-2_1");
  const step2_2: Step = makeStep("#selector-2_2");
  const step2_3: Step = makeStep("#selector-2_3");
  const tutorial2 = {
    label: "tutorial 2",
    steps: [step2_0, step2_1, step2_2, step2_3]
  };

  guidedTour.registerTutorial(tutorial1);
  guidedTour.registerTutorial(tutorial2);

  describe("getCurrentStep", () => {
    it("returns the current step", () => {
      expect(getCurrentStep(currentStep, tutorial1)).toBe(step2_1);
    });
  });

  describe("getSteps", () => {
    it("returns the steps for the current tutorial", () => {
      expect(getSteps(tutorial1)).toEqual([step0, step1, step2_0, step2_1, step2_2, step2_3, step3]);
    });
  });
});

function makeStep(selector: string, mode: Mode = new DemoMode()) {
  return new Step(mode, "", selector);
}
