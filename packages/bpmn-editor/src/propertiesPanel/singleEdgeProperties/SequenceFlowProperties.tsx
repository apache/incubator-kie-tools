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

import { BPMN20__tSequenceFlow } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import * as React from "react";
import { Normalized } from "../../normalization/normalize";
import { useBpmnEditorStoreApi } from "../../store/StoreContext";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { NameDocumentationAndId } from "../nameDocumentationAndId/NameDocumentationAndId";
import { CodeInput } from "../codeInput/CodeInput";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput/TextInput";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { useBpmnEditorI18n } from "../../i18n";
import { ScriptLanguage, getScriptFormat, getScriptLanguage } from "../scriptFormats";

export function SequenceFlowProperties({
  sequenceFlow,
}: {
  sequenceFlow: Normalized<BPMN20__tSequenceFlow> & { __$$element: "sequenceFlow" };
}) {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  const isReadOnly = bpmnEditorStoreApi((s) => s.settings.isReadOnly);

  const currentLanguage =
    getScriptLanguage((sequenceFlow.conditionExpression as any)?.["@_language"]) ?? ScriptLanguage.Java;

  return (
    <FormSection>
      <NameDocumentationAndId element={sequenceFlow} />
      <FormGroup label={i18n.propertiesPanel.priority}>
        <TextInput
          aria-label={"Priority"}
          type={"number"}
          isDisabled={isReadOnly}
          value={sequenceFlow["@_drools:priority"] || ""}
          onChange={(e, newPriority) =>
            bpmnEditorStoreApi.setState((s) => {
              const { process } = addOrGetProcessAndDiagramElements({
                definitions: s.bpmn.model.definitions,
              });
              visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                if (e["@_id"] === sequenceFlow?.["@_id"] && e.__$$element === sequenceFlow.__$$element) {
                  e["@_drools:priority"] = newPriority;
                }
              });
            })
          }
          placeholder={i18n.propertiesPanel.priorityPlaceholder}
        />
      </FormGroup>
      <CodeInput
        label={i18n.propertiesPanel.conditionalExpression}
        availableLanguages={[ScriptLanguage.Java, ScriptLanguage.MVEL, ScriptLanguage.Drools, ScriptLanguage.FEEL]}
        currentLanguage={currentLanguage}
        onLanguageChange={(newLanguage) =>
          bpmnEditorStoreApi.setState((s) => {
            const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
            visitFlowElementsAndArtifacts(process, ({ element: e }) => {
              if (e["@_id"] === sequenceFlow["@_id"] && e.__$$element === sequenceFlow.__$$element) {
                e.conditionExpression ??= {
                  "@_id": generateUuid(),
                };
                (e.conditionExpression as any)["@_xsi:type"] = "tFormalExpression";
                (e.conditionExpression as any)["@_language"] = getScriptFormat(newLanguage);
              }
            });
          })
        }
        value={sequenceFlow.conditionExpression?.__$$text || ""}
        onChange={(e, newScript) =>
          bpmnEditorStoreApi.setState((s) => {
            const { process } = addOrGetProcessAndDiagramElements({
              definitions: s.bpmn.model.definitions,
            });
            visitFlowElementsAndArtifacts(process, ({ element: e }) => {
              if (e["@_id"] === sequenceFlow?.["@_id"] && e.__$$element === sequenceFlow.__$$element) {
                e.conditionExpression ??= {
                  "@_id": generateUuid(),
                };
                e.conditionExpression.__$$text = newScript;
                (e.conditionExpression as any)["@_xsi:type"] = "tFormalExpression";
                // Derive the language from the element's current state inside the setState callback
                // rather than the render-time `currentLanguage` closure, so a language change
                // committed just before this keystroke is not overwritten by a stale captured value.
                const language = getScriptLanguage((e.conditionExpression as any)["@_language"]) ?? ScriptLanguage.Java;
                (e.conditionExpression as any)["@_language"] ??= getScriptFormat(language);
              }
            });
          })
        }
      ></CodeInput>
    </FormSection>
  );
}
