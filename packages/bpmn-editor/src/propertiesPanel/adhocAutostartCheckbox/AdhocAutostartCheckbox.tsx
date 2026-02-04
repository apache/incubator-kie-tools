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
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Normalized } from "../../normalization/normalize";
import { BPMN20__tProcess } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import {
  parseBpmn20Drools10MetaData,
  setBpmn20Drools10MetaData,
} from "@kie-tools/bpmn-marshaller/dist/drools-extension-metaData";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import "./AdhocAutostartCheckbox.css";
import { useBpmnEditorI18n } from "../../i18n";

export type WithAdhocAutostart = Normalized<
  ElementFilter<
    Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
    "adHocSubProcess" | "userTask" | "task" | "scriptTask" | "businessRuleTask" | "serviceTask"
  >
>;

export function AdhocAutostartCheckbox({ element }: { element: WithAdhocAutostart }) {
  const { i18n } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  return (
    <FormGroup
      fieldId="kie-bpmn-editor--properties-panel--adhoc-auto-start"
      // helperText={"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod."} // FIXME: Tiago -> Description
    >
      <Checkbox
        label={i18n.propertiesPanel.adhocAutoStart}
        id="kie-bpmn-editor--properties-panel--adhoc-auto-start"
        name="is-adhoc-auto-start"
        aria-label="Ad-hoc auto-start"
        isDisabled={isReadOnly}
        isChecked={(parseBpmn20Drools10MetaData(element).get("customAutoStart") ?? "false") === "true"}
        onChange={(e, checked) => {
          bpmnEditorStoreApi.setState((s) => {
            const { process } = addOrGetProcessAndDiagramElements({
              definitions: s.bpmn.model.definitions,
            });
            visitFlowElementsAndArtifacts(process, ({ element: e }) => {
              if (e["@_id"] === element["@_id"] && e.__$$element === element.__$$element) {
                setBpmn20Drools10MetaData(e, "customAutoStart", `${checked}`);
                return false; // Will stop visiting.
              }
            });
          });
        }}
      />
    </FormGroup>
  );
}
