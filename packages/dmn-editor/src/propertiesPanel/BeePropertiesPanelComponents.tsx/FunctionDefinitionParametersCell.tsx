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
import { BeeMap, ExpressionPath } from "../../boxedExpressions/getBeeMap";
import { useDmnEditorStore } from "../../store/Store";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { useDmnEditor } from "../../DmnEditorContext";
import { useUpdateBee } from "./useUpdateBee";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import {
  DMN15__tFunctionDefinition,
  DMN15__tInformationItem,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { PropertiesPanelHeader } from "../PropertiesPanelHeader";

/**
 * This component implements a form to change an object with the DMN15__tInformationItem type
 * It's used for: ContextExpressionVariableCell, InvocationExpressionParametersCell and RelationExpressionHeaderCell
 */
export function FunctionDefinitionParameterCell(props: { beeMap?: BeeMap; isReadonly: boolean }) {
  const { selectedObjectId } = useDmnEditorStore((s) => s.boxedExpressionEditor);
  const { dmnEditorRootElementRef } = useDmnEditor();
  const selectedObjectInfos = useMemo(
    () => props.beeMap?.get(selectedObjectId ?? ""),
    [props.beeMap, selectedObjectId]
  );

  const updateBee = useUpdateBee<DMN15__tFunctionDefinition>(
    useCallback((dmnObject, newContent, parameterIndex: number) => {
      dmnObject.formalParameter ??= [];
      if (newContent.formalParameter?.[parameterIndex]?.["@_name"] !== undefined) {
        dmnObject.formalParameter[parameterIndex] ??= { "@_name": "" };
        dmnObject.formalParameter[parameterIndex]["@_name"] = newContent.formalParameter![parameterIndex]!["@_name"];
      }
      if (newContent.formalParameter![parameterIndex]?.["@_typeRef"] !== undefined) {
        dmnObject.formalParameter[parameterIndex] ??= { "@_typeRef": "", "@_name": "" };
        dmnObject.formalParameter[parameterIndex]["@_typeRef"] =
          newContent.formalParameter![parameterIndex]!["@_typeRef"];
      }
      if (newContent.formalParameter![parameterIndex]?.description?.__$$text !== undefined) {
        dmnObject.formalParameter[parameterIndex] ??= { description: { __$$text: "" }, "@_name": "" };
        dmnObject.formalParameter[parameterIndex].description ??= { __$$text: "" };
        dmnObject.formalParameter[parameterIndex].description = newContent.formalParameter![parameterIndex]!
          .description as { __$$text: string };
      }
    }, []),
    props.beeMap
  );

  const cell = useMemo(() => selectedObjectInfos?.cell as DMN15__tInformationItem[], [selectedObjectInfos?.cell]);
  const [isParameterExpanded, setParameterExpaded] = useState<boolean[]>([]);

  return (
    <>
      {cell?.map((parameter, i) => (
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
            title={`Parameter ${parameter["@_name"]}`}
          />

          {isParameterExpanded[i] && (
            <>
              <FormGroup label="ID">
                <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
                  {selectedObjectId}
                </ClipboardCopy>
              </FormGroup>
              <NameField
                isReadonly={props.isReadonly}
                id={parameter["@_id"] ?? ""}
                name={parameter["@_name"] ?? ""}
                allUniqueNames={new Map()}
                onChange={(newName: string) => {
                  const formalParameter = [];
                  formalParameter[i] = { "@_name": newName };
                  updateBee({ formalParameter }, undefined, i);
                }}
              />
              <TypeRefField
                isReadonly={props.isReadonly}
                dmnEditorRootElementRef={dmnEditorRootElementRef}
                typeRef={parameter["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
                onChange={(newTypeRef) => {
                  const formalParameter = [];
                  formalParameter[i] = { "@_typeRef": newTypeRef };
                  updateBee({ formalParameter } as any, undefined, i);
                }}
              />
              <DescriptionField
                isReadonly={props.isReadonly}
                initialValue={parameter.description?.__$$text ?? ""}
                expressionPath={selectedObjectInfos?.expressionPath ?? []}
                onChange={(newDescription: string, expressionPath: ExpressionPath[]) => {
                  const formalParameter = [];
                  formalParameter[i] = { description: { __$$text: newDescription } };
                  updateBee({ formalParameter } as any, expressionPath, i);
                }}
              />
            </>
          )}
        </FormSection>
      ))}
    </>
  );
}
