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
import { useMemo } from "react";
import { DescriptionField, ExpressionLanguageField, TypeRefField } from "../Fields";
import { BoxedExpressionIndex } from "../../boxedExpressions/boxedExpressionIndex";
import {
  DMN15__tDecisionTable,
  DMN15__tLiteralExpression,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { useBoxedExpressionUpdater } from "./useBoxedExpressionUpdater";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { ConstraintsFromTypeConstraintAttribute } from "../../dataTypes/Constraints";
import { BoxedDecisionTable, DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { useDmnEditor } from "../../DmnEditorContext";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/StoreContext";
import { useExternalModels } from "../../includedModels/DmnEditorDependenciesContext";

export function DecisionTableOutputRuleCell(props: {
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

  const root = useMemo(
    () =>
      props.boxedExpressionIndex?.get(
        selectedObjectInfos?.expressionPath[selectedObjectInfos?.expressionPath.length - 1]?.root ?? ""
      ),
    [props.boxedExpressionIndex, selectedObjectInfos?.expressionPath]
  );

  const cell = useMemo(
    () => selectedObjectInfos?.cell as Normalized<DMN15__tLiteralExpression>,
    [selectedObjectInfos?.cell]
  );

  // In case the the output column is merged, the output column should have the same type as the Decision Node
  // It can happen to output column and Decision Node have different types, e.g. broken model.
  // For this case, the cell should have the column type.
  const cellMustHaveSameTypeAsRoot = useMemo(
    () =>
      (root?.cell as Normalized<BoxedDecisionTable> | undefined)?.output.length === 1 &&
      ((root?.cell as Normalized<BoxedDecisionTable> | undefined)?.["@_typeRef"] === cell?.["@_typeRef"] ||
        cell?.["@_typeRef"] === undefined),
    [cell, root?.cell]
  );

  const headerType = useMemo(() => {
    const cellPath = selectedObjectInfos?.expressionPath[selectedObjectInfos?.expressionPath.length - 1];
    if (cellPath && cellPath.root) {
      const { allDataTypesById, allTopLevelItemDefinitionUniqueNames } = dmnEditorStoreApi
        .getState()
        .computed(dmnEditorStoreApi.getState())
        .getDataTypes(externalModelsByNamespace);
      if (
        root?.expressionPath[root.expressionPath.length - 1]?.type === "decisionTable" &&
        cellPath.type === "decisionTable"
      ) {
        const typeRef =
          allTopLevelItemDefinitionUniqueNames.get(
            cellMustHaveSameTypeAsRoot
              ? (root?.cell as Normalized<DMN15__tDecisionTable> | undefined)?.["@_typeRef"] ?? ""
              : (root?.cell as Normalized<DMN15__tDecisionTable>)?.output?.[cellPath.column ?? 0]["@_typeRef"] ?? ""
          ) ?? DmnBuiltInDataType.Undefined;
        return { typeRef, itemDefinition: allDataTypesById.get(typeRef)?.itemDefinition };
      }
    }
  }, [
    cellMustHaveSameTypeAsRoot,
    dmnEditorStoreApi,
    externalModelsByNamespace,
    root?.cell,
    root?.expressionPath,
    selectedObjectInfos?.expressionPath,
  ]);

  const updater = useBoxedExpressionUpdater<Normalized<DMN15__tLiteralExpression>>(
    selectedObjectInfos?.expressionPath ?? []
  );

  return (
    <>
      <FormGroup label="ID">
        <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
          {selectedObjectId}
        </ClipboardCopy>
      </FormGroup>
      {headerType && (
        <>
          <TypeRefField
            alternativeFieldName={"Output header type"}
            isReadOnly={true}
            dmnEditorRootElementRef={dmnEditorRootElementRef}
            typeRef={
              cellMustHaveSameTypeAsRoot
                ? (root?.cell as Normalized<BoxedDecisionTable> | undefined)?.["@_typeRef"]
                : headerType.itemDefinition?.["@_name"] ?? headerType.typeRef
            }
          />
        </>
      )}
      {headerType?.itemDefinition && (
        <>
          <FormGroup label="Constraint">
            <ConstraintsFromTypeConstraintAttribute
              isReadOnly={true}
              itemDefinition={headerType.itemDefinition}
              editItemDefinition={() => {}}
              renderOnPropertiesPanel={true}
              defaultsToAllowedValues={true}
            />
          </FormGroup>
        </>
      )}
      <ExpressionLanguageField
        isReadOnly={props.isReadOnly}
        initialValue={cell["@_expressionLanguage"] ?? ""}
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
        onChange={(newExpressionLanguage: string) =>
          updater((dmnObject) => {
            dmnObject["@_expressionLanguage"] = newExpressionLanguage;
          })
        }
      />
      <DescriptionField
        isReadOnly={props.isReadOnly}
        initialValue={cell.description?.__$$text ?? ""}
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
        onChange={(newDescription: string) =>
          updater((dmnObject) => {
            dmnObject.description ??= { __$$text: "" };
            dmnObject.description.__$$text = newDescription;
          })
        }
      />
    </>
  );
}
