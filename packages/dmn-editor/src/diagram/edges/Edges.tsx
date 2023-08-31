import {
  DMN15__tDefinitions,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import * as React from "react";
import { useCallback, useState } from "react";
import * as RF from "reactflow";
import { Unpacked } from "../useDmnDiagramData";
import { Waypoints } from "./Waypoints";
import { useKieEdgePath } from "./useKieEdgePath";
import { useIsHovered } from "../useIsHovered";
import { useDmnEditorDiagramContainer } from "../DiagramContainerContext";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/Store";
import { addEdgeWaypoint } from "../../mutations/addEdgeWaypoint";
import { snapPoint } from "../SnapGrid";

const DEFAULT_EDGE_INTRACTION_WIDTH = 40;

export type DmnEditorDiagramEdgeData = {
  dmnEdge: (DMNDI15__DMNEdge & { index: number }) | undefined;
  dmnObject: {
    id: string;
    type:
      | Unpacked<DMN15__tDefinitions["artifact"]>["__$$element"]
      | Unpacked<DMN15__tDefinitions["drgElement"]>["__$$element"];
    requirementType: "informationRequirement" | "knowledgeRequirement" | "authorityRequirement" | "association";
  };
  dmnShapeSource: DMNDI15__DMNShape | undefined;
  dmnShapeTarget: DMNDI15__DMNShape | undefined;
};

export const InformationRequirementPath = React.memo(
  (_props: React.SVGProps<SVGPathElement> & { svgRef?: React.RefObject<SVGPathElement> }) => {
    const { svgRef, ...props } = _props;
    return (
      <>
        <path ref={svgRef} style={{ strokeWidth: 1, stroke: "black" }} markerEnd={"url(#closed-arrow)"} {...props} />
      </>
    );
  }
);

export const KnowledgeRequirementPath = React.memo(
  (__props: React.SVGProps<SVGPathElement> & { svgRef?: React.RefObject<SVGPathElement> }) => {
    const { svgRef, ...props } = __props;
    return (
      <>
        <path
          ref={svgRef}
          style={{ strokeWidth: 1, stroke: "black", strokeDasharray: "5,5" }}
          markerEnd={"url(#open-arrow)"}
          {...props}
        />
      </>
    );
  }
);

export const AuthorityRequirementPath = React.memo(
  (
    __props: React.SVGProps<SVGPathElement> & { centerToConnectionPoint: boolean | undefined } & {
      svgRef?: React.RefObject<SVGPathElement>;
    }
  ) => {
    const { centerToConnectionPoint: center, svgRef, ...props } = __props;
    return (
      <>
        <path
          ref={svgRef}
          style={{ strokeWidth: 1, stroke: "black", strokeDasharray: "5,5" }}
          markerEnd={center ? `url(#closed-circle-at-center)` : `url(#closed-circle-at-border)`}
          {...props}
        />
      </>
    );
  }
);

export const AssociationPath = React.memo(
  (__props: React.SVGProps<SVGPathElement> & { svgRef?: React.RefObject<SVGPathElement> }) => {
    const strokeWidth = __props.strokeWidth ?? 1.5;
    const { svgRef, ...props } = __props;
    return (
      <>
        <path
          ref={svgRef}
          strokeWidth={strokeWidth}
          strokeLinecap="butt"
          strokeLinejoin="round"
          style={{ stroke: "black", strokeDasharray: `${strokeWidth},10` }}
          {...props}
        />
      </>
    );
  }
);

export function useEdgeClassName(isConnecting: boolean) {
  if (isConnecting) {
    return "dimmed";
  }

  return "normal";
}

//

const interactionStrokeProps: Partial<React.SVGAttributes<SVGPathElement>> = {
  strokeOpacity: 1,
  markerEnd: undefined,
  style: undefined,
  className: "react-flow__edge-interaction",
  stroke: "transparent",
  strokeLinecap: "round",
};

export const InformationRequirementEdge = React.memo((props: RF.EdgeProps<DmnEditorDiagramEdgeData>) => {
  const { path, points: waypoints } = useKieEdgePath(props.source, props.target, props.data);

  const interactionPathRef = React.useRef<SVGPathElement>(null);
  const isHovered = useIsHovered(interactionPathRef);

  //

  const { diagram } = useDmnEditorStore();
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const reactFlowInstance = RF.useReactFlow();

  const [potentialWaypoint, setPotentialWaypoint] =
    useState<ReturnType<typeof approximateClosestPoint> | undefined>(undefined);

  const { container } = useDmnEditorDiagramContainer();

  const onMouseMove = useCallback(
    (e: React.MouseEvent) => {
      const containerBounds = container.current!.getBoundingClientRect();
      const projectedPoint = reactFlowInstance.project({
        x: e.clientX - containerBounds.left,
        y: e.clientY - containerBounds.top,
      });

      setPotentialWaypoint(approximateClosestPoint(interactionPathRef.current!, [projectedPoint.x, projectedPoint.y]));
    },
    [container, reactFlowInstance]
  );

  const onDoubleClick = useCallback(() => {
    if (!potentialWaypoint || props.data?.dmnEdge?.index === undefined) {
      return;
    }

    const snappedPotentialWaypoint = snapPoint(diagram.snapGrid, {
      "@_x": potentialWaypoint.point.x,
      "@_y": potentialWaypoint.point.y,
    });

    const existingWaypoint = waypoints.find(
      (w) => w["@_x"] === snappedPotentialWaypoint["@_x"] && w["@_y"] === snappedPotentialWaypoint["@_y"]
    );
    if (existingWaypoint) {
      console.debug("Preventing overlapping waypoint creation.");
      return;
    }

    // This only works because the lines connecting waypoints are ALWAYS straight lines.
    // This code will stop working properly if the interpolation method changes.
    let i = 1;
    for (let currentLength = 0; currentLength < potentialWaypoint.lengthInPath; i++) {
      currentLength += Math.sqrt(
        distanceComponentsSquared([waypoints[i]["@_x"], waypoints[i]["@_y"]], {
          x: waypoints[i - 1]["@_x"],
          y: waypoints[i - 1]["@_y"],
        })
      );
    }

    dmnEditorStoreApi.setState((state) => {
      addEdgeWaypoint({
        definitions: state.dmn.model.definitions,
        beforeIndex: i - 1,
        edgeIndex: props.data!.dmnEdge!.index,
        waypoint: snappedPotentialWaypoint,
      });
    });
  }, [diagram.snapGrid, dmnEditorStoreApi, potentialWaypoint, props.data, waypoints]);

  //

  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const className = useEdgeClassName(isConnecting);

  return (
    <>
      <InformationRequirementPath
        svgRef={interactionPathRef}
        d={path}
        {...interactionStrokeProps}
        strokeWidth={props.interactionWidth ?? DEFAULT_EDGE_INTRACTION_WIDTH}
        onMouseMove={onMouseMove}
        onDoubleClick={onDoubleClick}
      />
      <InformationRequirementPath d={path} className={`kie-dmn-editor--edge ${className}`} />
      {props.selected && !isConnecting && props.data?.dmnEdge && (
        <Waypoints edgeId={props.id} edgeIndex={props.data.dmnEdge.index} waypoints={waypoints} />
      )}
      {isHovered && potentialWaypoint && (
        <circle r={4} fill={"red"} cx={potentialWaypoint.point.x} cy={potentialWaypoint.point.y}></circle>
      )}
    </>
  );
});

export const KnowledgeRequirementEdge = React.memo((props: RF.EdgeProps<DmnEditorDiagramEdgeData>) => {
  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const className = useEdgeClassName(isConnecting);
  const { path, points } = useKieEdgePath(props.source, props.target, props.data);
  const interactionPathRef = React.useRef<SVGPathElement>(null);
  return (
    <>
      <KnowledgeRequirementPath
        svgRef={interactionPathRef}
        d={path}
        {...interactionStrokeProps}
        strokeWidth={props.interactionWidth ?? DEFAULT_EDGE_INTRACTION_WIDTH}
      />
      <KnowledgeRequirementPath d={path} className={`kie-dmn-editor--edge ${className}`} />
      {props.selected && !isConnecting && props.data?.dmnEdge && (
        <Waypoints edgeId={props.id} edgeIndex={props.data.dmnEdge.index} waypoints={points} />
      )}
    </>
  );
});

export const AuthorityRequirementEdge = React.memo((props: RF.EdgeProps<DmnEditorDiagramEdgeData>) => {
  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const className = useEdgeClassName(isConnecting);
  const { path, points } = useKieEdgePath(props.source, props.target, props.data);
  const interactionPathRef = React.useRef<SVGPathElement>(null);
  return (
    <>
      <AuthorityRequirementPath
        svgRef={interactionPathRef}
        d={path}
        centerToConnectionPoint={false}
        {...interactionStrokeProps}
        strokeWidth={props.interactionWidth ?? DEFAULT_EDGE_INTRACTION_WIDTH}
      />
      <AuthorityRequirementPath
        d={path}
        className={`kie-dmn-editor--edge ${className}`}
        centerToConnectionPoint={false}
      />
      {props.selected && !isConnecting && props.data?.dmnEdge && (
        <Waypoints edgeId={props.id} edgeIndex={props.data.dmnEdge.index} waypoints={points} />
      )}
    </>
  );
});

export const AssociationEdge = React.memo((props: RF.EdgeProps<DmnEditorDiagramEdgeData>) => {
  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const className = useEdgeClassName(isConnecting);
  const { path, points } = useKieEdgePath(props.source, props.target, props.data);
  const interactionPathRef = React.useRef<SVGPathElement>(null);
  return (
    <>
      <AssociationPath
        svgRef={interactionPathRef}
        d={path}
        {...interactionStrokeProps}
        strokeWidth={props.interactionWidth ?? DEFAULT_EDGE_INTRACTION_WIDTH}
      />
      <AssociationPath d={path} className={`kie-dmn-editor--edge ${className}`} />
      {props.selected && !isConnecting && props.data?.dmnEdge && (
        <Waypoints edgeId={props.id} edgeIndex={props.data.dmnEdge.index} waypoints={points} />
      )}
    </>
  );
});

function approximateClosestPoint(
  pathNode: SVGPathElement,
  point: [number, number]
): { point: DOMPoint; lengthInPath: number } {
  const pathLength = pathNode.getTotalLength();
  let precision = Math.floor(pathLength / 10);
  let best: DOMPoint;
  let bestLength = 0;
  let bestDistance = Infinity;

  let scan: DOMPoint;
  let scanDistance: number;
  for (let scanLength = 0; scanLength <= pathLength; scanLength += precision) {
    scan = pathNode.getPointAtLength(scanLength);
    scanDistance = distanceComponentsSquared(point, scan);

    if (scanDistance < bestDistance) {
      best = scan;
      bestLength = scanLength;
      bestDistance = scanDistance;
    }
  }

  precision /= 2;

  while (precision > 1) {
    const bLength = bestLength - precision;
    const b = pathNode.getPointAtLength(bLength);
    const bDistance = distanceComponentsSquared(point, b);
    if (bLength >= 0 && bDistance < bestDistance) {
      best = b;
      bestLength = bLength;
      bestDistance = bDistance;
      continue;
    }

    const afterLength = bestLength + precision;
    const after = pathNode.getPointAtLength(afterLength);
    const afterDistance = distanceComponentsSquared(point, after);
    if (afterLength <= pathLength && afterDistance < bestDistance) {
      best = after;
      bestLength = afterLength;
      bestDistance = afterDistance;
      continue;
    }

    precision /= 2;
  }

  return { point: best!, lengthInPath: bestLength };
}

function distanceComponentsSquared(a: [number, number], b: { x: number; y: number }) {
  const dx = b.x - a[0];
  const dy = b.y - a[1];
  return dx * dx + dy * dy;
}
