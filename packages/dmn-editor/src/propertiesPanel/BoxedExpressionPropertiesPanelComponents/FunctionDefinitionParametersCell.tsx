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
import { DescriptionField, NameField, TypeRefField } from "./Fields";
import { BoxedExpressionIndex } from "../../boxedExpressions/boxedExpressionIndex";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { useDmnEditor } from "../../DmnEditorContext";
import { useBoxedExpressionUpdater } from "./useBoxedExpressionUpdater";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import {
  DMN15__tFunctionDefinition,
  DMN15__tInformationItem,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { PropertiesPanelHeader } from "../PropertiesPanelHeader";
import { Text } from "@patternfly/react-core/dist/js/components/Text";
import { ConstraintsFromTypeConstraintAttribute } from "../../dataTypes/Constraints";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/StoreContext";
import { useExternalModels } from "../../includedModels/DmnEditorDependenciesContext";
import { State } from "../../store/Store";

export function FunctionDefinitionParameterCell(props: {
  boxedExpressionIndex?: BoxedExpressionIndex;
  isReadonly: boolean;
}) {
  const selectedObjectId = useDmnEditorStore((s) => s.boxedExpressionEditor.selectedObjectId);

  const selectedObjectInfos = useMemo(
    () => props.boxedExpressionIndex?.get(selectedObjectId ?? ""),
    [props.boxedExpressionIndex, selectedObjectId]
  );

  const updater = useBoxedExpressionUpdater<DMN15__tFunctionDefinition>(selectedObjectInfos?.expressionPath ?? []);

  const cell = useMemo(() => selectedObjectInfos?.cell as DMN15__tInformationItem[], [selectedObjectInfos?.cell]);
  const [isParameterExpanded, setParameterExpaded] = useState<boolean[]>([]);

  const getAllUniqueNames = useCallback((s: State) => new Map(), []);

  return (
    <>
      <FormGroup label="ID">
        <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
          {selectedObjectId}
        </ClipboardCopy>
      </FormGroup>
      {cell.length === 0 && (
        <>
          <Text>{"Empty parameters list"}</Text>
        </>
      )}
      {cell.map((parameter, i) => (
        <FormSection key={i}>
          <PropertiesPanelHeader
            expands={true}
            fixed={false}
            isSectionExpanded={isParameterExpanded[i] ?? false}
            toogleSectionExpanded={() =>
              setParameterExpaded((prev) => {
                const newExpanded = [...prev];
                newExpanded[i] = !(newExpanded[i] ?? false);
                return newExpanded;
              })
            }
            title={
              <p>
                Parameter <b>{parameter["@_name"]}</b>
              </p>
            }
          />
          {isParameterExpanded[i] && (
            <>
              <NameField
                isReadonly={props.isReadonly}
                id={parameter["@_id"]!}
                name={parameter["@_name"] ?? ""}
                getAllUniqueNames={getAllUniqueNames}
                onChange={(newName: string) => {
                  updater((dmnObject) => {
                    dmnObject.formalParameter ??= [];
                    dmnObject.formalParameter[i] ??= { "@_name": "" };
                    dmnObject.formalParameter[i]["@_name"] = newName;
                  });
                }}
              />
              <FunctionDefinitionParameterTypeRef
                parameter={parameter}
                isReadonly={props.isReadonly}
                onTypeRefChange={(newTypeRef) =>
                  updater((dmnObject) => {
                    dmnObject.formalParameter ??= [];
                    dmnObject.formalParameter[i] ??= { "@_name": "" };
                    dmnObject.formalParameter[i]["@_typeRef"] = newTypeRef;
                  })
                }
              />
              <DescriptionField
                isReadonly={props.isReadonly}
                initialValue={parameter.description?.__$$text ?? ""}
                expressionPath={selectedObjectInfos?.expressionPath ?? []}
                onChange={(newDescription: string) => {
                  updater((dmnObject) => {
                    dmnObject.formalParameter ??= [];
                    dmnObject.formalParameter[i] ??= { "@_name": "", description: { __$$text: "" } };
                    dmnObject.formalParameter[i].description ??= { __$$text: "" };
                    dmnObject.formalParameter[i].description!.__$$text = newDescription;
                  });
                }}
              />
            </>
          )}
        </FormSection>
      ))}
    </>
  );
}

function FunctionDefinitionParameterTypeRef(props: {
  parameter: DMN15__tInformationItem;
  isReadonly: boolean;
  onTypeRefChange: (newTypeRef: string) => void;
}) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { externalModelsByNamespace } = useExternalModels();
  const { dmnEditorRootElementRef } = useDmnEditor();

  const itemDefinition = useMemo(() => {
    const { allDataTypesById, allTopLevelItemDefinitionUniqueNames } = dmnEditorStoreApi
      .getState()
      .computed(dmnEditorStoreApi.getState())
      .getDataTypes(externalModelsByNamespace);
    return allDataTypesById.get(allTopLevelItemDefinitionUniqueNames.get(props.parameter?.["@_typeRef"] ?? "") ?? "")
      ?.itemDefinition;
  }, [dmnEditorStoreApi, externalModelsByNamespace, props.parameter]);

  return (
    <>
      <TypeRefField
        isReadonly={props.isReadonly}
        dmnEditorRootElementRef={dmnEditorRootElementRef}
        typeRef={props.parameter["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
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
    </>
  );
}
