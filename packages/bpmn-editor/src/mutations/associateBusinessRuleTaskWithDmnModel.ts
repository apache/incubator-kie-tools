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

import {
  BPMN20__tBusinessRuleTask,
  BPMN20__tDefinitions,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Normalized } from "../normalization/normalize";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";
import { visitFlowElementsAndArtifacts } from "./_elementVisitor";
import { addOrGetItemDefinitions, DEFAULT_DATA_TYPES } from "./addOrGetItemDefinitions";
import {
  BUSINESS_RULE_TASK_IMPLEMENTATIONS,
  BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING,
} from "@kie-tools/bpmn-marshaller/dist/drools-extension";
import { getDataMapping, setDataMappingForElement, updateDataMappingWithValue } from "./_dataMapping";

/* 
E.g.,
    <bpmn2:ioSpecification>
        <bpmn2:dataInput id="_E1ECA7DC-C0D8-41FD-9E6B-C5E3213D7EEE_fileNameInputX" drools:dtype="java.lang.String" itemSubjectRef="__E1ECA7DC-C0D8-41FD-9E6B-C5E3213D7EEE_fileNameInputXItem" name="fileName"/>
        <bpmn2:dataInput id="_E1ECA7DC-C0D8-41FD-9E6B-C5E3213D7EEE_namespaceInputX" drools:dtype="java.lang.String" itemSubjectRef="__E1ECA7DC-C0D8-41FD-9E6B-C5E3213D7EEE_namespaceInputXItem" name="namespace"/>
        <bpmn2:dataInput id="_E1ECA7DC-C0D8-41FD-9E6B-C5E3213D7EEE_modelInputX" drools:dtype="java.lang.String" itemSubjectRef="__E1ECA7DC-C0D8-41FD-9E6B-C5E3213D7EEE_modelInputXItem" name="model"/>
        <bpmn2:inputSet>
            <bpmn2:dataInputRefs>_E1ECA7DC-C0D8-41FD-9E6B-C5E3213D7EEE_fileNameInputX</bpmn2:dataInputRefs>
            <bpmn2:dataInputRefs>_E1ECA7DC-C0D8-41FD-9E6B-C5E3213D7EEE_namespaceInputX</bpmn2:dataInputRefs>
            <bpmn2:dataInputRefs>_E1ECA7DC-C0D8-41FD-9E6B-C5E3213D7EEE_modelInputX</bpmn2:dataInputRefs>
        </bpmn2:inputSet>
    </bpmn2:ioSpecification>

    <bpmn2:dataInputAssociation>
        <bpmn2:targetRef>_E1ECA7DC-C0D8-41FD-9E6B-C5E3213D7EEE_fileNameInputX</bpmn2:targetRef>
        <bpmn2:assignment>
            <bpmn2:from xsi:type="bpmn2:tFormalExpression"><![CDATA[Sample.dmn]]></bpmn2:from>
            <bpmn2:to xsi:type="bpmn2:tFormalExpression"><![CDATA[_E1ECA7DC-C0D8-41FD-9E6B-C5E3213D7EEE_fileNameInputX]]></bpmn2:to>
        </bpmn2:assignment>
    </bpmn2:dataInputAssociation>

    <bpmn2:dataInputAssociation>
        <bpmn2:targetRef>_E1ECA7DC-C0D8-41FD-9E6B-C5E3213D7EEE_namespaceInputX</bpmn2:targetRef>
        <bpmn2:assignment>
            <bpmn2:from xsi:type="bpmn2:tFormalExpression"><![CDATA[https://kie.apache.org/dmn/_857FE424-BEDA-4772-AB8E-2F4CDDB864AB]]></bpmn2:from>
            <bpmn2:to xsi:type="bpmn2:tFormalExpression"><![CDATA[_E1ECA7DC-C0D8-41FD-9E6B-C5E3213D7EEE_namespaceInputX]]></bpmn2:to>
        </bpmn2:assignment>
    </bpmn2:dataInputAssociation>

    <bpmn2:dataInputAssociation>
        <bpmn2:targetRef>_E1ECA7DC-C0D8-41FD-9E6B-C5E3213D7EEE_modelInputX</bpmn2:targetRef>
        <bpmn2:assignment>
            <bpmn2:from xsi:type="bpmn2:tFormalExpression"><![CDATA[loan_pre_qualification]]></bpmn2:from>
            <bpmn2:to xsi:type="bpmn2:tFormalExpression"><![CDATA[_E1ECA7DC-C0D8-41FD-9E6B-C5E3213D7EEE_modelInputX]]></bpmn2:to>
        </bpmn2:assignment>
    </bpmn2:dataInputAssociation>
*/

export function associateBusinessRuleTaskWithDmnModel({
  definitions,
  __readonly_businessRuleTaskId,
  __readonly_dmnModel,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  __readonly_businessRuleTaskId: string;
  __readonly_dmnModel: {
    normalizedPosixPathRelativeToTheOpenFile: string;
    namespace: string;
    name: string;
  };
}): void {
  const { process } = addOrGetProcessAndDiagramElements({ definitions });

  // Make sure String data type is available
  addOrGetItemDefinitions({ definitions, dataType: DEFAULT_DATA_TYPES.STRING });

  visitFlowElementsAndArtifacts(process, ({ element }) => {
    if (element["@_id"] === __readonly_businessRuleTaskId && element.__$$element === "businessRuleTask") {
      const { inputDataMapping, outputDataMapping } = getDataMapping(element);

      updateDataMappingWithValue(
        inputDataMapping,
        __readonly_dmnModel.name,
        BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.MODEL_NAME
      );
      updateDataMappingWithValue(
        inputDataMapping,
        __readonly_dmnModel.normalizedPosixPathRelativeToTheOpenFile,
        BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.FILE_PATH
      );
      updateDataMappingWithValue(
        inputDataMapping,
        __readonly_dmnModel.namespace,
        BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NAMESPACE
      );

      setDataMappingForElement({
        definitions,
        element: element.__$$element,
        elementId: element["@_id"],
        inputDataMapping,
        outputDataMapping,
      });

      return false; // Will stop visiting.
    }
  });
}

export function getDmnModelBinding(
  businessRuleTask: Normalized<BPMN20__tBusinessRuleTask> & { __$$element: "businessRuleTask" }
) {
  if (businessRuleTask["@_implementation"] !== BUSINESS_RULE_TASK_IMPLEMENTATIONS.dmn) {
    return undefined;
  }

  const { inputDataMapping } = getDataMapping(businessRuleTask);

  return {
    normalizedPosixPathRelativeToTheOpenFile: inputDataMapping
      .filter((dm) => dm.isExpression)
      .find((dm) => dm.name === BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.FILE_PATH)
      ?.value,
    modelNamespace: inputDataMapping
      .filter((dm) => dm.isExpression)
      .find((dm) => dm.name === BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NAMESPACE)
      ?.value,
    modelName: inputDataMapping
      .filter((dm) => dm.isExpression)
      .find((dm) => dm.name === BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.MODEL_NAME)
      ?.value,
  };
}
