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

import { BPMN20__tDefinitions } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Normalized } from "../normalization/normalize";
import { visitFlowElementsAndArtifacts } from "./_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";
import {
  BUSINESS_RULE_TASK_IMPLEMENTATIONS,
  BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING,
} from "@kie-tools/bpmn-marshaller/dist/drools-extension";
import { getDataMapping, setDataMappingForElement } from "./_dataMapping";

export function deassociateBusinessRuleTaskWithDmnModel({
  definitions,
  __readonly_businessRuleTaskId,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  __readonly_businessRuleTaskId: string;
}): void {
  const { process } = addOrGetProcessAndDiagramElements({ definitions });

  visitFlowElementsAndArtifacts(process, ({ element }) => {
    if (element["@_id"] === __readonly_businessRuleTaskId && element.__$$element === "businessRuleTask") {
      if (element["@_implementation"] !== BUSINESS_RULE_TASK_IMPLEMENTATIONS.dmn) {
        // Doesn't have a DMN association.
        return false; // Will stop visiting;
      }
      const { inputDataMapping, outputDataMapping } = getDataMapping(element);

      const filteredInputDataMapping = inputDataMapping.filter(
        (dm) =>
          dm.name !== BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.MODEL_NAME &&
          dm.name !== BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.FILE_PATH &&
          dm.name !== BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NAMESPACE
      );

      setDataMappingForElement({
        definitions,
        element: element.__$$element,
        elementId: element["@_id"],
        inputDataMapping: filteredInputDataMapping,
        outputDataMapping,
      });

      return false; // Will stop visiting.
    }
  });
}
