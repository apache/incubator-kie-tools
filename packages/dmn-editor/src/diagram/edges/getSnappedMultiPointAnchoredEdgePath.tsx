import {
  DC__Point,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import * as RF from "reactflow";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { snapPoint } from "../SnapGrid";
import { TargetHandleId } from "../connections/PositionalTargetNodeHandles";
import { getHandlePosition, getNodeCenterPoint, getNodeIntersection, pointsToPath } from "../maths/DmnMaths";
import { getDiscretelyAutoPositionedEdgeParamsForRfNodes } from "../maths/Maths";
import { AutoPositionedEdgeMarker } from "./AutoPositionedEdgeMarker";
import { SnapGrid } from "../../store/Store";

export function getSnappedMultiPointAnchoredEdgePath({
  snapGrid,
  dmnEdge,
  sourceNode,
  targetNode,
  dmnShapeSource,
  dmnShapeTarget,
}: {
  snapGrid: SnapGrid;
  dmnEdge: DMNDI15__DMNEdge | undefined;
  sourceNode: RF.Node<any, string | undefined> | undefined;
  targetNode: RF.Node<any, string | undefined> | undefined;
  dmnShapeSource: DMNDI15__DMNShape | undefined;
  dmnShapeTarget: DMNDI15__DMNShape | undefined;
}) {
  if (!sourceNode || !targetNode) {
    return { path: undefined, points: [] };
  }

  const points: DC__Point[] = new Array(Math.max(2, dmnEdge?.["di:waypoint"]?.length ?? 0));

  const discreteAuto = getDiscretelyAutoPositionedEdgeParamsForRfNodes(sourceNode, targetNode);

  if (dmnEdge?.["@_id"]?.endsWith(AutoPositionedEdgeMarker.BOTH)) {
    points[0] = { "@_x": discreteAuto.sx, "@_y": discreteAuto.sy };
    points[points.length - 1] = { "@_x": discreteAuto.tx, "@_y": discreteAuto.ty };
  } else if (dmnEdge?.["@_id"]?.endsWith(AutoPositionedEdgeMarker.SOURCE)) {
    points[0] = { "@_x": discreteAuto.sx, "@_y": discreteAuto.sy };
  } else if (dmnEdge?.["@_id"]?.endsWith(AutoPositionedEdgeMarker.TARGET)) {
    points[points.length - 1] = { "@_x": discreteAuto.tx, "@_y": discreteAuto.ty };
  }

  ///////

  if (!dmnEdge?.["di:waypoint"]) {
    console.warn("DMN DIAGRAM: No waypoints found. Creating a default straight line.");
    points[0] = { "@_x": discreteAuto.sx, "@_y": discreteAuto.sy };
    points[points.length - 1] = { "@_x": discreteAuto.tx, "@_y": discreteAuto.ty };
  } else if (dmnEdge?.["di:waypoint"].length < 2) {
    console.warn("DMN DIAGRAM: Invalid waypoints for edge. Creating a default straight line.");
    points[0] = { "@_x": discreteAuto.sx, "@_y": discreteAuto.sy };
    points[points.length - 1] = { "@_x": discreteAuto.tx, "@_y": discreteAuto.ty };
  } else {
    const firstWaypoint = dmnEdge["di:waypoint"][0];
    const secondWaypoint = points[1] ?? dmnEdge["di:waypoint"][1];
    const sourceHandlePoint = getSnappedHandlePosition(
      dmnShapeSource!,
      firstWaypoint,
      sourceNode,
      points.length === 2 ? getNodeCenterPoint(targetNode) : snapPoint(snapGrid, secondWaypoint)
    );
    points[0] ??= sourceHandlePoint;

    const lastWaypoint = dmnEdge["di:waypoint"][dmnEdge["di:waypoint"].length - 1];
    const secondToLastWaypoint = points[points.length - 2] ?? dmnEdge["di:waypoint"][dmnEdge["di:waypoint"].length - 2];
    const targetHandlePoint = getSnappedHandlePosition(
      dmnShapeTarget!,
      lastWaypoint,
      targetNode,
      points.length === 2 ? getNodeCenterPoint(sourceNode) : snapPoint(snapGrid, secondToLastWaypoint)
    );
    points[points.length - 1] ??= targetHandlePoint;
  }

  ///////

  // skip first and last elements, as they are pre-filled using the logic below.
  for (let i = 1; i < points.length - 1; i++) {
    points[i] = snapPoint(snapGrid, { ...(dmnEdge?.["di:waypoint"] ?? [])[i] });
  }

  return { path: pointsToPath(points), points };
}

export function getSnappedHandlePosition(
  shape: DMNDI15__DMNShape,
  handleWaypoint: DC__Point,
  snappedNode: RF.Node,
  snappedSecondWaypoint: DC__Point
): DC__Point {
  const position = getHandlePosition({ shapeBounds: shape["dc:Bounds"], waypoint: handleWaypoint });

  const xx = snappedNode.positionAbsolute?.x ?? 0;
  const yy = snappedNode.positionAbsolute?.y ?? 0;
  const ww = snappedNode.width ?? 0;
  const hh = snappedNode.height ?? 0;

  return switchExpression(position, {
    [TargetHandleId.TargetCenter]: getNodeIntersection(snappedSecondWaypoint, {
      position: snappedNode.positionAbsolute,
      dimensions: snappedNode,
    }),
    [TargetHandleId.TargetTop]: { "@_x": xx + ww / 2, "@_y": yy },
    [TargetHandleId.TargetRight]: { "@_x": xx + ww, "@_y": yy + hh / 2 },
    [TargetHandleId.TargetBottom]: { "@_x": xx + ww / 2, "@_y": yy + hh },
    [TargetHandleId.TargetLeft]: { "@_x": xx, "@_y": yy + hh / 2 },
  });
}
