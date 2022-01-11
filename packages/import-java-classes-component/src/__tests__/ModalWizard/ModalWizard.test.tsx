/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { ModalWizard } from "../../components/ModalWizard";
import { WizardStep } from "@patternfly/react-core";
import { ImportIcon } from "@patternfly/react-icons";

describe("ModalWizard component tests", () => {
  describe("ModalWizard Button component tests", () => {
    test("should render ModalWizard Button component", () => {
      const { container } = render(
        <ModalWizard
          buttonStyle={"primary"}
          buttonText={"bText"}
          wizardDescription={"wDescription"}
          wizardSteps={[]}
          wizardTitle={"wTitle"}
          buttonDisabledStatus={false}
        />
      );

      expect(container).toMatchSnapshot();
    });

    test("should render ModalWizard Button component with Icon", () => {
      const { container } = render(
        <ModalWizard
          buttonStyle={"primary"}
          buttonText={"bText"}
          wizardDescription={"wDescription"}
          wizardSteps={[]}
          wizardTitle={"wTitle"}
          buttonIcon={<ImportIcon />}
          buttonDisabledStatus={false}
        />
      );

      expect(container).toMatchSnapshot();
    });

    test("should render disabled ModalWizard Button component", () => {
      const { container } = render(
        <ModalWizard
          buttonStyle={"primary"}
          buttonText={"bText"}
          wizardDescription={"wDescription"}
          wizardSteps={[]}
          wizardTitle={"wTitle"}
          buttonDisabledStatus={true}
        />
      );

      expect(container).toMatchSnapshot();
    });

    test("should render disabled ModalWizard with tooltip Button component", async () => {
      const { baseElement, getByTestId } = render(
        <ModalWizard
          buttonStyle={"primary"}
          buttonText={"bText"}
          wizardDescription={"wDescription"}
          wizardSteps={[]}
          wizardTitle={"wTitle"}
          buttonDisabledStatus={true}
          buttonTooltipMessage={"TooltipMessage"}
        />
      );
      const modalWizardButton = getByTestId("modal-wizard-button")! as HTMLButtonElement;
      await fireEvent.mouseOver(modalWizardButton);

      expect(baseElement).toMatchSnapshot();
    });

    test("Should show Modal after clicking on the button", () => {
      const wizardSteps: WizardStep[] = [
        {
          name: "Ciao",
          component: <p>Step 1 content</p>,
          enableNext: false,
          canJumpTo: false,
          hideBackButton: true,
        },
      ];

      const { baseElement, getByTestId } = render(
        <ModalWizard
          buttonStyle={"primary"}
          buttonText={"bText"}
          wizardDescription={"wDescription"}
          buttonDisabledStatus={false}
          wizardSteps={wizardSteps}
          wizardTitle={"wTitle"}
        />
      );

      const modalWizardButton = getByTestId("modal-wizard-button")! as HTMLButtonElement;
      modalWizardButton.click();
      expect(baseElement).toMatchSnapshot();
    });

    test("Should close Modal after opening it and clicking on the close button", () => {
      const wizardSteps: WizardStep[] = [
        {
          name: "Ciao",
          component: <p>Step 1 content</p>,
          enableNext: false,
          canJumpTo: false,
          hideBackButton: true,
        },
      ];

      const { baseElement, getByTestId, getByText } = render(
        <ModalWizard
          buttonStyle={"primary"}
          buttonText={"bText"}
          wizardDescription={"wDescription"}
          buttonDisabledStatus={false}
          wizardSteps={wizardSteps}
          wizardTitle={"wTitle"}
        />
      );

      const modalWizardButton = getByTestId("modal-wizard-button")! as HTMLButtonElement;
      modalWizardButton.click();
      const cancelButton = getByText("Cancel") as HTMLButtonElement;
      cancelButton.click();
      expect(baseElement).toMatchSnapshot();
    });
  });
});
