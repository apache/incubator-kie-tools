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
import { FormSelect, FormSelectOption } from "@patternfly/react-core/dist/js/components/FormSelect";
import { Normalized } from "../../normalization/normalize";
import { BPMN20__tProcess } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import "./ActivitySelector.css";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { useCallback } from "react";
import { useBpmnEditorI18n } from "../../i18n";

export type WithActivity =
  | undefined
  | Normalized<
      ElementFilter<Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>, "intermediateThrowEvent" | "endEvent">
    >;

export function ActivitySelector({ element }: { element: WithActivity }) {
  const { i18n } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const activities = useBpmnEditorStore((s) =>
    addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions }).process.flowElement?.filter(
      (e) =>
        e.__$$element === "adHocSubProcess" ||
        e.__$$element === "subProcess" ||
        e.__$$element === "callActivity" ||
        e.__$$element === "task" ||
        e.__$$element === "userTask" ||
        e.__$$element === "businessRuleTask" ||
        e.__$$element === "scriptTask" ||
        e.__$$element === "serviceTask"
    )
  );

  const onChange = useCallback(
    (e: React.FormEvent, newActivityRef: string) => {
      bpmnEditorStoreApi.setState((s) => {
        const { process } = addOrGetProcessAndDiagramElements({
          definitions: s.bpmn.model.definitions,
        });
        visitFlowElementsAndArtifacts(process, ({ element: e }) => {
          if (e["@_id"] === element?.["@_id"] && e.__$$element === element.__$$element) {
            if (e.eventDefinition?.[0]?.__$$element === "compensateEventDefinition") {
              e.eventDefinition[0]["@_activityRef"] = newActivityRef ? newActivityRef : undefined; // empty string becomes `undefined`.
            }
          }
        });
      });
    },
    [bpmnEditorStoreApi, element]
  );

  const value =
    element?.eventDefinition?.[0].__$$element === "compensateEventDefinition"
      ? element.eventDefinition[0]["@_activityRef"] ?? ""
      : undefined;

  return (
    <FormGroup label={i18n.propertiesPanel.activity}>
      <FormSelect id={`activity-selector-${generateUuid()}`} value={value} isDisabled={isReadOnly} onChange={onChange}>
        <FormSelectOption id={"undefined"} value={""} isPlaceholder={true} label={i18n.undefined} />
        {activities?.map((e) => (
          <FormSelectOption
            key={e["@_id"]}
            id={e["@_id"]}
            value={e["@_id"]}
            label={e["@_name"] ?? `${i18n.unnamed} - ${e["@_id"]}`}
          />
        ))}
        {value && activities?.every((s) => s["@_id"] !== value) && (
          // Show option for when there's a broken reference too.
          <FormSelectOption id={value} isPlaceholder={true} value={value} label={`${i18n.unknown} - ${value}`} />
        )}
      </FormSelect>
    </FormGroup>
  );
}
