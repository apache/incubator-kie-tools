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

import { BPMN20__tDefinitions, BPMN20__tProcess } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Normalized } from "../normalization/normalize";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import {
  BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING,
  USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING,
} from "@kie-tools/bpmn-marshaller/dist/drools-extension";

export type Task = Normalized<
  ElementFilter<
    Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
    | "task"
    | "scriptTask"
    | "serviceTask"
    | "businessRuleTask"
    | "userTask"
    | "callActivity"
    | "subProcess"
    | "adHocSubProcess"
  >
>;

export type UserTaskReservedDataMappingInputNames =
  | typeof USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.TASK_NAME
  | typeof USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.SKIPPABLE
  | typeof USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.GROUP_ID
  | typeof USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.COMMENT
  | typeof USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.DESCRIPTION
  | typeof USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.PRIORITY
  | typeof USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.CREATED_BY
  | typeof USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.CONTENT
  | typeof USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_REASSIGN
  | typeof USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_COMPLETED_REASSIGN
  | typeof USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_NOTIFY
  | typeof USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_COMPLETELY_NOTIFY
  | typeof USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.MULTI_INSTANCE_ITEM_TYPE;

export type BusinessRuleTaskReservedDataMappingInputNames =
  | typeof BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.FILE_PATH
  | typeof BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NAMESPACE
  | typeof BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.MODEL_NAME;

export type MultiInstanceTaskReservedDataMappingInputNames =
  | typeof MULTI_INSTANCE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS.IN_COLLECTION
  | typeof MULTI_INSTANCE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS.OUT_COLLECTION;

export const MULTI_INSTANCE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS = {
  IN_COLLECTION: "IN_COLLECTION",
  OUT_COLLECTION: "OUT_COLLECTION",
};

export const DATA_INPUT_RESERVED_NAMES = new Map<Task["__$$element"], Set<string>>([
  [
    "businessRuleTask",
    new Set([
      BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.FILE_PATH,
      BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NAMESPACE,
      BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.MODEL_NAME,
    ]),
  ],
  [
    "userTask",
    new Set([
      USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.TASK_NAME,
      USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.SKIPPABLE,
      USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.GROUP_ID,
      USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.COMMENT,
      USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.DESCRIPTION,
      USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.PRIORITY,
      USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.CREATED_BY,
      USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.CONTENT,
      USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_REASSIGN,
      USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_COMPLETED_REASSIGN,
      USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_NOTIFY,
      USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_COMPLETELY_NOTIFY,
      USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.MULTI_INSTANCE_ITEM_TYPE,
    ]),
  ],
]);

export type WithDataMapping = Normalized<
  ElementFilter<
    Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
    | "callActivity"
    | "businessRuleTask"
    | "userTask"
    | "task"
    | "serviceTask"
    | "scriptTask"
    | "adHocSubProcess"
    | "subProcess"
  >
>;

export type WithInputDataMapping = Normalized<
  ElementFilter<Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>, "endEvent" | "intermediateThrowEvent">
>;

export type WithOutputDataMapping = Normalized<
  ElementFilter<
    Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
    "startEvent" | "intermediateCatchEvent" | "boundaryEvent"
  >
>;

export type DataMapping = {
  name: string;
  dtype: string;
} & (
  | {
      isExpression: true;
      value: string;
    }
  | {
      isExpression: false;
      variableRef: string | undefined;
    }
);

