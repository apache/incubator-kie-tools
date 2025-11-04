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
import { START_EVENT_NODE_ON_EVENT_SUB_PROCESSES_IS_INTERRUPTING_DEFAULT_VALUE } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/Bpmn20Spec";
import { BPMN20__tStartEvent } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { BpmnNodeElement } from "../../diagram/BpmnDiagramDomain";
import { StartEventIcon } from "../../diagram/nodes/NodeIcons";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { Normalized } from "../../normalization/normalize";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { OutputOnlyAssociationFormSection } from "../dataMapping/DataMappingFormSection";
import { EventDefinitionProperties } from "../eventDefinition/EventDefinitionProperties";
import { NameDocumentationAndId } from "../nameDocumentationAndId/NameDocumentationAndId";
import { PropertiesPanelHeaderFormSection } from "./_PropertiesPanelHeaderFormSection";
import { useBpmnEditorI18n } from "../../i18n";

export function StartEventProperties({
  startEvent,
}: {
  startEvent: Normalized<BPMN20__tStartEvent> & { __$$element: "startEvent" };
}) {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const shouldDisplayIsInterrupingFlag = useBpmnEditorStore((s) =>
    getShouldDisplayIsInterruptingFlag(
      s.computed(s).getDiagramData().nodesById.get(startEvent["@_id"])?.data.parentXyFlowNode?.data.bpmnElement,
      startEvent
    )
  );

  return (
    <>
      <PropertiesPanelHeaderFormSection
        title={startEvent["@_name"] || i18n.singleNodeProperties.startEvent}
        icon={<StartEventIcon />}
      >
        <NameDocumentationAndId element={startEvent} />

        <Divider inset={{ default: "insetXs" }} />

        {shouldDisplayIsInterrupingFlag && (
          <Checkbox
            id={"cancel-activity"}
            label={i18n.singleNodeProperties.interrupting}
            isChecked={
              startEvent["@_isInterrupting"] ?? START_EVENT_NODE_ON_EVENT_SUB_PROCESSES_IS_INTERRUPTING_DEFAULT_VALUE
            }
            onChange={(e, isChecked) => {
              bpmnEditorStoreApi.setState((s) => {
                const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
                visitFlowElementsAndArtifacts(process, ({ element }) => {
                  if (element["@_id"] === startEvent["@_id"] && element.__$$element === "startEvent") {
                    element["@_isInterrupting"] = isChecked;
                  }
                });
              });
            }}
          />
        )}

        <EventDefinitionProperties event={startEvent} />
      </PropertiesPanelHeaderFormSection>

      <OutputOnlyAssociationFormSection element={startEvent} />
    </>
  );
}

export function getShouldDisplayIsInterruptingFlag(
  parentBpmnElement: BpmnNodeElement | undefined,
  startEvent: Normalized<BPMN20__tStartEvent> & { __$$element: "startEvent" }
) {
  const isParentNodeAnEventSubProcess =
    !!parentBpmnElement &&
    parentBpmnElement.__$$element === "subProcess" &&
    (parentBpmnElement?.["@_triggeredByEvent"] ?? false);

  return (
    isParentNodeAnEventSubProcess &&
    !!startEvent.eventDefinition?.[0]?.__$$element &&
    startEvent.eventDefinition[0].__$$element !== "compensateEventDefinition" &&
    startEvent.eventDefinition[0].__$$element !== "errorEventDefinition"
  );
}
