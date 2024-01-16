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

import * as RF from "reactflow";
import { EdgeType, NodeType, graphStructure } from "./graphStructure";
import { DmnDiagramNodeData } from "../nodes/Nodes";

export function checkIsValidConnection(
  nodesById: Map<string, RF.Node<DmnDiagramNodeData>>,
  edgeOrConnection: RF.Edge | RF.Connection,
  ongoingConnectionEdgeType: EdgeType | undefined
) {
  if (!edgeOrConnection.source || !edgeOrConnection.target) {
    return false;
  }

  const sourceNode = nodesById.get(edgeOrConnection.source);
  const targetNode = nodesById.get(edgeOrConnection.target);

  return _checkIsValidConnection(sourceNode, targetNode, ongoingConnectionEdgeType ?? edgeOrConnection.sourceHandle);
}

export function _checkIsValidConnection(
  sourceNode: { type?: string; data: DmnDiagramNodeData } | undefined,
  targetNode: { type?: string; data: DmnDiagramNodeData } | undefined,
  edgeType: string | null | undefined
) {
  if (!sourceNode?.type || !targetNode?.type || !edgeType) {
    return false;
  }

  // External nodes cannot be targeted
  if (targetNode.data.dmnObjectQName.prefix) {
    return false;
  }

  const ret =
    graphStructure
      .get(sourceNode.type as NodeType)
      ?.get(edgeType as EdgeType)
      ?.has(targetNode.type as NodeType) ?? false;

  return ret;
}
