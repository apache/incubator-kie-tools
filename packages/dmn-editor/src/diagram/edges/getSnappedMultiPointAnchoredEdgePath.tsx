import * as RF from "reactflow";
import { getDiscretelyAutoPositionedEdgeParams } from "./getDiscretelyAutoPositionedEdgeParams";
import {
  DC__Point,
  DMNDI13__DMNEdge,
  DMNDI13__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { snapPoint } from "../SnapGrid";

export function getSnappedMultiPointAnchoredEdgePath({
  dmnEdge,
  sourceNode,
  targetNode,
  dmnShapeSource,
  dmnShapeTarget,
}: {
  dmnEdge: DMNDI13__DMNEdge | undefined;
  sourceNode: RF.Node<any, string | undefined> | undefined;
  targetNode: RF.Node<any, string | undefined> | undefined;
  dmnShapeSource: DMNDI13__DMNShape | undefined;
  dmnShapeTarget: DMNDI13__DMNShape | undefined;
}) {
  if (!sourceNode || !targetNode) {
    return { path: undefined, points: [] };
  }

  const points: DC__Point[] = new Array(dmnEdge?.["di:waypoint"]?.length ?? 2);

  const discreteAuto = getDiscretelyAutoPositionedEdgeParams(sourceNode, targetNode);

  if (dmnEdge?.["@_id"]?.endsWith("-AUTO-SOURCE-AUTO-TARGET")) {
    console.log("discrete - both");
    points[0] = { "@_x": discreteAuto.sx, "@_y": discreteAuto.sy };
    points[points.length - 1] = { "@_x": discreteAuto.tx, "@_y": discreteAuto.ty };
  }

  //
  else if (dmnEdge?.["@_id"]?.endsWith("-AUTO-SOURCE")) {
    console.log("discrete - source");
    points[0] = { "@_x": discreteAuto.sx, "@_y": discreteAuto.sy };
  }

  //
  else if (dmnEdge?.["@_id"]?.endsWith("-AUTO-TARGET")) {
    console.log("discrete - target");
    points[points.length - 1] = { "@_x": discreteAuto.tx, "@_y": discreteAuto.ty };
  }

  ///////

  if (!dmnEdge?.["di:waypoint"]) {
    console.warn("No waypoints found. Creating a default straight line.");
    points[0] = { "@_x": discreteAuto.sx, "@_y": discreteAuto.sy };
    points[points.length - 1] = { "@_x": discreteAuto.tx, "@_y": discreteAuto.ty };
  }

  //
  else if (dmnEdge?.["di:waypoint"].length < 2) {
    console.warn("Invalid waypoints for edge. Creating a default straight line.");
    points[0] = { "@_x": discreteAuto.sx, "@_y": discreteAuto.sy };
    points[points.length - 1] = { "@_x": discreteAuto.tx, "@_y": discreteAuto.ty };
  }

  //
  else {
    const firstWaypoint = dmnEdge["di:waypoint"][0];
    const secondWaypoint = points[1] ?? dmnEdge["di:waypoint"][1];
    const src = getSnappedHandlePosition(
      dmnShapeSource!,
      firstWaypoint,
      sourceNode,
      points.length === 2 ? centerPoint(targetNode) : snapPoint(secondWaypoint)
    );
    points[0] ??= src.point;

    const lastWaypoint = dmnEdge["di:waypoint"][dmnEdge["di:waypoint"].length - 1];
    const secondToLastWaypoint = points[points.length - 2] ?? dmnEdge["di:waypoint"][dmnEdge["di:waypoint"].length - 2];
    const tgt = getSnappedHandlePosition(
      dmnShapeTarget!,
      lastWaypoint,
      targetNode,
      points.length === 2 ? centerPoint(sourceNode) : snapPoint(secondToLastWaypoint)
    );
    points[points.length - 1] ??= tgt.point;
  }

  ///////

  // skip first and last elements, as they are pre-filled using the logic below.
  for (let i = 1; i < points.length - 1; i++) {
    points[i] = snapPoint({ ...(dmnEdge?.["di:waypoint"] ?? [])[i] });
  }

  const start = points[0];
  let path = `M ${start["@_x"]},${start["@_y"]}`;
  for (let i = 1; i < points.length - 1; i++) {
    const p = points[i];
    path += ` L ${p["@_x"]},${p["@_y"]} M ${p["@_x"]},${p["@_y"]}`;
  }
  const end = points[points.length - 1];
  path += ` L ${end["@_x"]},${end["@_y"]}`;

  return { path, points };
}

export function getSnappedHandlePosition(
  shape: DMNDI13__DMNShape,
  waypoint: DC__Point,
  snappedNode: RF.Node,
  snappedWaypoint2: DC__Point
) {
  const x = shape?.["dc:Bounds"]?.["@_x"] ?? 0;
  const y = shape?.["dc:Bounds"]?.["@_y"] ?? 0;
  const w = shape?.["dc:Bounds"]?.["@_width"] ?? 0;
  const h = shape?.["dc:Bounds"]?.["@_height"] ?? 0;

  const xx = snappedNode.positionAbsolute?.x ?? 0;
  const yy = snappedNode.positionAbsolute?.y ?? 0;
  const ww = snappedNode.width ?? 0;
  const hh = snappedNode.height ?? 0;

  const center = { "@_x": x + w / 2, "@_y": y + h / 2 };
  const left = { "@_x": x, "@_y": y + h / 2 };
  const right = { "@_x": x + w, "@_y": y + h / 2 };
  const top = { "@_x": x + w / 2, "@_y": y };
  const bottom = { "@_x": x + w / 2, "@_y": y + h };

  if (distance(center, waypoint) <= 1) {
    return {
      pos: "center",
      point: getNodeIntersection(snappedNode, snappedWaypoint2),
    };
  } else if (distance(top, waypoint) <= 1) {
    return {
      pos: "top",
      point: { "@_x": xx + ww / 2, "@_y": yy },
    };
  } else if (distance(right, waypoint) <= 1) {
    return {
      pos: "right",
      point: { "@_x": xx + ww, "@_y": yy + hh / 2 },
    };
  } else if (distance(bottom, waypoint) <= 1) {
    return {
      pos: "bottom",
      point: { "@_x": xx + ww / 2, "@_y": yy + hh },
    };
  } else if (distance(left, waypoint) <= 1) {
    return {
      pos: "left",
      point: { "@_x": xx, "@_y": yy + hh / 2 },
    };
  } else {
    console.warn("Can't find match of NSWE/Center handles. Using Center as default.");
    return {
      pos: "center",
      point: getNodeIntersection(snappedNode, snappedWaypoint2),
    };
  }
}

function distance(a: DC__Point, b: DC__Point) {
  return Math.sqrt(Math.pow(a["@_x"] - b["@_x"], 2) + Math.pow(a["@_y"] - b["@_y"], 2));
}

function centerPoint(node: RF.Node): DC__Point {
  const xx = node.positionAbsolute?.x ?? 0;
  const yy = node.positionAbsolute?.y ?? 0;
  const ww = node.width ?? 0;
  const hh = node.height ?? 0;

  return { "@_x": xx + ww / 2, "@_y": yy + hh / 2 };
}

// this helper function returns the intersection point
// of the line between the center of the intersectionNode and the target node
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

  const xx3 = a * xx1;
  const yy3 = a * yy1;

  const x = w * (xx3 + yy3) + x2;
  const y = h * (-xx3 + yy3) + y2;

  return { "@_x": x, "@_y": y };
}
