import { DC__Point } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { useLayoutEffect } from "react";

export function useEdgeUpdatersAtEdgeTips(interactionPathRef: React.RefObject<SVGPathElement>, waypoints: DC__Point[]) {
  useLayoutEffect(() => {
    const edgeSvgGroup = interactionPathRef.current!.parentElement;

    // source
    const edgeUpdaterSource = edgeSvgGroup!.querySelector(".react-flow__edgeupdater-source") as SVGCircleElement;
    edgeUpdaterSource.setAttribute("cx", "" + waypoints[0]!["@_x"]);
    edgeUpdaterSource.setAttribute("cy", "" + waypoints[0]!["@_y"]);

    // target
    const edgeUpdaterTarget = edgeSvgGroup!.querySelector(".react-flow__edgeupdater-target") as SVGCircleElement;
    edgeUpdaterTarget.setAttribute("cx", "" + waypoints[waypoints.length - 1]!["@_x"]);
    edgeUpdaterTarget.setAttribute("cy", "" + waypoints[waypoints.length - 1]!["@_y"]);
  }, [interactionPathRef, waypoints]);
}
