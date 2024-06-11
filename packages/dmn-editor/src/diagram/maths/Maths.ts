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

import { DC__Point } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import * as RF from "reactflow";

export type Bounds = {
  width: number | undefined | null;
  height: number | undefined | null;
  x: number | undefined | null;
  y: number | undefined | null;
};

export function getDiscretelyAutoPositionedEdgeParams(src: Bounds, tgt: Bounds) {
  const [sx, sy, sourcePos] = getPositionalHandlePosition(src, tgt);
  const [tx, ty, targetPos] = getPositionalHandlePosition(tgt, src);
  return { sx, sy, tx, ty, sourcePos, targetPos };
}

// returns the position (top, right, bottom, or right) passed node compared to
export function getPositionalHandlePosition(a: Bounds, b: Bounds) {
  const centerA = getCenter(a.x, a.y, a.width, a.height);
  const centerB = getCenter(b.x, b.y, b.width, b.height);

  const horizontalDiff = Math.abs(centerA.x - centerB.x);
  const verticalDiff = Math.abs(centerA.y - centerB.y);

  let position;

  // when the horizontal difference between the nodes is bigger, we use Position.Left or Position.Right for the handle
  if (horizontalDiff > verticalDiff) {
    position = centerA.x > centerB.x ? RF.Position.Left : RF.Position.Right;
  } else {
    // here the vertical difference between the nodes is bigger, so we use Position.Top or Position.Bottom for the handle
    position = centerA.y > centerB.y ? RF.Position.Top : RF.Position.Bottom;
  }

  const [x, y] = getHandleCoordsByPosition(a, position);
  return [x, y, position] as const;
}

export function getCenter(
  x: number | null | undefined,
  y: number | null | undefined,
  width: number | null | undefined,
  height: number | null | undefined
) {
  return {
    x: (x ?? 0) + (width ?? 0) / 2,
    y: (y ?? 0) + (height ?? 0) / 2,
  };
}

export function scaleFromCenter(
  amount: number,
  node: {
    position: RF.XYPosition | undefined;
    dimensions: Pick<RF.Node, "width" | "height">;
  }
) {
  return {
    position: {
      x: (node.position?.x ?? 0) - amount,
      y: (node.position?.y ?? 0) - amount,
    },
    dimensions: {
      width: (node.dimensions.width ?? 0) + amount * 2,
      height: (node.dimensions.height ?? 0) + amount * 2,
    },
  };
}

function getHandleCoordsByPosition(node: Bounds, handlePosition: RF.Position) {
  let handleX = 0;
  let handleY = 0;

  switch (handlePosition) {
    case RF.Position.Left:
      handleX = 0;
      handleY = (node.height ?? 0) / 2;
      break;
    case RF.Position.Right:
      handleX = node.width ?? 0;
      handleY = (node.height ?? 0) / 2;
      break;
    case RF.Position.Top:
      handleX = (node.width ?? 0) / 2;
      handleY = 0;
      break;
    case RF.Position.Bottom:
      handleX = (node.width ?? 0) / 2;
      handleY = node.height ?? 0;
      break;
  }

  return [(node?.x ?? 0) + handleX, (node?.y ?? 0) + handleY];
}
export function getBoundsCenterPoint(node: Bounds | undefined): DC__Point {
  const { x, y } = getCenter(node?.x, node?.y, node?.width, node?.height);
  return { "@_x": x, "@_y": y };
}
