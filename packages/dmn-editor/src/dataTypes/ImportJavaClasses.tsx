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
import { useCallback, useMemo } from "react";
import { DropdownItem } from "@patternfly/react-core/dist/js/components/Dropdown";
import {
  ImportJavaClasses,
  // JavaClass,
  JavaCodeCompletionService,
  useImportJavaClassesWizardI18n,
  useLanguageServerAvailable,
} from "@kie-tools/import-java-classes-component";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { DataType } from "./DataTypes";
import { addTopLevelItemDefinition as _addTopLevelItemDefinition } from "../mutations/addTopLevelItemDefinition";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { getNewItemDefinition } from "./DataTypeSpec";
import { Spinner } from "@patternfly/react-core/dist/esm/components/Spinner";
import PlusIcon from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";

type JavaField = {
  /* Field Name */
  name: string;
  /* The Java Type of the field (eg. java.lang.String OR com.mypackace.Test) */
  type: string;
  /* List Type */
  isList: boolean;
  /* The DMN Type reference */
  dmnTypeRef: string;
};

type JavaClass = {
  /** Java Class Name (eg. java.lang.String OR com.mypackage.Test) */
  name: string;
  /** Java Fields of the class */
  fields: JavaField[];
  /** It indicates if the fields has been loaded, in order to support empty fields Java Classes */
  fieldsLoaded: boolean;
};

const NAME_SEPARATOR: string = "-";

const useImportJavaClasses = () => {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { externalModelsByNamespace } = useExternalModels();
  const dataTypesTree = useDmnEditorStore((s) => s.computed(s).getDataTypes(externalModelsByNamespace).dataTypesTree);

  const updatePropertiesReferences = useCallback(
    (javaClasses: JavaClass[], javaClassNameToDMNTypeNameMap: Map<string, string>): JavaClass[] => {
      return javaClasses.map((javaClass) => {
        const updatedFields = (javaClass ?? [])?.fields?.map((field) => {
          if (javaClassNameToDMNTypeNameMap.has(field.type)) {
            const renamedFieldType = javaClassNameToDMNTypeNameMap.get(field.type)!;
            return { ...field, dmnTypeRef: renamedFieldType };
          }
          return field;
        });

        return { ...javaClass, fields: updatedFields };
      });
    },
    []
  );

  const buildName = useCallback(
    (nameCandidate: string, namesCount: Map<string, number>, nameSeparator: string = NAME_SEPARATOR): string => {
      if (namesCount.has(nameCandidate)) {
        const occurrences = namesCount.get(nameCandidate)!;
        namesCount.set(nameCandidate, occurrences + 1);
        return nameCandidate + nameSeparator + occurrences;
      }
      namesCount.set(nameCandidate, 1);
      return nameCandidate;
    },
    []
  );
  const renameJavaClassToDMNName = useCallback(
    (javaClasses: JavaClass[]): JavaClass[] => {
      const namesCount: Map<string, number> = new Map();
      const javaClassNameToDMNTypeNameMap: Map<string, string> = new Map();

      // Map the javaClasses to new renamed classes
      const renamedJavaClasses = javaClasses.map((javaClass: JavaClass) => {
        const nameCandidate = javaClass.name.substring(javaClass.name.lastIndexOf(".") + 1);
        const newName = buildName(nameCandidate, namesCount);
        javaClassNameToDMNTypeNameMap.set(javaClass.name, newName);
        return { ...javaClass, name: newName };
      });

      // Return the updated Java classes with renamed types
      return updatePropertiesReferences(renamedJavaClasses, javaClassNameToDMNTypeNameMap);
    },
    [updatePropertiesReferences, buildName]
  );

  const renameJavaClassIfExists = useCallback(
    (javaClasses: JavaClass[], dataTypesTree: DataType[]): JavaClass[] => {
      const namesCount: Map<string, number> = new Map();
      return javaClasses?.map((javaClass: JavaClass) => {
        console.log({ dataTypesTree });
        const isExisting = dataTypesTree?.filter(
          ({ feelName, itemDefinition }) => feelName === javaClass?.name || itemDefinition["@_name"] === javaClass?.name
        );
        console.log("same name exist", isExisting);
        if (isExisting?.length > 0) {
          console.log("enter is existing");
          // const newName = buildName(javaClass?.name, namesCount);
          // console.log("newName", newName);
          return { ...javaClass, name: `${javaClass?.name}-${isExisting?.length}` };
        }
        return javaClass;
      });
    },
    [buildName]
  );

  const mapJavaClassesToDMNItemDefinitions = useCallback(
    (javaClasses: JavaClass[]): ReturnType<typeof getNewItemDefinition>[] => {
      return javaClasses?.map((javaClass: JavaClass) => {
        const itemsComponents = javaClass?.fields?.map((field) =>
          getNewItemDefinition({ "@_name": field?.name, typeRef: { __$$text: field?.dmnTypeRef } })
        );
        return getNewItemDefinition({ "@_name": javaClass?.name, typeRef: undefined, itemComponent: itemsComponents });
      });
    },
    []
  );

  const importJavaClassesInDataTypeEditor = useCallback(
    (javaClasses: JavaClass[]) => {
      if (javaClasses?.length === 0) return;
      console.log("actualClasses", javaClasses);
      const newDataType = renameJavaClassToDMNName(javaClasses);
      console.log("newDataType", newDataType);
      const updatedClasses = renameJavaClassIfExists(newDataType, dataTypesTree);
      console.log("updatedClasses", updatedClasses);
      const itemDefinitions = mapJavaClassesToDMNItemDefinitions(updatedClasses);
      dmnEditorStoreApi.setState((state) => {
        state.dmn.model.definitions.itemDefinition?.unshift(...itemDefinitions);
        state.dataTypesEditor.activeItemDefinitionId = itemDefinitions?.[0]?.["@_id"];
        state.focus.consumableId = itemDefinitions?.[0]?.["@_id"];
      });
    },
    [
      dataTypesTree,
      dmnEditorStoreApi,
      mapJavaClassesToDMNItemDefinitions,
      renameJavaClassIfExists,
      renameJavaClassToDMNName,
    ]
  );

  const javaCodeCompletionService: JavaCodeCompletionService = useMemo(
    () => ({
      getClasses: (query: string) => window.envelope?.javaCodeCompletionService?.getClasses(query),
      getFields: (fullClassName: string) => window.envelope?.javaCodeCompletionService?.getAccessors(fullClassName, ""),
      isLanguageServerAvailable: () => window.envelope?.javaCodeCompletionService?.isLanguageServerAvailable(),
    }),
    []
  );
  return { javaCodeCompletionService, importJavaClassesInDataTypeEditor };
};

