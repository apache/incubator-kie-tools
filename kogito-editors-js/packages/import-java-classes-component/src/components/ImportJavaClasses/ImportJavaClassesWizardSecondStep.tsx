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
import { Spinner } from "@patternfly/react-core";
import { useEffect, useState } from "react";
import { ImportJavaClassesWizardFieldListTable } from "./ImportJavaClassesWizardFieldListTable";
import { JavaClassField } from "./Model/JavaClassField";
import { JavaClass } from "./Model/JavaClass";

export interface ImportJavaClassesWizardSecondStepProps {
  /** List of the selected classes by user */
  selectedJavaClasses: string[];
}

export const ImportJavaClassesWizardSecondStep: React.FunctionComponent<ImportJavaClassesWizardSecondStepProps> = ({
  selectedJavaClasses,
}: ImportJavaClassesWizardSecondStepProps) => {
  const [retrievedJavaClassFields, setRetrievedJavaClassFields] = useState<JavaClass[]>([]);
  /* This function temporary mocks a call to the LSP service method getClasseFields */
  const loadJavaClassFields = (className: string) => {
    const retrieved: Map<string, string> = window.envelopeMock.lspGetClassFieldsServiceMocked(className);
    setRetrievedJavaClassFields((prevState) => {
      const javaFields = Array.from(retrieved, ([key, value]) => new JavaClassField(key, value));
      const javaClass = new JavaClass(className, javaFields);
      return [...prevState, javaClass].sort((a, b) => (a.name < b.name ? -1 : 1));
    });
  };
  useEffect(
    () => selectedJavaClasses.forEach((className: string) => loadJavaClassFields(className)),
    [selectedJavaClasses]
  );

  return (
    <>
      {retrievedJavaClassFields.length != selectedJavaClasses.length ? (
        <Spinner isSVG diameter="80px" />
      ) : (
        <ImportJavaClassesWizardFieldListTable selectedJavaClassFields={retrievedJavaClassFields} />
      )}
    </>
  );
};
