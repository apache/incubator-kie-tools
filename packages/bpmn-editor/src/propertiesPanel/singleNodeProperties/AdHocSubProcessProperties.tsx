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

import {
  BPMN20__tAdHocOrdering,
  BPMN20__tAdHocSubProcess,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import * as React from "react";
import { Normalized } from "../../normalization/normalize";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { NameDocumentationAndId } from "../nameDocumentationAndId/NameDocumentationAndId";
import { OnEntryAndExitScriptsFormSection } from "../onEntryAndExitScripts/OnEntryAndExitScriptsFormSection";
import { SubProcessIcon } from "../../diagram/nodes/NodeIcons";
import { PropertiesPanelHeaderFormSection } from "./_PropertiesPanelHeaderFormSection";
import { VariablesFormSection } from "../variables/VariablesFormSection";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { CodeInput } from "../codeInput/CodeInput";
import { FormSelect, FormSelectOption } from "@patternfly/react-core/dist/js/components/FormSelect";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import {
  parseBpmn20Drools10MetaData,
  setBpmn20Drools10MetaData,
} from "@kie-tools/bpmn-marshaller/dist/drools-extension-metaData";
import { AdhocAutostartCheckbox } from "../adhocAutostartCheckbox/AdhocAutostartCheckbox";
import { SlaDueDateInput } from "../slaDueDate/SlaDueDateInput";
import { AsyncCheckbox } from "../asyncCheckbox/AsyncCheckbox";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { useBpmnEditorI18n } from "../../i18n";

export function AdHocSubProcessProperties({
  adHocSubProcess,
}: {
  adHocSubProcess: Normalized<BPMN20__tAdHocSubProcess> & { __$$element: "adHocSubProcess" };
}) {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const orderingOptions = [
    { value: "Sequential", label: "Sequential" },
    { value: "Parallel", label: "Parallel" },
  ];

  return (
    <>
      <PropertiesPanelHeaderFormSection
        title={adHocSubProcess["@_name"] || i18n.singleNodeProperties.adhocSubprocess}
        icon={<SubProcessIcon />}
      >
        <NameDocumentationAndId element={adHocSubProcess} />

        <Divider inset={{ default: "insetXs" }} />

        <SlaDueDateInput element={adHocSubProcess} />

        <AsyncCheckbox element={adHocSubProcess} />

        <Divider inset={{ default: "insetXs" }} />

        <FormGroup label={i18n.singleNodeProperties.adhocOrdering}>
          <FormSelect
            id={"ad-hoc-subprocess-ordering-selector" + generateUuid()}
            type={"text"}
            isDisabled={isReadOnly}
            value={adHocSubProcess?.["@_ordering"] ?? i18n.singleNodeProperties.parallel}
            onChange={(e, newOrderingValue) => {
              bpmnEditorStoreApi.setState((s) => {
                const { process } = addOrGetProcessAndDiagramElements({
                  definitions: s.bpmn.model.definitions,
                });
                visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                  if (e["@_id"] === adHocSubProcess["@_id"] && e.__$$element === adHocSubProcess.__$$element) {
                    e["@_ordering"] = newOrderingValue as BPMN20__tAdHocOrdering;
                  }
                });
              });
            }}
          >
            {orderingOptions.map((option) => (
              <FormSelectOption key={option.label} label={option.label} value={option.value} />
            ))}
          </FormSelect>
        </FormGroup>

        <AdhocAutostartCheckbox element={adHocSubProcess} />

        <CodeInput
          label={i18n.singleNodeProperties.adhocActivationCondition}
          languages={["Drools"]}
          value={parseBpmn20Drools10MetaData(adHocSubProcess).get("customActivationCondition") ?? ""}
          onChange={(e, newCode) => {
            bpmnEditorStoreApi.setState((s) => {
              const { process } = addOrGetProcessAndDiagramElements({
                definitions: s.bpmn.model.definitions,
              });
              visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                if (e["@_id"] === adHocSubProcess["@_id"] && e.__$$element === adHocSubProcess.__$$element) {
                  setBpmn20Drools10MetaData(e, "customActivationCondition", newCode);
                }
              });
            });
          }}
        />

        <CodeInput
          label={i18n.singleNodeProperties.adhocCompletionCondition}
          languages={["MVEL", "Drools"]}
          value={adHocSubProcess.completionCondition?.__$$text ?? ""}
          onChange={(e, newCompletionConnditionValue) => {
            bpmnEditorStoreApi.setState((s) => {
              const { process } = addOrGetProcessAndDiagramElements({
                definitions: s.bpmn.model.definitions,
              });
              visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                if (e["@_id"] === adHocSubProcess["@_id"] && e.__$$element === adHocSubProcess.__$$element) {
                  e.completionCondition ??= { "@_id": generateUuid(), __$$text: "" };
                  e.completionCondition.__$$text = newCompletionConnditionValue;
                }
              });
            });
          }}
        />
      </PropertiesPanelHeaderFormSection>

      <VariablesFormSection p={adHocSubProcess} />

      <OnEntryAndExitScriptsFormSection element={adHocSubProcess} />
    </>
  );
}
