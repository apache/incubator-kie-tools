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
import { getSnappedMultiPointAnchoredEdgePath, MultiPointAnchoredEdge } from "./getSnappedMultiPointAnchoredEdgePath";
import { DC__Dimension, DC__Shape } from "../maths/model";
import { useXyFlowReactKieDiagramStore } from "../store/Store";
import { snapBounds, SnapGrid } from "../snapgrid/SnapGrid";

export function usePathForEdgeWithWaypoints(
  edge: MultiPointAnchoredEdge | undefined,
  shapeSource: DC__Shape | undefined,
  shapeTarget: DC__Shape | undefined,
  sourceMinSizes: undefined | ((args: { snapGrid: SnapGrid }) => DC__Dimension),
  targetMinSizes: undefined | ((args: { snapGrid: SnapGrid }) => DC__Dimension)
) {
  const snapGridForWaypoints = useXyFlowReactKieDiagramStore((s) => ({
    isEnabled: s.xyFlowReactKieDiagram.snapGrid.isEnabled,
    x: s.xyFlowReactKieDiagram.snapGrid.x / 2,
    y: s.xyFlowReactKieDiagram.snapGrid.y / 2,
  }));

  const snapGrid = useXyFlowReactKieDiagramStore((s) => s.xyFlowReactKieDiagram.snapGrid);

  return useMemo(
    () =>
      getSnappedMultiPointAnchoredEdgePath({
        snapGrid: snapGridForWaypoints,
        edge,
        snappedSourceNodeBounds: snapBounds(
          snapGrid,
          shapeSource?.["dc:Bounds"],
          sourceMinSizes?.({ snapGrid }) ?? { "@_height": 0, "@_width": 0 }
        ),
        snappedTargetNodeBounds: snapBounds(
          snapGrid,
          shapeTarget?.["dc:Bounds"],
          targetMinSizes?.({ snapGrid }) ?? { "@_height": 0, "@_width": 0 }
        ),
        shapeSource,
        shapeTarget,
      }),
    [edge, shapeSource, shapeTarget, snapGrid, snapGridForWaypoints, sourceMinSizes, targetMinSizes]
  );
}
