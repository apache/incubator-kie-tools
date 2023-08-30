import {
  DMN15__tDefinitions,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import * as React from "react";
import { useCallback } from "react";
import * as RF from "reactflow";
import { Unpacked } from "../useDmnDiagramData";
import { Waypoints } from "./Waypoints";
import { useKieEdgePath } from "./useKieEdgePath";

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
      {props.selected && !isConnecting && props.data?.dmnEdge && (
        <Waypoints edgeId={props.id} edgeIndex={props.data.dmnEdge.index} waypoints={points} />
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
  return (
    <>
      <AssociationPath
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
