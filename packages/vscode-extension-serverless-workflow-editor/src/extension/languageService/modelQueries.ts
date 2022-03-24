/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Specification } from "@severlessworkflow/sdk-typescript";
import * as jsonc from "jsonc-parser";

export function getFunctions(rootNode: jsonc.Node) {
  const functionsNode = jsonc.findNodeAtLocation(rootNode, ["functions"]);
  if (functionsNode?.type !== "array") {
    return [];
  }

  return Array.from(functionsNode.children ?? []).flatMap((functionNode) => {
    const name = jsonc.findNodeAtLocation(functionNode, ["name"])?.value;
    const operation = jsonc.findNodeAtLocation(functionNode, ["operation"])?.value;

    if (!name || !operation) {
      return [];
    }

    return [{ name, operation } as Omit<Specification.Function, "normalize">];
  });
}
