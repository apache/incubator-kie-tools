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
import { GraphStructure } from "../graph/graphStructure";

export function checkIsValidConnection<N extends string, E extends string, NData>(
  graphStructure: GraphStructure<N, E>,
  nodesById: Map<string, RF.Node<NData, N>>,
  edgeOrConnection: RF.Edge | RF.Connection,
  ongoingConnectionEdgeType: E | undefined
) {
  if (!edgeOrConnection.source || !edgeOrConnection.target) {
    return false;
  }

  const sourceNode = nodesById.get(edgeOrConnection.source);
  const targetNode = nodesById.get(edgeOrConnection.target);

  return _checkIsValidConnection(
    graphStructure,
    sourceNode,
    targetNode,
    ongoingConnectionEdgeType ?? edgeOrConnection.sourceHandle
  );
}

export function _checkIsValidConnection<N extends string, E extends string, NData>(
  graphStructure: GraphStructure<N, E>,
  sourceNode: { type?: string } | undefined,
  targetNode: { type?: string } | undefined,
  edgeType: string | null | undefined
) {
  if (!sourceNode?.type || !targetNode?.type || !edgeType) {
    return false;
  }

  const ret =
    graphStructure
      .get(sourceNode.type as N)
      ?.get(edgeType as E)
      ?.has(targetNode.type as N) ?? false;

  return ret;
}
