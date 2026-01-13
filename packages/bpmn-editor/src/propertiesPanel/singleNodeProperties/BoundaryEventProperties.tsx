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
import { BOUNDARY_EVENT_CANCEL_ACTIVITY_DEFAULT_VALUE } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/Bpmn20Spec";
import { BPMN20__tBoundaryEvent } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Normalized } from "../../normalization/normalize";
import { useBpmnEditorStoreApi } from "../../store/StoreContext";
import { NameDocumentationAndId } from "../nameDocumentationAndId/NameDocumentationAndId";
import { EventDefinitionProperties } from "../eventDefinition/EventDefinitionProperties";
import { OutputOnlyAssociationFormSection } from "../dataMapping/DataMappingFormSection";
import { PropertiesPanelHeaderFormSection } from "./_PropertiesPanelHeaderFormSection";
import { IntermediateCatchEventIcon } from "../../diagram/nodes/NodeIcons";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { useBpmnEditorI18n } from "../../i18n";

export function BoundaryEventProperties({
  boundaryEvent,
}: {
  boundaryEvent: Normalized<BPMN20__tBoundaryEvent> & { __$$element: "boundaryEvent" };
}) {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  return (
    <>
      <PropertiesPanelHeaderFormSection
        title={boundaryEvent["@_name"] || i18n.singleNodeProperties.boundaryEvent}
        icon={<IntermediateCatchEventIcon variant={boundaryEvent.eventDefinition?.[0]?.__$$element} />}
      >
        <NameDocumentationAndId element={boundaryEvent} />

        <Divider inset={{ default: "insetXs" }} />

        <Checkbox
          id={"cancel-activity"}
          label={i18n.singleNodeProperties.cancelActivity}
          isChecked={boundaryEvent["@_cancelActivity"] ?? BOUNDARY_EVENT_CANCEL_ACTIVITY_DEFAULT_VALUE}
          onChange={(e, isChecked) => {
            bpmnEditorStoreApi.setState((s) => {
              const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
              visitFlowElementsAndArtifacts(process, ({ element }) => {
                if (element["@_id"] === boundaryEvent["@_id"] && element.__$$element === "boundaryEvent") {
                  element["@_cancelActivity"] = isChecked;
                }
              });
            });
          }}
        />

        <EventDefinitionProperties event={boundaryEvent} />
      </PropertiesPanelHeaderFormSection>

      <OutputOnlyAssociationFormSection element={boundaryEvent} />
    </>
  );
}
