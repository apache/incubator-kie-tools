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

import { NodeNature } from "./NodeNature";
import { SwfEdge } from "../diagram/graph/graph";
import { deleteEdge } from "./deleteEdge";
import { Specification } from "@serverlessworkflow/sdk-typescript";

export function deleteNode({
  definitions,
  __readonly_swfEdges,
  __readonly_nodeNature,
  __readonly_swfObjectId,
}: {
  definitions: Specification.IWorkflow;
  __readonly_swfEdges: SwfEdge[];
  __readonly_nodeNature: NodeNature;
  __readonly_swfObjectId: string | undefined;
}) {
  // Delete Edges
  // We need to look for what SWF edges to delete when deleting a node from any SWF.
  const nodeId = __readonly_swfObjectId;

  for (let i = 0; i < __readonly_swfEdges.length; i++) {
    const swfEdge = __readonly_swfEdges[i];
    // Delete only edges where target points to node to be deleted
    // Edges originating from the node to be deleted will be removed with the node
    if (swfEdge.targetId === nodeId) {
      deleteEdge({
        definitions,
        edge: swfEdge,
      });
    }
  }

  // Delete the swfObject itself
  if (__readonly_nodeNature === NodeNature.SWF_STATE) {
    const nodeIndex = (definitions.states ?? []).findIndex((d) => d["name"] === __readonly_swfObjectId);
    definitions.states?.splice(nodeIndex, 1)?.[0];
  } else if (__readonly_nodeNature === NodeNature.UNKNOWN) {
    // Ignore. There's no swfObject here.
  } else {
    throw new Error(`SWF MUTATION: Unknown node nature '${__readonly_nodeNature}'.`);
  }
}
