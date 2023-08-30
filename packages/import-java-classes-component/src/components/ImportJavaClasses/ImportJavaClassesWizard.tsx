/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useImportJavaClassesWizardI18n } from "../../i18n";
import { ImportJavaClassesWizardFirstStep } from "./ImportJavaClassesWizardFirstStep";
import { ImportJavaClassesWizardSecondStep } from "./ImportJavaClassesWizardSecondStep";
import { ImportJavaClassesWizardThirdStep } from "./ImportJavaClassesWizardThirdStep";
import { useCallback, useEffect, useState } from "react";
import { JavaClass } from "./model/JavaClass";
import { JavaField } from "./model/JavaField";
import { DMNSimpleType } from "./model/DMNSimpleType";
import { getJavaClassSimpleName } from "./model/JavaClassUtils";
import { GWTLayerService, JavaCodeCompletionService } from "./services";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Wizard } from "@patternfly/react-core/dist/js/components/Wizard";

export interface ImportJavaClassesWizardProps {
  /** Service class which contains all API methods to dialog with GWT layer */
  gwtLayerService: GWTLayerService;
  /** Service class which contains all API methods to dialog with Java Code Completion Extension*/
  javaCodeCompletionService: JavaCodeCompletionService;
}

export const ImportJavaClassesWizard = ({
  gwtLayerService,
  javaCodeCompletionService,
}: ImportJavaClassesWizardProps) => {
  type ButtonStatus = "disable" | "enable" | "loading" | "error";
  const { i18n } = useImportJavaClassesWizardI18n();
  const [javaClasses, setJavaClasses] = useState<JavaClass[]>([]);
  const [isOpen, setOpen] = useState(false);
  const [buttonStatus, setButtonStatus] = useState<ButtonStatus>("loading");

  useEffect(() => {
    try {
      javaCodeCompletionService
        .isLanguageServerAvailable()
        .then((available: boolean) => {
          setButtonStatus(available ? "enable" : "disable");
        })
        .catch((reason) => {
          setButtonStatus("error");
          console.error(reason);
        });
    } catch (error) {
      setButtonStatus("error");
      console.error(error);
    }
  }, [javaCodeCompletionService]);

  const isButtonDisabled = useCallback(() => {
    return "enable" !== buttonStatus;
  }, [buttonStatus]);

  const isButtonLoading = useCallback(() => {
    return "loading" == buttonStatus;
  }, [buttonStatus]);

  const defineTooltipMessage = useCallback(() => {
    if ("disable" === buttonStatus) {
      return i18n.modalButton.disabledMessage;
    } else if ("error" === buttonStatus) {
      return i18n.modalButton.errorMessage;
    }
    return undefined;
  }, [buttonStatus, i18n.modalButton.disabledMessage, i18n.modalButton.errorMessage]);

  const updateJavaFieldsReferences = useCallback(
    (updatedJavaClasses: JavaClass[], previousJavaClasses: JavaClass[]) => {
      const updatedJavaClassesNames = updatedJavaClasses.map((javaClass) => javaClass.name);
      const previousJavaClassesNames = previousJavaClasses.map((javaClass) => javaClass.name);
      const allFields = javaClasses.map((javaClass) => javaClass.fields).flat(1);
      allFields.forEach((field) => {
        if (field.dmnTypeRef === DMNSimpleType.ANY && updatedJavaClassesNames.includes(field.type)) {
          field.dmnTypeRef = getJavaClassSimpleName(field.type);
        } else if (previousJavaClassesNames.includes(field.type) && !updatedJavaClassesNames.includes(field.type)) {
          field.dmnTypeRef = DMNSimpleType.ANY;
        }
      });
    },
    [javaClasses]
  );

  const addJavaClass = useCallback(
    (fullClassName: string) => {
      setJavaClasses((prevState) => {
        if (!prevState.some((javaClass) => javaClass.name === fullClassName)) {
          const updatedSelectedJavaClasses = [...prevState, new JavaClass(fullClassName)];
          updatedSelectedJavaClasses.sort((a, b) => (a.name < b.name ? -1 : 1));
          updateJavaFieldsReferences(updatedSelectedJavaClasses, prevState);
          return updatedSelectedJavaClasses;
        }
        return prevState;
      });
    },
    [updateJavaFieldsReferences]
  );

  const removeJavaClass = useCallback(
    (fullClassName: string) => {
      setJavaClasses((prevState) => {
        const updatedSelectedJavaClasses = prevState.filter((javaClass) => javaClass.name !== fullClassName);
        updateJavaFieldsReferences(updatedSelectedJavaClasses, prevState);
        return updatedSelectedJavaClasses;
      });
    },
    [updateJavaFieldsReferences]
  );

  const updateSelectedClassesFields = useCallback((fullClassName: string, fields: JavaField[]) => {
    setJavaClasses((prevState) => {
      const updatedJavaClasses = [...prevState];
      const javaClassIndex = updatedJavaClasses.findIndex((javaClass) => javaClass.name === fullClassName);
      if (javaClassIndex > -1) {
        updatedJavaClasses[javaClassIndex].setFields(fields);
      }
      return updatedJavaClasses;
    });
  }, []);

  const isSecondStepActivatable = useCallback(() => {
    return javaClasses.length > 0;
  }, [javaClasses]);

  const isThirdStepActivatable = useCallback(() => {
    return javaClasses.length > 0 && javaClasses.every((javaClass) => javaClass.fieldsLoaded);
  }, [javaClasses]);

  const handleButtonClick = useCallback(() => setOpen((prevState) => !prevState), []);

  const handleWizardClose = useCallback(() => {
    handleButtonClick();
    setJavaClasses([]);
  }, [handleButtonClick]);

  const handleWizardSave = useCallback(() => {
    handleWizardClose();
    gwtLayerService.importJavaClassesInDataTypeEditor(javaClasses);
  }, [javaClasses, handleWizardClose, gwtLayerService]);

  const steps = [
    {
      canJumpTo: true,
      component: (
        <ImportJavaClassesWizardFirstStep
          javaCodeCompletionService={javaCodeCompletionService}
          onAddJavaClass={addJavaClass}
          onRemoveJavaClass={removeJavaClass}
          selectedJavaClasses={javaClasses}
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
          javaCodeCompletionService={javaCodeCompletionService}
          onAddJavaClass={addJavaClass}
          onSelectedJavaClassedFieldsLoaded={updateSelectedClassesFields}
          selectedJavaClasses={javaClasses}
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
    <>
      {defineTooltipMessage() ? (
        <Tooltip content={defineTooltipMessage()}>
          <Button
            data-testid={"modal-wizard-button"}
            isAriaDisabled={isButtonDisabled()}
            isLoading={isButtonLoading()}
            onClick={handleButtonClick}
            variant={"secondary"}
          >
            {i18n.modalButton.text}
          </Button>
        </Tooltip>
      ) : (
        <Button
          data-testid={"modal-wizard-button"}
          isAriaDisabled={isButtonDisabled()}
          isLoading={isButtonLoading()}
          onClick={handleButtonClick}
          variant={"secondary"}
        >
          {i18n.modalButton.text}
        </Button>
      )}
      {isOpen ? (
        <Modal
          description={i18n.modalWizard.description}
          isOpen={isOpen}
          onClose={handleWizardClose}
          title={i18n.modalWizard.title}
          variant={ModalVariant.large}
        >
          <Wizard
            className={"import-java-classes"}
            height={600}
            onClose={handleWizardClose}
            onSave={handleWizardSave}
            steps={steps}
          />
        </Modal>
      ) : null}
    </>
  );
};
