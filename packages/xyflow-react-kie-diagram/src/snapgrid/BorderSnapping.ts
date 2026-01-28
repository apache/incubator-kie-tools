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
import { getCenter } from "../maths/Maths";
import { DC__Bounds } from "../maths/model";
import { NodeSizes } from "../nodes/NodeSizes";
import { XyFlowDiagramState, XyFlowReactKieDiagramEdgeData, XyFlowReactKieDiagramNodeData } from "../store/State";
import { SnapGrid, snapBoundsDimensions, snapShapeDimensions, snapShapePosition } from "./SnapGrid";

export const DEFAULT_BORDER_ALLOWANCE_IN_PX = 14;
export const OFFSET_IN_PX_TO_MAKE_BORDER_SNAPPING_LOOK_CENTRALIZED_BASED_ON_STYLING_ON_BORDER_OF_CONTAINER = 5;

export function snapToDropTargetsBorder<
  S extends XyFlowDiagramState<S, N, NData, EData>,
  N extends string,
  NData extends XyFlowReactKieDiagramNodeData<N, NData>,
  EData extends XyFlowReactKieDiagramEdgeData,
>(
  dropTarget: NonNullable<S["xyFlowReactKieDiagram"]["dropTarget"]>,
  shapeBounds: DC__Bounds,
  nodeType: N,
  snapGrid: SnapGrid,
  minNodeSizes: NodeSizes<N>,
  borderAllowanceInPx: number
): RF.XYPosition {
  const dropTargetPosition = snapShapePosition(snapGrid, dropTarget.node.data.shape);
  const dropTargetDimensions = snapShapeDimensions(
    snapGrid,
    dropTarget.node.data.shape,
    minNodeSizes[dropTarget.node.type!]({ snapGrid })
  );

  const shapeDimensions = snapBoundsDimensions(snapGrid, shapeBounds, minNodeSizes[nodeType!]({ snapGrid }));
  const shapeCenterPoint = getCenter(
    shapeBounds["@_x"],
    shapeBounds["@_y"],
    shapeDimensions.width,
    shapeDimensions.height
  );

  // Rectangle coordinates
  const dropTargetX_min = dropTargetPosition.x;
  const dropTargetY_min = dropTargetPosition.y;
  const dropTargetX_max = dropTargetPosition.x + dropTargetDimensions.width;
  const dropTargetY_max = dropTargetPosition.y + dropTargetDimensions.height;

  // Calculate distances to each side
  const distanceLeft = Math.abs(shapeCenterPoint.x - dropTargetX_min);
  const distanceRight = Math.abs(shapeCenterPoint.x - dropTargetX_max);
  const distanceTop = Math.abs(shapeCenterPoint.y - dropTargetY_min);
  const distanceBottom = Math.abs(shapeCenterPoint.y - dropTargetY_max);

  let snappedX: number, snappedY: number;

  // By doing this in two steps, and non-exclusively (note the lack of `else`s),
  // we allow snapping to two sides at the same time, thus snapping to the corners.

  // Step 1: Snap
  if (distanceLeft <= borderAllowanceInPx) {
    snappedX =
      Math.round(dropTargetX_min - shapeDimensions.width / 2) +
      OFFSET_IN_PX_TO_MAKE_BORDER_SNAPPING_LOOK_CENTRALIZED_BASED_ON_STYLING_ON_BORDER_OF_CONTAINER; // Snap to the left side
  }
  if (distanceRight <= borderAllowanceInPx) {
    snappedX =
      Math.round(dropTargetX_max - shapeDimensions.width / 2) -
      OFFSET_IN_PX_TO_MAKE_BORDER_SNAPPING_LOOK_CENTRALIZED_BASED_ON_STYLING_ON_BORDER_OF_CONTAINER; // Snap to the right side
  }
  if (distanceTop <= borderAllowanceInPx) {
    snappedY =
      Math.round(dropTargetY_min - shapeDimensions.height / 2) +
      OFFSET_IN_PX_TO_MAKE_BORDER_SNAPPING_LOOK_CENTRALIZED_BASED_ON_STYLING_ON_BORDER_OF_CONTAINER; // Snap to the top side
  }
  if (distanceBottom <= borderAllowanceInPx) {
    snappedY =
      Math.round(dropTargetY_max - shapeDimensions.height / 2) -
      OFFSET_IN_PX_TO_MAKE_BORDER_SNAPPING_LOOK_CENTRALIZED_BASED_ON_STYLING_ON_BORDER_OF_CONTAINER; // Snap to the bottom side
  }

  // Step 2: Leave movement free if not already snapped.
  if (distanceLeft <= borderAllowanceInPx) {
    snappedY ??= shapeBounds["@_y"]; // Snap to the left side
  }
  if (distanceRight <= borderAllowanceInPx) {
    snappedY ??= shapeBounds["@_y"]; // Snap to the right side
  }
  if (distanceTop <= borderAllowanceInPx) {
    snappedX ??= shapeBounds["@_x"]; // Snap to the top side
  }
  if (distanceBottom <= borderAllowanceInPx) {
    snappedX ??= shapeBounds["@_x"]; // Snap to the bottom side
  }

  return { x: snappedX!, y: snappedY! };
}
