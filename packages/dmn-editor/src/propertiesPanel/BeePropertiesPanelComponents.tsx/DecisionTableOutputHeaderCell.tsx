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
import { KieConstraintTypeField, NameField, TextAreaField, TextInputField, TypeRefField } from "./Fields";
import { FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { useDmnEditorStore } from "../../store/Store";
import { DMN15__tOutputClause } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { PropertiesPanelHeader } from "../PropertiesPanelHeader";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { useDmnEditor } from "../../DmnEditorContext";
import { useUpdateBee } from "./useUpdateBee";

export function DecisionTableOutputHeaderCell(props: { beeMap?: BeeMap; isReadonly: boolean }) {
  const { selectedObjectId } = useDmnEditorStore((s) => s.boxedExpressionEditor);
  const { dmnEditorRootElementRef } = useDmnEditor();

  const selectedObjectInfos = useMemo(
    () => props.beeMap?.get(selectedObjectId ?? ""),
    [props.beeMap, selectedObjectId]
  );

  const updateBee = useUpdateBee<DMN15__tOutputClause>(
    useCallback((dmnObject, newContent) => {
      // NAME
      if (newContent["@_name"] !== undefined) {
        dmnObject["@_name"] = newContent?.["@_name"];
      }

      // DESCRIPTION
      if (newContent.description?.__$$text !== undefined) {
        dmnObject.description ??= { __$$text: "" };
        dmnObject.description = newContent.description as { __$$text: string };
      }

      // DEFAULT OUTPUT ENTRY
      if (newContent.defaultOutputEntry) {
        // DESCRIPTION
        if (newContent.defaultOutputEntry?.description !== undefined) {
          dmnObject.defaultOutputEntry ??= { description: { __$$text: "" } };
          dmnObject.defaultOutputEntry.description ??= { __$$text: "" };
          dmnObject.defaultOutputEntry.description = newContent.defaultOutputEntry.description;
        }

        // TEXT
        if (newContent.defaultOutputEntry?.text !== undefined) {
          dmnObject.defaultOutputEntry ??= { text: { __$$text: "" } };
          dmnObject.defaultOutputEntry.text = newContent.defaultOutputEntry.text;
        }

        // TYPEREF
        if (newContent.defaultOutputEntry["@_typeRef"] !== undefined) {
          dmnObject.defaultOutputEntry ??= { ["@_typeRef"]: "", text: { __$$text: "" } };
          dmnObject.defaultOutputEntry["@_typeRef"] = newContent.defaultOutputEntry?.["@_typeRef"];
        }
      }

      // OUTPUT VALUES
      if (newContent.outputValues) {
        // DESCRIPTION
        if (newContent.outputValues?.description !== undefined) {
          dmnObject.outputValues ??= { description: { __$$text: "" }, text: { __$$text: "" } };
          dmnObject.outputValues.description ??= { __$$text: "" };
          dmnObject.outputValues.description = newContent.outputValues.description;
        }

        // TEXT
        if (newContent.outputValues?.text !== undefined) {
          dmnObject.outputValues ??= { text: { __$$text: "" } };
          dmnObject.outputValues.text = newContent.outputValues.text;
        }

        // TYPEREF
        if (newContent.outputValues["@_typeRef"] !== undefined) {
          dmnObject.outputValues ??= { ["@_typeRef"]: "", text: { __$$text: "" } };
          dmnObject.outputValues["@_typeRef"] = newContent.outputValues?.["@_typeRef"];
        }
      }
    }, []),
    props.beeMap
  );

  const cell = useMemo(() => selectedObjectInfos?.cell as DMN15__tOutputClause, [selectedObjectInfos?.cell]);
  const defaultOutputEntry = useMemo(() => cell.defaultOutputEntry, [cell.defaultOutputEntry]);
  const outputValues = useMemo(() => cell.outputValues, [cell.outputValues]);

  const [isDefaultOutputEntryExpanded, setDefaultOutputEntryExpanded] = useState(false);
  const [isOutputValuesExpanded, setOutputValuesExpanded] = useState(false);

  return (
    <>
      <NameField
        isReadonly={props.isReadonly}
        id={cell?.["@_id"] ?? ""}
        name={cell?.["@_name"] ?? ""}
        allUniqueNames={new Map()}
        onChange={(newTypeRef) => updateBee({ "@_name": newTypeRef })}
      />
      <TypeRefField
        isReadonly={true}
        dmnEditorRootElementRef={dmnEditorRootElementRef}
        typeRef={cell?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
      />
      <TextAreaField
        title={"Description"}
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
          isSectionExpanded={isDefaultOutputEntryExpanded}
          toogleSectionExpanded={() => setDefaultOutputEntryExpanded((prev) => !prev)}
          title={"Default Output Entry"}
        />
        {isDefaultOutputEntryExpanded && (
          <>
            <TypeRefField
              isReadonly={true}
              dmnEditorRootElementRef={dmnEditorRootElementRef}
              typeRef={outputValues?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
            />
            <TextInputField
              title={"Expression Language"}
              isReadonly={props.isReadonly}
              initialValue={defaultOutputEntry?.["@_expressionLanguage"] ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newExpressionLanguage, expressionPath: ExpressionPath[]) =>
                updateBee({ defaultOutputEntry: { "@_expressionLanguage": newExpressionLanguage } }, expressionPath)
              }
            />
            <TextAreaField
              title={"Content"}
              isReadonly={props.isReadonly}
              initialValue={defaultOutputEntry?.text?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newText, expressionPath: ExpressionPath[]) =>
                updateBee({ defaultOutputEntry: { text: { __$$text: newText } } }, expressionPath)
              }
            />
            <TextAreaField
              title={"Description"}
              isReadonly={props.isReadonly}
              initialValue={defaultOutputEntry?.description?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newDescription, expressionPath: ExpressionPath[]) =>
                updateBee({ defaultOutputEntry: { description: { __$$text: newDescription } } }, expressionPath)
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
            <TypeRefField
              isReadonly={true}
              dmnEditorRootElementRef={dmnEditorRootElementRef}
              typeRef={outputValues?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
            />
            <TextInputField
              title={"Expression Language"}
              isReadonly={props.isReadonly}
              initialValue={outputValues?.["@_expressionLanguage"] ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newExpressionLanguage, expressionPath: ExpressionPath[]) =>
                updateBee({ outputValues: { "@_expressionLanguage": newExpressionLanguage } }, expressionPath)
              }
            />
            <TextAreaField
              title={"Content"}
              isReadonly={props.isReadonly}
              initialValue={outputValues?.text?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newText, expressionPath: ExpressionPath[]) =>
                updateBee({ outputValues: { text: { __$$text: newText } } }, expressionPath)
              }
            />
            <TextAreaField
              title={"Description"}
              isReadonly={props.isReadonly}
              initialValue={outputValues?.description?.__$$text ?? ""}
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
