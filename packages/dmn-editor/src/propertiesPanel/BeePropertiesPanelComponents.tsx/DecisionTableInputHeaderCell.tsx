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
import { BeeMap, DeepPartial, getDmnObject } from "../../boxedExpressions/getBeeMap";
import {
  DescriptionField,
  ExpressionLanguageField,
  KieConstraintTypeField,
  LabelField,
  TextField,
  TypeRefField,
} from "./Fields";
import { FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/Store";
import { useDmnEditorDerivedStore } from "../../store/DerivedStore";
import { buildXmlHref } from "../../xml/xmlHrefs";
import {
  DMN15__tBusinessKnowledgeModel,
  DMN15__tDecision,
  DMN15__tInputClause,
  DMN15__tUnaryTests,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { useDmnEditor } from "../../DmnEditorContext";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { PropertiesPanelHeader } from "../PropertiesPanelHeader";

export function DecisionTableInputHeaderCell(props: { beeMap?: BeeMap; isReadonly: boolean }) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { selectedObjectId, activeDrgElementId } = useDmnEditorStore((s) => s.boxedExpressionEditor);
  const { nodesById } = useDmnEditorDerivedStore();
  const { dmnEditorRootElementRef } = useDmnEditor();

  const node = useMemo(
    () => (activeDrgElementId ? nodesById.get(buildXmlHref({ id: activeDrgElementId })) : undefined),
    [activeDrgElementId, nodesById]
  );
  const selectedObjectInfos = useMemo(
    () => props.beeMap?.get(selectedObjectId ?? ""),
    [props.beeMap, selectedObjectId]
  );

  const updateDmnObject = useCallback(
    (dmnObject: DMN15__tInputClause, newContent: DeepPartial<DMN15__tInputClause>) => {
      // LABEL
      if (newContent["@_label"]) {
        dmnObject["@_label"] = newContent?.["@_label"];
      }

      // DESCRIPTION
      if (newContent?.description?.__$$text && dmnObject?.description) {
        dmnObject.description = newContent.description as { __$$text: string };
      } else if (newContent?.description?.__$$text) {
        dmnObject = {
          ...dmnObject,
          description: newContent.description as { __$$text: string },
        };
      }

      if (!dmnObject.inputExpression && newContent.inputExpression) {
        dmnObject.inputExpression = newContent.inputExpression;
      }
      if (newContent.inputExpression) {
        // DESCRIPTION
        if (newContent.inputExpression?.description && dmnObject.inputExpression?.description) {
          dmnObject.inputExpression.description = newContent.inputExpression.description;
        } else {
          dmnObject.inputExpression = {
            ...dmnObject.inputExpression,
            description: newContent.inputExpression.description,
          };
        }

        // TEXT
        if (newContent.inputExpression?.text && dmnObject.inputExpression?.text) {
          dmnObject.inputExpression.text = newContent.inputExpression.text;
        } else {
          dmnObject.inputExpression = { ...dmnObject.inputExpression, text: newContent.inputExpression.text };
        }

        // TYPEREF
        if (newContent.inputExpression["@_typeRef"]) {
          dmnObject.inputExpression["@_typeRef"] = newContent.inputExpression?.["@_typeRef"];
        }

        // LABEL
        if (newContent.inputExpression["@_label"]) {
          dmnObject.inputExpression["@_label"] = newContent.inputExpression?.["@_label"];
        }
      }

      if (!dmnObject.inputValues) {
        dmnObject = { ...dmnObject, inputValues: newContent.inputValues as DMN15__tUnaryTests };
      }
      if (newContent.inputValues) {
        // DESCRIPTION
        if (newContent.inputValues?.description && dmnObject.inputValues?.description) {
          dmnObject.inputValues.description = newContent.inputValues.description;
        } else {
          dmnObject.inputValues = {
            ...dmnObject.inputValues!,
            description: newContent.inputValues.description,
          };
        }

        // TEXT
        if (newContent.inputValues?.text && dmnObject.inputValues?.text) {
          dmnObject.inputValues.text = newContent.inputValues.text;
        } else {
          dmnObject.inputValues = { ...dmnObject.inputValues, text: newContent.inputValues.text! };
        }

        // TYPEREF
        if (newContent.inputValues["@_typeRef"]) {
          dmnObject.inputValues["@_typeRef"] = newContent.inputValues?.["@_typeRef"];
        }

        // LABEL
        if (newContent.inputValues["@_label"]) {
          dmnObject.inputValues["@_label"] = newContent.inputValues?.["@_label"];
        }
      }
    },
    []
  );

  const updateBee = useCallback(
    (newContent: DeepPartial<DMN15__tInputClause>, expressionPath = selectedObjectInfos?.expressionPath) => {
      dmnEditorStoreApi.setState((state) => {
        if (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0]?.__$$element === "businessKnowledgeModel") {
          const dmnObject = getDmnObject(
            expressionPath ?? [],
            (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0] as DMN15__tBusinessKnowledgeModel)
              ?.encapsulatedLogic?.expression
          );
          dmnObject && updateDmnObject(dmnObject as DMN15__tInputClause, newContent);
        }
        if (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0]?.__$$element === "decision") {
          const dmnObject = getDmnObject(
            expressionPath ?? [],
            (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0] as DMN15__tDecision)?.expression
          );
          dmnObject && updateDmnObject(dmnObject as DMN15__tInputClause, newContent);
        }
      });
    },
    [dmnEditorStoreApi, node?.data.index, selectedObjectInfos?.expressionPath, updateDmnObject]
  );

  const cell = useMemo(() => selectedObjectInfos?.cell as DMN15__tInputClause, [selectedObjectInfos?.cell]);
  const inputExpression = useMemo(() => cell.inputExpression, [cell.inputExpression]);
  const inputValues = useMemo(() => cell.inputValues, [cell.inputValues]);

  const [isInputExpressionExpanded, setInputExpressionExpanded] = useState(false);
  const [isInputValuesExpanded, setInputValuesExpanded] = useState(false);

  return (
    <>
      <LabelField
        isReadonly={props.isReadonly}
        label={cell?.["@_label"] ?? ""}
        onChange={(newLabel: string) => updateBee({ "@_label": newLabel })}
      />
      <DescriptionField
        isReadonly={props.isReadonly}
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
        initialValue={cell?.description?.__$$text ?? ""}
        onChange={(newDescription: string) => updateBee({ description: { __$$text: newDescription } })}
      />
      <FormSection>
        <PropertiesPanelHeader
          expands={true}
          fixed={false}
          isSectionExpanded={isInputExpressionExpanded}
          toogleSectionExpanded={() => setInputExpressionExpanded((prev) => !prev)}
          title={"Default Output Entry"}
        />
        {isInputExpressionExpanded && (
          <>
            <ExpressionLanguageField
              isReadonly={props.isReadonly}
              expressionLanguage={inputExpression?.["@_expressionLanguage"] ?? ""}
              onChange={(newExpressionLanguage) =>
                updateBee({ inputExpression: { "@_expressionLanguage": newExpressionLanguage } })
              }
            />
            <TypeRefField
              isReadonly={props.isReadonly}
              dmnEditorRootElementRef={dmnEditorRootElementRef}
              typeRef={inputExpression?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
              onChange={(newTypeRef) => updateBee({ inputExpression: { "@_typeRef": newTypeRef } })}
            />
            <TextField
              isReadonly={props.isReadonly}
              initialValue={inputExpression?.text?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newText) => updateBee({ inputExpression: { text: { __$$text: newText } } })}
            />
            <LabelField
              isReadonly={props.isReadonly}
              label={inputExpression?.["@_label"] ?? ""}
              onChange={(newLabel) => updateBee({ inputExpression: { "@_label": newLabel } })}
            />
            <DescriptionField
              isReadonly={props.isReadonly}
              initialValue={inputExpression?.description?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newDescription) =>
                updateBee({ inputExpression: { description: { __$$text: newDescription } } })
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
          title={"Default Output Entry"}
        />
        {isInputValuesExpanded && (
          <>
            <ExpressionLanguageField
              isReadonly={props.isReadonly}
              expressionLanguage={inputValues?.["@_expressionLanguage"] ?? ""}
              onChange={(newExpressionLanguage) =>
                updateBee({ inputValues: { "@_expressionLanguage": newExpressionLanguage } })
              }
            />
            <TextField
              isReadonly={props.isReadonly}
              initialValue={inputValues?.text?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newText) => updateBee({ inputValues: { text: { __$$text: newText } } })}
            />
            <LabelField
              isReadonly={props.isReadonly}
              label={inputValues?.["@_label"] ?? ""}
              onChange={(newLabel) => updateBee({ inputValues: { "@_label": newLabel } })}
            />
            <DescriptionField
              isReadonly={props.isReadonly}
              initialValue={inputValues?.description?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newDescription) => updateBee({ inputValues: { description: { __$$text: newDescription } } })}
            />
            <KieConstraintTypeField />
          </>
        )}
      </FormSection>
    </>
  );
}
