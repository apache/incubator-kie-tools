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
import "./ImportJavaClassesWizardSecondStep.css";
import { Spinner } from "@patternfly/react-core";
import { useEffect } from "react";
import { ImportJavaClassesWizardFieldListTable } from "./ImportJavaClassesWizardFieldListTable";
import { JavaField } from "./Model/JavaField";
import { JavaClass } from "./Model/JavaClass";
import { DMNSimpleType, JAVA_TO_DMN_MAP } from "./Model/DMNSimpleType";
import { getJavaClassSimpleName } from "./Model/JavaClassUtils";

export interface ImportJavaClassesWizardSecondStepProps {
  /** List of the selected classes by user */
  selectedJavaClasses: JavaClass[];
  /** Function to be called to update selected Java Class, after a Fetching request */
  onSelectedJavaClassesUpdated: (fullClassName: string, add: boolean) => void;
  /** Function to be called to update a Java Class with its retrieved Fields */
  onSelectedJavaClassedFieldsLoaded: (fullClassName: string, fields: JavaField[]) => void;
  /** Fetch button label */
  fetchButtonLabel: string;
}

export const ImportJavaClassesWizardSecondStep: React.FunctionComponent<ImportJavaClassesWizardSecondStepProps> = ({
  selectedJavaClasses,
  onSelectedJavaClassesUpdated,
  onSelectedJavaClassedFieldsLoaded,
  fetchButtonLabel,
}: ImportJavaClassesWizardSecondStepProps) => {
  useEffect(
    () =>
      selectedJavaClasses
        .filter((javaClass: JavaClass) => !javaClass.fieldsLoaded)
        .forEach((javaClass: JavaClass) => loadJavaFields(javaClass.name)),
    // eslint-disable-next-line
    [selectedJavaClasses]
  );
  const loadJavaFields = (className: string) => {
    window.envelopeMock
      .lspGetClassFieldsServiceMocked(className)
      .then((value: Map<string, string>) => {
        const fields = Array.from(value, ([name, type]) => generateJavaClassField(name, type, selectedJavaClasses));
        fields.sort((a, b) => (a.name < b.name ? -1 : 1));
        onSelectedJavaClassedFieldsLoaded(className, fields);
      })
      .catch((reason) => {
        console.error(reason);
      });
  };
  const generateJavaClassField = (name: string, type: string, selectedJavaClasses: JavaClass[]) => {
    let dmnTypeRef: string = (JAVA_TO_DMN_MAP as any)[getJavaClassSimpleName(type)] || DMNSimpleType.ANY;
    if (dmnTypeRef === DMNSimpleType.ANY && selectedJavaClasses.some((javaClass) => javaClass.name === type)) {
      dmnTypeRef = getJavaClassSimpleName(type);
    }
    return new JavaField(name, type, dmnTypeRef);
  };

  return (
    <>
      {selectedJavaClasses.some((javaClass: JavaClass) => !javaClass.fieldsLoaded) ? (
        <Spinner isSVG={true} diameter="150px" className={"loader"} />
      ) : (
        <ImportJavaClassesWizardFieldListTable
          selectedJavaClassFields={selectedJavaClasses}
          readOnly={false}
          onFetchButtonClick={(fullClassName: string) => onSelectedJavaClassesUpdated(fullClassName, true)}
          fetchButtonLabel={fetchButtonLabel}
        />
      )}
    </>
  );
};
