/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import getObjectValueByPath from "lodash/get";
import setObjectValueByPath from "lodash/set";

interface RefProperty {
  $ref?: string;
}

function getPropertiesFullKey(fieldKey: string, parentKey?: string) {
  return parentKey ? `${parentKey}.properties.${fieldKey}` : fieldKey;
}

function refPathToObjectPath(path: string) {
  return path.split("/").splice(1).join(".");
}

// Pass the JSON Schema and which property should be dereferenced
// if no property is passed down, it will dereference the entire JSON Schema
export function dereferenceProperties<
  JSONSchema extends Record<string, any>,
  Properties extends Record<string, RefProperty>
>(
  jsonSchema: JSONSchema,
  properties?: Properties,
  parentKey?: string,
  dereferencedJsonSchema?: Record<string, any>
): Record<string, any> {
  if (properties === undefined && jsonSchema.$ref) {
    return dereferenceProperties(
      jsonSchema,
      getObjectValueByPath(jsonSchema, refPathToObjectPath(jsonSchema.$ref) + ".properties")
    );
  }
  return Object.entries(properties ?? {}).reduce((dereferencedJsonSchema, [fieldKey, jsonSchemaField]) => {
    if (jsonSchemaField.$ref) {
      const referenceField = getObjectValueByPath(jsonSchema, refPathToObjectPath(jsonSchemaField.$ref));
      setObjectValueByPath(dereferencedJsonSchema, getPropertiesFullKey(fieldKey, parentKey), referenceField);
      if (referenceField.properties) {
        dereferenceProperties(jsonSchema, referenceField.properties, fieldKey, dereferencedJsonSchema);
      }
    } else {
      setObjectValueByPath(dereferencedJsonSchema, getPropertiesFullKey(fieldKey, parentKey), jsonSchemaField);
    }
    return dereferencedJsonSchema;
  }, dereferencedJsonSchema ?? ({} as Record<string, any>));
}
