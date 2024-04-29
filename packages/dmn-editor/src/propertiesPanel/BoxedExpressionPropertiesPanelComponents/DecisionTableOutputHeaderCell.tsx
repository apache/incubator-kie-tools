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
import { ContentField, DescriptionField, ExpressionLanguageField, NameField, TypeRefField } from "./Fields";
import { FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { DMN15__tOutputClause } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { PropertiesPanelHeader } from "../PropertiesPanelHeader";
import { BoxedDecisionTable, DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { useDmnEditor } from "../../DmnEditorContext";
import { useBoxedExpressionUpdater } from "./useBoxedExpressionUpdater";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { ConstraintsFromTypeConstraintAttribute } from "../../dataTypes/Constraints";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/StoreContext";
import { useExternalModels } from "../../includedModels/DmnEditorDependenciesContext";
import { State } from "../../store/Store";

export function DecisionTableOutputHeaderCell(props: {
  boxedExpressionIndex?: BoxedExpressionIndex;
  isReadonly: boolean;
}) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const selectedObjectId = useDmnEditorStore((s) => s.boxedExpressionEditor.selectedObjectId);
  const { dmnEditorRootElementRef } = useDmnEditor();
  const { externalModelsByNamespace } = useExternalModels();

  const selectedObjectInfos = useMemo(
    () => props.boxedExpressionIndex?.get(selectedObjectId ?? ""),
    [props.boxedExpressionIndex, selectedObjectId]
  );

  const updater = useBoxedExpressionUpdater<DMN15__tOutputClause>(selectedObjectInfos?.expressionPath ?? []);

  const cell = useMemo(() => selectedObjectInfos?.cell as DMN15__tOutputClause, [selectedObjectInfos?.cell]);
  const defaultOutputEntry = useMemo(() => cell.defaultOutputEntry, [cell.defaultOutputEntry]);
  const outputValues = useMemo(() => cell.outputValues, [cell.outputValues]);

  const itemDefinition = useMemo(() => {
    const { allDataTypesById, allTopLevelItemDefinitionUniqueNames } = dmnEditorStoreApi
      .getState()
      .computed(dmnEditorStoreApi.getState())
      .getDataTypes(externalModelsByNamespace);
    return allDataTypesById.get(allTopLevelItemDefinitionUniqueNames.get(cell?.["@_typeRef"] ?? "") ?? "")
      ?.itemDefinition;
  }, [cell, dmnEditorStoreApi, externalModelsByNamespace]);

  const root = useMemo(
    () =>
      props.boxedExpressionIndex?.get(
        selectedObjectInfos?.expressionPath[selectedObjectInfos?.expressionPath.length - 1]?.root ?? ""
      )?.cell as BoxedDecisionTable | undefined,
    [props.boxedExpressionIndex, selectedObjectInfos?.expressionPath]
  );

  const [isDefaultOutputEntryExpanded, setDefaultOutputEntryExpanded] = useState(false);
  const [isOutputValuesExpanded, setOutputValuesExpanded] = useState(false);

  const getAllUniqueNames = useCallback((s: State) => new Map(), []);

  const alternativeFieldName = useMemo(() => {
    if (selectedObjectInfos?.expressionPath.length === 1) {
      return "Decision";
    }
    const parentType = selectedObjectInfos?.expressionPath[selectedObjectInfos?.expressionPath.length - 2].type;
    switch (parentType) {
      case "context":
        return "Entry";
      case "functionDefinition":
        return "Function";
      case "invocation":
        return "Parameter";
      case "list":
        return "Item";
      case "conditional":
      case "every":
      case "filter":
      case "for":
      case "some":
      default:
        return "Expression";
    }
  }, [selectedObjectInfos?.expressionPath]);

  return (
    <>
      <FormGroup label="ID">
        <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
          {selectedObjectId}
        </ClipboardCopy>
      </FormGroup>
      {root?.output.length === 1 && (
        <>
          <NameField
            alternativeFieldName={`${alternativeFieldName} Name`}
            isReadonly={true}
            id={root["@_id"]!}
            name={root?.["@_label"] ?? ""}
            getAllUniqueNames={getAllUniqueNames}
          />
          <TypeRefField
            alternativeFieldName={`${alternativeFieldName} Type`}
            isReadonly={true}
            dmnEditorRootElementRef={dmnEditorRootElementRef}
            typeRef={root?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
          />
        </>
      )}
      <NameField
        alternativeFieldName={root?.output.length === 1 ? "Column Name" : undefined}
        isReadonly={props.isReadonly}
        id={cell["@_id"]!}
        name={cell?.["@_name"] ?? ""}
        getAllUniqueNames={getAllUniqueNames}
        onChange={(newName) =>
          updater((dmnObject) => {
            dmnObject["@_name"] = newName;
          })
        }
      />
      <TypeRefField
        alternativeFieldName={root?.output.length === 1 ? "Column Type" : undefined}
        isReadonly={props.isReadonly}
        dmnEditorRootElementRef={dmnEditorRootElementRef}
        typeRef={cell?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
        onChange={(newTypeRef) =>
          updater((dmnObject) => {
            dmnObject["@_typeRef"] = newTypeRef;
          })
        }
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
          isSectionExpanded={isDefaultOutputEntryExpanded}
          toogleSectionExpanded={() => setDefaultOutputEntryExpanded((prev) => !prev)}
          title={"Default Output Entry"}
        />
        {isDefaultOutputEntryExpanded && (
          <>
            <ExpressionLanguageField
              isReadonly={props.isReadonly}
              initialValue={defaultOutputEntry?.["@_expressionLanguage"] ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newExpressionLanguage) =>
                updater((dmnObject) => {
                  dmnObject.defaultOutputEntry ??= {};
                  dmnObject.defaultOutputEntry["@_expressionLanguage"] = newExpressionLanguage;
                })
              }
            />
            <ContentField
              isReadonly={props.isReadonly}
              initialValue={defaultOutputEntry?.text?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newText) =>
                updater((dmnObject) => {
                  dmnObject.defaultOutputEntry ??= { text: { __$$text: "" } };
                  dmnObject.defaultOutputEntry.text ??= { __$$text: "" };
                  dmnObject.defaultOutputEntry.text.__$$text = newText;
                })
              }
            />
            <DescriptionField
              isReadonly={props.isReadonly}
              initialValue={defaultOutputEntry?.description?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newDescription) =>
                updater((dmnObject) => {
                  dmnObject.defaultOutputEntry ??= { description: { __$$text: "" } };
                  dmnObject.defaultOutputEntry.description ??= { __$$text: "" };
                  dmnObject.defaultOutputEntry.description.__$$text = newDescription;
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
          isSectionExpanded={isOutputValuesExpanded}
          toogleSectionExpanded={() => setOutputValuesExpanded((prev) => !prev)}
          title={"Output Values"}
        />
        {isOutputValuesExpanded && (
          <>
            <ExpressionLanguageField
              isReadonly={props.isReadonly}
              initialValue={outputValues?.["@_expressionLanguage"] ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newExpressionLanguage) =>
                updater((dmnObject) => {
                  dmnObject.outputValues ??= { text: { __$$text: "" } };
                  dmnObject.outputValues["@_expressionLanguage"] = newExpressionLanguage;
                })
              }
            />
            <ContentField
              isReadonly={props.isReadonly}
              initialValue={outputValues?.text?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newText) =>
                updater((dmnObject) => {
                  dmnObject.outputValues ??= { text: { __$$text: "" } };
                  dmnObject.outputValues.text.__$$text = newText;
                })
              }
            />
            <DescriptionField
              isReadonly={props.isReadonly}
              initialValue={outputValues?.description?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newDescription: string) =>
                updater((dmnObject) => {
                  dmnObject.outputValues ??= { text: { __$$text: "" } };
                  dmnObject.outputValues.description ??= { __$$text: "" };
                  dmnObject.outputValues.description.__$$text = newDescription;
                })
              }
            />
          </>
        )}
      </FormSection>
    </>
  );
}
