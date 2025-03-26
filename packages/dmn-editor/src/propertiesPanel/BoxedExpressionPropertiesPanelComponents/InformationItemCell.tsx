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
import { DescriptionField, NameField, TypeRefField } from "../Fields";
import { BoxedExpressionIndex } from "../../boxedExpressions/boxedExpressionIndex";
import { DMN15__tInformationItem } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/StoreContext";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { useDmnEditor } from "../../DmnEditorContext";
import { ConstraintsFromTypeConstraintAttribute } from "../../dataTypes/Constraints";
import { useExternalModels } from "../../includedModels/DmnEditorDependenciesContext";
import { State } from "../../store/Store";

export function InformationItemCell(props: {
  boxedExpressionIndex?: BoxedExpressionIndex;
  isReadOnly: boolean;
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

  const cell = useMemo(
    () => selectedObjectInfos?.cell as Normalized<DMN15__tInformationItem>,
    [selectedObjectInfos?.cell]
  );

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
        isReadOnly={props.isReadOnly}
        id={cell["@_id"]!}
        name={cell["@_name"] ?? ""}
        getAllUniqueNames={getAllUniqueNames}
        onChange={props.onNameChange}
      />
      <TypeRefField
        isReadOnly={props.isReadOnly}
        typeRef={cell["@_typeRef"]}
        dmnEditorRootElementRef={dmnEditorRootElementRef}
        onChange={props.onTypeRefChange}
      />
      {itemDefinition && (
        <FormGroup label="Constraint">
          <ConstraintsFromTypeConstraintAttribute
            isReadOnly={true}
            itemDefinition={itemDefinition}
            editItemDefinition={() => {}}
            renderOnPropertiesPanel={true}
            defaultsToAllowedValues={true}
          />
        </FormGroup>
      )}
      <DescriptionField
        isReadOnly={props.isReadOnly}
        initialValue={cell.description?.__$$text ?? ""}
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
        onChange={props.onDescriptionChange}
      />
    </>
  );
}
