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

import { findNodesAtLocation } from "./findNodesAtLocation";
import { nodeUpUntilType } from "./nodeUpUntilType";
import { ELsJsonPath, ELsNode } from "./types";

/**
 * Check if a Node is in Location.
 *
 * @param root root node
 * @param node the Node to check
 * @param path the location to verify
 * @returns true if the node is in the location, false otherwise
 */
export function matchNodeWithLocation(
  root: ELsNode | undefined,
  node: ELsNode | undefined,
  path: ELsJsonPath
): boolean {
  if (!root || !node || !path || !path.length) {
    return false;
  }

  const nodesAtLocation = findNodesAtLocation({ root, path, includeUncompleteProps: true });
  const nodeToMatch = nodeUpUntilType(node, ["object", "property"]);
  const starSelector = path[path.length - 1] === "*";

  if (starSelector && node.type === "array" && node.children) {
    return matchNodeWithLocation(root, node, path.slice(0, -1));
  }

  return nodesAtLocation.some(
    (currentNode) => (starSelector && currentNode === nodeToMatch) || (!starSelector && currentNode === node)
  );
}
