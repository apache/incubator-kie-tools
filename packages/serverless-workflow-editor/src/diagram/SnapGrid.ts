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

import { SnapGrid } from "../store/Store";
import * as RF from "reactflow";
import { Bounds } from "./maths/Maths";

export function snapShapePosition(snapGrid: SnapGrid, bounds: Bounds) {
  return snapBoundsPosition(snapGrid, bounds);
}

export function snapBoundsPosition(snapGrid: SnapGrid, bounds: Bounds | undefined) {
  return {
    x: snap(snapGrid, "x", bounds!.x!),
    y: snap(snapGrid, "y", bounds!.y!),
  };
}

export function offsetShapePosition(bounds: Bounds, offset: { x: number; y: number }): Bounds {
  if (!bounds) {
    return bounds;
  }

  return {
    ...bounds,
    x: offset.x + bounds["x"]!,
    y: offset.y + bounds["y"]!,
  };
}

export function snapShapeDimensions(grid: SnapGrid, bounds: Bounds, minSizes: RF.Dimensions) {
  return snapBoundsDimensions(grid, bounds, minSizes);
}

export function snapBoundsDimensions(grid: SnapGrid, bounds: Bounds | undefined, minSizes: RF.Dimensions) {
  return {
    width: Math.max(snap(grid, "x", bounds!.width!), minSizes["width"]),
    height: Math.max(snap(grid, "y", bounds!.height!), minSizes["height"]),
  };
}

export function snapPoint(
  grid: SnapGrid,
  point: RF.XYPosition,
  method: "floor" | "ceil" | "round" = "round"
): RF.XYPosition {
  return {
    x: snap(grid, "x", point?.["x"], method),
    y: snap(grid, "y", point?.["y"], method),
  };
}

export function snap(
  grid: SnapGrid,
  coord: "x" | "y",
  value: number | undefined,
  method: "floor" | "ceil" | "round" = "round"
) {
  return grid.isEnabled //
    ? Math[method]((value ?? 0) / grid[coord]) * grid[coord]
    : value ?? 0;
}
