/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useMemo } from "react";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { DmnUnitablesJsonSchemaBridge } from "./uniforms/DmnUnitablesJsonSchemaBridge";
import { DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH } from "./DmnRunnerOutputsTable";
import { DecisionResult, DmnInputFieldProperties } from "@kie-tools/extended-services-api";

interface OutputField {
  dataType: DmnBuiltInDataType;
  width?: number;
  name: string;
  joinedName: string;
  properties?: Record<string, any>;
  items?: Record<string, any>[];
  // Save any additional information to use for build the columns and rows;
  infos?: Record<string, any>;
}

interface OutputTypesField extends OutputField {
  type: string;
}

interface OutputWithInsideProperties extends OutputTypesField {
  insideProperties: Array<OutputFields>;
}

export type OutputFields = OutputField | OutputWithInsideProperties;

export function useDmnRunnerOutputs(
  jsonSchemaBridge: DmnUnitablesJsonSchemaBridge,
  results: Array<DecisionResult[] | undefined> | undefined
) {
  return useMemo(() => {
    if (jsonSchemaBridge === undefined || results === undefined) {
      return { outputs: [] as OutputFields[] };
    }

    // generate a map that contains output types
    const outputTypeMap = Object.entries(jsonSchemaBridge.schema.definitions?.OutputSet?.properties ?? []).reduce(
      (outputTypeMap: Map<string, OutputFields>, [name, properties]: [string, DmnInputFieldProperties]) => {
        const dataType = jsonSchemaBridge.getFieldDataType(properties).dataType;
        outputTypeMap.set(name, {
          type: properties.type!,
          dataType,
          name,
          joinedName: name,
          properties: properties.properties,
          items: properties.items,
        });
        return outputTypeMap;
      },
      new Map<string, OutputFields>()
    );

    // generate outputs
    const decisionResults = results?.filter((result) => result !== undefined);
    const outputMap = decisionResults.reduce(
      (outputFieldsMap: Map<string, OutputFields>, decisionResult: DecisionResult[] | undefined) => {
        if (decisionResult) {
          decisionResult.forEach(({ decisionName }) => {
            const outputField = outputTypeMap.get(decisionName);
            if (outputField && isOutputWithInsideProperties(outputField)) {
              outputFieldsMap.set(decisionName, {
                name: decisionName,
                joinedName: decisionName,
                dataType: outputField.dataType ?? DmnBuiltInDataType.Undefined,
                insideProperties: outputField.insideProperties,
                width: outputField.insideProperties.reduce(
                  (outputFieldsMap, column) => outputFieldsMap + (column?.width ?? DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH),
                  0
                ),
              });
            } else {
              outputFieldsMap.set(decisionName, {
                name: decisionName,
                joinedName: decisionName,
                dataType: outputField?.dataType ?? DmnBuiltInDataType.Undefined,
                width: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
              });
            }
          });
        }
        return outputFieldsMap;
      },
      new Map<string, OutputFields>()
    );

    return {
      outputs: [...outputMap.values()],
      outputTypeMap,
    };
  }, [jsonSchemaBridge, results]);
}

export function isOutputWithInsideProperties(
  toBeDetermined: OutputFields
): toBeDetermined is OutputWithInsideProperties {
  return (toBeDetermined as OutputWithInsideProperties).insideProperties !== undefined;
}
