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
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { BpmnEditorEnvelopeI18n } from "../i18n";
import { RestServiceTaskPropertiesPanel } from "./RestServiceTaskPropertiesPanel";
import { DataMapping, setInputAndOutputDataMapping } from "@kie-tools/bpmn-editor/dist/mutations/_dataMapping";
import { DEFAULT_DATA_TYPES } from "@kie-tools/bpmn-editor/dist/mutations/addOrGetItemDefinitions";
import { RestProperties, REST_PROPERTIES_KEYS, REST_TASK_ICON } from "./RestServiceTaskConstants";

export function getRestServiceTask(i18n: BpmnEditorEnvelopeI18n): CustomTask {
  return {
    id: "rest-task",
    displayGroup: i18n.restService.integration,
    displayName: i18n.restService.name,
    displayDescription: i18n.restService.description,
    iconSvgElement: REST_TASK_ICON,
    propertiesPanelComponent: RestServiceTaskPropertiesPanel,
    matches: (task) => task["@_drools:taskName"] === "Rest",
    produce: () => ({
      __$$element: "task",
      "@_id": generateUuid(),
      "@_drools:taskName": "Rest",
      "@_name": i18n.restService.name,
    }),
    onAdded: (state, task) => {
      const inputs: DataMapping[] = [
        {
          dtype: DEFAULT_DATA_TYPES.STRING,
          name: RestProperties.Method,
          isExpression: true,
          value: "GET",
        },
        {
          dtype: DEFAULT_DATA_TYPES.STRING,
          name: RestProperties.Url,
          isExpression: true,
          value: "",
        },
        {
          dtype: DEFAULT_DATA_TYPES.STRING,
          name: RestProperties.AccessTokenAcquisitionStrategy,
          isExpression: true,
          value: "none",
        },
      ];
      const outputs: DataMapping[] = [
        {
          dtype: DEFAULT_DATA_TYPES.STRING,
          name: "Result",
          isExpression: false,
          variableRef: undefined,
        },
      ];
      const itemDefinitionIdByDataTypes = new Map(
        state.bpmn.model.definitions.rootElement
          ?.filter((r) => r.__$$element === "itemDefinition")
          .map((i) => [i["@_structureRef"]!, i["@_id"]])
      );
      setInputAndOutputDataMapping(itemDefinitionIdByDataTypes, inputs, outputs, task);
    },
    dataInputReservedNames: ["TaskName", ...REST_PROPERTIES_KEYS],
    dataOutputReservedNames: [],
  };
}
