import * as React from "react";
import * as RF from "reactflow";
import { useCallback, useMemo } from "react";
import {
  DMN15__tDefinitions,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { getSnappedMultiPointAnchoredEdgePath } from "./getSnappedMultiPointAnchoredEdgePath";
import { Unpacked } from "../useDmnDiagramData";
import { useDmnEditorStore } from "../../store/Store";

const DEFAULT_EDGE_INTRACTION_WDITH = 20;

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

export function useKieEdgePath(source: string, target: string, data: DmnEditorDiagramEdgeData | undefined) {
  const { diagram } = useDmnEditorStore();
  const sourceNode = RF.useStore(useCallback((store) => store.nodeInternals.get(source), [source]));
  const targetNode = RF.useStore(useCallback((store) => store.nodeInternals.get(target), [target]));
  const dmnEdge = data?.dmnEdge;
  const dmnShapeSource = data?.dmnShapeSource;
  const dmnShapeTarget = data?.dmnShapeTarget;

  const { path } = useMemo(
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

  return path;
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

export function useEdgeClassName() {
  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));
  if (isConnecting) {
    return "dimmed";
  }

  return "normal";
}

//

const interactionStrokeProps = {
  strokeOpacity: 0,
  markerEnd: undefined,
  style: undefined,
  className: "react-flow__edge-interaction",
  stroke: "transparent",
};

export const InformationRequirementEdge = React.memo((props: RF.EdgeProps<DmnEditorDiagramEdgeData>) => {
  const className = useEdgeClassName();
  const path = useKieEdgePath(props.source, props.target, props.data);
  return (
    <>
      <InformationRequirementPath d={path} className={`kie-dmn-editor--edge ${className}`} />

      <InformationRequirementPath
        d={path}
        {...interactionStrokeProps}
        strokeWidth={props.interactionWidth ?? DEFAULT_EDGE_INTRACTION_WDITH}
      />
    </>
  );
});

export const KnowledgeRequirementEdge = React.memo((props: RF.EdgeProps<DmnEditorDiagramEdgeData>) => {
  const className = useEdgeClassName();
  const path = useKieEdgePath(props.source, props.target, props.data);
  return (
    <>
      <KnowledgeRequirementPath d={path} className={`kie-dmn-editor--edge ${className}`} />

      <KnowledgeRequirementPath
        d={path}
        {...interactionStrokeProps}
        strokeWidth={props.interactionWidth ?? DEFAULT_EDGE_INTRACTION_WDITH}
      />
    </>
  );
});

export const AuthorityRequirementEdge = React.memo((props: RF.EdgeProps<DmnEditorDiagramEdgeData>) => {
  const className = useEdgeClassName();
  const path = useKieEdgePath(props.source, props.target, props.data);
  return (
    <>
      <AuthorityRequirementPath
        d={path}
        className={`kie-dmn-editor--edge ${className}`}
        centerToConnectionPoint={false}
      />
      <AuthorityRequirementPath
        d={path}
        centerToConnectionPoint={false}
        {...interactionStrokeProps}
        strokeWidth={props.interactionWidth ?? DEFAULT_EDGE_INTRACTION_WDITH}
      />
    </>
  );
});

export const AssociationEdge = React.memo((props: RF.EdgeProps<DmnEditorDiagramEdgeData>) => {
  const className = useEdgeClassName();
  const path = useKieEdgePath(props.source, props.target, props.data);
  return (
    <>
      <AssociationPath d={path} className={`kie-dmn-editor--edge ${className}`} />
      {(props.interactionWidth ?? DEFAULT_EDGE_INTRACTION_WDITH) && (
        <AssociationPath
          d={path}
          {...interactionStrokeProps}
          strokeWidth={props.interactionWidth ?? DEFAULT_EDGE_INTRACTION_WDITH}
        />
      )}
    </>
  );
});
