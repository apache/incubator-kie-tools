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
import { useMemo, useState } from "react";
import { BoxedExpressionIndex } from "../../boxedExpressions/getBeeMap";
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
import { DMN15__tOutputClause } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { PropertiesPanelHeader } from "../PropertiesPanelHeader";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { useDmnEditor } from "../../DmnEditorContext";
import { useBoxedExpressionUpdater } from "./useUpdateBee";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";

export function DecisionTableOutputHeaderCell(props: {
  boxedExpressionIndex?: BoxedExpressionIndex;
  isReadonly: boolean;
}) {
  const selectedObjectId = useDmnEditorStore((s) => s.boxedExpressionEditor.selectedObjectId);
  const { dmnEditorRootElementRef } = useDmnEditor();

  const selectedObjectInfos = useMemo(
    () => props.boxedExpressionIndex?.get(selectedObjectId ?? ""),
    [props.boxedExpressionIndex, selectedObjectId]
  );

  const updater = useBoxedExpressionUpdater<DMN15__tOutputClause>(selectedObjectInfos?.expressionPath ?? []);

  const cell = useMemo(() => selectedObjectInfos?.cell as DMN15__tOutputClause, [selectedObjectInfos?.cell]);
  const defaultOutputEntry = useMemo(() => cell.defaultOutputEntry, [cell.defaultOutputEntry]);
  const outputValues = useMemo(() => cell.outputValues, [cell.outputValues]);

  const [isDefaultOutputEntryExpanded, setDefaultOutputEntryExpanded] = useState(false);
  const [isOutputValuesExpanded, setOutputValuesExpanded] = useState(false);

  return (
    <>
      <FormGroup label="ID">
        <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
          {selectedObjectId}
        </ClipboardCopy>
      </FormGroup>
      <NameField
        isReadonly={props.isReadonly}
        id={cell?.["@_id"] ?? ""}
        name={cell?.["@_name"] ?? ""}
        allUniqueNames={new Map()}
        onChange={(newName) =>
          updater((dmnObject) => {
            dmnObject["@_name"] ??= newName;
          })
        }
      />
      <TypeRefField
        isReadonly={true}
        dmnEditorRootElementRef={dmnEditorRootElementRef}
        typeRef={cell?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
        onChange={(newTypeRef) =>
          updater((dmnObject) => {
            dmnObject["@_typeRef"] ??= newTypeRef;
          })
        }
      />
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
                  dmnObject.defaultOutputEntry.text!.__$$text = newText;
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
                  dmnObject.defaultOutputEntry.description!.__$$text = newDescription;
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
            <KieConstraintTypeField />
          </>
        )}
      </FormSection>
    </>
  );
}
