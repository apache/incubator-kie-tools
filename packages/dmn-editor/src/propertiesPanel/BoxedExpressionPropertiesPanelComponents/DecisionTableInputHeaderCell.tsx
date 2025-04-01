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
import { BoxedExpressionIndex } from "../../boxedExpressions/boxedExpressionIndex";
import { ContentField, DescriptionField, ExpressionLanguageField, NameField, TypeRefField } from "../Fields";
import { FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { DMN15__tInputClause } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { useDmnEditor } from "../../DmnEditorContext";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { PropertiesPanelHeader } from "../PropertiesPanelHeader";
import { useBoxedExpressionUpdater } from "./useBoxedExpressionUpdater";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { ConstraintsFromTypeConstraintAttribute } from "../../dataTypes/Constraints";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/StoreContext";
import { useExternalModels } from "../../includedModels/DmnEditorDependenciesContext";
import { State } from "../../store/Store";

export function DecisionTableInputHeaderCell(props: {
  boxedExpressionIndex?: BoxedExpressionIndex;
  isReadOnly: boolean;
}) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const selectedObjectId = useDmnEditorStore((s) => s.boxedExpressionEditor.selectedObjectId);
  const { externalModelsByNamespace } = useExternalModels();
  const { dmnEditorRootElementRef } = useDmnEditor();
  const selectedObjectInfos = useMemo(
    () => props.boxedExpressionIndex?.get(selectedObjectId ?? ""),
    [props.boxedExpressionIndex, selectedObjectId]
  );

  const updater = useBoxedExpressionUpdater<Normalized<DMN15__tInputClause>>(selectedObjectInfos?.expressionPath ?? []);

  const cell = useMemo(() => selectedObjectInfos?.cell as Normalized<DMN15__tInputClause>, [selectedObjectInfos?.cell]);
  const inputExpression = useMemo(() => cell.inputExpression, [cell.inputExpression]);
  const inputValues = useMemo(() => cell.inputValues, [cell.inputValues]);

  const inputExpressionItemDefinition = useMemo(() => {
    const { allDataTypesById, allTopLevelItemDefinitionUniqueNames } = dmnEditorStoreApi
      .getState()
      .computed(dmnEditorStoreApi.getState())
      .getDataTypes(externalModelsByNamespace);
    return allDataTypesById.get(allTopLevelItemDefinitionUniqueNames.get(inputExpression?.["@_typeRef"] ?? "") ?? "")
      ?.itemDefinition;
  }, [dmnEditorStoreApi, externalModelsByNamespace, inputExpression]);

  const [isInputExpressionExpanded, setInputExpressionExpanded] = useState(true);
  const [isInputValuesExpanded, setInputValuesExpanded] = useState(false);

  const getAllUniqueNames = useCallback((s: State) => new Map(), []);

  return (
    <>
      <FormGroup label="ID">
        <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
          {selectedObjectId}
        </ClipboardCopy>
      </FormGroup>
      <DescriptionField
        isReadOnly={props.isReadOnly}
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
        initialValue={cell?.description?.__$$text ?? ""}
        onChange={(newDescription: string) =>
          updater((dmnObject) => {
            dmnObject.description ??= { __$$text: "" };
            dmnObject.description.__$$text = newDescription;
          })
        }
      />
      <FormSection>
        <PropertiesPanelHeader
          expands={true}
          fixed={false}
          isSectionExpanded={isInputExpressionExpanded}
          toogleSectionExpanded={() => setInputExpressionExpanded((prev) => !prev)}
          title={"Input Expression"}
        />
        {isInputExpressionExpanded && (
          <>
            <NameField
              id={inputExpression["@_id"]!}
              isReadOnly={props.isReadOnly}
              name={inputExpression?.text?.__$$text ?? ""}
              getAllUniqueNames={getAllUniqueNames}
              onChange={(newName) =>
                updater((dmnObject) => {
                  dmnObject.inputExpression ??= { "@_id": generateUuid() };
                  dmnObject.inputExpression.text ??= { __$$text: "" };
                  dmnObject.inputExpression.text.__$$text = newName;
                })
              }
            />
            <TypeRefField
              isReadOnly={props.isReadOnly}
              dmnEditorRootElementRef={dmnEditorRootElementRef}
              typeRef={inputExpression?.["@_typeRef"]}
              onChange={(newTypeRef) =>
                updater((dmnObject) => {
                  dmnObject.inputExpression ??= { "@_id": generateUuid() };
                  dmnObject.inputExpression["@_typeRef"] = newTypeRef;
                  dmnObject.inputValues ??= { "@_id": generateUuid(), text: { __$$text: "" } };
                  dmnObject.inputValues["@_typeRef"] = newTypeRef;
                })
              }
            />
            {inputExpressionItemDefinition && (
              <FormGroup label="Constraint">
                <ConstraintsFromTypeConstraintAttribute
                  isReadOnly={true}
                  itemDefinition={inputExpressionItemDefinition}
                  editItemDefinition={() => {}}
                  renderOnPropertiesPanel={true}
                  defaultsToAllowedValues={true}
                />
              </FormGroup>
            )}
            <ExpressionLanguageField
              isReadOnly={props.isReadOnly}
              initialValue={inputExpression?.["@_expressionLanguage"] ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newExpressionLanguage) =>
                updater((dmnObject) => {
                  dmnObject.inputExpression ??= { "@_id": generateUuid() };
                  dmnObject.inputExpression["@_expressionLanguage"] = newExpressionLanguage;
                })
              }
            />
            <DescriptionField
              isReadOnly={props.isReadOnly}
              initialValue={inputExpression?.description?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newDescription) =>
                updater((dmnObject) => {
                  dmnObject.inputExpression ??= { "@_id": generateUuid() };
                  dmnObject.inputExpression.description ??= { __$$text: "" };
                  dmnObject.inputExpression.description.__$$text = newDescription;
                })
              }
            />
          </>
        )}
      </FormSection>
      <FormSection>
        <PropertiesPanelHeader
          expands={true}
          fixed={false}
          isSectionExpanded={isInputValuesExpanded}
          toogleSectionExpanded={() => setInputValuesExpanded((prev) => !prev)}
          title={"Input Values"}
        />
        {isInputValuesExpanded && (
          <>
            <ExpressionLanguageField
              isReadOnly={props.isReadOnly}
              initialValue={inputValues?.["@_expressionLanguage"] ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newExpressionLanguage) =>
                updater((dmnObject) => {
                  dmnObject.inputValues ??= { "@_id": generateUuid(), text: { __$$text: "" } };
                  dmnObject.inputValues["@_expressionLanguage"] = newExpressionLanguage;
                })
              }
            />
            <ContentField
              isReadOnly={props.isReadOnly}
              initialValue={inputValues?.text?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newText) =>
                updater((dmnObject) => {
                  dmnObject.inputValues ??= { "@_id": generateUuid(), text: { __$$text: "" } };
                  dmnObject.inputValues.text ??= { __$$text: "" };
                  dmnObject.inputValues.text.__$$text = newText;
                })
              }
            />
            <DescriptionField
              isReadOnly={props.isReadOnly}
              initialValue={inputValues?.description?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newDescription: string) =>
                updater((dmnObject) => {
                  dmnObject.inputValues ??= { "@_id": generateUuid(), text: { __$$text: "" } };
                  dmnObject.inputValues.description ??= { __$$text: "" };
                  dmnObject.inputValues.description.__$$text = newDescription;
                })
              }
            />
          </>
        )}
      </FormSection>
    </>
  );
}