export function getDataMapping(element: WithDataMapping | WithInputDataMapping | WithOutputDataMapping) {
  let inputDataMapping: DataMapping[] = [];
  let outputDataMapping: DataMapping[] = [];

  if (
    element.__$$element === "callActivity" ||
    element.__$$element === "businessRuleTask" ||
    element.__$$element === "userTask" ||
    element.__$$element === "task" ||
    element.__$$element === "serviceTask" ||
    element.__$$element === "scriptTask" ||
    element.__$$element === "endEvent" ||
    element.__$$element === "intermediateThrowEvent"
  ) {
    const dataInputs =
      element.__$$element === "endEvent" || element.__$$element === "intermediateThrowEvent"
        ? element.dataInput
        : element.ioSpecification?.dataInput;

    inputDataMapping =
      dataInputs?.flatMap((dataInput) => {
        const association = element.dataInputAssociation?.find(
          (association) => association.targetRef?.__$$text === dataInput["@_id"]
        );

        return association?.assignment
          ? {
              name: dataInput?.["@_name"] || "",
              dtype: dataInput?.["@_drools:dtype"] || "",
              isExpression: true,
              value: association.assignment?.[0]?.from.__$$text || "",
            }
          : {
              name: dataInput?.["@_name"] || "",
              dtype: dataInput?.["@_drools:dtype"] || "",
              isExpression: false,
              variableRef: association?.sourceRef?.[0]?.__$$text,
            };
      }) ?? [];
  }

  if (
    element.__$$element === "callActivity" ||
    element.__$$element === "businessRuleTask" ||
    element.__$$element === "userTask" ||
    element.__$$element === "task" ||
    element.__$$element === "serviceTask" ||
    element.__$$element === "scriptTask" ||
    element.__$$element === "startEvent" ||
    element.__$$element === "intermediateCatchEvent" ||
    element.__$$element === "boundaryEvent"
  ) {
    const dataOutputs =
      element.__$$element === "startEvent" ||
      element.__$$element === "intermediateCatchEvent" ||
      element.__$$element === "boundaryEvent"
        ? element.dataOutput
        : element.ioSpecification?.dataOutput;

    outputDataMapping =
      dataOutputs?.flatMap((dataOutput) => {
        const association = element.dataOutputAssociation?.find(
          (association) => association.sourceRef?.[0]?.__$$text === dataOutput["@_id"]
        );

        return association?.assignment
          ? {
              name: dataOutput?.["@_name"] || "",
              dtype: dataOutput?.["@_drools:dtype"] || "",
              isExpression: true,
              value: association?.assignment?.[0]?.to.__$$text || "",
            }
          : {
              name: dataOutput?.["@_name"] || "",
              dtype: dataOutput?.["@_drools:dtype"] || "",
              isExpression: false,
              variableRef: association?.targetRef.__$$text,
            };
      }) ?? [];
  }
  return { inputDataMapping, outputDataMapping };
}

import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";
import { visitFlowElementsAndArtifacts } from "./_elementVisitor";
import { DEFAULT_DATA_TYPES } from "./addOrGetItemDefinitions";

export function setInputDataMapping(
  itemDefinitionIdByDataTypes: Map<string, string>,
  inputDataMapping: DataMapping[],
  elementWithData: WithInputDataMapping | NonNullable<WithDataMapping["ioSpecification"]>,
  elementWithAssociation: WithInputDataMapping | WithDataMapping
) {
  elementWithData.dataInput = [];
  elementWithAssociation.dataInputAssociation = [];

  inputDataMapping.forEach((dataMapping) => {
    const dataInput: Unpacked<(typeof elementWithData)["dataInput"]> = {
      "@_id": generateUuid(),
      "@_name": dataMapping.name,
      "@_drools:dtype": dataMapping.dtype,
      "@_itemSubjectRef": itemDefinitionIdByDataTypes.get(dataMapping.dtype),
    };

    elementWithData.dataInput?.push(dataInput);

    elementWithAssociation.dataInputAssociation?.push(
      dataMapping.isExpression
        ? {
            "@_id": generateUuid(),
            targetRef: { __$$text: dataInput["@_id"] },
            sourceRef: [],
            assignment: [
              {
                "@_id": generateUuid(),
                from: {
                  "@_id": generateUuid(),
                  "@_xsi:type": "tFormalExpression",
                  __$$text: dataMapping.value,
                } as any,
                to: { "@_id": generateUuid(), __$$text: dataInput["@_id"] },
              },
            ],
          }
        : {
            "@_id": generateUuid(),
            targetRef: { __$$text: dataInput["@_id"] },
            sourceRef: [{ __$$text: dataMapping.variableRef || "" }],
          }
    );
  });
}

