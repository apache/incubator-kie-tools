/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Specification } from "@severlessworkflow/sdk-typescript";
import * as jsonc from "jsonc-parser";
import { OmitRecursively, SwfJsonPath, SwfLsNodeType } from "./types";

/**
 * Get a node in the format of the SWF Specification
 *
 * @param args.fields the fields to pick up
 * @param args.nodeType the node type of the node to get
 */
function getNodes<T>(args: {
  fields: string[];
  nodeType: SwfLsNodeType;
  path: SwfJsonPath;
  rootNode: jsonc.Node;
}): OmitRecursively<T, "normalize"> {
  const node = jsonc.findNodeAtLocation(args.rootNode, args.path);
  if (node?.type !== args.nodeType) {
    return [] as unknown as OmitRecursively<T, "normalize">;
  }

  return Array.from(node.children ?? []).flatMap((childNode) => {
    const nodeProps: { [key: string]: string } = {};

    args.fields.forEach((field) => {
      nodeProps[field] = jsonc.findNodeAtLocation(childNode, [field])?.value;
    });

    return [{ ...nodeProps }];
  }) as unknown as OmitRecursively<T, "normalize">;
}

export function getFunctions(rootNode: jsonc.Node) {
  return getNodes<Specification.Function[]>({
    rootNode,
    path: ["functions"],
    nodeType: "array",
    fields: ["name", "operation"],
  }).filter((f) => f.name && f.operation);
}

export function getEvents(rootNode: jsonc.Node) {
  return getNodes<Specification.Eventdef[]>({
    rootNode,
    path: ["events"],
    nodeType: "array",
    fields: ["name"],
  }).filter((e) => e.name);
}

export function getStates(rootNode: jsonc.Node) {
  return getNodes<Specification.States>({
    rootNode,
    path: ["states"],
    nodeType: "array",
    fields: ["name"],
  }).filter((e) => e.name) as Specification.States;
}
