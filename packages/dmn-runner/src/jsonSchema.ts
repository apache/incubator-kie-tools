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
    (acc: Record<string, any>, [key, field]) => {
      acc[key] = getFieldDefaultValue(field as any);
      return acc;
    },
    {}
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
export function detectRenamedProperties(schemaDiff: Record<string, any>): Map<string, string> {
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
 * Detects nested property renames by recursively analyzing the schema diff.
 * Returns a map of paths where renames occurred.
 *
 * @param schemaDiff - The diff object between previous and current JSON schemas
 * @returns Map of path prefixes to Maps of old property names to new property names
 */
export function detectNestedPropertyRenames(schemaDiff: Record<string, any>): Map<string, Map<string, string>> {
  const nestedRenames = new Map<string, Map<string, string>>();

  /**
   * Recursively check for property renames at any depth
   */
  function checkForRenames(obj: Record<string, any>, currentPath: string[]): void {
    if (!obj || typeof obj !== "object") {
      return;
    }

    // Check if this level has a "properties" object with changes
    if (obj.properties && typeof obj.properties === "object") {
      const removedProps: string[] = [];
      const addedProps: string[] = [];

      Object.entries(obj.properties).forEach(([key, value]) => {
        if (value === undefined) {
          removedProps.push(key);
        } else if (value && typeof value === "object") {
          addedProps.push(key);
          // Recursively check nested properties
          checkForRenames(value, [...currentPath, key]);
        }
      });

      // If exactly one removed and one added, it's likely a rename
      if (removedProps.length === 1 && addedProps.length === 1) {
        const pathKey = currentPath.join(".");
        const renameMap = new Map<string, string>();
        renameMap.set(removedProps[0], addedProps[0]);
        nestedRenames.set(pathKey, renameMap);
      }
    }

    // Continue checking other nested objects
    Object.entries(obj).forEach(([key, value]) => {
      if (key !== "properties" && value && typeof value === "object") {
        checkForRenames(value, [...currentPath, key]);
      }
    });
  }

  // Start from InputSet properties in the diff
  const inputSetDiff = schemaDiff?.definitions?.InputSet?.properties;
  if (inputSetDiff && typeof inputSetDiff === "object") {
    Object.entries(inputSetDiff).forEach(([topLevelKey, topLevelValue]) => {
      if (topLevelValue && typeof topLevelValue === "object") {
        checkForRenames(topLevelValue, [topLevelKey]);
      }
    });
  }

  return nestedRenames;
}

/**
 * Applies nested property renames to the input data.
 * Recursively traverses the input and renames properties based on the detected renames.
 *
 * @param currentInputs - The input object to update
 * @param nestedRenames - Map of path prefixes to Maps of old property names to new property names
 */
export function copyNestedPropertyRenames(
  currentInputs: Record<string, any>,
  nestedRenames: Map<string, Map<string, string>>
): void {
  if (nestedRenames.size === 0) {
    return;
  }

  /**
   * Recursively copy renames at the appropriate depth
   */
  function copyRenamesRecursively(obj: Record<string, any>, currentPath: string[]): void {
    const pathKey = currentPath.join(".");

    // Check if there are renames at this level
    if (nestedRenames.has(pathKey)) {
      const renameMap = nestedRenames.get(pathKey)!;

      renameMap.forEach((newName, oldName) => {
        if (oldName in obj) {
          obj[newName] = obj[oldName];
          delete obj[oldName];
        }
      });
    }

    // Recursively process nested objects
    Object.entries(obj).forEach(([key, value]) => {
      if (value && typeof value === "object" && !Array.isArray(value)) {
        copyRenamesRecursively(value, [...currentPath, key]);
      }
    });
  }

  // Start from the root of the input
  Object.entries(currentInputs).forEach(([topLevelKey, topLevelValue]) => {
    if (topLevelValue && typeof topLevelValue === "object" && !Array.isArray(topLevelValue)) {
      copyRenamesRecursively(topLevelValue, [topLevelKey]);
    }
  });
}

/**
 * Detects renamed and deleted enum values by recursively comparing InputSet properties.
 * Directly compares enum arrays at the same path in previous and current schemas.
 * Returns a map of property paths to their enum changes (renames and valid values).
 *
 * @param previousSchema - The previous JSON schema
 * @param currentSchema - The current JSON schema
 * @returns Map of property paths to objects containing renames and valid enum values
 */
export function detectRenamedEnumValues(
  previousSchema: JSONSchema4,
  currentSchema: JSONSchema4
): Map<string, { renames: Map<string, string>; validValues: string[] }> {
  const enumChangesByPath = new Map<string, { renames: Map<string, string>; validValues: string[] }>();

  const previousInputSet = previousSchema?.definitions?.InputSet?.properties as Record<string, any> | undefined;
  const currentInputSet = currentSchema?.definitions?.InputSet?.properties as Record<string, any> | undefined;

  if (!previousInputSet || !currentInputSet) {
    return enumChangesByPath;
  }

  /**
   * Recursively check for enum changes at each property path
   */
  function checkPropertiesForEnumChanges(
    previousProperties: Record<string, any>,
    currentProperties: Record<string, any>,
    currentPath: string[]
  ): void {
    Object.keys(currentProperties).forEach((propertyName) => {
      const prevProperty = previousProperties[propertyName];
      const currProperty = currentProperties[propertyName];

      if (!prevProperty || !currProperty) {
        return;
      }

      // Check if this property has enum arrays
      if (
        prevProperty?.enum &&
        Array.isArray(prevProperty.enum) &&
        currProperty?.enum &&
        Array.isArray(currProperty.enum)
      ) {
        const previousEnums = prevProperty.enum as string[];
        const currentEnums = currProperty.enum as string[];

        // Find removed and added enum values
        const removedEnums = previousEnums.filter((e) => !currentEnums.includes(e));
        const addedEnums = currentEnums.filter((e) => !previousEnums.includes(e));

        // Detect renames (exactly one removed and one added)
        const renames = new Map<string, string>();
        if (removedEnums.length === 1 && addedEnums.length === 1) {
          renames.set(removedEnums[0], addedEnums[0]);
        }

        // Store both renames and the current valid values
        const pathKey = [...currentPath, propertyName].join(".");
        enumChangesByPath.set(pathKey, {
          renames,
          validValues: currentEnums,
        });
      }

      // Recursively check nested properties for complex types
      if (
        prevProperty?.type === "object" &&
        prevProperty?.properties &&
        currProperty?.type === "object" &&
        currProperty?.properties
      ) {
        checkPropertiesForEnumChanges(prevProperty.properties, currProperty.properties, [...currentPath, propertyName]);
      }
    });
  }

  // Start checking from InputSet properties
  checkPropertiesForEnumChanges(previousInputSet, currentInputSet, []);

  return enumChangesByPath;
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
 * 1. Updates enum values based on path-based renames
 * 2. Deletes enum values that are no longer valid according to the current schema
 *
 * @param currentInputs - The input object to update
 * @param enumChangesByPath - Map of property paths to their enum changes (renames and valid values)
 * @param jsonSchema - The JSON schema (not used in simplified version, kept for compatibility)
 */
export function copyRenamedEnumValues(
  currentInputs: Record<string, any>,
  enumChangesByPath: Map<string, { renames: Map<string, string>; validValues: string[] }>
): void {
  if (enumChangesByPath.size === 0) {
    return;
  }

  /**
   * Recursively update enum values in the input based on property path
   */
  function updateEnumValuesRecursively(inputObj: Record<string, any>, currentPath: string[]): void {
    Object.keys(inputObj).forEach((key) => {
      const pathKey = [...currentPath, key].join(".");
      let currentValue = inputObj[key];

      // Check if this property path has enum changes
      if (enumChangesByPath.has(pathKey)) {
        const { renames, validValues } = enumChangesByPath.get(pathKey)!;

        // Copy renames if the current value matches an old enum value
        if (renames.size > 0) {
          renames.forEach((newEnumValue, oldEnumValue) => {
            if (currentValue === oldEnumValue) {
              inputObj[key] = newEnumValue;
              currentValue = newEnumValue;
            }
          });
        }

        // Validate the current value against valid enum values
        if (currentValue !== undefined && !validValues.includes(currentValue)) {
          delete inputObj[key];
          return; // Skip further processing for this property
        }
      }

      // Recursively process nested objects
      if (inputObj[key] && typeof inputObj[key] === "object" && !Array.isArray(inputObj[key])) {
        updateEnumValuesRecursively(inputObj[key], [...currentPath, key]);
      }
    });
  }

  updateEnumValuesRecursively(currentInputs, []);
}
