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
import { SwfFunctionDefinition } from "../../../catalog";

export function getWorkflowSwfFunctionDefinitions(
  rootNode: jsonc.Node,
  workflow?: Specification.Workflow
): SwfFunctionDefinition[] {
  if (workflow) {
    if (typeof workflow?.functions === "string") {
      return [];
    }

    return Array.from(workflow.functions ?? []).map((f) => ({
      name: f.name,
      operation: f.operation,
    }));
  }

  const functionNode = jsonc.findNodeAtLocation(rootNode, ["functions"]);

  if (functionNode?.type === "array") {
    const result: SwfFunctionDefinition[] = [];
    Array.from(functionNode.children ?? []).forEach((fNode) => {
      const name = jsonc.findNodeAtLocation(fNode, ["name"])?.value;
      const operation = jsonc.findNodeAtLocation(fNode, ["operation"])?.value;

      if (name && operation) {
        result.push({
          name,
          operation,
        });
      }
    });
    return result;
  }
  return [];
}
