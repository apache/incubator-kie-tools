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
import { KieConstraintTypeField, NameField, TextAreaField, TextInputField, TypeRefField } from "./Fields";
import { FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/Store";
import { useDmnEditorDerivedStore } from "../../store/DerivedStore";
import { buildXmlHref } from "../../xml/xmlHrefs";
import {
  DMN15__tBusinessKnowledgeModel,
  DMN15__tDecision,
  DMN15__tOutputClause,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { PropertiesPanelHeader } from "../PropertiesPanelHeader";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { useDmnEditor } from "../../DmnEditorContext";

export function DecisionTableOutputHeaderCell(props: { beeMap?: BeeMap; isReadonly: boolean }) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { selectedObjectId, activeDrgElementId } = useDmnEditorStore((s) => s.boxedExpressionEditor);
  const { allFeelVariableUniqueNames, nodesById } = useDmnEditorDerivedStore();
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
    (dmnObject: DMN15__tOutputClause, newContent: DeepPartial<DMN15__tOutputClause>) => {
      // NAME
      if (newContent["@_name"]) {
        dmnObject["@_name"] = newContent?.["@_name"];
      }

      // TYPEREF
      if (newContent["@_typeRef"]) {
        dmnObject["@_typeRef"] = newContent?.["@_typeRef"];
      }

      // LABEL
      if (newContent["@_label"]) {
        dmnObject["@_label"] = newContent?.["@_label"];
      }

      // DESCRIPTION
      if (newContent.description?.__$$text) {
        dmnObject.description ??= { __$$text: "" };
        dmnObject.description = newContent.description as { __$$text: string };
      }

      // DEFAULT OUTPUT ENTRY
      if (newContent.defaultOutputEntry) {
        // DESCRIPTION
        if (newContent.defaultOutputEntry?.description) {
          dmnObject.defaultOutputEntry ??= { description: { __$$text: "" } };
          dmnObject.defaultOutputEntry.description ??= { __$$text: "" };
          dmnObject.defaultOutputEntry.description = newContent.defaultOutputEntry.description;
        }

        // TEXT
        if (newContent.defaultOutputEntry?.text) {
          dmnObject.defaultOutputEntry ??= { text: { __$$text: "" } };
          dmnObject.defaultOutputEntry.text = newContent.defaultOutputEntry.text;
        }

        // TYPEREF
        if (newContent.defaultOutputEntry["@_typeRef"]) {
          dmnObject.defaultOutputEntry ??= { ["@_typeRef"]: "" };
          dmnObject.defaultOutputEntry["@_typeRef"] = newContent.defaultOutputEntry?.["@_typeRef"];
        }

        // LABEL
        if (newContent.defaultOutputEntry["@_label"]) {
          dmnObject.defaultOutputEntry ??= { ["@_label"]: "" };
          dmnObject.defaultOutputEntry["@_label"] = newContent.defaultOutputEntry?.["@_label"];
        }
      }

      // OUTPUT VALUES
      if (newContent.outputValues) {
        // DESCRIPTION
        if (newContent.outputValues?.description) {
          dmnObject.outputValues ??= { description: { __$$text: "" }, text: { __$$text: "" } };
          dmnObject.outputValues.description ??= { __$$text: "" };
          dmnObject.outputValues.description = newContent.outputValues.description;
        }

        // TEXT
        if (newContent.outputValues?.text) {
          dmnObject.outputValues ??= { text: { __$$text: "" } };
          dmnObject.outputValues.text = newContent.outputValues.text;
        }

        // TYPEREF
        if (newContent.outputValues["@_typeRef"]) {
          dmnObject.outputValues ??= { ["@_typeRef"]: "", text: { __$$text: "" } };
          dmnObject.outputValues["@_typeRef"] = newContent.outputValues?.["@_typeRef"];
        }

        // LABEL
        if (newContent.outputValues["@_label"]) {
          dmnObject.outputValues ??= { ["@_label"]: "", text: { __$$text: "" } };
          dmnObject.outputValues["@_label"] = newContent.outputValues?.["@_label"];
        }
      }
    },
    []
  );

  const updateBee = useCallback(
    (newContent: DeepPartial<DMN15__tOutputClause>, expressionPath = selectedObjectInfos?.expressionPath) => {
      dmnEditorStoreApi.setState((state) => {
        if (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0]?.__$$element === "businessKnowledgeModel") {
          const dmnObject = getDmnObject(
            expressionPath ?? [],
            (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0] as DMN15__tBusinessKnowledgeModel)
              ?.encapsulatedLogic?.expression
          );
          dmnObject && updateDmnObject(dmnObject as DMN15__tOutputClause, newContent);
        }
        if (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0]?.__$$element === "decision") {
          const dmnObject = getDmnObject(
            expressionPath ?? [],
            (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0] as DMN15__tDecision)?.expression
          );
          dmnObject && updateDmnObject(dmnObject as DMN15__tOutputClause, newContent);
        }
      });
    },
    [dmnEditorStoreApi, node?.data.index, selectedObjectInfos?.expressionPath, updateDmnObject]
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
        allUniqueNames={allFeelVariableUniqueNames}
        onChange={(newTypeRef) => updateBee({ "@_name": newTypeRef })}
      />
      <TypeRefField
        isReadonly={props.isReadonly}
        dmnEditorRootElementRef={dmnEditorRootElementRef}
        typeRef={cell?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined}
        onChange={(newTypeRef) => updateBee({ "@_typeRef": newTypeRef })}
      />
      <TextInputField
        title={"Label"}
        isReadonly={props.isReadonly}
        initialValue={cell?.["@_label"] ?? ""}
        onChange={(newLabel: string) => updateBee({ "@_label": newLabel })}
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
      />

      <TextAreaField
        title={"Description"}
        isReadonly={props.isReadonly}
        expressionPath={selectedObjectInfos?.expressionPath ?? []}
        initialValue={cell?.description?.__$$text ?? ""}
        onChange={(newDescription: string) => updateBee({ description: { __$$text: newDescription } })}
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
            <TextInputField
              title={"Expression Language"}
              isReadonly={props.isReadonly}
              initialValue={defaultOutputEntry?.["@_expressionLanguage"] ?? ""}
              onChange={(newExpressionLanguage) =>
                updateBee({ defaultOutputEntry: { "@_expressionLanguage": newExpressionLanguage } })
              }
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
            />
            <TextAreaField
              title={"Content"}
              isReadonly={props.isReadonly}
              initialValue={defaultOutputEntry?.text?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newText) => updateBee({ defaultOutputEntry: { text: { __$$text: newText } } })}
            />
            <TextInputField
              title={"Label"}
              isReadonly={props.isReadonly}
              initialValue={defaultOutputEntry?.["@_label"] ?? ""}
              onChange={(newLabel) => updateBee({ defaultOutputEntry: { "@_label": newLabel } })}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
            />
            <TextAreaField
              title={"Description"}
              isReadonly={props.isReadonly}
              initialValue={defaultOutputEntry?.description?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newDescription) =>
                updateBee({ defaultOutputEntry: { description: { __$$text: newDescription } } })
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
            <TextInputField
              title={"Expression Language"}
              isReadonly={props.isReadonly}
              initialValue={outputValues?.["@_expressionLanguage"] ?? ""}
              onChange={(newExpressionLanguage) =>
                updateBee({ outputValues: { "@_expressionLanguage": newExpressionLanguage } })
              }
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
            />
            <TextAreaField
              title={"Content"}
              isReadonly={props.isReadonly}
              initialValue={outputValues?.text?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newText) => updateBee({ outputValues: { text: { __$$text: newText } } })}
            />
            <TextInputField
              title={"Label"}
              isReadonly={props.isReadonly}
              initialValue={outputValues?.["@_label"] ?? ""}
              onChange={(newLabel) => updateBee({ outputValues: { "@_label": newLabel } })}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
            />
            <TextAreaField
              title={"Description"}
              isReadonly={props.isReadonly}
              initialValue={outputValues?.description?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newDescription) => updateBee({ outputValues: { description: { __$$text: newDescription } } })}
            />
            <KieConstraintTypeField />
          </>
        )}
      </FormSection>
    </>
  );
}
