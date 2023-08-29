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

import { ELsNode } from "./types";

export interface FindNodesAtLocationArgs {
  /**
   * root node
   */
  root: ELsNode | undefined;

  /**
   * the location of the node to search
   */
  path: any;

  /**
   * true to include uncomplete properties. eg: { parent: { child: }}
   */
  includeUncompleteProps?: boolean;
}

/**
 * This is very similar to `jsonc.findNodeAtLocation`, but it allows the use of '*' as a wildcard selector.
 * This means that unlike `jsonc.findNodeAtLocation`, this method always returns a list of nodes, which can be empty if no matches are found.
 *
 * @returns an array of nodes matching the path, empty array if no matches
 */
export function findNodesAtLocation({
  root,
  path,
  includeUncompleteProps = false,
}: FindNodesAtLocationArgs): ELsNode[] {
  if (!root) {
    return [];
  }

  let nodes: ELsNode[] = [root];

  for (const segment of path) {
    if (segment === "*") {
      nodes = nodes.flatMap((s) => s.children ?? []);
      continue;
    }

    if (typeof segment === "number") {
      const index = segment as number;
      nodes = nodes.flatMap((n) => {
        if (n.type !== "array" || index < 0 || !Array.isArray(n.children) || index >= n.children.length) {
          return [];
        }

        return [n.children[index]];
      });
    }

    if (typeof segment === "string") {
      nodes = nodes.flatMap((n) => {
        if (n.type !== "object" || !Array.isArray(n.children)) {
          return [];
        }

        for (const prop of n.children) {
          if (Array.isArray(prop.children) && prop.children[0].value === segment) {
            if (prop.children.length === 2) {
              return [prop.children[1]];
            }
            if (includeUncompleteProps) {
              return [prop];
            }
          }
        }

        return [];
      });
    }
  }

  return nodes;
}
