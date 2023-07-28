import * as RF from "reactflow";
import { DC__Bounds, DC__Point } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { getCenter } from "./Maths";
import { TargetHandleId } from "../connections/NodeHandles";

export function getDistance(a: DC__Point, b: DC__Point) {
  return Math.sqrt(Math.pow(a["@_x"] - b["@_x"], 2) + Math.pow(a["@_y"] - b["@_y"], 2));
}

export function getNodeCenterPoint(node: RF.Node | undefined): DC__Point {
  const { x, y } = getCenter(node?.positionAbsolute?.x, node?.positionAbsolute?.y, node?.width, node?.height);
  return { "@_x": x, "@_y": y };
}

export function getBoundsCenterPoint(bounds: DC__Bounds): DC__Point {
  const { x, y } = getCenter(bounds["@_x"], bounds["@_y"], bounds["@_width"], bounds["@_height"]);
  return { "@_x": x, "@_y": y };
}

export function getPointForHandle({ handle, bounds }: { bounds: DC__Bounds; handle: TargetHandleId }): DC__Point {
  if (handle === TargetHandleId.TargetCenter) {
    return getBoundsCenterPoint(bounds);
  } else if (handle === TargetHandleId.TargetTop) {
    return { "@_x": bounds["@_x"] + bounds["@_width"] / 2, "@_y": bounds["@_y"] };
  } else if (handle === TargetHandleId.TargetRight) {
    return { "@_x": bounds["@_x"] + bounds["@_width"], "@_y": bounds["@_y"] + bounds["@_height"] / 2 };
  } else if (handle === TargetHandleId.TargetBottom) {
    return { "@_x": bounds["@_x"] + bounds["@_width"] / 2, "@_y": bounds["@_y"] + bounds["@_height"] };
  } else if (handle === TargetHandleId.TargetLeft) {
    return { "@_x": bounds["@_x"], "@_y": bounds["@_y"] + bounds["@_height"] / 2 };
  } else {
    throw new Error(`Invalid target handle id '${handle}'.`);
  }
}

// this helper function returns the intersection point
// of the line between the center of the intersectionNode and `point`
export function getNodeIntersection(intersectionNode: RF.Node, point: DC__Point | undefined): DC__Point {
  // https://math.stackexchange.com/questions/1724792/an-algorithm-for-finding-the-intersection-point-between-a-center-of-vision-and-a

  const { width: nodeW, height: nodeH, positionAbsolute: node } = intersectionNode;

  const w = (nodeW ?? 0) / 2;
  const h = (nodeH ?? 0) / 2;

  const x2 = (node?.x ?? 0) + w;
  const y2 = (node?.y ?? 0) + h;
  const x1 = point?.["@_x"] ?? 0;
  const y1 = point?.["@_y"] ?? 0;

  const xx1 = (x1 - x2) / (2 * w) - (y1 - y2) / (2 * h);
  const yy1 = (x1 - x2) / (2 * w) + (y1 - y2) / (2 * h);

  const a = 1 / (Math.abs(xx1) + Math.abs(yy1));
  if (!Number.isFinite(a)) {
    return { "@_x": x1, "@_y": y1 };
  }

  const xx3 = a * xx1;
  const yy3 = a * yy1;

  const x = w * (xx3 + yy3) + x2;
  const y = h * (-xx3 + yy3) + y2;

  return { "@_x": x, "@_y": y };
}

export function pointsToPath(points: DC__Point[]): string {
  const start = points[0];
  let path = `M ${start["@_x"]},${start["@_y"]}`;
  for (let i = 1; i < points.length - 1; i++) {
    const p = points[i];
    path += ` L ${p["@_x"]},${p["@_y"]} M ${p["@_x"]},${p["@_y"]}`;
  }
  const end = points[points.length - 1];
  path += ` L ${end["@_x"]},${end["@_y"]}`;

  return path;
}