export function setOutputDataMapping(
  itemDefinitionIdByDataTypes: Map<string, string>,
  outputDataMapping: DataMapping[],
  elementWithData: WithOutputDataMapping | NonNullable<WithDataMapping["ioSpecification"]>,
  elementWithAssociation: WithOutputDataMapping | WithDataMapping
) {
  elementWithData.dataOutput = [];
  elementWithAssociation.dataOutputAssociation = [];

  outputDataMapping.forEach((dataMapping) => {
    const dataOutput = {
      "@_name": dataMapping.name,
      "@_id": generateUuid(),
      "@_drools:dtype": dataMapping.dtype,
      "@_itemSubjectRef": itemDefinitionIdByDataTypes.get(dataMapping.dtype),
    };

    elementWithData.dataOutput?.push(dataOutput);

    elementWithAssociation.dataOutputAssociation?.push(
      dataMapping.isExpression
        ? {
            "@_id": generateUuid(),
            targetRef: { __$$text: "" }, // Empty on purpose. There's no targetRef here.
            sourceRef: [{ __$$text: dataOutput["@_id"] }],
            assignment: [
              {
                "@_id": generateUuid(),
                from: { "@_id": generateUuid(), __$$text: dataOutput["@_id"] },
                to: {
                  "@_id": generateUuid(),
                  "@_xsi:type": "tFormalExpression",
                  __$$text: dataMapping.value,
                } as any,
              },
            ],
          }
        : {
            "@_id": generateUuid(),
            targetRef: { __$$text: dataMapping.variableRef || "" },
            sourceRef: [{ __$$text: dataOutput["@_id"] }],
          }
    );
  });
}

export function setInputAndOutputDataMapping(
  itemDefinitionIdByDataTypes: Map<string, string>,
  inputDataMapping: DataMapping[],
  outputDataMapping: DataMapping[],
  e: WithDataMapping
) {
  e.ioSpecification = {
    "@_id": generateUuid(),
    inputSet: [],
    outputSet: [],
    dataInput: [],
    dataOutput: [],
  };

  // input
  setInputDataMapping(itemDefinitionIdByDataTypes, inputDataMapping, e.ioSpecification, e);

  e.ioSpecification.inputSet[0] ??= {
    "@_id": generateUuid(),
    dataInputRefs: [],
  };

  for (let i = 0; i < (e.ioSpecification.dataInput ?? []).length; i++) {
    const dataInput = e.ioSpecification.dataInput![i];
    e.ioSpecification.inputSet[0].dataInputRefs!.push({ __$$text: dataInput["@_id"] });
  }

  // output
  setOutputDataMapping(itemDefinitionIdByDataTypes, outputDataMapping, e.ioSpecification, e);

  e.ioSpecification!.outputSet[0] ??= {
    "@_id": generateUuid(),
    dataOutputRefs: [],
  };

  for (let i = 0; i < (e.ioSpecification.dataOutput ?? []).length; i++) {
    const dataOutput = e.ioSpecification.dataOutput![i];
    e.ioSpecification.outputSet[0].dataOutputRefs!.push({ __$$text: dataOutput["@_id"] });
  }
}

