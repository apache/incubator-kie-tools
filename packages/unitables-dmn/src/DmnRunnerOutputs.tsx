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
        if (properties["x-dmn-type"]) {
          const dataType = jsonSchemaBridge.getFieldDataType(properties).dataType;
          outputTypeMap.set(name, {
            type: properties.type!,
            dataType,
            name,
            joinedName: name,
          });
        } else {
          const path = properties.$ref?.split("/").slice(1); // remove #
          const data = path?.reduce((acc: any, property: string) => acc[property], jsonSchemaBridge.schema);
          const dataType = jsonSchemaBridge.getFieldDataType(data).dataType;
          if (data.properties) {
            const insideProperties = deepGenerateOutputTypesMapFields(outputTypeMap, data.properties, jsonSchemaBridge);
            outputTypeMap.set(name, {
              type: data.type,
              insideProperties,
              dataType,
              name,
              joinedName: name,
            });
          } else {
            outputTypeMap.set(name, {
              type: data.type,
              dataType,
              name,
              joinedName: name,
            });
          }
        }

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

function deepGenerateOutputTypesMapFields(
  outputTypeMap: Map<string, OutputFields>,
  properties: DmnInputFieldProperties[],
  jsonSchemaBridge: DmnUnitablesJsonSchemaBridge,
  parentName?: string
): OutputFields[] {
  return Object.entries(properties).map(([name, property]: [string, DmnInputFieldProperties]) => {
    if (property["x-dmn-type"]) {
      const dataType = jsonSchemaBridge.getFieldDataType(property).dataType;
      outputTypeMap.set(name, {
        type: property.type!,
        dataType,
        name,
        joinedName: parentName ? `${parentName}-${name}` : name,
      });
      return {
        name,
        type: property.type!,
        width: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
        dataType,
        joinedName: parentName ? `${parentName}-${name}` : name,
      };
    }
    const path = property.$ref?.split("/").slice(1); // remove #
    const field: DmnInputFieldProperties = path?.reduce(
      (acc: Record<string, any>, property: string) => acc[property],
      jsonSchemaBridge.schema
    );
    const dataType = jsonSchemaBridge.getFieldDataType(field).dataType;
    if (field.properties) {
      const insideProperties = deepGenerateOutputTypesMapFields(
        outputTypeMap,
        field.properties,
        jsonSchemaBridge,
        name
      );
      outputTypeMap.set(name, {
        type: field.type!,
        insideProperties,
        dataType,
        name,
        joinedName: name,
      });
      return {
        name,
        type: field.type,
        dataType,
        width: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
        insideProperties,
        joinedName: name,
      } as OutputTypesField;
    } else {
      outputTypeMap.set(name, {
        type: field.type!,
        dataType,
        name,
        joinedName: name,
      });
    }
    return {
      name,
      type: field.type,
      dataType,
      width: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
    } as OutputTypesField;
  });
}

export function isOutputWithInsideProperties(
  toBeDetermined: OutputFields
): toBeDetermined is OutputWithInsideProperties {
  return (toBeDetermined as OutputWithInsideProperties).insideProperties !== undefined;
}
