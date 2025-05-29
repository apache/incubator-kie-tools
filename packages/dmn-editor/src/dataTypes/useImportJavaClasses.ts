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

import { useCallback, useMemo, useState } from "react";
import { JavaClass } from "@kie-tools/import-java-classes-component";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { findDataTypeById, getNewItemDefinition } from "./DataTypeSpec";
import { EditItemDefinition } from "./DataTypes";

const NAME_SEPARATOR: string = "-";

export enum JavaClassConflictOptions {
  REPLACE = "Replace",
  KEEP_BOTH = "Keep Both",
}
export type JavaClassWithConflictInfo = JavaClass & {
  isExternalConflict: boolean;
};

const useImportJavaClasses = () => {
  const [isConflictsOccured, setIsConflictsOccured] = useState<boolean>(false);
  const [conflictsClasses, setConflictsClasses] = useState<JavaClassWithConflictInfo[]>([]);

  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { externalModelsByNamespace } = useExternalModels();
  const allDataTypesById = useDmnEditorStore(
    (s) => s.computed(s).getDataTypes(externalModelsByNamespace).allDataTypesById
  );
  const dataTypesTree = useDmnEditorStore((s) => s.computed(s).getDataTypes(externalModelsByNamespace).dataTypesTree);

  const dataTypeNames = useMemo(() => {
    const dataTypeNames = new Set<string>();
    for (let i = 0, len = dataTypesTree?.length; i < len; i++) {
      dataTypeNames.add(dataTypesTree?.[i]?.feelName || dataTypesTree?.[i]?.itemDefinition?.["@_name"]);
    }
    return dataTypeNames;
  }, [dataTypesTree]);

  const handleCloseConflictsModal = () => setIsConflictsOccured(false);

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

  const updatePropertiesReferences = useCallback(
    (javaClasses: JavaClass[], javaClassNameToDMNTypeNameMap: Map<string, string>): JavaClass[] => {
      return javaClasses.map((javaClass) => {
        const namesCount: Map<string, number> = new Map();
        const updatedFields = (javaClass ?? [])?.fields?.map((field) => {
          const newFieldName = buildName(field?.name, namesCount);
          if (javaClassNameToDMNTypeNameMap.has(field.type)) {
            const renamedFieldType = javaClassNameToDMNTypeNameMap.get(field.type)!;
            return { ...field, name: newFieldName, dmnTypeRef: renamedFieldType };
          }
          return { ...field, name: newFieldName };
        });
        return { ...javaClass, fields: updatedFields } as JavaClass;
      });
    },
    [buildName]
  );

  const editItemDefinition = useCallback<EditItemDefinition>(
    (id, consumer) => {
      dmnEditorStoreApi.setState((state) => {
        const { itemDefinition, items, index } = findDataTypeById({
          definitions: state.dmn.model.definitions,
          itemDefinitionId: id,
          allDataTypesById,
        });

        state.dmn.model.definitions.itemDefinition ??= [];
        consumer(itemDefinition, items, index, state.dmn.model.definitions.itemDefinition, state);
      });
    },
    [allDataTypesById, dmnEditorStoreApi]
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
        return { ...javaClass, name: newName } as JavaClass;
      });

      // Return the updated Java classes with renamed types
      return updatePropertiesReferences(renamedJavaClasses, javaClassNameToDMNTypeNameMap);
    },
    [updatePropertiesReferences, buildName]
  );

  const generateUniqueDmnTypeNames = useCallback(
    (javaClasses: JavaClass[], nameSeparator: string = NAME_SEPARATOR): JavaClass[] => {
      const namesCount: Map<string, number> = new Map();
      const javaClassNameToDMNTypeNameMap: Map<string, string> = new Map();

      // Map the javaClasses to new renamed classes
      const renamedJavaClasses = javaClasses.map((javaClass: JavaClass) => {
        let newName = javaClass.name;

        // Check if the name already exists in dataTypeNames
        if (dataTypeNames.has(newName)) {
          // Initialize counter for this name if it doesn't exist yet
          if (!namesCount.has(newName)) {
            namesCount.set(newName, 0);
          }

          // Increment counter and append it to name until we find a unique one
          let counter = namesCount.get(newName)!;
          do {
            counter++;
            newName = javaClass.name + nameSeparator + counter;
          } while (dataTypeNames.has(newName));

          // Update the counter for this base name
          namesCount.set(javaClass.name, counter);
        } else {
          // If it's a new name, add it to our tracking
          namesCount.set(javaClass.name, 0);
        }

        // Store the mapping from original class name to new DMN name
        javaClassNameToDMNTypeNameMap.set(javaClass.name, newName);
        return { ...javaClass, name: newName } as JavaClass;
      });

      // Return the updated Java classes with renamed types and references
      return updatePropertiesReferences(renamedJavaClasses, javaClassNameToDMNTypeNameMap);
    },
    [updatePropertiesReferences, dataTypeNames]
  );

  const overwriteExistingDMNTypes = useCallback(
    (javaClasses: JavaClass[]) => {
      for (let i = 0, len = javaClasses?.length; i < len; i++) {
        const className = javaClasses?.[i]?.name;
        if (dataTypeNames?.has(className)) {
          const dataType = dataTypesTree?.find(
            (type) => type?.feelName === className || type?.itemDefinition?.["@_name"] === className
          );
          if (dataType && dataType?.itemDefinition?.["@_id"]) {
            const itemComponents = javaClasses?.[i]?.fields?.map((field) =>
              getNewItemDefinition({ "@_name": field?.name, typeRef: { __$$text: field?.dmnTypeRef } })
            );
            if (itemComponents && itemComponents?.length > 0) {
              editItemDefinition(dataType.itemDefinition!["@_id"], (itemDefinition) => {
                itemDefinition.itemComponent = itemComponents;
              });
            }
          }
        }
      }
    },
    [dataTypeNames, dataTypesTree, editItemDefinition]
  );

  const checkNameConflicts = useCallback(
    (javaClasses: JavaClass[]) => {
      const updatedJavaClasses = renameJavaClassToDMNName(javaClasses);

      // Pre-allocate arrays to avoid resizing
      const conflicts: JavaClassWithConflictInfo[] = [];
      const nonConflicts: JavaClass[] = [];
      if (!dataTypesTree || !externalModelsByNamespace) {
        console.error("Data types or external models are undefined.");
        return { conflicts: [], nonConflicts: [] };
      }

      // Use a traditional for loop for better performance
      for (let i = 0, len = updatedJavaClasses?.length; i < len; i++) {
        const javaClass = updatedJavaClasses?.[i];
        const fullClassName = javaClass.name;
        if (dataTypeNames.has(fullClassName)) {
          const isExternalConflict = dataTypesTree.some((dataType) => {
            return (
              dataType.namespace && externalModelsByNamespace[dataType.namespace] && dataType.feelName === fullClassName
            );
          });

          const javaClassWithConflict: JavaClassWithConflictInfo = Object.assign(javaClass, {
            isExternalConflict,
          });
          conflicts.push(javaClassWithConflict);
        } else {
          nonConflicts.push(javaClass);
        }
      }

      return {
        conflicts,
        nonConflicts,
      };
    },
    [renameJavaClassToDMNName, dataTypesTree, externalModelsByNamespace, dataTypeNames]
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
      const itemDefinitions = mapJavaClassesToDMNItemDefinitions(javaClasses);
      dmnEditorStoreApi.setState((state) => {
        state.dmn.model.definitions.itemDefinition ??= [];
        state.dmn.model.definitions.itemDefinition?.unshift(...itemDefinitions);
        // Keep the last selected if any. Default to first on list.
        state.dataTypesEditor.activeItemDefinitionId = itemDefinitions?.[0]?.["@_id"];
        state.focus.consumableId = itemDefinitions?.[0]?.["@_id"];
      });
    },
    [dmnEditorStoreApi, mapJavaClassesToDMNItemDefinitions]
  );

  const handleImportJavaClasses = useCallback(
    (javaClass: JavaClass[]) => {
      const { conflicts, nonConflicts } = checkNameConflicts(javaClass);
      if (nonConflicts?.length !== 0) {
        importJavaClassesInDataTypeEditor(nonConflicts);
      }
      if (conflicts?.length !== 0) {
        setIsConflictsOccured(true);
        setConflictsClasses(conflicts);
      }
    },
    [checkNameConflicts, importJavaClassesInDataTypeEditor]
  );

  const handleConflictAction = useCallback(
    (action: { internal: JavaClassConflictOptions; external: JavaClassConflictOptions }) => {
      if (conflictsClasses?.length === 0) return;
      const internalConflicts = conflictsClasses.filter((c) => !c.isExternalConflict);
      const externalConflicts = conflictsClasses.filter((c) => c.isExternalConflict);
      if (internalConflicts.length > 0) {
        if (action.internal === JavaClassConflictOptions.REPLACE) {
          overwriteExistingDMNTypes(internalConflicts);
        } else if (action.internal === JavaClassConflictOptions.KEEP_BOTH) {
          const updatedJavaClasses = generateUniqueDmnTypeNames(internalConflicts);
          importJavaClassesInDataTypeEditor(updatedJavaClasses);
        }
      }
      if (externalConflicts.length > 0 && action.external === JavaClassConflictOptions.KEEP_BOTH) {
        const updatedJavaClasses = generateUniqueDmnTypeNames(externalConflicts);
        importJavaClassesInDataTypeEditor(updatedJavaClasses);
      }
      setIsConflictsOccured(false);
      setConflictsClasses([]);
    },
    [conflictsClasses, generateUniqueDmnTypeNames, importJavaClassesInDataTypeEditor, overwriteExistingDMNTypes]
  );

  return {
    handleImportJavaClasses,
    handleConflictAction,
    conflictsClasses,
    isConflictsOccured,
    handleCloseConflictsModal,
  };
};

export { useImportJavaClasses };
