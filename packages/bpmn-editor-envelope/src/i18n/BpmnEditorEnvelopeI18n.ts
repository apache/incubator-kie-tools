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

import { CommonI18n } from "@kie-tools/i18n-common-dictionary";
import { ReferenceDictionary } from "@kie-tools-core/i18n/dist/core";

interface BpmnEditorEnvelopeDictionary
  extends ReferenceDictionary<{
    unselect: string;
    deleteSelection: string;
    selectDeselectAll: string;
    createGroupWrappingSelection: string;
    hideFromDrd: string;
    copyNodes: string;
    cutNodes: string;
    pasteNodes: string;
    openClosePropertiesPanel: string;
    toggleHierarchyHighlights: string;
    selectionUp: string;
    selectionDown: string;
    selectionLeft: string;
    selectionRight: string;
    selectionUpBigDistance: string;
    selectionDownBigDistance: string;
    selectionLeftBigDistance: string;
    selectionRightBigDistance: string;
    focusOnSelection: string;
    resetPositionToOrigin: string;
    rightMouseButton: string;
    holdAndDragtoPan: string;
    holdAndScrollToZoomInOut: string;
    holdAndScrollToNavigateHorizontally: string;
    appendTasknode: string;
    appendGatewayNode: string;
    appendIntermediateCatchEventNode: string;
    appendIntermediateThrowEventNode: string;
    appendTextAnnotationNode: string;
    appendEndEventNode: string;
    milestone: string;
    flexibleProcesses: string;
    unableToOpenFile: string;
    errorMessage: string;
    restService: {
      name: string;
      integration: string;
      description: string;
      url: string;
      urlHelp: string;
      protocol: string;
      protocolHelp: string;
      httpMethodGet: string;
      httpMethodPost: string;
      httpMethodPut: string;
      httpMethodPatch: string;
      httpMethodDelete: string;
      host: string;
      hostHelp: string;
      hostRequiredError: string;
      port: string;
      portHelp: string;
      contentData: string;
      contentDataHelp: string;
      testVariables: string;
      testVariablesHelp: string;
      testVariableMissingError: string;
      variableName: string;
      variableValue: string;
      requestTimeout: string;
      headers: string;
      queryParameters: string;
      authStrategyPropagated: string;
      authStrategyConfigured: string;
      authStrategyNone: string;
      accessTokenStrategy: string;
      restServiceCallTaskId: string;
      restServiceCallTaskIdPlaceholder: string;
      restServiceCallTaskIdRequiredError: string;
      testSection: string;
      moreInfoForTestVariables: string;
      contentDataVariablesTable: string;
      valueForVariable: string;
      testTokenHelper: string;
      testToken: string;
      testTokenPlaceholder: string;
      useCorsProxy: string;
      useCorsProxyAriaLabel: string;
      useCorsProxyHelper: string;
      testRequest: string;
      testing: string;
      testFailed: string;
      testResult: string;
      response: string;
      urlRequired: string;
      headerName: string;
      headerValue: string;
      noHeaders: string;
      queryParameterName: string;
      queryParameterValue: string;
      noQueryParameters: string;
      accessTokenStrategyPropagatedHelp: string;
      accessTokenStrategyConfiguredHelp: string;
      accessTokenStrategyNoneHelp: string;
      protocolConflictError: string;
      hostConflictError: string;
      portConflictError: string;
      testTokenRequiredError: string;
      apiNotAvailableError: string;
      genericTestError: string;
    };
  }> {}

export interface BpmnEditorEnvelopeI18n extends BpmnEditorEnvelopeDictionary, CommonI18n {}
