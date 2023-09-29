import {
  DC__Point,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import * as RF from "reactflow";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { snapPoint } from "../SnapGrid";
import { TargetHandleId } from "../connections/PositionalTargetNodeHandles";
import {
  getLineRectangleIntersectionPoint,
  getHandlePosition,
  getNodeCenterPoint,
  pointsToPath,
} from "../maths/DmnMaths";
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
      sourceNode,
      firstWaypoint,
      points.length === 2 ? getNodeCenterPoint(targetNode) : snapPoint(snapGrid, secondWaypoint)
    );
    points[0] ??= sourceHandlePoint;

    const lastWaypoint = dmnEdge["di:waypoint"][dmnEdge["di:waypoint"].length - 1];
    const secondToLastWaypoint = points[points.length - 2] ?? dmnEdge["di:waypoint"][dmnEdge["di:waypoint"].length - 2];
    const targetHandlePoint = getSnappedHandlePosition(
      dmnShapeTarget!,
      targetNode,
      lastWaypoint,
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
  snappedNode: RF.Node,
  originalHandleWaypoint: DC__Point,
  snappedSecondWaypoint: DC__Point
): DC__Point {
  const { handlePosition } = getHandlePosition({ shapeBounds: shape["dc:Bounds"], waypoint: originalHandleWaypoint });

  const centerHandleWaypoint = getNodeCenterPoint(snappedNode);

  const nodeRectangle = {
    x: snappedNode.positionAbsolute?.x ?? 0,
    y: snappedNode.positionAbsolute?.y ?? 0,
    width: snappedNode.width ?? 0,
    height: snappedNode.height ?? 0,
  };

  return switchExpression(handlePosition, {
    [TargetHandleId.TargetTop]: { "@_x": nodeRectangle.x + nodeRectangle.width / 2, "@_y": nodeRectangle.y },
    [TargetHandleId.TargetRight]: {
      "@_x": nodeRectangle.x + nodeRectangle.width,
      "@_y": nodeRectangle.y + nodeRectangle.height / 2,
    },
    [TargetHandleId.TargetBottom]: {
      "@_x": nodeRectangle.x + nodeRectangle.width / 2,
      "@_y": nodeRectangle.y + nodeRectangle.height,
    },
    [TargetHandleId.TargetLeft]: { "@_x": nodeRectangle.x, "@_y": nodeRectangle.y + nodeRectangle.height / 2 },
    [TargetHandleId.TargetCenter]: getLineRectangleIntersectionPoint(
      snappedSecondWaypoint,
      centerHandleWaypoint,
      nodeRectangle
    ),
  });
}
