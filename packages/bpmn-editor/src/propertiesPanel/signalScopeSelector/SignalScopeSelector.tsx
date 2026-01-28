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
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { FormSelect, FormSelectOption } from "@patternfly/react-core/dist/js/components/FormSelect";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { Normalized } from "../../normalization/normalize";
import { BPMN20__tProcess, WithMetaData } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import {
  parseBpmn20Drools10MetaData,
  setBpmn20Drools10MetaData,
} from "@kie-tools/bpmn-marshaller/dist/drools-extension-metaData";
import { useBpmnEditorI18n } from "../../i18n";

export type WithSignalScope =
  | undefined
  | Normalized<
      ElementFilter<
        Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
        "intermediateThrowEvent" | "endEvent" | "boundaryEvent"
      >
    >;

const SignalScope = [
  { value: "default", label: "Default" },
  { value: "processInstance", label: "Process Instance" },
  { value: "project", label: "Project" },
  { value: "external", label: "External" },
];

export function SignalScopeSelector({ element }: { element: WithSignalScope }) {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  const settings = useBpmnEditorStore((s) => s.settings);

  return (
    <FormSection>
      <FormGroup label={i18n.propertiesPanel.signalScope} aria-label={"Signal Scope"}>
        <FormSelect
          type={"text"}
          isDisabled={settings.isReadOnly}
          value={parseBpmn20Drools10MetaData(element).get("customScope")}
          onChange={(e, newSignalScope) =>
            bpmnEditorStoreApi.setState((s) => {
              const { process } = addOrGetProcessAndDiagramElements({
                definitions: s.bpmn.model.definitions,
              });
              visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                if (element && e["@_id"] === element["@_id"]) {
                  setBpmn20Drools10MetaData(e as { extensionElements?: WithMetaData }, "customScope", newSignalScope);
                }
              });
            })
          }
          placeholder={"-- None --"}
          style={{ resize: "vertical", minHeight: "40px" }}
          rows={1}
        >
          {SignalScope.map((option) => (
            <FormSelectOption key={option.label} label={option.label} value={option.value} />
          ))}
        </FormSelect>
      </FormGroup>
    </FormSection>
  );
}
