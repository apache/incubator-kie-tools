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
import { useCallback, useMemo, useState } from "react";
import {
  DMN15__tDefinitions,
  DMN15__tItemDefinition,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { EditableNodeLabel, useEditableNodeLabel } from "../diagram/nodes/EditableNodeLabel";
import { TypeRefLabel } from "./TypeRefLabel";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { renameItemDefinition } from "../mutations/renameItemDefinition";
import { UniqueNameIndex } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { buildFeelQNameFromNamespace } from "../feel/buildFeelQName";
import { InlineFeelNameInput, OnInlineFeelNameRenamed } from "../feel/InlineFeelNameInput";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { State } from "../store/Store";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import {
  isIdentifierReferencedInSomeExpression,
  RefactorConfirmationDialog,
} from "../refactor/RefactorConfirmationDialog";
import { DataTypeIndex } from "./DataTypes";
import { isStruct } from "./DataTypeSpec";

export function DataTypeName({
  isReadOnly,
  itemDefinition,
  isActive,
  editMode,
  relativeToNamespace,
  shouldCommitOnBlur,
  onGetAllUniqueNames,
  enableAutoFocusing,
}: {
  isReadOnly: boolean;
  editMode: "hover" | "double-click";
  itemDefinition: Normalized<DMN15__tItemDefinition>;
  isActive: boolean;
  relativeToNamespace: string;
  shouldCommitOnBlur?: boolean;
  onGetAllUniqueNames: (s: State) => UniqueNameIndex;
  enableAutoFocusing?: boolean;
}) {
  const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(
    enableAutoFocusing ?? true ? itemDefinition["@_id"] : undefined
  );

  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { externalModelsByNamespace } = useExternalModels();
  const dataType = useDmnEditorStore((s) =>
    s.computed(s).getDataTypes(externalModelsByNamespace).allDataTypesById.get(itemDefinition["@_id"]!)
  );
  const importsByNamespace = useDmnEditorStore((s) => s.computed(s).importsByNamespace());

  const feelQNameToDisplay = buildFeelQNameFromNamespace({
    namedElement: itemDefinition,
    importsByNamespace,
    namespace: dataType!.namespace,
    relativeToNamespace,
  });

  const externalDmnModelsByNamespaceMap = useDmnEditorStore((s) =>
    s.computed(s).getExternalDmnModelsByNamespaceMap(externalModelsByNamespace)
  );

  const _shouldCommitOnBlur = shouldCommitOnBlur ?? true; // Defaults to true

  const [isRefactorModalOpen, setIsRefactorModalOpen] = useState(false);
  const [newName, setNewName] = useState("");
  const identifierId = useMemo(() => itemDefinition["@_id"], [itemDefinition]);
  const oldName = useMemo(() => itemDefinition["@_name"], [itemDefinition]);

  const currentName = useMemo(() => {
    if (editMode === "hover") {
      return newName === "" ? feelQNameToDisplay.full : newName;
    } else if (editMode === "double-click") {
      return newName === "" ? itemDefinition["@_name"] : newName;
    } else {
      throw new Error(`Unknown edit mode in DataTypeName: ${editMode}`);
    }
  }, [editMode, feelQNameToDisplay.full, itemDefinition, newName]);

  const applyRename = useCallback(
    (args: {
      definitions: Normalized<DMN15__tDefinitions>;
      newName: string;
      shouldRenameReferencedExpressions: boolean;
      allDataTypesById: DataTypeIndex;
    }) => {
      renameItemDefinition({
        ...args,
        itemDefinitionId: itemDefinition["@_id"]!,
        externalDmnModelsByNamespaceMap,
      });
    },
    [externalDmnModelsByNamespaceMap, itemDefinition]
  );

  const onRenamed = useCallback<OnInlineFeelNameRenamed>(
    (newName) => {
      if (isReadOnly || newName === oldName) {
        return;
      }

      dmnEditorStoreApi.setState((state) => {
        if (
          isIdentifierReferencedInSomeExpression({
            identifierUuid: identifierId,
            dmnDefinitions: state.dmn.model.definitions,
            externalDmnModelsByNamespaceMap,
          })
        ) {
          setNewName(newName);
          setIsRefactorModalOpen(true);
        } else {
          applyRename({
            definitions: state.dmn.model.definitions,
            newName,
            shouldRenameReferencedExpressions: false,
            allDataTypesById: state.computed(state).getDataTypes(externalModelsByNamespace).allDataTypesById,
          });
        }
      });
    },
    [
      applyRename,
      dmnEditorStoreApi,
      externalDmnModelsByNamespaceMap,
      externalModelsByNamespace,
      identifierId,
      isReadOnly,
      oldName,
    ]
  );

  const confirmRename = useCallback(
    (shouldRenameReferencedExpressions: boolean) => {
      setIsRefactorModalOpen(false);
      dmnEditorStoreApi.setState((state) => {
        applyRename({
          definitions: state.dmn.model.definitions,
          newName,
          shouldRenameReferencedExpressions,
          allDataTypesById: state.computed(state).getDataTypes(externalModelsByNamespace).allDataTypesById,
        });
      });
    },
    [applyRename, dmnEditorStoreApi, externalModelsByNamespace, newName]
  );

  return (
    <>
      <RefactorConfirmationDialog
        onConfirmExpressionRefactor={() => {
          confirmRename(true);
        }}
        onConfirmRenameOnly={() => {
          confirmRename(false);
        }}
        isRefactorModalOpen={isRefactorModalOpen}
        fromName={oldName}
        toName={newName}
        onCancel={() => {
          setNewName("");
          setIsRefactorModalOpen(false);
        }}
      />
      {editMode === "hover" && (
        <InlineFeelNameInput
          isPlain={true}
          isReadOnly={isReadOnly}
          id={itemDefinition["@_id"]!}
          shouldCommitOnBlur={_shouldCommitOnBlur}
          name={currentName}
          onRenamed={onRenamed}
          allUniqueNames={onGetAllUniqueNames}
          enableAutoFocusing={enableAutoFocusing}
        />
      )}
      {editMode === "double-click" && (
        <Flex
          tabIndex={-1}
          style={isEditingLabel ? { flexGrow: 1 } : {}}
          flexWrap={{ default: "nowrap" }}
          spaceItems={{ default: "spaceItemsNone" }}
          justifyContent={{ default: "justifyContentFlexStart" }}
          alignItems={{ default: "alignItemsCenter" }}
          onDoubleClick={triggerEditing}
          onKeyDown={triggerEditingIfEnter}
        >
          {/* Using this component here is not ideal, as we're not dealing with Node names, but it works well enough */}
          <EditableNodeLabel
            truncate={true}
            enableAutoFocusing={enableAutoFocusing}
            grow={true}
            isEditing={isEditingLabel}
            setEditing={setEditingLabel}
            onChange={onRenamed}
            shouldCommitOnBlur={shouldCommitOnBlur}
            value={currentName}
            key={itemDefinition["@_id"]}
            position={"top-left"}
            namedElement={itemDefinition}
            namedElementQName={{
              type: "xml-qname",
              localPart: itemDefinition["@_name"],
              prefix: feelQNameToDisplay.prefix,
            }}
            onGetAllUniqueNames={onGetAllUniqueNames}
          />
          {!isEditingLabel && (
            <TypeRefLabel
              typeRef={isStruct(itemDefinition) ? "" : itemDefinition.typeRef?.__$$text ?? DmnBuiltInDataType.Undefined}
              isCollection={itemDefinition["@_isCollection"]}
              relativeToNamespace={relativeToNamespace}
            />
          )}
        </Flex>
      )}
    </>
  );
}
