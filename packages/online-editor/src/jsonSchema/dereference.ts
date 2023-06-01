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

import { get as getObjectValueByPath, set as setObjectValueByPath } from "lodash";

interface RefProperty {
  $ref?: string;
  items?: Record<string, any>;
  type?: string;
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

// Pass the JSON Schema and which property should be dereferenced
// if no property is passed down, it will dereference the entire JSON Schema
export function dereferenceProperties<
  JSONSchema extends Record<string, any>,
  Properties extends Record<string, RefProperty> & RefProperty
>(
  jsonSchema: JSONSchema,
  properties?: Properties,
  parentKey?: string,
  dereferencedJsonSchema?: Record<string, any>
): Record<string, any> {
  if (properties === undefined && jsonSchema.$ref) {
    const dereferencedJsonSchema = {};
    const schemaFirstProperty = refPathToObjectPath(jsonSchema.$ref);
    setObjectValueByPath(
      dereferencedJsonSchema,
      schemaFirstProperty,
      getObjectValueByPath(jsonSchema, schemaFirstProperty)
    );

    const fieldKey = getPropertiesFullKey(schemaFirstProperty);
    const properties = getObjectValueByPath(jsonSchema, fieldKey);
    if (properties !== undefined) {
      return dereferenceProperties(jsonSchema, properties, fieldKey, dereferencedJsonSchema);
    }
    return jsonSchema;
  }

  if (properties !== null && typeof properties === "object" && properties.$ref && dereferencedJsonSchema && parentKey) {
    const referenceField = getObjectValueByPath(jsonSchema, refPathToObjectPath(properties.$ref));

    addMissingPropertyToField(referenceField);
    setObjectValueByPath(dereferencedJsonSchema, parentKey, referenceField);
    if (referenceField.properties) {
      return dereferenceProperties(
        jsonSchema,
        referenceField.properties,
        getPropertiesFullKey(parentKey),
        dereferencedJsonSchema
      );
    }
    if (referenceField.items) {
      return dereferenceProperties(
        jsonSchema,
        referenceField.items,
        getItemsFullKey(parentKey),
        dereferencedJsonSchema
      );
    }
    return dereferencedJsonSchema;
  }

  return Object.entries(properties ?? {}).reduce((dereferencedJsonSchema, [fieldKey, jsonSchemaField]) => {
    if (jsonSchemaField.$ref) {
      let referenceField = getObjectValueByPath(jsonSchema, refPathToObjectPath(jsonSchemaField.$ref));
      if (referenceField !== null && typeof referenceField === "object" && referenceField.$ref) {
        referenceField = getObjectValueByPath(jsonSchema, refPathToObjectPath(referenceField.$ref));
      }

      addMissingPropertyToField(referenceField);
      setObjectValueByPath(dereferencedJsonSchema, getFullKey(fieldKey, parentKey), referenceField);
      if (referenceField.properties) {
        dereferenceProperties(
          jsonSchema,
          referenceField.properties,
          getPropertiesFullKey(getFullKey(fieldKey, parentKey)),
          dereferencedJsonSchema
        );
      }
      if (referenceField.items) {
        dereferenceProperties(
          jsonSchema,
          referenceField.items,
          getItemsFullKey(getFullKey(fieldKey, parentKey)),
          dereferencedJsonSchema
        );
      }
    } else {
      addMissingPropertyToField(jsonSchemaField);
      setObjectValueByPath(dereferencedJsonSchema, getFullKey(fieldKey, parentKey), jsonSchemaField);
    }
    return dereferencedJsonSchema;
  }, dereferencedJsonSchema ?? ({} as Record<string, any>));
}

function addMissingPropertyToField(field: Record<string, any>) {
  if (field.items && !field.properties && !field.items.type && !field.items.$ref) {
    field.maxItems = 0;
  }
}
