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
import { BeeMap, ExpressionPath } from "../../boxedExpressions/getBeeMap";
import {
  ContentField,
  DescriptionField,
  ExpressionLanguageField,
  KieConstraintTypeField,
  NameField,
  TypeRefField,
} from "./Fields";
import { FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { useDmnEditorStore } from "../../store/Store";
import { DMN15__tInputClause } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { useDmnEditor } from "../../DmnEditorContext";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { PropertiesPanelHeader } from "../PropertiesPanelHeader";
import { useUpdateBee } from "./useUpdateBee";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";

export function DecisionTableInputHeaderCell(props: { beeMap?: BeeMap; isReadonly: boolean }) {
  const { selectedObjectId } = useDmnEditorStore((s) => s.boxedExpressionEditor);
  const { dmnEditorRootElementRef } = useDmnEditor();
  const selectedObjectInfos = useMemo(
    () => props.beeMap?.get(selectedObjectId ?? ""),
    [props.beeMap, selectedObjectId]
  );

  const updateBee = useUpdateBee<DMN15__tInputClause>(
    useCallback((dmnObject, newContent) => {
      // DESCRIPTION
      if (newContent.description?.__$$text !== undefined) {
        dmnObject.description ??= { __$$text: "" };
        dmnObject.description = newContent.description as { __$$text: string };
      }

      if (newContent.inputExpression) {
        // DESCRIPTION
        if (newContent.inputExpression?.description !== undefined) {
          dmnObject.inputExpression ??= { description: { __$$text: "" } };
          dmnObject.inputExpression.description ??= { __$$text: "" };
          dmnObject.inputExpression.description = newContent.inputExpression.description;
        }

        // TEXT
        if (newContent.inputExpression?.text !== undefined) {
          dmnObject.inputExpression ??= { text: { __$$text: "" } };
          dmnObject.inputExpression.text = newContent.inputExpression.text;
        }

        // TYPEREF
        if (newContent.inputExpression["@_typeRef"] !== undefined) {
          dmnObject.inputExpression ??= { ["@_typeRef"]: "", text: { __$$text: "" } };
          dmnObject.inputExpression["@_typeRef"] = newContent.inputExpression?.["@_typeRef"];
        }
      }

      if (newContent.inputValues) {
        // DESCRIPTION
        if (newContent.inputValues?.description !== undefined) {
          dmnObject.inputValues ??= { description: { __$$text: "" }, text: { __$$text: "" } };
          dmnObject.inputValues.description ??= { __$$text: "" };
          dmnObject.inputValues.description = newContent.inputValues.description;
        }

        // TEXT
        if (newContent.inputValues?.text !== undefined) {
          dmnObject.inputValues ??= { text: { __$$text: "" } };
          dmnObject.inputValues.text = newContent.inputValues.text;
        }

        // TYPEREF
        if (newContent.inputValues["@_typeRef"] !== undefined) {
          dmnObject.inputValues ??= { ["@_typeRef"]: "", text: { __$$text: "" } };
          dmnObject.inputValues["@_typeRef"] = newContent.inputValues?.["@_typeRef"];
        }
      }
    }, []),
    props.beeMap
  );

  const cell = useMemo(() => selectedObjectInfos?.cell as DMN15__tInputClause, [selectedObjectInfos?.cell]);
  const inputExpression = useMemo(() => cell.inputExpression, [cell.inputExpression]);
  const inputValues = useMemo(() => cell.inputValues, [cell.inputValues]);

  const [isInputExpressionExpanded, setInputExpressionExpanded] = useState(true);
  const [isInputValuesExpanded, setInputValuesExpanded] = useState(false);

  return (
    <>
      <FormGroup label="ID">
        <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
          {selectedObjectId}
        </ClipboardCopy>
      </FormGroup>
      <DescriptionField
        isReadonly={props.isReadonly}
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
        initialValue={cell?.description?.__$$text ?? ""}
        onChange={(newDescription: string, expressionPath: ExpressionPath[]) =>
          updateBee({ description: { __$$text: newDescription } }, expressionPath)
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
              id={inputExpression["@_id"] ?? ""}
              isReadonly={props.isReadonly}
              name={inputExpression?.text?.__$$text ?? ""}
              allUniqueNames={new Map()}
              onChange={(newText) => updateBee({ inputExpression: { text: { __$$text: newText } } })}
            />
            <TypeRefField
              isReadonly={true}
              dmnEditorRootElementRef={dmnEditorRootElementRef}
              typeRef={inputExpression?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
            />
            <ExpressionLanguageField
              isReadonly={props.isReadonly}
              initialValue={inputExpression?.["@_expressionLanguage"] ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newExpressionLanguage, expressionPath: ExpressionPath[]) =>
                updateBee({ inputExpression: { "@_expressionLanguage": newExpressionLanguage } }, expressionPath)
              }
            />
            <DescriptionField
              isReadonly={props.isReadonly}
              initialValue={inputExpression?.description?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newDescription, expressionPath: ExpressionPath[]) =>
                updateBee({ inputExpression: { description: { __$$text: newDescription } } }, expressionPath)
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
            <TypeRefField
              isReadonly={true}
              dmnEditorRootElementRef={dmnEditorRootElementRef}
              typeRef={inputExpression?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
            />
            <ExpressionLanguageField
              isReadonly={props.isReadonly}
              initialValue={inputValues?.["@_expressionLanguage"] ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newExpressionLanguage, expressionPath: ExpressionPath[]) =>
                updateBee({ inputValues: { "@_expressionLanguage": newExpressionLanguage } }, expressionPath)
              }
            />
            <ContentField
              isReadonly={props.isReadonly}
              initialValue={inputValues?.text?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newText, expressionPath: ExpressionPath[]) =>
                updateBee({ inputValues: { text: { __$$text: newText } } }, expressionPath)
              }
            />
            <DescriptionField
              isReadonly={props.isReadonly}
              initialValue={inputValues?.description?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newDescription: string, expressionPath: ExpressionPath[]) =>
                updateBee({ description: { __$$text: newDescription } }, expressionPath)
              }
            />
            <KieConstraintTypeField />
          </>
        )}
      </FormSection>
    </>
  );
}
