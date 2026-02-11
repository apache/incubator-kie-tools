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

import { JSON_SCHEMA_INPUT_SET_PATH, RECURSION_KEYWORD, RECURSION_REF_KEYWORD } from "./jsonSchemaConstants";
import { DmnAjvSchemaFormat } from "./ajv";
import { ValidateFunction } from "ajv-draft-04";
import { ExtendedServicesFormSchema, DmnInputFieldProperties } from "@kie-tools/extended-services-api/dist/formSchema";
import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";
import { resolveRefs, pathFromPtr } from "json-refs";
import cloneDeep from "lodash/cloneDeep";
import getObjectValueByPath from "lodash/get";
import setObjectValueByPath from "lodash/set";
import unsetObjectValueByPath from "lodash/unset";
import { X_DMN_TYPE_KEYWORD } from "./jitExecutorKeywords";
import type { JSONSchema4 } from "json-schema";

function getFieldDefaultValue(dmnField: DmnInputFieldProperties): string | boolean | [] | object | undefined {
  if (dmnField?.type === "string" && dmnField?.format === undefined) {
    return undefined;
  }
  if (dmnField?.type === "number") {
    return undefined;
  }
  if (dmnField?.type === "boolean") {
    return false;
  }
  if (dmnField?.type === "array") {
    return [];
  }
  if (dmnField?.type === "object") {
    return {};
  }
  return undefined;
}

/**
 * get default values based on the jsonSchema
 */
export function getDefaultValues(jsonSchema: JSONSchema4) {
  return Object.entries(getObjectValueByPath(jsonSchema, JSON_SCHEMA_INPUT_SET_PATH) ?? {})?.reduce(
    (acc, [key, field]: [string, Record<string, string>]) => {
      acc[key] = getFieldDefaultValue(field);
      return acc;
    },
    {} as Record<string, any>
  );
}

/**
 * Remove properties from "toValidate" that are not present in the "validator" or
 * properties that have they type or format changed.
 */
export function removeChangedPropertiesAndAdditionalProperties<T extends ValidateFunction>(
  validator: T,
  toValidate: Record<string, any>
) {
  const validation = validator(toValidate);
  if (!validation && validator.errors) {
    validator.errors.forEach((error) => {
      if (error.keyword !== "type" && error.keyword !== "format") {
        return;
      }

      // uniforms-patternfly saves the DateTimeField component value as a Date object and
      // AJV handles data-time as a string, causing an error with keyword type.
      // Also, the ajv.ErrorObject doesn't correctly type the parentSchema property
      if (
        error.keyword === "type" &&
        (error.parentSchema as any)?.format === DmnAjvSchemaFormat.DATE_TIME &&
        error.data instanceof Date
      ) {
        return;
      }

      const pathList = error.schemaPath
        .replace(/\['([^']+)'\]/g, "$1")
        .replace(/\[(\d+)\]/g, ".$1")
        .split(".")
        .filter((e) => e !== "");

      const path = pathList.length === 1 ? pathList[0] : pathList.slice(0, -1).join(".");
      unsetObjectValueByPath(toValidate, path);
    });
  }
}

/**
 * Resolve references and handle circular references.
 * All circular $refs are changed to "recursionRef" instead of "$ref"
 * The JSON schema is resolved a second time to ensure all circular $ref were removed.
 */
