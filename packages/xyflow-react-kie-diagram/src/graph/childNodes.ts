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
import { XyFlowDiagramState, XyFlowReactKieDiagramEdgeData, XyFlowReactKieDiagramNodeData } from "../store/State";

export function getDeepChildNodes<
  S extends XyFlowDiagramState<S, N, NData, EData>,
  N extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
>(nodeIds: string[], allNodes: RF.Node<NData, N>[]): Map<string, string[]> {
  const deepChildNodesById = new Map<string, string[]>();

  for (const nodeId of nodeIds) {
    const childNodeIds: string[] = [];

    for (const n of allNodes) {
      let p = n.data.parentXyFlowNode;
      while (p) {
        if (p.id === nodeId) {
          childNodeIds.push(n.id);
          break;
        } else {
          p = p?.data.parentXyFlowNode;
        }
      }
    }

    deepChildNodesById.set(nodeId, childNodeIds);
  }

  return deepChildNodesById;
}
