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
import { DescriptionField, ExpressionLanguageField, KieConstraintTypeField, LabelField, TextField } from "./Fields";
import { FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/Store";
import { useDmnEditorDerivedStore } from "../../store/DerivedStore";
import { buildXmlHref } from "../../xml/xmlHrefs";
import {
  DMN15__tBusinessKnowledgeModel,
  DMN15__tDecision,
  DMN15__tOutputClause,
  DMN15__tUnaryTests,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { PropertiesPanelHeader } from "../PropertiesPanelHeader";

export function DecisionTableOutputHeaderCell(props: { beeMap?: BeeMap; isReadonly: boolean }) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { selectedObjectId, activeDrgElementId } = useDmnEditorStore((s) => s.boxedExpressionEditor);
  const { nodesById } = useDmnEditorDerivedStore();

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

      // DEFAULT OUTPUT ENTRY
      if (!dmnObject.defaultOutputEntry) {
        dmnObject.defaultOutputEntry = newContent.defaultOutputEntry;
      }
      if (newContent.defaultOutputEntry) {
        // DESCRIPTION
        if (newContent.defaultOutputEntry?.description && dmnObject.defaultOutputEntry?.description) {
          dmnObject.defaultOutputEntry.description = newContent.defaultOutputEntry.description;
        } else {
          dmnObject.defaultOutputEntry = {
            ...dmnObject.defaultOutputEntry,
            description: newContent.defaultOutputEntry.description,
          };
        }

        // TEXT
        if (newContent.defaultOutputEntry?.text && dmnObject.defaultOutputEntry?.text) {
          dmnObject.defaultOutputEntry.text = newContent.defaultOutputEntry.text;
        } else {
          dmnObject.defaultOutputEntry = { ...dmnObject.defaultOutputEntry, text: newContent.defaultOutputEntry.text };
        }

        // TYPEREF
        if (newContent.defaultOutputEntry["@_typeRef"]) {
          dmnObject.defaultOutputEntry["@_typeRef"] = newContent.defaultOutputEntry?.["@_typeRef"];
        }

        // LABEL
        if (newContent.defaultOutputEntry["@_label"]) {
          dmnObject.defaultOutputEntry["@_label"] = newContent.defaultOutputEntry?.["@_label"];
        }
      }

      // DEFAULT OUTPUT VALUES
      if (!dmnObject.outputValues) {
        dmnObject = { ...dmnObject, outputValues: newContent.outputValues as DMN15__tUnaryTests };
      }
      if (newContent.outputValues) {
        // DESCRIPTION
        if (newContent.outputValues?.description && dmnObject.outputValues?.description) {
          dmnObject.outputValues.description = newContent.outputValues.description;
        } else {
          dmnObject.outputValues = {
            ...dmnObject.outputValues!,
            description: newContent.outputValues.description,
          };
        }

        // TEXT
        if (newContent.outputValues?.text && dmnObject.outputValues?.text) {
          dmnObject.outputValues.text = newContent.outputValues.text;
        } else {
          dmnObject.outputValues = { ...dmnObject.outputValues, text: newContent.outputValues.text! };
        }

        // TYPEREF
        if (newContent.outputValues["@_typeRef"]) {
          dmnObject.outputValues["@_typeRef"] = newContent.outputValues?.["@_typeRef"];
        }

        // LABEL
        if (newContent.outputValues["@_label"]) {
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
          isSectionExpanded={isDefaultOutputEntryExpanded}
          toogleSectionExpanded={() => setDefaultOutputEntryExpanded((prev) => !prev)}
          title={"Default Output Entry"}
        />
        {isDefaultOutputEntryExpanded && (
          <>
            <ExpressionLanguageField
              isReadonly={props.isReadonly}
              expressionLanguage={defaultOutputEntry?.["@_expressionLanguage"] ?? ""}
              onChange={(newExpressionLanguage) =>
                updateBee({ defaultOutputEntry: { "@_expressionLanguage": newExpressionLanguage } })
              }
            />
            <TextField
              isReadonly={props.isReadonly}
              initialValue={defaultOutputEntry?.text?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newText) => updateBee({ defaultOutputEntry: { text: { __$$text: newText } } })}
            />
            <LabelField
              isReadonly={props.isReadonly}
              label={defaultOutputEntry?.["@_label"] ?? ""}
              onChange={(newLabel) => updateBee({ defaultOutputEntry: { "@_label": newLabel } })}
            />
            <DescriptionField
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
            <ExpressionLanguageField
              isReadonly={props.isReadonly}
              expressionLanguage={outputValues?.["@_expressionLanguage"] ?? ""}
              onChange={(newExpressionLanguage) =>
                updateBee({ outputValues: { "@_expressionLanguage": newExpressionLanguage } })
              }
            />
            <TextField
              isReadonly={props.isReadonly}
              initialValue={outputValues?.text?.__$$text ?? ""}
              expressionPath={selectedObjectInfos?.expressionPath ?? []}
              onChange={(newText) => updateBee({ outputValues: { text: { __$$text: newText } } })}
            />
            <LabelField
              isReadonly={props.isReadonly}
              label={outputValues?.["@_label"] ?? ""}
              onChange={(newLabel) => updateBee({ outputValues: { "@_label": newLabel } })}
            />
            <DescriptionField
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