export async function dereferenceAndCheckForRecursion(
  formSchema: ExtendedServicesFormSchema,
  canceled?: Holder<boolean>
): Promise<ExtendedServicesFormSchema | undefined> {
  try {
    const formSchemaCopy = cloneDeep(formSchema);
    const $ref = getObjectValueByPath(formSchemaCopy, "$ref");
    unsetObjectValueByPath(formSchemaCopy, "$ref");

    const { refs, resolved } = await resolveRefs(formSchemaCopy as any);
    if (canceled?.get()) {
      return;
    }

    let reResolve = false;
    Object.entries(refs).forEach(([ptr, properties]) => {
      if (properties?.circular) {
        const path = pathFromPtr(ptr);
        const recursiveRefPath = pathFromPtr(properties.def.$ref);
        setObjectValueByPath(resolved, path.join("."), {
          [`${RECURSION_KEYWORD}`]: true,
          [`${RECURSION_REF_KEYWORD}`]: properties.def.$ref,
          [`${X_DMN_TYPE_KEYWORD}`]: recursiveRefPath[recursiveRefPath.length - 1],
        });
        reResolve = true;
      }
    });

    if (reResolve) {
      const { resolved: reResolved } = await resolveRefs(resolved);
      if (canceled?.get()) {
        return;
      }

      if ($ref) {
        setObjectValueByPath(reResolved, "$ref", $ref);
      }
      return reResolved;
    }

    if ($ref) {
      setObjectValueByPath(resolved, "$ref", $ref);
    }
    return resolved;
  } catch (err) {
    console.log(err);
    return;
  }
}

/**
 * Detects if an Input Data Node was renamed by analyzing the schema diff.
 * A rename is detected when exactly one property is removed and one is added.
 *
 * @param schemaDiff - The diff object between previous and current JSON schemas
 * @returns Map of old property names to new property names
 */
export function detectRenamedProperties(schemaDiff: any): Map<string, string> {
  const renamedProperties = new Map<string, string>();

  const diffProperties = schemaDiff?.definitions?.InputSet?.properties;
  if (!diffProperties || typeof diffProperties !== "object") {
    return renamedProperties;
  }

  const removedProperties: string[] = [];
  const addedProperties: string[] = [];

  // Identify removed and added properties
  Object.entries(diffProperties).forEach(([key, value]) => {
    if (value === undefined) {
      removedProperties.push(key);
    } else if (value && typeof value === "object") {
      addedProperties.push(key);
    }
  });

  // If we have exactly one removed and one added property, it's likely a rename
  if (removedProperties.length === 1 && addedProperties.length === 1) {
    renamedProperties.set(removedProperties[0], addedProperties[0]);
  }

  return renamedProperties;
}

/**
 * Detects renamed enum values by comparing type definitions in the schema.
 * Uses x-dmn-type to match types across the schema and find enum renames.
 * Recursively checks nested properties within complex types.
 * Returns a map of x-dmn-type identifiers to their enum value renames.
 *
 * @param previousSchema - The previous JSON schema
 * @param currentSchema - The current JSON schema
 * @returns Map of x-dmn-type to Map of old enum values to new enum values
 */
export function detectRenamedEnumValues(
  previousSchema: JSONSchema4,
  currentSchema: JSONSchema4
): Map<string, Map<string, string>> {
  const renamedEnumsByType = new Map<string, Map<string, string>>();

  const previousDefinitions = previousSchema?.definitions as Record<string, any> | undefined;
  const currentDefinitions = currentSchema?.definitions as Record<string, any> | undefined;

  if (!previousDefinitions || !currentDefinitions) {
    return renamedEnumsByType;
  }

  /**
   * Recursively check for enum renames in a type definition and its nested properties
   */
  function checkTypeForEnumRenames(previousType: any, currentType: any): void {
    // Check if the type itself has enum arrays
    if (
      previousType?.enum &&
      Array.isArray(previousType.enum) &&
      currentType?.enum &&
      Array.isArray(currentType.enum)
    ) {
      const previousEnums = previousType.enum as string[];
      const currentEnums = currentType.enum as string[];

      // Find removed and added enum values
      const removedEnums = previousEnums.filter((e) => !currentEnums.includes(e));
      const addedEnums = currentEnums.filter((e) => !previousEnums.includes(e));

      // If exactly one removed and one added, it's likely a rename
      if (removedEnums.length === 1 && addedEnums.length === 1) {
        const enumRenames = new Map<string, string>();
        enumRenames.set(removedEnums[0], addedEnums[0]);

        // Use x-dmn-type as the key
        const typeKey = currentType["x-dmn-type"];
        if (typeKey) {
          renamedEnumsByType.set(typeKey, enumRenames);
        }
      }
    }

    // Recursively check nested properties for complex types
    if (
      previousType?.type === "object" &&
      previousType?.properties &&
      currentType?.type === "object" &&
      currentType?.properties
    ) {
      Object.keys(currentType.properties).forEach((propName) => {
        const prevProp = previousType.properties[propName];
        const currProp = currentType.properties[propName];

        if (prevProp && currProp) {
          checkTypeForEnumRenames(prevProp, currProp);
        }
      });
    }
  }

  // Check all type definitions
  Object.keys(currentDefinitions).forEach((typeName) => {
    const previousType = previousDefinitions[typeName];
    const currentType = currentDefinitions[typeName];

    // Skip if type doesn't exist in previous schema
    if (!previousType) {
      return;
    }

    checkTypeForEnumRenames(previousType, currentType);
  });

  return renamedEnumsByType;
}

