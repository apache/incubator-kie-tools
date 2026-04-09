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

import type { JSONSchema4 } from "json-schema";
import $RefParser from "@apidevtools/json-schema-ref-parser";

type Field = {
  type?: string;
  properties?: Record<string, Field>;
};

function getFieldDefaultValue(field: Field): string | boolean | [] | object | undefined {
  if (field.type === "string") {
    return "";
  }
  if (field.type === "number") {
    return undefined;
  }
  if (field.type === "boolean") {
    return false;
  }
  if (field.type === "array") {
    return [];
  }
  if (field.type === "object") {
    if (field.properties) {
      return Object.keys(field.properties).reduce((objectValues: Record<string, any>, key: string) => {
        if (field.properties?.[key]) {
          objectValues[key] = getFieldDefaultValue(field.properties[key]);
        } else {
          objectValues[key] = undefined;
        }
        return objectValues;
      }, {});
    } else {
      return {};
    }
  }
  return undefined;
}

/**
 * get default values based on the jsonSchema
 */
export async function getDefaultValues(jsonSchema: JSONSchema4, skipDereference = false) {
  // Dereference schema (replace $refs with their values)
  if (!skipDereference) {
    await $RefParser.dereference(jsonSchema, { continueOnError: true, resolve: { external: false } });
  }

  return Object.entries(jsonSchema.properties ?? {})?.reduce(
    (acc, [key, field]: [string, Record<string, string>]) => {
      acc[key] = getFieldDefaultValue(field);
      return acc;
    },
    {} as Record<string, any>
  );
}
