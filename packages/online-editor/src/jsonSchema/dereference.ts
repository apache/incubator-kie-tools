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

import { get as getObjectValueByPath, set as setObjectValueByPath, cloneDeep } from "lodash";

interface RefProperty {
  $ref?: string;
}

function getItemsFullKey(fieldKey: string) {
  return `${fieldKey}.items`;
}

function getPropertiesFullKey(fieldKey: string) {
  return `${fieldKey}.properties`;
}

function getFullKey(fieldKey: string, parentKey?: string) {
  return parentKey ? `${parentKey}.${fieldKey}` : fieldKey;
}

function refPathToObjectPath(path: string) {
  return path.split("/").splice(1).join(".");
}

function objectPathToRefPath(path: string) {
  return "#/" + path.split(".").join("/");
}

// Pass the JSON Schema and which property should be dereferenced
// if no property is passed down, it will dereference the entire JSON Schema
export function dereferenceProperties<
  JSONSchema extends Record<string, any>,
  Properties extends Record<string, RefProperty> & RefProperty
>(
  jsonSchema: JSONSchema,
  properties?: Properties,
  parentKey?: string,
  dereferencedJsonSchema?: Record<string, any>,
  referenceMap?: Map<string, Array<string>>
): Record<string, any> {
  if (referenceMap === undefined) {
    // referenceMap is a map of <reference, Array<propertyPath>>
    referenceMap = new Map<string, Array<string>>();
  }

  if (properties === undefined && jsonSchema.$ref) {
    const dereferencedJsonSchema = {};
    const schemaFirstProperty = refPathToObjectPath(jsonSchema.$ref);
    const referenceObj = cloneDeep(getObjectValueByPath(jsonSchema, schemaFirstProperty));
    setObjectValueByPath(dereferencedJsonSchema, schemaFirstProperty, referenceObj);

    const fieldKey = getPropertiesFullKey(schemaFirstProperty);
    const properties = cloneDeep(getObjectValueByPath(jsonSchema, fieldKey));
    if (properties !== undefined) {
      return dereferenceProperties(jsonSchema, properties, fieldKey, dereferencedJsonSchema, referenceMap);
    }
    return jsonSchema;
  }

  if (properties !== null && typeof properties === "object" && properties.$ref && dereferencedJsonSchema && parentKey) {
    const referenceObj = cloneDeep(getObjectValueByPath(jsonSchema, refPathToObjectPath(properties.$ref)));
    const propertyPaths = referenceMap!.get(properties.$ref);
    const recursionRoot = propertyPaths?.find((path) => parentKey.includes(path));
    referenceMap.set(properties.$ref, (propertyPaths ?? []).concat([parentKey]));

    if (recursionRoot) {
      setObjectValueByPath(dereferencedJsonSchema, parentKey, {
        recursionRef: objectPathToRefPath(recursionRoot),
      });
      return dereferencedJsonSchema;
    }

    setObjectValueByPath(dereferencedJsonSchema, parentKey, referenceObj);
    if (referenceObj.properties) {
      return dereferenceProperties(
        jsonSchema,
        referenceObj.properties,
        getPropertiesFullKey(parentKey),
        dereferencedJsonSchema,
        referenceMap
      );
    }
    if (referenceObj.items) {
      return dereferenceProperties(
        jsonSchema,
        referenceObj.items,
        getItemsFullKey(parentKey),
        dereferencedJsonSchema,
        referenceMap
      );
    }
    return dereferencedJsonSchema;
  }

  return Object.entries(properties ?? {}).reduce((dereferencedJsonSchema, [fieldKey, jsonSchemaField]) => {
    if (jsonSchemaField.$ref) {
      let referenceObj = cloneDeep(getObjectValueByPath(jsonSchema, refPathToObjectPath(jsonSchemaField.$ref)));
      const propertyPaths = referenceMap!.get(jsonSchemaField.$ref);
      const recursionRoot = propertyPaths?.find((path) => getFullKey(fieldKey, parentKey).includes(path));
      referenceMap!.set(jsonSchemaField.$ref, (propertyPaths ?? []).concat([getFullKey(fieldKey, parentKey)]));

      if (referenceObj !== null && typeof referenceObj === "object" && referenceObj.$ref) {
        referenceObj = cloneDeep(getObjectValueByPath(jsonSchema, refPathToObjectPath(referenceObj.$ref)));
      }

      if (recursionRoot) {
        setObjectValueByPath(dereferencedJsonSchema, getFullKey(fieldKey, parentKey), {
          recursionRef: objectPathToRefPath(recursionRoot),
        });
      } else {
        setObjectValueByPath(dereferencedJsonSchema, getFullKey(fieldKey, parentKey), referenceObj);

        if (referenceObj.properties) {
          dereferenceProperties(
            jsonSchema,
            referenceObj.properties,
            getPropertiesFullKey(getFullKey(fieldKey, parentKey)),
            dereferencedJsonSchema,
            referenceMap
          );
        }
        if (referenceObj.items) {
          dereferenceProperties(
            jsonSchema,
            referenceObj.items,
            getItemsFullKey(getFullKey(fieldKey, parentKey)),
            dereferencedJsonSchema,
            referenceMap
          );
        }
      }
    } else {
      setObjectValueByPath(dereferencedJsonSchema, getFullKey(fieldKey, parentKey), jsonSchemaField);
    }
    return dereferencedJsonSchema;
  }, dereferencedJsonSchema ?? ({} as Record<string, any>));
}
