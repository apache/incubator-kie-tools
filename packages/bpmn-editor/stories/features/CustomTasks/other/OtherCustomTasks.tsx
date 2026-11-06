/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * License); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing;
 * software distributed under the License is distributed on an
 * AS IS BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { CustomTask } from "../../../../src/BpmnEditor";
import { BidirectionalDataMappingFormSection } from "../../../../src/propertiesPanel/dataMapping/DataMappingFormSection";
import { DataMapping, setInputAndOutputDataMapping } from "../../../../src/mutations/_dataMapping";
import { DEFAULT_DATA_TYPES } from "../../../../src/mutations/addOrGetItemDefinitions";
import "@kie-tools/bpmn-marshaller/dist/drools-extension";

export const PropertiesPanelComponent: CustomTask["propertiesPanelComponent"] = ({ task }) => (
  <>
    <span>
      Hello from Custom Task properties panel for <b>{task?.["@_name"]}</b>
    </span>

    <span>This is where the interactive panel will be.</span>
  </>
);

export const REST_API_CALL_TASK: CustomTask = {
  id: "rest-api-call-task",
  displayGroup: "Other",
  displayName: "Rest API call Task",
  displayDescription: "",
  dataInputReservedNames: [],
  dataOutputReservedNames: [],
  iconSvgElement: (
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
        🔅
      </text>
    </svg>
  ),
  propertiesPanelComponent: PropertiesPanelComponent,
  matches: (task) => task["@_drools:taskName"] === "rest-api-call-task",
  produce: () => ({
    __$$element: "task",
    "@_id": generateUuid(),
    "@_drools:taskName": "rest-api-call-task",
    "@_name": "Rest API call Task",
  }),
};

export const GRPC_API_CALL_TASK: CustomTask = {
  id: "grpc-api-call-task",
  displayGroup: "Other",
  displayName: "gRPC API call Task",
  displayDescription: "",
  dataInputReservedNames: [],
  dataOutputReservedNames: [],
  iconSvgElement: (
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
        ✴️
      </text>
    </svg>
  ),
  propertiesPanelComponent: PropertiesPanelComponent,
  matches: (task) => task["@_drools:taskName"] === "grpc-api-call-task",
  produce: () => ({
    __$$element: "task",
    "@_id": generateUuid(),
    "@_drools:taskName": "grpc-api-call-task",
    "@_name": "gRPC API call Task",
  }),
};

export const RESERVED_INPUTS_TASK_NAME = "reserved-inputs-task";

/**
 * Simulates a built-in Custom Task (like Rest or Milestone) where specific Data Mapping inputs
 * are reserved/owned by the task's custom properties panel ("Url", "HEADER_*"),
 * and one regular unreserved input ("Message") is meant to be edited in the Data Mapping modal.
 */
export const RESERVED_INPUTS_TASK: CustomTask = {
  id: RESERVED_INPUTS_TASK_NAME,
  displayGroup: "Other",
  displayName: "Reserved Inputs Task",
  displayDescription: "",
  dataInputReservedNames: ["Url", "HEADER_*"],
  dataOutputReservedNames: [],
  iconSvgElement: (
    <svg width="30" height="30" viewBox="0 0 30 30" xmlns="http://www.w3.org/2000/svg" fill="none">
      <text x="15" y="25" textAnchor="middle" fontSize="24" fontFamily="Arial" fontWeight={"light"}>
        🔒
      </text>
    </svg>
  ),
  propertiesPanelComponent: ({ task }) => <BidirectionalDataMappingFormSection element={task} />,
  matches: (task) => task["@_drools:taskName"] === RESERVED_INPUTS_TASK_NAME,
  produce: () => ({
    __$$element: "task",
    "@_id": generateUuid(),
    "@_drools:taskName": RESERVED_INPUTS_TASK_NAME,
    "@_name": "Reserved Inputs Task",
  }),
  onAdded: (_state, task) => {
    const inputs: DataMapping[] = [
      { dtype: DEFAULT_DATA_TYPES.STRING, name: "Url", isExpression: true, value: "http://localhost" },
      { dtype: DEFAULT_DATA_TYPES.STRING, name: "HEADER_Accept", isExpression: true, value: "text/plain" },
      { dtype: DEFAULT_DATA_TYPES.STRING, name: "Message", isExpression: false, variableRef: undefined },
    ];
    setInputAndOutputDataMapping(new Map(), inputs, [], task);
  },
};

export const WID_LIKE_TASK_NAME = "wid-like-task";

/**
 * Simulates a Custom Task generated from a WID file where no inputs are reserved
 * (`dataInputReservedNames: []`), so all WID parameters must be visible and editable
 * in the Data Mapping modal.
 */
export const WID_LIKE_TASK: CustomTask = {
  id: WID_LIKE_TASK_NAME,
  displayGroup: "Other",
  displayName: "WID Custom Task",
  displayDescription: "",
  dataInputReservedNames: [],
  dataOutputReservedNames: [],
  iconSvgElement: (
    <svg width="30" height="30" viewBox="0 0 30 30" xmlns="http://www.w3.org/2000/svg" fill="none">
      <text x="15" y="25" textAnchor="middle" fontSize="24" fontFamily="Arial" fontWeight={"light"}>
        📋
      </text>
    </svg>
  ),
  propertiesPanelComponent: ({ task }) => <BidirectionalDataMappingFormSection element={task} />,
  matches: (task) => task["@_drools:taskName"] === WID_LIKE_TASK_NAME,
  produce: () => ({
    __$$element: "task",
    "@_id": generateUuid(),
    "@_drools:taskName": WID_LIKE_TASK_NAME,
    "@_name": "WID Custom Task",
  }),
  onAdded: (_state, task) => {
    const inputs: DataMapping[] = [
      { dtype: DEFAULT_DATA_TYPES.STRING, name: "Param1", isExpression: false, variableRef: undefined },
      { dtype: DEFAULT_DATA_TYPES.STRING, name: "Param2", isExpression: false, variableRef: undefined },
    ];
    setInputAndOutputDataMapping(new Map(), inputs, [], task);
  },
};
