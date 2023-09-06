import * as RF from "reactflow";
import { DC__Point } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { useCallback, useEffect, useLayoutEffect } from "react";
import { getHandlePosition, getNodeIntersection, getPointForHandle } from "../maths/DmnMaths";
import { scaleFromCenter } from "../maths/Maths";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { TargetHandleId } from "../connections/PositionalTargetNodeHandles";

export function useEdgeUpdatersAtEdgeTips(
  interactionPathRef: React.RefObject<SVGPathElement>,
  sourceNode: RF.Node,
  targetNode: RF.Node,
  waypoints: DC__Point[]
) {
  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));

  useLayoutEffect(() => {
    const edgeSvgGroup = interactionPathRef.current!.parentElement;

    const edgeUpdaterSource = edgeSvgGroup!.querySelector(".react-flow__edgeupdater-source") as SVGCircleElement;
    const edgeUpdaterTarget = edgeSvgGroup!.querySelector(".react-flow__edgeupdater-target") as SVGCircleElement;

    if (isConnecting) {
      edgeUpdaterSource.classList.add("hidden");
      edgeUpdaterTarget.classList.add("hidden");
    } else {
      edgeUpdaterSource.classList.remove("hidden");
      edgeUpdaterTarget.classList.remove("hidden");
    }
  }, [interactionPathRef, isConnecting]);

  useLayoutEffect(() => {
    const edgeSvgGroup = interactionPathRef.current!.parentElement;

    // const sourceHandlePosition = getHandlePosition({
    //   shapeBounds: sourceNode.data.shape["dc:Bounds"],
    //   waypoint: waypoints[0],
    // });
    // const targetHandlePosition = getHandlePosition({
    //   shapeBounds: targetNode.data.shape["dc:Bounds"],
    //   waypoint: waypoints[waypoints.length - 1],
    // });

    // const scaledSourceNode = scaleFromCenter(10, { position: sourceNode.positionAbsolute, dimensions: sourceNode });
    // const sourcePoint = switchExpression(sourceHandlePosition, {
    //   [TargetHandleId.TargetCenter]: getNodeIntersection(waypoints[1], scaledSourceNode),
    //   default: getPointForHandle({
    //     handle: sourceHandlePosition,
    //     bounds: {
    //       "@_x": scaledSourceNode.position.x,
    //       "@_y": scaledSourceNode.position.y,
    //       "@_width": scaledSourceNode.dimensions.width,
    //       "@_height": scaledSourceNode.dimensions.height,
    //     },
    //   }),
    // });

    // const scaledTargetNode = scaleFromCenter(10, { position: targetNode.positionAbsolute, dimensions: targetNode });
    // const targetPoint = switchExpression(targetHandlePosition, {
    //   [TargetHandleId.TargetCenter]: getNodeIntersection(waypoints[waypoints.length - 2], scaledTargetNode),
    //   default: getPointForHandle({
    //     handle: targetHandlePosition,
    //     bounds: {
    //       "@_x": scaledTargetNode.position.x,
    //       "@_y": scaledTargetNode.position.y,
    //       "@_width": scaledTargetNode.dimensions.width,
    //       "@_height": scaledTargetNode.dimensions.height,
    //     },
    //   }),
    // });

    // source
    const sourcePoint = interactionPathRef.current?.getPointAtLength(10);
    const edgeUpdaterSource = edgeSvgGroup!.querySelector(".react-flow__edgeupdater-source") as SVGCircleElement;
    edgeUpdaterSource.setAttribute("cx", "" + sourcePoint!.x);
    edgeUpdaterSource.setAttribute("cy", "" + sourcePoint!.y);

    // target
    const targetPoint = interactionPathRef.current?.getPointAtLength(interactionPathRef.current?.getTotalLength() - 10);
    const edgeUpdaterTarget = edgeSvgGroup!.querySelector(".react-flow__edgeupdater-target") as SVGCircleElement;
    edgeUpdaterTarget.setAttribute("cx", "" + targetPoint!.x);
    edgeUpdaterTarget.setAttribute("cy", "" + targetPoint!.y);
  }, [interactionPathRef, sourceNode, targetNode, waypoints]);
}
