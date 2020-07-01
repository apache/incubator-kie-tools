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
import { useContext, useCallback, useMemo } from "react";

import { Button } from "@patternfly/react-core";
import { AngleLeftIcon, AngleRightIcon } from "@patternfly/react-icons";

import { getSteps, getCurrentStep } from "./utils";
import { CurrentTutorialContext } from "../contexts";

import "./NavigationControls.sass";

export const NavigationControls = () => {
  const { currentTutorial, currentStep, setCurrentStep } = useContext(CurrentTutorialContext);
  const isButtonsHidden = useMemo(() => getCurrentStep(currentStep, currentTutorial)?.navigatorEnabled !== true, [
    currentStep,
    currentTutorial
  ]);
  const numberOfSteps = useMemo(() => getSteps(currentTutorial).length, [currentTutorial]);
  const currentStepNumber = (currentStep ?? 0) + 1;

  const prev = useCallback(() => setCurrentStep(currentStep - 1), [currentStep]);
  const next = useCallback(() => setCurrentStep(currentStep + 1), [currentStep]);
  const stepBullets = useCallback(() => {
    const bullets = [...Array(numberOfSteps).keys()].map(stepNumber => {
      const baseClassName = "kgt-nav-controls__bullet";
      const isCurrentStep = currentStepNumber === stepNumber + 1;
      const className = `${baseClassName} ${isCurrentStep ? `${baseClassName}--current` : ""}`;

      return (
        <div className={className} key={stepNumber}>
          {"  "}
        </div>
      );
    });

    return <div className="kgt-nav-controls__bullets">{bullets}</div>;
  }, [currentTutorial, currentStep]);

  return (
    <div className="kgt-nav-controls">
      <Button
        hidden={isButtonsHidden}
        data-kgt-prev="true"
        onClick={prev}
        isDisabled={currentStep === 0}
        variant="link"
      >
        <AngleLeftIcon />
      </Button>
      {stepBullets()}
      <Button
        hidden={isButtonsHidden}
        data-kgt-next="true"
        onClick={next}
        isDisabled={currentStep === numberOfSteps - 1}
        variant="link"
      >
        <AngleRightIcon />
      </Button>
    </div>
  );
};
