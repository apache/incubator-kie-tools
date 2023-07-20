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
import { DecisionResult, DmnInputFieldProperties } from "@kie-tools/extended-services-api";

export interface OutputField {
  type: string;
  dataType: DmnBuiltInDataType;
  width?: number;
  name: string;
  joinedName: string;
  properties?: Record<string, any>;
  items?: Record<string, any>[];
}

export function useDmnRunnerOutputs(
  jsonSchemaBridge: DmnUnitablesJsonSchemaBridge,
  results: Array<DecisionResult[] | undefined> | undefined
) {
  return useMemo(() => {
    if (jsonSchemaBridge === undefined || results === undefined) {
      return { outputsPropertiesMap: new Map<string, OutputField>() };
    }

    // generate a map that contains output types
    const outputsPropertiesMap = Object.entries(
      jsonSchemaBridge.schema.definitions?.OutputSet?.properties ?? []
    ).reduce((outputTypeMap: Map<string, OutputField>, [name, properties]: [string, DmnInputFieldProperties]) => {
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
    }, new Map<string, OutputField>());

    return {
      outputsPropertiesMap,
    };
  }, [jsonSchemaBridge, results]);
}
