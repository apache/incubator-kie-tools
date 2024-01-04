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
import { useCallback, useMemo } from "react";
import { getSnappedMultiPointAnchoredEdgePath } from "./getSnappedMultiPointAnchoredEdgePath";
import { useDmnEditorStore } from "../../store/Store";
import { DmnDiagramEdgeData } from "./Edges";

export function useKieEdgePath(
  source: string | undefined,
  target: string | undefined,
  data: DmnDiagramEdgeData | undefined
) {
  const diagram = useDmnEditorStore((s) => s.diagram);
  const sourceNode = RF.useStore(
    useCallback((store) => (source ? store.nodeInternals.get(source) : undefined), [source])
  );
  const targetNode = RF.useStore(
    useCallback((store) => (target ? store.nodeInternals.get(target) : undefined), [target])
  );
  const dmnEdge = data?.dmnEdge;
  const dmnShapeSource = data?.dmnShapeSource;
  const dmnShapeTarget = data?.dmnShapeTarget;

  return useMemo(
    () =>
      getSnappedMultiPointAnchoredEdgePath({
        snapGrid: diagram.snapGrid,
        dmnEdge,
        sourceNode,
        targetNode,
        dmnShapeSource,
        dmnShapeTarget,
      }),
    [diagram.snapGrid, dmnEdge, dmnShapeSource, dmnShapeTarget, sourceNode, targetNode]
  );
}
