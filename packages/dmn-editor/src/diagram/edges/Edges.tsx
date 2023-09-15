import {
  DMN15__tDefinitions,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import * as React from "react";
import * as RF from "reactflow";
import { Unpacked } from "../../store/useDiagramData";
import { PotentialWaypoint, Waypoints } from "./Waypoints";
import { useKieEdgePath } from "./useKieEdgePath";
import { useIsHovered } from "../useIsHovered";
import { usePotentialWaypointControls } from "./usePotentialWaypointControls";
import { useEdgeUpdatersAtEdgeTips } from "./useEdgeUpdatersAtEdgeTips";
import { DEFAULT_INTRACTION_WIDTH } from "../maths/DmnMaths";

export type DmnDiagramEdgeData = {
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

export function useEdgeClassName(isConnecting: boolean, isDraggingWaypoint: boolean) {
  if (isConnecting) {
    return "dimmed";
  }

  if (isDraggingWaypoint) {
    return "dragging-waypoint";
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

export const InformationRequirementEdge = React.memo((props: RF.EdgeProps<DmnDiagramEdgeData>) => {
  const { path, points: waypoints } = useKieEdgePath(props.source, props.target, props.data);

  const interactionPathRef = React.useRef<SVGPathElement>(null);
  const isHovered = useIsHovered(interactionPathRef);

  const { onMouseMove, onDoubleClick, potentialWaypoint, isDraggingWaypoint } = usePotentialWaypointControls(
    waypoints,
    props.selected,
    props.id,
    props.data?.dmnEdge?.index,
    interactionPathRef
  );

  const isConnecting = !!RF.useStore((s) => s.connectionNodeId);
  const className = useEdgeClassName(isConnecting, isDraggingWaypoint);

  const sourceNode = RF.useStore((s) => s.nodeInternals.get(props.source)!);
  const targetNode = RF.useStore((s) => s.nodeInternals.get(props.target)!);
  useEdgeUpdatersAtEdgeTips(interactionPathRef, sourceNode, targetNode, waypoints);

  return (
    <>
      <InformationRequirementPath
        svgRef={interactionPathRef}
        d={path}
        {...interactionStrokeProps}
        className={`${interactionStrokeProps.className} ${className}`}
        strokeWidth={props.interactionWidth ?? DEFAULT_INTRACTION_WIDTH}
        onMouseMove={onMouseMove}
        onDoubleClick={onDoubleClick}
      />
      <InformationRequirementPath d={path} className={`kie-dmn-editor--edge ${className}`} />
      {props.selected && !isConnecting && props.data?.dmnEdge && (
        <Waypoints
          edgeId={props.id}
          edgeIndex={props.data.dmnEdge.index}
          waypoints={waypoints}
          onDragStop={onMouseMove}
        />
      )}
      {isHovered && potentialWaypoint && <PotentialWaypoint point={potentialWaypoint.point} />}
    </>
  );
});

export const KnowledgeRequirementEdge = React.memo((props: RF.EdgeProps<DmnDiagramEdgeData>) => {
  const { path, points: waypoints } = useKieEdgePath(props.source, props.target, props.data);

  const interactionPathRef = React.useRef<SVGPathElement>(null);
  const isHovered = useIsHovered(interactionPathRef);

  const { onMouseMove, onDoubleClick, potentialWaypoint, isDraggingWaypoint } = usePotentialWaypointControls(
    waypoints,
    props.selected,
    props.id,
    props.data?.dmnEdge?.index,
    interactionPathRef
  );

  const isConnecting = !!RF.useStore((s) => s.connectionNodeId);
  const className = useEdgeClassName(isConnecting, isDraggingWaypoint);

  const sourceNode = RF.useStore((s) => s.nodeInternals.get(props.source)!);
  const targetNode = RF.useStore((s) => s.nodeInternals.get(props.target)!);
  useEdgeUpdatersAtEdgeTips(interactionPathRef, sourceNode, targetNode, waypoints);

  return (
    <>
      <KnowledgeRequirementPath
        svgRef={interactionPathRef}
        d={path}
        {...interactionStrokeProps}
        className={`${interactionStrokeProps.className} ${className}`}
        strokeWidth={props.interactionWidth ?? DEFAULT_INTRACTION_WIDTH}
        onMouseMove={onMouseMove}
        onDoubleClick={onDoubleClick}
      />
      <KnowledgeRequirementPath d={path} className={`kie-dmn-editor--edge ${className}`} />
      {props.selected && !isConnecting && props.data?.dmnEdge && (
        <Waypoints
          edgeId={props.id}
          edgeIndex={props.data.dmnEdge.index}
          waypoints={waypoints}
          onDragStop={onMouseMove}
        />
      )}
      {isHovered && potentialWaypoint && <PotentialWaypoint point={potentialWaypoint.point} />}
    </>
  );
});

export const AuthorityRequirementEdge = React.memo((props: RF.EdgeProps<DmnDiagramEdgeData>) => {
  const { path, points: waypoints } = useKieEdgePath(props.source, props.target, props.data);

  const interactionPathRef = React.useRef<SVGPathElement>(null);
  const isHovered = useIsHovered(interactionPathRef);

  const { onMouseMove, onDoubleClick, potentialWaypoint, isDraggingWaypoint } = usePotentialWaypointControls(
    waypoints,
    props.selected,
    props.id,
    props.data?.dmnEdge?.index,
    interactionPathRef
  );

  const isConnecting = !!RF.useStore((s) => s.connectionNodeId);
  const className = useEdgeClassName(isConnecting, isDraggingWaypoint);

  const sourceNode = RF.useStore((s) => s.nodeInternals.get(props.source)!);
  const targetNode = RF.useStore((s) => s.nodeInternals.get(props.target)!);
  useEdgeUpdatersAtEdgeTips(interactionPathRef, sourceNode, targetNode, waypoints);

  return (
    <>
      <AuthorityRequirementPath
        svgRef={interactionPathRef}
        d={path}
        centerToConnectionPoint={false}
        {...interactionStrokeProps}
        className={`${interactionStrokeProps.className} ${className}`}
        strokeWidth={props.interactionWidth ?? DEFAULT_INTRACTION_WIDTH}
        onMouseMove={onMouseMove}
        onDoubleClick={onDoubleClick}
      />
      <AuthorityRequirementPath
        d={path}
        className={`kie-dmn-editor--edge ${className}`}
        centerToConnectionPoint={false}
      />
      {props.selected && !isConnecting && props.data?.dmnEdge && (
        <Waypoints
          edgeId={props.id}
          edgeIndex={props.data.dmnEdge.index}
          waypoints={waypoints}
          onDragStop={onMouseMove}
        />
      )}
      {isHovered && potentialWaypoint && <PotentialWaypoint point={potentialWaypoint.point} />}
    </>
  );
});

export const AssociationEdge = React.memo((props: RF.EdgeProps<DmnDiagramEdgeData>) => {
  const { path, points: waypoints } = useKieEdgePath(props.source, props.target, props.data);

  const interactionPathRef = React.useRef<SVGPathElement>(null);
  const isHovered = useIsHovered(interactionPathRef);

  const { onMouseMove, onDoubleClick, potentialWaypoint, isDraggingWaypoint } = usePotentialWaypointControls(
    waypoints,
    props.selected,
    props.id,
    props.data?.dmnEdge?.index,
    interactionPathRef
  );

  const isConnecting = !!RF.useStore((s) => s.connectionNodeId);
  const className = useEdgeClassName(isConnecting, isDraggingWaypoint);

  const sourceNode = RF.useStore((s) => s.nodeInternals.get(props.source)!);
  const targetNode = RF.useStore((s) => s.nodeInternals.get(props.target)!);
  useEdgeUpdatersAtEdgeTips(interactionPathRef, sourceNode, targetNode, waypoints);

  return (
    <>
      <AssociationPath
        svgRef={interactionPathRef}
        d={path}
        {...interactionStrokeProps}
        className={`${interactionStrokeProps.className} ${className}`}
        strokeWidth={props.interactionWidth ?? DEFAULT_INTRACTION_WIDTH}
        onMouseMove={onMouseMove}
        onDoubleClick={onDoubleClick}
      />
      <AssociationPath d={path} className={`kie-dmn-editor--edge ${className}`} />
      {props.selected && !isConnecting && props.data?.dmnEdge && (
        <Waypoints
          edgeId={props.id}
          edgeIndex={props.data.dmnEdge.index}
          waypoints={waypoints}
          onDragStop={onMouseMove}
        />
      )}
      {isHovered && potentialWaypoint && <PotentialWaypoint point={potentialWaypoint.point} />}
    </>
  );
});
