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

import { Operation, TestOperation, applyPatch, getValueByPointer } from "fast-json-patch";
import { parseK8sResourceYamls } from "./parseK8sResourceYamls";
import * as jsYaml from "js-yaml";
import { TokenMap, interpolateK8sResourceYaml } from "./interpolateK8sResourceYaml";
import { consoleDebugMessage } from "./common";

export type CheckTypeOperation = {
  path: string;
  op: "checkType";
  type: "array" | "object" | "basic" | "null" | "undefined";
};

export type PatchOperation = Operation | CheckTypeOperation;

export type ResourcePatch = {
  testFilters?: TestOperation<any>[];
  jsonPatches: PatchOperation[];
};

// Because the characters '~' (%x7E) and '/' (%x2F) have special
// meanings in JSON Pointer, '~' needs to be encoded as '~0' and '/'
// needs to be encoded as '~1' when these characters appear in a
// reference token.
// https://datatracker.ietf.org/doc/html/rfc6901#section-3
export function encodeJsonPatchSubpath(path: string) {
  return path.replaceAll("~", "~0").replaceAll("/", "~1");
}

function isValueOfType(type: CheckTypeOperation["type"], value: any) {
  switch (type) {
    case "null":
    case "undefined":
      if (!value) {
        return true;
      }
      return false;
    case "array":
      if (Array.isArray(value)) {
        return true;
      }
      return false;
    case "object":
      if (typeof value === "object" && !Array.isArray(value)) {
        return true;
      }
      return false;
    case "basic":
      if (typeof value === "boolean" || typeof value === "string" || typeof value === "number") {
        return true;
      }
      return false;
  }
}

export function patchK8sResourceYaml(k8sResourceYaml: string, patches: ResourcePatch[], parametersTokens?: TokenMap) {
  const parsedAndPatchedYamls = parseK8sResourceYamls(k8sResourceYaml.split("\n---\n")).map((resource) => {
    let updatedResource = resource;
    for (const patch of patches) {
      try {
        const testFiltersResults = !patch.testFilters
          ? true
          : applyPatch(updatedResource, patch.testFilters, false, false).every(({ test }) => Boolean(test));
        if (testFiltersResults) {
          for (const jsonPatch of patch.jsonPatches) {
            if (jsonPatch.op === "checkType") {
              const value = getValueByPointer(updatedResource, jsonPatch.path);
              if (isValueOfType(jsonPatch.type, value)) {
                continue;
              } else {
                break;
              }
            }
            try {
              const { newDocument } = applyPatch(updatedResource, [jsonPatch], false, false);
              updatedResource = newDocument;
            } catch (e) {
              consoleDebugMessage(`Failed to apply patch -> \n${JSON.stringify(jsonPatch)}`);
            }
          }
        }
      } catch (e) {
        consoleDebugMessage(`Failed to test filters -> \n${JSON.stringify(patch.testFilters)}`);
      }
    }
    return updatedResource;
  });
  const finalYaml = parsedAndPatchedYamls.map((resource) => jsYaml.dump(resource)).join("\n---\n");
  return interpolateK8sResourceYaml(finalYaml, parametersTokens);
}
