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
  }> {}

export interface BpmnEditorEnvelopeI18n extends BpmnEditorEnvelopeDictionary, CommonI18n {}
