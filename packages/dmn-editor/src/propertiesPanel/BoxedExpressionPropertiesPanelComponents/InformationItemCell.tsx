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
import { DescriptionField, NameField, TypeRefField } from "./Fields";
import { BoxedExpressionIndex } from "../../boxedExpressions/boxedExpressionIndex";
import { DMN15__tInformationItem } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/StoreContext";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { useDmnEditor } from "../../DmnEditorContext";
import { Constraints, ConstraintsFromTypeConstraintAttribute } from "../../dataTypes/Constraints";
import { useExternalModels } from "../../includedModels/DmnEditorDependenciesContext";
import { State } from "../../store/Store";

export function InformationItemCell(props: {
  boxedExpressionIndex?: BoxedExpressionIndex;
  isReadonly: boolean;
  onNameChange: (newName: string) => void;
  onTypeRefChange: (newTypeRef: string) => void;
  onDescriptionChange: (newDescription: string) => void;
}) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const selectedObjectId = useDmnEditorStore((s) => s.boxedExpressionEditor.selectedObjectId);
  const { externalModelsByNamespace } = useExternalModels();
  const { dmnEditorRootElementRef } = useDmnEditor();
  const selectedObjectInfos = useMemo(
    () => props.boxedExpressionIndex?.get(selectedObjectId ?? ""),
    [props.boxedExpressionIndex, selectedObjectId]
  );

  const cell = useMemo(() => selectedObjectInfos?.cell as DMN15__tInformationItem, [selectedObjectInfos?.cell]);

  const itemDefinition = useMemo(() => {
    const { allDataTypesById, allTopLevelItemDefinitionUniqueNames } = dmnEditorStoreApi
      .getState()
      .computed(dmnEditorStoreApi.getState())
      .getDataTypes(externalModelsByNamespace);
    return allDataTypesById.get(allTopLevelItemDefinitionUniqueNames.get(cell?.["@_typeRef"] ?? "") ?? "")
      ?.itemDefinition;
  }, [cell, dmnEditorStoreApi, externalModelsByNamespace]);

  const getAllUniqueNames = useCallback((s: State) => new Map(), []);

  return (
    <>
      <FormGroup label="ID">
        <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
          {selectedObjectId}
        </ClipboardCopy>
      </FormGroup>
      <NameField
        isReadonly={props.isReadonly}
        id={cell["@_id"]!}
        name={cell["@_name"] ?? ""}
        getAllUniqueNames={getAllUniqueNames}
        onChange={props.onNameChange}
      />
      <TypeRefField
        isReadonly={props.isReadonly}
        typeRef={cell["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
        dmnEditorRootElementRef={dmnEditorRootElementRef}
        onChange={props.onTypeRefChange}
      />
      {itemDefinition && (
        <FormGroup label="Constraint">
          <ConstraintsFromTypeConstraintAttribute
            isReadonly={true}
            itemDefinition={itemDefinition}
            editItemDefinition={() => {}}
            renderOnPropertiesPanel={true}
            defaultsToAllowedValues={true}
          />
        </FormGroup>
      )}
      <DescriptionField
        isReadonly={props.isReadonly}
        initialValue={cell.description?.__$$text ?? ""}
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
        onChange={props.onDescriptionChange}
      />
    </>
  );
}
