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
import { useCallback, useMemo } from "react";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import * as ReactTable from "react-table";
import { DmnUnitablesJsonSchemaBridge } from "./uniforms/DmnUnitablesJsonSchemaBridge";
import { UnitablesOutputRows } from "@kie-tools/unitables/dist/UnitablesTypes";
import { DecisionResult, DmnSchemaProperties, Result } from "./DmnTypes";
import { CELL_MINIMUM_WIDTH } from "@kie-tools/unitables/dist/bee";

interface OutputField {
  dataType: DmnBuiltInDataType;
  width?: number;
  name: string;
}

interface OutputTypesField extends OutputField {
  type: string;
}

interface OutputWithInsideProperties extends OutputTypesField {
  insideProperties: Array<OutputTypesField>;
}

type OutputTypesFields = OutputTypesField | OutputWithInsideProperties;
type OutputFields = OutputField | OutputWithInsideProperties;
type OutputTypesAndNormalFields = OutputTypesFields | OutputFields;

export function isOutputWithInsideProperties(
  toBeDetermined: OutputTypesAndNormalFields
): toBeDetermined is OutputWithInsideProperties {
  return (toBeDetermined as OutputWithInsideProperties).insideProperties !== undefined;
}

export function useDmnBoxedOutputs(
  jsonSchemaBridge: DmnUnitablesJsonSchemaBridge,
  results: Array<DecisionResult[] | undefined> | undefined,
  rowCount: number,
  outputColumnsCache: React.MutableRefObject<ReactTable.ColumnInstance[]>
) {
  const updateOutputCellsWidth = useCallback(
    (outputs: OutputFields[]) => {
      outputColumnsCache.current?.forEach((cachedColumn) => {
        if (cachedColumn.groupType === "output") {
          const outputToUpdate = outputs.find((e) => e.name === cachedColumn.label);
          if (outputToUpdate && isOutputWithInsideProperties(outputToUpdate) && cachedColumn?.columns) {
            outputToUpdate.insideProperties.forEach((insideProperty: any) => {
              if (insideProperty !== null && typeof insideProperty === "object") {
                Object.keys(insideProperty).map((insidePropertyKey: keyof OutputTypesField) => {
                  const columnFound = cachedColumn.columns?.find(
                    (nestedColumn) => nestedColumn.label === insidePropertyKey
                  );
                  if (columnFound) {
                    insideProperty[insidePropertyKey] = {
                      value: insideProperty[insidePropertyKey],
                      width: columnFound.width as number,
                    };
                  }
                });
              } else if (insideProperty) {
                const columnFound = cachedColumn.columns?.find(
                  (nestedColumn) => nestedColumn.label === insideProperty.name
                );
                if (columnFound) {
                  insideProperty.width = columnFound.width as number;
                }
              }
            });
          }
          if (outputToUpdate) {
            outputToUpdate.width = cachedColumn.width as number;
          }
        }
      });
    },
    [outputColumnsCache]
  );

  const deepFlattenOutput = useCallback((acc: any, entry: string, value: object) => {
    return Object.entries(value).map(([deepEntry, deepValue]) => {
      if (typeof deepValue === "object" && deepValue !== null) {
        deepFlattenOutput(acc, deepEntry, deepValue);
      }
      acc[`${entry}-${deepEntry}`] = deepValue;
      return acc;
    });
  }, []);

  const deepGenerateOutputTypesMapFields = useCallback(
    (
      outputTypeMap: Map<string, OutputTypesFields>,
      properties: DmnSchemaProperties[],
      jsonSchemaBridge: DmnUnitablesJsonSchemaBridge
    ) => {
      return Object.entries(properties).map(([name, property]: [string, DmnSchemaProperties]) => {
        if (property["x-dmn-type"]) {
          const dataType = jsonSchemaBridge.getBoxedDataType(property).dataType;
          outputTypeMap.set(name, { type: property.type, dataType, name });
          return { name, type: property.type, width: CELL_MINIMUM_WIDTH, dataType };
        }
        const path: string[] = property.$ref.split("/").slice(1); // remove #
        const data = path.reduce(
          (acc: { [x: string]: object }, property: string) => acc[property],
          jsonSchemaBridge.schema
        );
        const dataType = jsonSchemaBridge.getBoxedDataType(data).dataType;
        if (data.properties) {
          const insideProperties = deepGenerateOutputTypesMapFields(outputTypeMap, data.properties, jsonSchemaBridge);
          outputTypeMap.set(name, { type: data.type, insideProperties, dataType, name });
        } else {
          outputTypeMap.set(name, { type: data.type, dataType, name });
        }
        return { name, dataType: data.type, width: CELL_MINIMUM_WIDTH } as OutputTypesField;
      });
    },
    []
  );

  const { outputs, outputRules } = useMemo(() => {
    const decisionResults = results?.filter((result) => result !== undefined);
    if (jsonSchemaBridge === undefined || decisionResults === undefined) {
      return { outputs: [] as OutputFields[], outputRules: [] as UnitablesOutputRows[] };
    }

    // generate a map that contains output types
    const outputTypeMap = Object.entries(jsonSchemaBridge.schema?.definitions?.OutputSet?.properties ?? []).reduce(
      (outputTypeMap: Map<string, OutputTypesFields>, [name, properties]: [string, DmnSchemaProperties]) => {
        if (properties["x-dmn-type"]) {
          const dataType = jsonSchemaBridge.getBoxedDataType(properties).dataType;
          outputTypeMap.set(name, { type: properties.type, dataType, name });
        } else {
          const path = properties.$ref.split("/").slice(1); // remove #
          const data = path.reduce((acc: any, property: string) => acc[property], jsonSchemaBridge.schema);
          const dataType = jsonSchemaBridge.getBoxedDataType(data).dataType;
          if (data.properties) {
            const insideProperties = deepGenerateOutputTypesMapFields(outputTypeMap, data.properties, jsonSchemaBridge);
            outputTypeMap.set(name, { type: data.type, insideProperties, dataType, name });
          } else {
            outputTypeMap.set(name, { type: data.type, dataType, name });
          }
        }

        return outputTypeMap;
      },
      new Map<string, OutputFields>()
    );

    // generate outputs
    const outputMap = decisionResults.reduce(
      (acc: Map<string, OutputFields>, decisionResult: DecisionResult[] | undefined) => {
        if (decisionResult) {
          decisionResult.forEach(({ decisionName }) => {
            const data = outputTypeMap.get(decisionName);
            const dataType = data?.dataType ?? DmnBuiltInDataType.Undefined;
            if (data && isOutputWithInsideProperties(data)) {
              acc.set(decisionName, {
                name: decisionName,
                dataType,
                insideProperties: data.insideProperties,
                width: data.insideProperties.reduce((acc: number, column: any) => acc + column.width, 0),
              });
            } else {
              acc.set(decisionName, {
                name: decisionName,
                dataType,
                width: CELL_MINIMUM_WIDTH,
              });
            }
          });
        }
        return acc;
      },
      new Map<string, OutputFields>()
    );

    const outputEntries = decisionResults.reduce((acc: Result[], decisionResult: DecisionResult[] | undefined) => {
      if (decisionResult) {
        const outputResults = decisionResult.map(({ result, decisionName }) => {
          if (result === null || typeof result !== "object") {
            const dmnRunnerClause = outputMap.get(decisionName);
            if (dmnRunnerClause && isOutputWithInsideProperties(dmnRunnerClause)) {
              return dmnRunnerClause.insideProperties.reduce((acc, insideProperty) => {
                acc[insideProperty.name] = "null";
                return acc;
              }, {} as Record<string, any>);
            }
          }
          if (result === null) {
            return "null";
          }
          if (result === true) {
            return "true";
          }
          if (result === false) {
            return "false";
          }
          if (typeof result === "object") {
            return Object.entries(result).reduce((flattenObject: Record<string, string>, [entry, value]) => {
              if (typeof value === "object" && value !== null) {
                deepFlattenOutput(flattenObject, entry, value);
              } else {
                flattenObject[entry] = value as string;
              }
              return flattenObject;
            }, {});
          }
          return result;
        });
        return [...acc, outputResults];
      }
      return acc;
    }, []);

    const outputRules: UnitablesOutputRows[] = Array.from(Array(rowCount)).map((e, i) => ({
      outputEntries: (outputEntries?.[i] as string[]) ?? [],
    }));

    const outputs = Array.from(outputMap.values());
    updateOutputCellsWidth(outputs);
    return { outputs, outputRules };
  }, [
    deepFlattenOutput,
    deepGenerateOutputTypesMapFields,
    jsonSchemaBridge,
    results,
    rowCount,
    updateOutputCellsWidth,
  ]);

  return useMemo(() => {
    return {
      outputs,
      outputRules,
      updateOutputCellsWidth,
    };
  }, [outputRules, outputs, updateOutputCellsWidth]);
}
