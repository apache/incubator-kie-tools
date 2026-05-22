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

import { BpmnEditorEnvelopeI18n } from "..";
import { en as en_common } from "@kie-tools/i18n-common-dictionary";

export const en: BpmnEditorEnvelopeI18n = {
  ...en_common,
  unselect: "Unselect",
  deleteSelection: "Delete selection",
  selectDeselectAll: "Select/Deselect all",
  createGroupWrappingSelection: "Create group wrapping selection",
  hideFromDrd: "Hide from DRD",
  copyNodes: "Copy nodes",
  cutNodes: "Cut nodes",
  pasteNodes: "Paste nodes",
  openClosePropertiesPanel: "Open/Close properties panel",
  toggleHierarchyHighlights: "Toggle hierarchy highlights",
  selectionUp: "Move selection up",
  selectionDown: "Move selection down",
  selectionRight: "Move selection right",
  selectionLeft: "Move selection left",
  selectionUpBigDistance: "Move selection up a big distance",
  selectionDownBigDistance: "Move selection down a big distance",
  selectionLeftBigDistance: "Move selection left a big distance",
  selectionRightBigDistance: "Move selection right a big distance",
  focusOnSelection: "Focus on selection",
  resetPositionToOrigin: "Reset position to origin",
  rightMouseButton: "Right Mouse Button",
  holdAndDragtoPan: "Hold and drag to Pan",
  holdAndScrollToZoomInOut: "Hold and scroll to zoom in/out",
  holdAndScrollToNavigateHorizontally: "Hold and scroll to navigate horizontally",
  appendTasknode: "Append Task node",
  appendGatewayNode: "Append Gateway node",
  appendIntermediateCatchEventNode: "Append Intermediate Catch event node",
  appendIntermediateThrowEventNode: "Append Intermediate Throw event node",
  appendEndEventNode: "Append End event node",
  appendTextAnnotationNode: "Append Text Annotation node",
  milestone: "Milestone",
  flexibleProcesses: "Flexible processes",
  unableToOpenFile: "Unable to open file.",
  errorMessage: "Error details: ",
  restService: {
    name: "Rest Service Task",
    integration: "Integration",
    description: "Call a REST API endpoint",
    url: "URL",
    urlHelp: "The complete URL or path to the REST endpoint",
    urlPlaceholder: "https://api.example.com/endpoint",
    method: "Method",
    protocol: "Protocol",
    protocolHelp: "Alternative to full URL (e.g., http, https)",
    protocolPlaceholder: "https",
    httpMethodGet: "GET",
    httpMethodPost: "POST",
    httpMethodPut: "PUT",
    httpMethodPatch: "PATCH",
    httpMethodDelete: "DELETE",
    host: "Host",
    hostHelp: "Alternative to full URL",
    hostPlaceholder: "api.example.com",
    hostRequiredError: "Host is required when URL is not complete",
    port: "Port",
    portHelp: "Alternative to full URL",
    portPlaceholder: "443",
    contentData: "Content Data",
    contentDataPlaceholder: '{"key": "value"} or use {variableName} for process variables',
    contentDataHelp:
      "Request payload for POST, PUT, and PATCH requests. Use {variableName} to reference process variables or provide literal values. Supports JSON objects, strings, or expressions like #{myPayload}.",
    contentDataHelperText: "Literal JSON, process variable #{var}, or {{var}} for test interpolation",
    testVariables: "Test Variables",
    testVariableMissingError: "Value for variable is missing. Please enter a value.",
    testVariablesHelp:
      "Define test values for variables referenced in Content Data (e.g., {variableName}). These are used only for testing and not saved in the BPMN.",
    addTestVariable: "Add Test Variable",
    variableName: "Variable Name",
    variableValue: "Mock Value",
    variableNamePlaceholder: "variableName",
    variableValuePlaceholder: "mock value",
    noTestVariables: "No test variables defined",
    requestTimeout: "Request Timeout (ms)",
    headers: "Headers",
    addHeader: "Add Header",
    queryParameters: "Query Parameters",
    addQueryParameter: "Add Query Parameter",
    authStrategy: "Authentication Strategy",
    authStrategyPropagated: "Propagated",
    authStrategyConfigured: "Configured",
    authStrategyNone: "None",
    accessTokenStrategy: "Access Token Acquisition Strategy",
    restServiceCallTaskId: "REST Service Call Task ID",
    restServiceCallTaskIdPlaceholder: "Enter task ID",
    testToken: "Test Token",
    testTokenPlaceholder: "Enter Bearer token for testing",
    testTokenHelper: "For testing only. Not saved in BPMN.",
    useCorsProxy: "Use CORS Proxy",
    useCorsProxyAriaLabel: "Use CORS proxy",
    useCorsProxyHelper: "Enable CORS proxy to bypass cross-origin restrictions during testing",
    testRequest: "Test Request",
    testing: "Testing...",
    testFailed: "Test Failed",
    testResult: "Test Result",
    urlRequired: "URL is required",
    headerName: "Name",
    headerValue: "Value",
    headerNamePlaceholder: "Name",
    headerValuePlaceholder: "Value",
    noHeaders: "No headers defined",
    queryParameterName: "Name",
    queryParameterValue: "Value",
    noQueryParameters: "No query parameters defined",
    accessTokenStrategyPropagatedHelp:
      "Access Token is taken from the current thread, potentially coming from human-originated interactions such as completing a User Task.",
    accessTokenStrategyConfiguredHelp:
      "Access Token is taken from the application.properties configuration using the RestServiceCallTaskId.",
    accessTokenStrategyNoneHelp: "The request is sent without any authentication.",
  },
};
