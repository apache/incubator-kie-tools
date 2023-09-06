import * as RF from "reactflow";
import { DC__Point } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { useLayoutEffect } from "react";
import { getNodeIntersection } from "../maths/DmnMaths";
import { scaleFromCenter } from "../maths/Maths";

export function useEdgeUpdatersAtEdgeTips(
  interactionPathRef: React.RefObject<SVGPathElement>,
  sourceNode: RF.Node,
  targetNode: RF.Node,
  waypoints: DC__Point[]
) {
  useLayoutEffect(() => {
    const edgeSvgGroup = interactionPathRef.current!.parentElement;

    const sourcePoint = getNodeIntersection(
      waypoints[1],
      scaleFromCenter(10, { position: sourceNode.positionAbsolute, dimensions: sourceNode })
    );
    const targetPoint = getNodeIntersection(
      waypoints[waypoints.length - 2],
      scaleFromCenter(10, { position: targetNode.positionAbsolute, dimensions: targetNode })
    );

    // source
    // const sourcePoint = interactionPathRef.current?.getPointAtLength(10);
    const edgeUpdaterSource = edgeSvgGroup!.querySelector(".react-flow__edgeupdater-source") as SVGCircleElement;
    edgeUpdaterSource.setAttribute("cx", "" + sourcePoint["@_x"]);
    edgeUpdaterSource.setAttribute("cy", "" + sourcePoint["@_y"]);

    // target
    // const targetPoint = interactionPathRef.current?.getPointAtLength(interactionPathRef.current?.getTotalLength() - 10);
    const edgeUpdaterTarget = edgeSvgGroup!.querySelector(".react-flow__edgeupdater-target") as SVGCircleElement;
    edgeUpdaterTarget.setAttribute("cx", "" + targetPoint!["@_x"]);
    edgeUpdaterTarget.setAttribute("cy", "" + targetPoint!["@_y"]);
  }, [interactionPathRef, sourceNode, targetNode, waypoints]);
}
