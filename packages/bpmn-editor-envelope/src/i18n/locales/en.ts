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
    urlHelp: "The complete URL or path to the REST endpoint (e.g., https://api.example.com/endpoint)",
    protocol: "Protocol",
    protocolHelp: "Alternative to full URL (e.g., http, https)",
    httpMethodGet: "GET",
    httpMethodPost: "POST",
    httpMethodPut: "PUT",
    httpMethodPatch: "PATCH",
    httpMethodDelete: "DELETE",
    host: "Host",
    hostHelp: "Alternative to full URL (e.g., api.example.com)",
    hostRequiredError: "Host is required when URL is not complete",
    port: "Port",
    portHelp: "Alternative to full URL (e.g., 443)",
    contentData: "Content Data",
    contentDataHelp: "Literal, JSON or process variable #{var}",
    testVariables: "Test Variables",
    testVariablesHelp: "Provide test values for process variables used in the content data for the test request.",
    testVariableMissingError: "Value for variable is missing. Please enter a value.",
    variableName: "Variable Name",
    variableValue: "Test Value",
    requestTimeout: "Request Timeout (ms)",
    headers: "Headers",
    queryParameters: "Query Parameters",
    authStrategyPropagated: "Propagated",
    authStrategyConfigured: "Configured",
    authStrategyNone: "None",
    accessTokenStrategy: "Access Token Acquisition Strategy",
    restServiceCallTaskId: "REST Service Call Task ID",
    restServiceCallTaskIdPlaceholder: "Enter task ID",
    restServiceCallTaskIdRequiredError: "Task ID is required when Access Token Strategy is Configured",
    testSection: "Test",
    moreInfoForTestVariables: "More info for test variables",
    contentDataVariablesTable: "Content data variables table",
    valueForVariable: "Value for {{variableName}}",
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
    response: "Test Response",
    urlRequired: "URL is required",
    headerName: "Name",
    headerValue: "Value",
    noHeaders: "No headers defined",
    queryParameterName: "Name",
    queryParameterValue: "Value",
    noQueryParameters: "No query parameters defined",
    accessTokenStrategyPropagatedHelp:
      "Access Token is taken from the current thread, potentially coming from human-originated interactions such as completing a User Task.",
    accessTokenStrategyConfiguredHelp:
      "Access Token is taken from the application.properties configuration using the RestServiceCallTaskId.",
    accessTokenStrategyNoneHelp: "The request is sent without any authentication.",
    protocolConflictError: "Protocol conflict: URL already defines protocol",
    hostConflictError: "Host conflict: URL already defines host",
    portConflictError: "Port conflict: URL already defines port",
    testTokenRequiredError: "Test token is required for authentication strategy",
    apiNotAvailableError: "REST task test API is not available. Please ensure the editor is properly initialized.",
    genericTestError: "An error occurred while testing the REST call",
  },
};
