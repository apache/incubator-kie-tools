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
  SwfServiceCatalogFunctionArgumentType,
  SwfServiceCatalogFunctionType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSource,
  SwfServiceCatalogServiceType,
} from "../../../../api";
import { convertSource } from "../convertSource";
import { JSONSchema7 } from "json-schema";

export function parseJsonSchema(
  args: {
    source: SwfServiceCatalogServiceSource;
    serviceFileName: string;
    serviceFileContent: string;
  },
  jsonSchemaDocument: JSONSchema7
): SwfServiceCatalogService {
  return {
    name: jsonSchemaDocument.title ?? args.serviceFileName,
    type: "jsonschema" as SwfServiceCatalogServiceType,
    source: args.source,
    functions: [
      {
        source: convertSource(args.source),
        arguments: getPropertiesRecursively(jsonSchemaDocument),
        type: "jsonschema" as SwfServiceCatalogFunctionType,
        name: jsonSchemaDocument.title ?? args.serviceFileName,
      },
    ],
    rawContent: args.serviceFileContent,
  };
}
export function getPropertiesRecursively(schema: JSONSchema7): Record<string, SwfServiceCatalogFunctionArgumentType> {
  const result: Record<string, SwfServiceCatalogFunctionArgumentType> = {};
  function recursiveFunc(subSchema: any, tempArray: Record<string, SwfServiceCatalogFunctionArgumentType>) {
    const schemaType = subSchema.type;
    if (schemaType == SwfServiceCatalogFunctionArgumentType.array) {
      recursiveFunc(subSchema.items, tempArray);
    } else if (schemaType == SwfServiceCatalogFunctionArgumentType.object) {
      Object.entries(subSchema.properties).forEach(([propertyName, propertyValue]: [string, any]) => {
        result[propertyName] = resolveArgumentType(propertyValue["type"]);
        recursiveFunc(propertyValue, tempArray);
      });
    } else {
      return;
    }
    return tempArray;
  }
  return recursiveFunc(schema, result) ?? {};
}

function resolveArgumentType(type: string): SwfServiceCatalogFunctionArgumentType {
  return SwfServiceCatalogFunctionArgumentType[type as keyof typeof SwfServiceCatalogFunctionArgumentType];
}
