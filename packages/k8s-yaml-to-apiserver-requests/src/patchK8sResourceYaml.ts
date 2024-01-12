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

import { Operation, applyPatch, deepClone } from "fast-json-patch";
import { parseK8sResourceYamls } from "./parseK8sResourceYamls";
import * as jsYaml from "js-yaml";
import { TokenMap, interpolateK8sResourceYaml } from "./interpolateK8sResourceYaml";

export type ResourcePatch = {
  targetKinds: string[];
  jsonPatches: Operation[];
};

export function patchK8sResourceYaml(k8sResourceYaml: string, patches: ResourcePatch[], parametersTokens?: TokenMap) {
  console.log({ k8sResourceYaml });
  const parsedAndPatchedYamls = parseK8sResourceYamls(k8sResourceYaml.split("\n---\n")).map((resource) => {
    console.log({ resource });
    let updatedResource = resource;
    patches.forEach((patch) => {
      console.log({ patch });
      if (patch.targetKinds.includes(resource.kind)) {
        const { newDocument } = applyPatch(updatedResource, deepClone(patch.jsonPatches), false, false);
        updatedResource = newDocument;
      }
    });
    return updatedResource;
  });
  console.log({ parsedAndPatchedYamls });
  const finalYaml = parsedAndPatchedYamls.map((resource) => jsYaml.dump(resource)).join("\n---\n");
  console.log({ finalYaml });
  return interpolateK8sResourceYaml(finalYaml, parametersTokens);
}
