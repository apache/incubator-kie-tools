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

import {
  DC__Bounds,
  DC__Dimension,
  DC__Point,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { SnapGrid } from "../store/Store";

export function snapShapePosition(snapGrid: SnapGrid, shape: DMNDI15__DMNShape) {
  return snapBoundsPosition(snapGrid, shape["dc:Bounds"]);
}

export function snapBoundsPosition(snapGrid: SnapGrid, bounds: DC__Bounds | undefined) {
  return {
    x: snap(snapGrid, "x", bounds?.["@_x"]),
    y: snap(snapGrid, "y", bounds?.["@_y"]),
  };
}

export function offsetShapePosition(shape: DMNDI15__DMNShape, offset: { x: number; y: number }): DMNDI15__DMNShape {
  if (!shape["dc:Bounds"]) {
    return shape;
  }

  return {
    ...shape,
    "dc:Bounds": {
      ...shape["dc:Bounds"],
      "@_x": offset.x + shape["dc:Bounds"]["@_x"],
      "@_y": offset.y + shape["dc:Bounds"]["@_y"],
    },
  };
}

export function snapShapeDimensions(grid: SnapGrid, shape: DMNDI15__DMNShape, minSizes: DC__Dimension) {
  return snapBoundsDimensions(grid, shape["dc:Bounds"], minSizes);
}

export function snapBoundsDimensions(grid: SnapGrid, bounds: DC__Bounds | undefined, minSizes: DC__Dimension) {
  return {
    width: Math.max(snap(grid, "x", bounds?.["@_width"]), minSizes["@_width"]),
    height: Math.max(snap(grid, "y", bounds?.["@_height"]), minSizes["@_height"]),
  };
}

export function snapPoint(grid: SnapGrid, point: DC__Point, method: "floor" | "ceil" | "round" = "round"): DC__Point {
  return {
    "@_x": snap(grid, "x", point?.["@_x"], method),
    "@_y": snap(grid, "y", point?.["@_y"], method),
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
