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
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { useCallback, useEffect } from "react";
import { ImportJavaClassesWizardFieldListTable } from "./ImportJavaClassesWizardFieldListTable";
import { JavaField } from "./model/JavaField";
import { JavaClass } from "./model/JavaClass";
import { DMNSimpleType, JAVA_TO_DMN_MAP } from "./model/DMNSimpleType";
import { getJavaClassSimpleName } from "./model/JavaClassUtils";
import { JavaCodeCompletionService } from "./services";

export interface ImportJavaClassesWizardSecondStepProps {
  /** Service class which contains all API methods to dialog with Java Code Completion Extension*/
  javaCodeCompletionService: JavaCodeCompletionService;
  /** Function to be called when adding a Java Class */
  onAddJavaClass: (fullClassName: string) => void;
  /** Function to be called to update a Java Class with its retrieved Fields */
  onSelectedJavaClassedFieldsLoaded: (fullClassName: string, fields: JavaField[]) => void;
  /** List of the selected classes by user */
  selectedJavaClasses: JavaClass[];
}

export const ImportJavaClassesWizardSecondStep = ({
  javaCodeCompletionService,
  onAddJavaClass,
  onSelectedJavaClassedFieldsLoaded,
  selectedJavaClasses,
}: ImportJavaClassesWizardSecondStepProps) => {
  const accessorPrefixesList = ["get", "is"];
  const denyList = ["getClass()", "serialVersionUID"];

  const isGetterMethod = useCallback((accessorName: string) => {
    return accessorName.includes("()") && accessorPrefixesList.some((value) => accessorName.startsWith(value));
  }, []);

  const isMethod = useCallback((accessorName: string) => {
    return accessorName.includes("(") && accessorName.includes(")");
  }, []);

  const renameAccessorName = useCallback(
    (originalName: string) => {
      if (isGetterMethod(originalName)) {
        const prefixIndex = accessorPrefixesList.findIndex((value) => originalName.startsWith(value));
        const name = originalName.substring(accessorPrefixesList[prefixIndex].length, originalName.lastIndexOf("("));
        return name.charAt(0).toLowerCase() + name.slice(1);
      } else {
        return originalName;
      }
    },
    [isGetterMethod]
  );

  const generateJavaClassField = useCallback((name: string, type: string, javaClasses: JavaClass[]) => {
    let dmnTypeRef: string = JAVA_TO_DMN_MAP.get(getJavaClassSimpleName(type)) || DMNSimpleType.ANY;
    if (dmnTypeRef === DMNSimpleType.ANY && javaClasses.some((javaClass) => javaClass.name === type)) {
      dmnTypeRef = getJavaClassSimpleName(type);
    }
    return new JavaField(name, type, dmnTypeRef);
  }, []);

  const isInDenyList = useCallback((accessorName: string) => {
    return denyList.includes(accessorName);
  }, []);

  const loadJavaFields = useCallback(
    (className: string) => {
      try {
        javaCodeCompletionService
          .getFields(className)
          .then((javaCodeCompletionFields) => {
            const retrievedFields = javaCodeCompletionFields
              .filter(
                (javaCodeCompletionField) =>
                  !isInDenyList(javaCodeCompletionField.accessor) &&
                  (!isMethod(javaCodeCompletionField.accessor) || isGetterMethod(javaCodeCompletionField.accessor))
              )
              .map((javaCodeCompletionField) =>
                generateJavaClassField(
                  renameAccessorName(javaCodeCompletionField.accessor),
                  javaCodeCompletionField.type,
                  selectedJavaClasses
                )
              );
            retrievedFields.sort((a, b) => (a.name < b.name ? -1 : 1));
            onSelectedJavaClassedFieldsLoaded(className, retrievedFields);
          })
          .catch((reason) => {
            console.error(reason);
          });
      } catch (error) {
        console.error(error);
      }
    },
    [
      generateJavaClassField,
      javaCodeCompletionService,
      onSelectedJavaClassedFieldsLoaded,
      selectedJavaClasses,
      isGetterMethod,
      isMethod,
      renameAccessorName,
    ]
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