const ImportJavaClassesWrapper = () => {
  const { importJavaClassesInDataTypeEditor, javaCodeCompletionService } = useImportJavaClasses();
  return (
    <ImportJavaClasses
      loadJavaClassesInDataTypeEditor={importJavaClassesInDataTypeEditor}
      javaCodeCompletionService={javaCodeCompletionService}
    />
  );
};

const ImportJavaClassesDropdownItem = (props: React.ComponentProps<typeof DropdownItem>) => {
  const { i18n } = useImportJavaClassesWizardI18n();
  const { javaCodeCompletionService } = useImportJavaClasses();
  const { isLanguageServerLoading, isLanguageServerDisabled, isLanguageServerError } =
    useLanguageServerAvailable(javaCodeCompletionService);
  const defineTooltipMessage = React.useCallback(() => {
    if (isLanguageServerDisabled) {
      return i18n.modalButton.disabledMessage;
    } else if (isLanguageServerError) {
      return i18n.modalButton.errorMessage;
    }
    return undefined;
  }, [
    isLanguageServerDisabled,
    isLanguageServerError,
    i18n.modalButton.disabledMessage,
    i18n.modalButton.errorMessage,
  ]);
  return (
    <>
      {defineTooltipMessage() ? (
        <Tooltip content={defineTooltipMessage()}>
          <DropdownItem
            style={{ minWidth: "240px" }}
            icon={isLanguageServerLoading ? <Spinner size="md" /> : <PlusIcon />}
            isDisabled={isLanguageServerDisabled || isLanguageServerLoading}
            {...props}
          >
            {i18n.modalButton.text}
          </DropdownItem>
        </Tooltip>
      ) : (
        <DropdownItem
          style={{ minWidth: "240px" }}
          icon={isLanguageServerLoading ? <Spinner size="md" /> : <PlusIcon />}
          isDisabled={isLanguageServerDisabled || isLanguageServerLoading}
          {...props}
        >
          {i18n.modalButton.text}
        </DropdownItem>
      )}
    </>
  );
};

export { ImportJavaClassesWrapper, ImportJavaClassesDropdownItem, useImportJavaClasses };
