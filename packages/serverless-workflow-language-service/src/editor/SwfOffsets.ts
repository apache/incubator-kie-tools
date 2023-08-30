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

import { ELsNode, findNodesAtLocation } from "@kie-tools/json-yaml-language-service/dist/channel";
import { FullTextOffsets } from "./index";

/**
 * Get All States Offsets for an easy access.
 *
 * @param args.rootNode the root node.
 * @returns the resulting FullTextOffsets
 */
export function getAllStateOffsets(args: { rootNode?: ELsNode }): FullTextOffsets {
  const result: FullTextOffsets = { states: {} };
  const allStateNames = findNodesAtLocation({ root: args.rootNode, path: ["states", "*", "name"] });

  allStateNames.forEach((node) => {
    if (!node.parent || !node.parent.parent) {
      return;
    }
    result.states[node.value] = {
      stateNameOffset: node.parent.offset,
      offset: {
        start: node.parent.parent.offset,
        end: node.parent.parent.offset + node.parent.parent.length,
      },
    };
  });

  return result;
}

/**
 * Get the Offset of a State Name.
 *
 * @param args.rootNode the root node.
 * @param args.stateName the state name to search.
 * @returns The offset of the stateName, or undefined if not found.
 */
export function getStateNameOffset(args: { rootNode?: ELsNode; stateName: string }): number | undefined {
  if (!args.rootNode || !args.stateName) {
    return;
  }

  const stateNode = findNodesAtLocation({
    root: args.rootNode,
    path: ["states", "*", "name"],
  }).filter((node) => node.value === args.stateName)[0];

  if (!stateNode || !stateNode.parent) {
    return undefined;
  }

  return stateNode.parent.offset;
}

/**
 * Get State Name from a given Offset, checking the whole state block.
 *
 * @param args.rootNode the root node.
 * @param args.offset any offset inside the state block
 * @returns the stateName found, null otherwise
 */
export function getStateNameFromOffset(args: { rootNode?: ELsNode; offset: number }): string | undefined {
  if (!args.rootNode || !args.offset) {
    return;
  }

  const fullTextOffsets = getAllStateOffsets({ rootNode: args.rootNode });

  for (const stateName in fullTextOffsets?.states) {
    const blockOffset = fullTextOffsets!.states[stateName].offset;
    if (args.offset >= blockOffset.start && args.offset <= blockOffset.end) {
      return stateName;
    }
  }

  return;
}