/**
 * Copies renamed property values to the input object.
 * Copies values from old property names to new property names.
 *
 * @param currentInputs - The input object to update
 * @param renamedPropertiesMap - Map of old property names to new property names
 */
export function copyRenamedInputValue(
  currentInputs: Record<string, any>,
  renamedPropertiesMap: Map<string, string>
): void {
  renamedPropertiesMap.forEach((newName, oldName) => {
    if (oldName in currentInputs) {
      currentInputs[newName] = currentInputs[oldName];
    }
  });
}

/**
 * Copies renamed enum values to the input object and removes invalid enum values.
 * Recursively searches through the input object and:
 * 1. Updates enum values based on their x-dmn-type match with the renamed enums map
 * 2. Deletes enum values that are no longer valid according to the current schema
 *
 * @param currentInputs - The input object to update
 * @param renamedEnumsByType - Map of x-dmn-type to their enum value renames
 * @param jsonSchema - The JSON schema to use for type matching
 */
export function copyRenamedEnumValues(
  currentInputs: Record<string, any>,
  renamedEnumsByType: Map<string, Map<string, string>>,
  jsonSchema?: JSONSchema4
): void {
  const inputSetProperties = jsonSchema?.definitions?.InputSet?.properties as Record<string, any> | undefined;
  if (!inputSetProperties) {
    return;
  }

  /**
   * Recursively update enum values in the input based on x-dmn-type
   */
  function updateEnumValuesRecursively(inputObj: Record<string, any>, schemaProps: Record<string, any>): void {
    Object.keys(inputObj).forEach((key) => {
      const schemaProp = schemaProps[key];
      if (!schemaProp) {
        return;
      }

      const xDmnType = schemaProp["x-dmn-type"];
      let currentValue = inputObj[key];

      // Check if this property's type has renamed enums
      if (xDmnType && renamedEnumsByType.has(xDmnType)) {
        const enumRenames = renamedEnumsByType.get(xDmnType)!;

        // Update the value if it matches an old enum value
        enumRenames.forEach((newEnumValue, oldEnumValue) => {
          if (currentValue === oldEnumValue) {
            inputObj[key] = newEnumValue;
            currentValue = newEnumValue;
          }
        });
      }

      // Check if the current value is still valid according to the schema's enum
      if (schemaProp?.enum && Array.isArray(schemaProp.enum)) {
        const validEnumValues = schemaProp.enum;

        // If the current value is not in the valid enum list, delete it
        if (currentValue !== undefined && !validEnumValues.includes(currentValue)) {
          delete inputObj[key];
          return; // Skip further processing for this property
        }
      }

      // Recursively process nested objects
      if (
        schemaProp?.type === "object" &&
        schemaProp?.properties &&
        inputObj[key] &&
        typeof inputObj[key] === "object"
      ) {
        updateEnumValuesRecursively(inputObj[key], schemaProp.properties);
      }
    });
  }

  updateEnumValuesRecursively(currentInputs, inputSetProperties);
}
