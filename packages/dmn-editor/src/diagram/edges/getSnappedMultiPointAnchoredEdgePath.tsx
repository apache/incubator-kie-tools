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
    return undefined;
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
    points[0] ??= getSnappedHandlePosition(
      //
      dmnShapeSource!,
      dmnEdge["di:waypoint"][0],
      sourceNode
    ).point;

    points[points.length - 1] ??= getSnappedHandlePosition(
      dmnShapeTarget!,
      dmnEdge["di:waypoint"][dmnEdge["di:waypoint"].length - 1],
      targetNode
    ).point;
  }

  ///////
  // skip first and last elements, as they are pre-filled using the logic below.
  for (let i = 1; i < points.length - 1; i++) {
    points[i] = snapPoint({ ...(dmnEdge?.["di:waypoint"] ?? [])[i] });
  }

  const start = snapPoint(points[0]);
  let path = `M ${start["@_x"]},${start["@_y"]}`;
  for (let i = 1; i < points.length - 1; i++) {
    const p = snapPoint(points[i]);
    path += ` L ${p["@_x"]},${p["@_y"]} M ${p["@_x"]},${p["@_y"]}`;
  }
  const end = snapPoint(points[points.length - 1]);
  path += ` L ${end["@_x"]},${end["@_y"]}`;

  return path;
}

export function getSnappedHandlePosition(shape: DMNDI13__DMNShape, waypoint: DC__Point, node: RF.Node) {
  const x = shape?.["dc:Bounds"]?.["@_x"] ?? 0;
  const y = shape?.["dc:Bounds"]?.["@_y"] ?? 0;
  const w = shape?.["dc:Bounds"]?.["@_width"] ?? 0;
  const h = shape?.["dc:Bounds"]?.["@_height"] ?? 0;

  const xx = node.positionAbsolute?.x ?? 0;
  const yy = node.positionAbsolute?.y ?? 0;
  const ww = node.width ?? 0;
  const hh = node.height ?? 0;

  const center = { "@_x": x + w / 2, "@_y": y + h / 2 };
  const left = { "@_x": x, "@_y": y + h / 2 };
  const right = { "@_x": x + w, "@_y": y + h / 2 };
  const top = { "@_x": x + w / 2, "@_y": y };
  const bottom = { "@_x": x + w / 2, "@_y": y + h };

  if (distance(center, waypoint) <= 1) {
    return {
      pos: "center",
      point: { "@_x": xx + ww / 2, "@_y": yy + hh / 2 },
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
      point: { "@_x": xx + ww / 2, "@_y": yy + hh / 2 },
    };
  }
}

function distance(a: DC__Point, b: DC__Point) {
  return Math.sqrt(Math.pow(a["@_x"] - b["@_x"], 2) + Math.pow(a["@_y"] - b["@_y"], 2));
}
