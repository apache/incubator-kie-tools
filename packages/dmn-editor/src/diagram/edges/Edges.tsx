import * as React from "react";
import * as RF from "reactflow";
import { useCallback, useMemo } from "react";
import {
  DC__Point,
  DMN15__tDefinitions,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { getSnappedMultiPointAnchoredEdgePath } from "./getSnappedMultiPointAnchoredEdgePath";
import { Unpacked } from "../useDmnDiagramData";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/Store";
import { useNodeHovered } from "../nodes/Nodes";
import { removeEdgeWaypoint } from "../../mutations/removeEdgeWaypoint";

const DEFAULT_EDGE_INTRACTION_WIDTH = 20;

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

export function Waypoint({
  edgeId,
  edgeIndex,
  index,
  point,
}: {
  edgeId: string;
  edgeIndex: number;
  index: number;
  point: DC__Point;
}) {
  const circleRef = React.useRef<SVGCircleElement>(null);
  const isHovered = useNodeHovered(circleRef);
  const { dispatch, diagram } = useDmnEditorStore();
  const { setState } = useDmnEditorStoreApi();

  return (
    <circle
      ref={circleRef}
      cx={point["@_x"]}
      cy={point["@_y"]}
      stroke={isHovered ? "red" : undefined}
      r={1}
      // FIXME: Tiago --> Use `d3-drag` for this. That's how ReactFlow does it.
      onMouseDown={() => {
        setState((state) => dispatch.diagram.setEdgeStatus(state, edgeId, { isDraggingWaypoint: true }));
        console.info("start");
      }}
      onMouseMove={(e) => {
        if (diagram.draggingWaypoints.find((s) => s === edgeId)) {
          return console.info("dragging", e);
        }
      }}
      onMouseUp={() => {
        setState((state) => dispatch.diagram.setEdgeStatus(state, edgeId, { isDraggingWaypoint: false }));
        console.info("end");
      }}
      onDoubleClick={(e) => {
        e.preventDefault();
        e.stopPropagation();

        setState((state) => {
          removeEdgeWaypoint({
            definitions: state.dmn.model.definitions,
            edgeIndex,
            waypointIndex: index,
          });
        });
      }}
    />
  );
}

export function Waypoints(props: { edgeId: string; edgeIndex: number; points: DC__Point[] }) {
  return (
    <g className={"kie-dmn-editor--diagram-edge-waypoints"}>
      {props.points.slice(1, -1).map((p, i) => (
        <Waypoint
          key={i}
          edgeIndex={props.edgeIndex}
          edgeId={props.edgeId}
          point={p}
          index={i + 1 /* Plus one because we're removing the 1st element of the array before iterating */}
        />
      ))}
    </g>
  );
}

export function useKieEdgePath(source: string, target: string, data: DmnEditorDiagramEdgeData | undefined) {
  const { diagram } = useDmnEditorStore();
  const sourceNode = RF.useStore(useCallback((store) => store.nodeInternals.get(source), [source]));
  const targetNode = RF.useStore(useCallback((store) => store.nodeInternals.get(target), [target]));
  const dmnEdge = data?.dmnEdge;
  const dmnShapeSource = data?.dmnShapeSource;
  const dmnShapeTarget = data?.dmnShapeTarget;

  return useMemo(
    () =>
      getSnappedMultiPointAnchoredEdgePath({
        snapGrid: diagram.snapGrid,
        dmnEdge,
        sourceNode,
        targetNode,
        dmnShapeSource,
        dmnShapeTarget,
      }),
    [diagram.snapGrid, dmnEdge, dmnShapeSource, dmnShapeTarget, sourceNode, targetNode]
  );
}

export const InformationRequirementPath = React.memo((props: React.SVGProps<SVGPathElement>) => {
  return (
    <>
      <path style={{ strokeWidth: 1, stroke: "black" }} markerEnd={"url(#closed-arrow)"} {...props} />
    </>
  );
});

export const KnowledgeRequirementPath = React.memo((props: React.SVGProps<SVGPathElement>) => {
  return (
    <>
      <path
        style={{ strokeWidth: 1, stroke: "black", strokeDasharray: "5,5" }}
        markerEnd={"url(#open-arrow)"}
        {...props}
      />
    </>
  );
});

export const AuthorityRequirementPath = React.memo(
  (_props: React.SVGProps<SVGPathElement> & { centerToConnectionPoint: boolean | undefined }) => {
    const { centerToConnectionPoint: center, ...props } = _props;
    return (
      <>
        <path
          style={{ strokeWidth: 1, stroke: "black", strokeDasharray: "5,5" }}
          markerEnd={center ? `url(#closed-circle-at-center)` : `url(#closed-circle-at-border)`}
          {...props}
        />
      </>
    );
  }
);

export const AssociationPath = React.memo((props: React.SVGProps<SVGPathElement>) => {
  const strokeWidth = props.strokeWidth ?? 1.5;
  return (
    <>
      <path
        strokeWidth={strokeWidth}
        strokeLinecap="butt"
        strokeLinejoin="round"
        style={{ stroke: "black", strokeDasharray: `${strokeWidth},10` }}
        {...props}
      />
    </>
  );
});

export function useEdgeClassName(isConnecting: boolean) {
  if (isConnecting) {
    return "dimmed";
  }

  return "normal";
}

//

const interactionStrokeProps = {
  strokeOpacity: 0.01,
  markerEnd: undefined,
  style: undefined,
  className: "react-flow__edge-interaction",
  stroke: "transparent",
};

export const InformationRequirementEdge = React.memo((props: RF.EdgeProps<DmnEditorDiagramEdgeData>) => {
  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const className = useEdgeClassName(isConnecting);
  const { path, points } = useKieEdgePath(props.source, props.target, props.data);
  return (
    <>
      <InformationRequirementPath
        d={path}
        {...interactionStrokeProps}
        strokeWidth={props.interactionWidth ?? DEFAULT_EDGE_INTRACTION_WIDTH}
      />
      <InformationRequirementPath d={path} className={`kie-dmn-editor--edge ${className}`} />
      {props.selected && !isConnecting && (
        <Waypoints edgeId={props.id} edgeIndex={props.data!.dmnEdge!.index} points={points} />
      )}
    </>
  );
});

export const KnowledgeRequirementEdge = React.memo((props: RF.EdgeProps<DmnEditorDiagramEdgeData>) => {
  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const className = useEdgeClassName(isConnecting);
  const { path, points } = useKieEdgePath(props.source, props.target, props.data);
  return (
    <>
      <KnowledgeRequirementPath
        d={path}
        {...interactionStrokeProps}
        strokeWidth={props.interactionWidth ?? DEFAULT_EDGE_INTRACTION_WIDTH}
      />
      <KnowledgeRequirementPath d={path} className={`kie-dmn-editor--edge ${className}`} />
      {props.selected && !isConnecting && (
        <Waypoints edgeId={props.id} edgeIndex={props.data!.dmnEdge!.index} points={points} />
      )}
    </>
  );
});

export const AuthorityRequirementEdge = React.memo((props: RF.EdgeProps<DmnEditorDiagramEdgeData>) => {
  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const className = useEdgeClassName(isConnecting);
  const { path, points } = useKieEdgePath(props.source, props.target, props.data);
  return (
    <>
      <AuthorityRequirementPath
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
      {props.selected && !isConnecting && (
        <Waypoints edgeId={props.id} edgeIndex={props.data!.dmnEdge!.index} points={points} />
      )}
    </>
  );
});

export const AssociationEdge = React.memo((props: RF.EdgeProps<DmnEditorDiagramEdgeData>) => {
  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const className = useEdgeClassName(isConnecting);
  const { path, points } = useKieEdgePath(props.source, props.target, props.data);
  return (
    <>
      <AssociationPath
        d={path}
        {...interactionStrokeProps}
        strokeWidth={props.interactionWidth ?? DEFAULT_EDGE_INTRACTION_WIDTH}
      />
      <AssociationPath d={path} className={`kie-dmn-editor--edge ${className}`} />
      {props.selected && !isConnecting && (
        <Waypoints edgeId={props.id} edgeIndex={props.data!.dmnEdge!.index} points={points} />
      )}
    </>
  );
});