export function setDataMappingForElement({
  definitions,
  elementId,
  element,
  inputDataMapping,
  outputDataMapping,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  elementId: string;
  element: string;
  inputDataMapping: DataMapping[];
  outputDataMapping: DataMapping[];
}) {
  const { process } = addOrGetProcessAndDiagramElements({ definitions });

  const itemDefinitionIdByDataTypes = new Map(
    definitions.rootElement
      ?.filter((r) => r.__$$element === "itemDefinition")
      .map((i) => [i["@_structureRef"]!, i["@_id"]])
  );

  visitFlowElementsAndArtifacts(process, ({ element: e }) => {
    if (e["@_id"] === elementId && e.__$$element === element) {
      if (
        e.__$$element === "callActivity" ||
        e.__$$element === "businessRuleTask" ||
        e.__$$element === "userTask" ||
        e.__$$element === "task" ||
        e.__$$element === "serviceTask" ||
        e.__$$element === "scriptTask"
      ) {
        setInputAndOutputDataMapping(itemDefinitionIdByDataTypes, inputDataMapping, outputDataMapping, e);
      } else if (e.__$$element === "endEvent" || e.__$$element === "intermediateThrowEvent") {
        setInputDataMapping(itemDefinitionIdByDataTypes, inputDataMapping, e, e);
      } else if (
        e.__$$element === "startEvent" ||
        e.__$$element === "intermediateCatchEvent" ||
        e.__$$element === "boundaryEvent"
      ) {
        setOutputDataMapping(itemDefinitionIdByDataTypes, outputDataMapping, e, e);
      }

      return false; // Will stop visiting.
    }
  });
}

function updateDataMappingArray(dataMappingArray: DataMapping[], index: number, dataMappingUpdate: DataMappingUpdate) {
  if (dataMappingUpdate.nonEmpty) {
    if (index > -1) {
      dataMappingArray[index] = dataMappingUpdate.dataMapping;
    } else {
      dataMappingArray.push(dataMappingUpdate.dataMapping);
    }
  } else if (index > -1) {
    dataMappingArray.splice(index, 1);
  }
}

export function updateDataMappingWithValue<
  T extends
    | BusinessRuleTaskReservedDataMappingInputNames
    | UserTaskReservedDataMappingInputNames
    | MultiInstanceTaskReservedDataMappingInputNames,
>(dataMappingArray: DataMapping[], value: string | undefined, name: T) {
  const index = dataMappingArray.findIndex((d) => d.name === name);
  const dataMappingUpdate = valueToDataMapping(value, name);

  updateDataMappingArray(dataMappingArray, index, dataMappingUpdate);
}

export function updateDataMappingWithVariableRef<
  T extends
    | BusinessRuleTaskReservedDataMappingInputNames
    | UserTaskReservedDataMappingInputNames
    | MultiInstanceTaskReservedDataMappingInputNames,
>(dataMappingArray: DataMapping[], value: string | undefined, name: T) {
  const index = dataMappingArray.findIndex((d) => d.name === name);
  const dataMappingUpdate = variableRefToDataMapping(value, name);

  updateDataMappingArray(dataMappingArray, index, dataMappingUpdate);
}

type DataMappingUpdate =
  | {
      nonEmpty: false;
      dataMapping: undefined;
    }
  | {
      nonEmpty: true;
      dataMapping: DataMapping;
    };

export function variableRefToDataMapping<
  T extends
    | BusinessRuleTaskReservedDataMappingInputNames
    | UserTaskReservedDataMappingInputNames
    | MultiInstanceTaskReservedDataMappingInputNames,
>(variableRef: string | undefined, type: T): DataMappingUpdate {
  return variableRef === undefined
    ? {
        nonEmpty: false,
        dataMapping: undefined,
      }
    : {
        nonEmpty: true,
        dataMapping: {
          dtype: DEFAULT_DATA_TYPES.OBJECT,
          name: type,
          isExpression: false,
          variableRef,
        },
      };
}

export function valueToDataMapping<
  T extends
    | BusinessRuleTaskReservedDataMappingInputNames
    | UserTaskReservedDataMappingInputNames
    | MultiInstanceTaskReservedDataMappingInputNames,
>(value: string | undefined, type: T): DataMappingUpdate {
  return value === undefined
    ? {
        nonEmpty: false,
        dataMapping: undefined,
      }
    : {
        nonEmpty: true,
        dataMapping: {
          dtype: DEFAULT_DATA_TYPES.STRING,
          name: type,
          isExpression: true,
          value,
        },
      };
}
