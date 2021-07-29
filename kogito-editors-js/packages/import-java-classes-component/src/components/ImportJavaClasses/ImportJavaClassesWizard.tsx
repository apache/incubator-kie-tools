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
import { ModalWizard } from "../ModalWizard";
import { useImportJavaClassesWizardI18n } from "../../i18n";
import { ImportJavaClassesWizardFirstStep } from "./ImportJavaClassesWizardFirstStep";
import { useState } from "react";

export interface ImportJavaClassesWizardProps {
  /** Button disabled status */
  buttonDisabledStatus: boolean;
  /** Button tooltip message */
  buttonTooltipMessage?: string;
}

export const ImportJavaClassesWizard: React.FunctionComponent<ImportJavaClassesWizardProps> = ({
  buttonDisabledStatus,
  buttonTooltipMessage,
}: ImportJavaClassesWizardProps) => {
  const { i18n } = useImportJavaClassesWizardI18n();
  const [selectedJavaClasses, setSelectedJavaClasses] = useState<string[]>([]);
  const updateSelectedClasses = (fullClassName: string, add: boolean) => {
    if (add) {
      const classesSet = new Set(selectedJavaClasses);
      classesSet.add(fullClassName);
      setSelectedJavaClasses(Array.from(classesSet));
    } else {
      const classesSet = new Set(selectedJavaClasses);
      classesSet.delete(fullClassName);
      setSelectedJavaClasses(Array.from(classesSet));
    }
  };

  const steps = [
    {
      name: i18n.modalWizard.firstStep.stepName,
      component: (
        <ImportJavaClassesWizardFirstStep
          selectedJavaClasses={selectedJavaClasses}
          onSelectedJavaClassesUpdated={updateSelectedClasses}
        />
      ),
      enableNext: selectedJavaClasses.length > 0,
      canJumpTo: true,
      hideBackButton: true,
    },
    {
      name: i18n.modalWizard.secondStep.stepName,
      component: <p>You selected: {selectedJavaClasses.join(", ")}</p>,
      enableNext: false,
      canJumpTo: selectedJavaClasses.length > 0,
    },
    {
      name: i18n.modalWizard.thirdStep.stepName,
      component: <p>Step 3 content</p>,
      enableNext: false,
      canJumpTo: false,
    },
  ];

  return (
    <ModalWizard
      buttonStyle="secondary"
      buttonText={i18n.modalButton.text}
      buttonDisabledStatus={buttonDisabledStatus}
      buttonTooltipMessage={buttonTooltipMessage}
      wizardTitle={i18n.modalWizard.title}
      wizardDescription={i18n.modalWizard.description}
      wizardSteps={steps}
    />
  );
};
