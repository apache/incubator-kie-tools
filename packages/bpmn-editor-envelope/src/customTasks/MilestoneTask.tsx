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
import "@kie-tools/bpmn-marshaller/dist/drools-extension";
import { CustomTask } from "@kie-tools/bpmn-editor/dist/BpmnEditor";
import { useBpmnEditorStoreApi, useBpmnEditorStore } from "@kie-tools/bpmn-editor/dist/store/StoreContext";
import { PropertiesPanelHeaderFormSection } from "@kie-tools/bpmn-editor/dist/propertiesPanel/singleNodeProperties/_PropertiesPanelHeaderFormSection";
import { NameDocumentationAndId } from "@kie-tools/bpmn-editor/dist/propertiesPanel/nameDocumentationAndId/NameDocumentationAndId";
import { BidirectionalDataMappingFormSection } from "@kie-tools/bpmn-editor/dist/propertiesPanel/dataMapping/DataMappingFormSection";
import { OnEntryAndExitScriptsFormSection } from "@kie-tools/bpmn-editor/dist/propertiesPanel/onEntryAndExitScripts/OnEntryAndExitScriptsFormSection";
import { AsyncCheckbox } from "@kie-tools/bpmn-editor/dist/propertiesPanel/asyncCheckbox/AsyncCheckbox";
import { AdhocAutostartCheckbox } from "@kie-tools/bpmn-editor/dist/propertiesPanel/adhocAutostartCheckbox/AdhocAutostartCheckbox";
import { SlaDueDateInput } from "@kie-tools/bpmn-editor/dist/propertiesPanel/slaDueDate/SlaDueDateInput";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { bpmnEditorEnvelopeI18nDefaults, bpmnEditorEnvelopeI18nDictionaries } from "../i18n";
import { I18n } from "@kie-tools-core/i18n/dist/core";

export const MILESTONE_TASK_ICON = (
  <svg
    width="30"
    height="30"
    viewBox="0 0 30 30"
    xmlns="http://www.w3.org/2000/svg"
    fill="none"
    stroke="black"
    strokeWidth="1"
  >
    <text x="15" y="25" textAnchor="middle" fontSize="24" fontFamily="Arial" fontWeight={"light"}>
      🚩
    </text>
  </svg>
);

export const MilestoneTaskProperties: CustomTask["propertiesPanelComponent"] = ({ task }) => {
  // Note how you can access and modify the entire BPMN Editor state here.
  const i18n = new I18n(bpmnEditorEnvelopeI18nDefaults, bpmnEditorEnvelopeI18nDictionaries).getCurrent();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  const bpmnModelName = useBpmnEditorStore((s) => s.bpmn.model.definitions["@_name"]);

  return (
    <>
      <PropertiesPanelHeaderFormSection
        title={task["@_name"] || i18n.milestone}
        icon={MILESTONE_TASK_ICON}
        shouldStartExpanded={true}
      >
        <NameDocumentationAndId element={task} />

        <AsyncCheckbox element={task} />

        <AdhocAutostartCheckbox element={task} />

        <SlaDueDateInput element={task} />
      </PropertiesPanelHeaderFormSection>

      <BidirectionalDataMappingFormSection element={task} />

      <OnEntryAndExitScriptsFormSection element={task} />
    </>
  );
};

export const MILESTONE_TASK: CustomTask = {
  id: "milestone",
  displayGroup: "Flexible processes",
  displayName: "Milestone",
  displayDescription: "",
  iconSvgElement: MILESTONE_TASK_ICON,
  propertiesPanelComponent: MilestoneTaskProperties,
  matches: (task) => task["@_drools:taskName"] === "Milestone",
  produce: () => ({
    __$$element: "task",
    "@_id": generateUuid(),
    "@_drools:taskName": "Milestone",
    "@_name": "Milestone",
  }),
  dataInputReservedNames: [],
  dataOutputReservedNames: [],
};
