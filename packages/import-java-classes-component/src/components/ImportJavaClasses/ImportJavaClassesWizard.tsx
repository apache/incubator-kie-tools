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
import { ImportJavaClassesWizardSecondStep } from "./ImportJavaClassesWizardSecondStep";
import { ImportJavaClassesWizardThirdStep } from "./ImportJavaClassesWizardThirdStep";
import { useState } from "react";
import { JavaClass } from "./Model/JavaClass";
import { JavaField } from "./Model/JavaField";
import { DMNSimpleType } from "./Model/DMNSimpleType";
import { getJavaClassSimpleName } from "./Model/JavaClassUtils";

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
  const [javaClasses, setJavaClasses] = useState<JavaClass[]>([]);
  const updateSelectedClasses = (fullClassName: string, add: boolean) => {
    if (add) {
      addJavaClass(fullClassName);
    } else {
      removeJavaClass(fullClassName);
    }
  };
  const addJavaClass = (fullClassName: string) => {
    if (!javaClasses.some((javaClass) => javaClass.name === fullClassName)) {
      const updatedSelectedJavaClasses: JavaClass[] = [...javaClasses, new JavaClass(fullClassName)];
      updatedSelectedJavaClasses.sort((a, b) => (a.name < b.name ? -1 : 1));
      updateJavaFieldsReferences(updatedSelectedJavaClasses, javaClasses);
      setJavaClasses(updatedSelectedJavaClasses);
    }
  };
  const removeJavaClass = (fullClassName: string) => {
    const updatedSelectedJavaClasses: JavaClass[] = javaClasses.filter((javaClass) => javaClass.name !== fullClassName);
    updateJavaFieldsReferences(updatedSelectedJavaClasses, javaClasses);
    setJavaClasses(updatedSelectedJavaClasses);
  };
  const updateSelectedClassesFields = (fullClassName: string, fields: JavaField[]) => {
    const javaClassIndex: number = javaClasses.findIndex((javaClass) => javaClass.name === fullClassName);
    if (javaClassIndex > -1) {
      javaClasses[javaClassIndex].setFields(fields);
    }
    setJavaClasses([...javaClasses]);
  };
  const updateJavaFieldsReferences = (updatedJavaClasses: JavaClass[], previousJavaClasses: JavaClass[]) => {
    const updatedJavaClassesNames: string[] = updatedJavaClasses.map((javaClass) => javaClass.name);
    const previousJavaClassesNames: string[] = previousJavaClasses.map((javaClass) => javaClass.name);
    const allFields: JavaField[] = javaClasses.map((javaClass) => javaClass.fields).flat(1);
    allFields.forEach((field: JavaField) => {
      if (field.dmnTypeRef === DMNSimpleType.ANY && updatedJavaClassesNames.includes(field.type)) {
        field.dmnTypeRef = getJavaClassSimpleName(field.type);
      } else if (previousJavaClassesNames.includes(field.type) && !updatedJavaClassesNames.includes(field.type)) {
        field.dmnTypeRef = DMNSimpleType.ANY;
      }
    });
  };
  const isSecondStepActivatable = () => {
    return javaClasses.length > 0;
  };
  const isThirdStepActivatable = () => {
    return javaClasses.length > 0 && javaClasses.every((javaClass: JavaClass) => javaClass.fieldsLoaded);
  };
  const resetJavaClassState = () => {
    setJavaClasses([]);
  };
  const steps = [
    {
      canJumpTo: true,
      component: (
        <ImportJavaClassesWizardFirstStep
          selectedJavaClasses={javaClasses}
          onSelectedJavaClassesUpdated={updateSelectedClasses}
        />
      ),
      enableNext: isSecondStepActivatable(),
      hideBackButton: true,
      name: i18n.modalWizard.firstStep.stepName,
    },
    {
      canJumpTo: isSecondStepActivatable(),
      component: (
        <ImportJavaClassesWizardSecondStep
          selectedJavaClasses={javaClasses}
          onSelectedJavaClassesUpdated={updateSelectedClasses}
          onSelectedJavaClassedFieldsLoaded={updateSelectedClassesFields}
          fetchButtonLabel={i18n.modalWizard.secondStep.fetchButtonLabel}
        />
      ),
      enableNext: isThirdStepActivatable(),
      name: i18n.modalWizard.secondStep.stepName,
    },
    {
      canJumpTo: isThirdStepActivatable(),
      component: <ImportJavaClassesWizardThirdStep selectedJavaClasses={javaClasses} />,
      name: i18n.modalWizard.thirdStep.stepName,
      nextButtonText: i18n.modalWizard.thirdStep.nextButtonText,
    },
  ];

  return (
    <ModalWizard
      buttonStyle="secondary"
      buttonText={i18n.modalButton.text}
      buttonDisabledStatus={buttonDisabledStatus}
      buttonTooltipMessage={buttonTooltipMessage}
      className={"import-java-classes"}
      onWizardClose={resetJavaClassState}
      wizardDescription={i18n.modalWizard.description}
      wizardSteps={steps}
      wizardTitle={i18n.modalWizard.title}
    />
  );
};
