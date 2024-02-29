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

import { JSON_SCHEMA_INPUT_SET_PATH, RECURSION_KEYWORD, RECURSION_REF_KEYWORD, X_DMN_TYPE_KEYWORD } from "./constants";
import { DmnAjvSchemaFormat, ValidateFunction } from "./ajv";
import {
  ExtendedServicesDmnJsonSchema,
  DmnInputFieldProperties,
} from "@kie-tools/extended-services-api/dist/jsonSchema";
import { Holder } from "@kie-tools-core/react-hooks/dist/Holder";
import { resolveRefs, pathFromPtr } from "json-refs";
import cloneDeep from "lodash/cloneDeep";
import getObjectValueByPath from "lodash/get";
import setObjectValueByPath from "lodash/set";
import unsetObjectValueByPath from "lodash/unset";

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
export function getDefaultValues(jsonSchema: ExtendedServicesDmnJsonSchema) {
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

      const pathList = error.dataPath
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
export async function resolveReferencesAndCheckForRecursion(
  jsonSchema: ExtendedServicesDmnJsonSchema,
  canceled?: Holder<boolean>
): Promise<ExtendedServicesDmnJsonSchema | undefined> {
  try {
    const jsonSchemaCopy = cloneDeep(jsonSchema);
    const $ref = getObjectValueByPath(jsonSchemaCopy, "$ref");
    unsetObjectValueByPath(jsonSchemaCopy, "$ref");

    const { refs, resolved } = await resolveRefs(jsonSchemaCopy as any);
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
