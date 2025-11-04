import * as React from "react";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { CustomTask } from "../../../../src/BpmnEditor";
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
