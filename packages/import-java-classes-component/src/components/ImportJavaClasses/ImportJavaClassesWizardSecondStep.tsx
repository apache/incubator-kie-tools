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
import { Bullseye, Spinner } from "@patternfly/react-core";
import { useCallback, useEffect } from "react";
import { ImportJavaClassesWizardFieldListTable } from "./ImportJavaClassesWizardFieldListTable";
import { JavaField } from "./Model/JavaField";
import { JavaClass } from "./Model/JavaClass";
import { DMNSimpleType, JAVA_TO_DMN_MAP } from "./Model/DMNSimpleType";
import { getJavaClassSimpleName } from "./Model/JavaClassUtils";

export interface ImportJavaClassesWizardSecondStepProps {
  /** List of the selected classes by user */
  selectedJavaClasses: JavaClass[];
  /** Function to be called when adding a Java Class */
  onAddJavaClass: (fullClassName: string) => void;
  /** Function to be called to update a Java Class with its retrieved Fields */
  onSelectedJavaClassedFieldsLoaded: (fullClassName: string, fields: JavaField[]) => void;
}

export const ImportJavaClassesWizardSecondStep = ({
  selectedJavaClasses,
  onAddJavaClass,
  onSelectedJavaClassedFieldsLoaded,
}: ImportJavaClassesWizardSecondStepProps) => {
  const generateJavaClassField = useCallback((name: string, type: string, javaClasses: JavaClass[]) => {
    let dmnTypeRef: string = JAVA_TO_DMN_MAP.get(getJavaClassSimpleName(type)) || DMNSimpleType.ANY;
    if (dmnTypeRef === DMNSimpleType.ANY && javaClasses.some((javaClass) => javaClass.name === type)) {
      dmnTypeRef = getJavaClassSimpleName(type);
    }
    return new JavaField(name, type, dmnTypeRef);
  }, []);

  const loadJavaFields = useCallback(
    (className: string) => {
      window.envelopeMock
        .lspGetClassFieldsServiceMocked(className)
        .then((value) => {
          const fields = Array.from(value, ([name, type]) => generateJavaClassField(name, type, selectedJavaClasses));
          fields.sort((a, b) => (a.name < b.name ? -1 : 1));
          onSelectedJavaClassedFieldsLoaded(className, fields);
        })
        .catch((reason) => {
          console.error(reason);
        });
    },
    [generateJavaClassField, onSelectedJavaClassedFieldsLoaded, selectedJavaClasses]
  );

  useEffect(
    () =>
      selectedJavaClasses
        .filter((javaClass) => !javaClass.fieldsLoaded)
        .forEach((javaClass) => loadJavaFields(javaClass.name)),
    [selectedJavaClasses, loadJavaFields]
  );

  return (
    <>
      {selectedJavaClasses.some((javaClass) => !javaClass.fieldsLoaded) ? (
        <Bullseye>
          <Spinner isSVG={true} />
        </Bullseye>
      ) : (
        <ImportJavaClassesWizardFieldListTable
          selectedJavaClassFields={selectedJavaClasses}
          loadJavaClass={onAddJavaClass}
        />
      )}
    </>
  );
};
