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

import { useMemo } from "react";
import * as RF from "reactflow";
import { useDmnEditorStore } from "../../store/StoreContext";
import { DmnDiagramEdgeData } from "./Edges";
import { getSnappedMultiPointAnchoredEdgePath } from "./getSnappedMultiPointAnchoredEdgePath";

export function useKieEdgePath(
  source: string | undefined,
  target: string | undefined,
  data: DmnDiagramEdgeData | undefined
) {
  const snapGrid = useDmnEditorStore((s) => s.diagram.snapGrid);

  const sourceNodeX = RF.useStore((s) => (source ? s.nodeInternals.get(source)?.positionAbsolute?.x : undefined));
  const sourceNodeY = RF.useStore((s) => (source ? s.nodeInternals.get(source)?.positionAbsolute?.y : undefined));
  const sourceNodeWidth = RF.useStore((s) => (source ? s.nodeInternals.get(source)?.width : undefined));
  const sourceNodeHeight = RF.useStore((s) => (source ? s.nodeInternals.get(source)?.height : undefined));

  const targetNodeX = RF.useStore((s) => (target ? s.nodeInternals.get(target)?.positionAbsolute?.x : undefined));
  const targetNodeY = RF.useStore((s) => (target ? s.nodeInternals.get(target)?.positionAbsolute?.y : undefined));
  const targetNodeWidth = RF.useStore((s) => (target ? s.nodeInternals.get(target)?.width : undefined));
  const targetNodeHeight = RF.useStore((s) => (target ? s.nodeInternals.get(target)?.height : undefined));

  const dmnEdge = data?.dmnEdge;
  const dmnShapeSource = data?.dmnShapeSource;
  const dmnShapeTarget = data?.dmnShapeTarget;

  return useMemo(
    () =>
      getSnappedMultiPointAnchoredEdgePath({
        snapGrid,
        dmnEdge,
        sourceNodeBounds: { x: sourceNodeX, y: sourceNodeY, width: sourceNodeWidth, height: sourceNodeHeight },
        targetNodeBounds: { x: targetNodeX, y: targetNodeY, width: targetNodeWidth, height: targetNodeHeight },
        dmnShapeSource,
        dmnShapeTarget,
      }),
    [
      dmnEdge,
      dmnShapeSource,
      dmnShapeTarget,
      snapGrid,
      sourceNodeHeight,
      sourceNodeWidth,
      sourceNodeX,
      sourceNodeY,
      targetNodeHeight,
      targetNodeWidth,
      targetNodeX,
      targetNodeY,
    ]
  );
}
