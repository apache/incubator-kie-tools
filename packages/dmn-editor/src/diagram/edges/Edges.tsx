import * as React from "react";
import * as RF from "reactflow";
import { useCallback, useMemo } from "react";
import { DMNDI13__DMNEdge, DMNDI13__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { getSnappedMultiPointAnchoredEdgePath } from "./getSnappedMultiPointAnchoredEdgePath";

export type EdgeData = {
  dmnEdge: DMNDI13__DMNEdge | undefined;
  dmnShapeSource: DMNDI13__DMNShape | undefined;
  dmnShapeTarget: DMNDI13__DMNShape | undefined;
};

export function useKieEdgePath(source: string, target: string, data: EdgeData) {
  const sourceNode = RF.useStore(useCallback((store) => store.nodeInternals.get(source), [source]));
  const targetNode = RF.useStore(useCallback((store) => store.nodeInternals.get(target), [target]));
  const dmnEdge = data.dmnEdge as DMNDI13__DMNEdge | undefined;
  const dmnShapeSource = data.dmnShapeSource as DMNDI13__DMNShape | undefined;
  const dmnShapeTarget = data.dmnShapeTarget as DMNDI13__DMNShape | undefined;

  const { path, points } = useMemo(
    () =>
      getSnappedMultiPointAnchoredEdgePath({
        dmnEdge,
        sourceNode,
        targetNode,
        dmnShapeSource,
        dmnShapeTarget,
      }),
    [dmnEdge, dmnShapeSource, dmnShapeTarget, sourceNode, targetNode]
  );

  return path;
}

export function InformationRequirementPath(props: React.SVGProps<SVGPathElement>) {
  return (
    <>
      <path {...props} style={{ strokeWidth: 1, stroke: "black" }} markerEnd={"url(#closed-arrow)"} />
    </>
  );
}

export function KnowledgeRequirementPath(props: React.SVGProps<SVGPathElement>) {
  return (
    <>
      <path
        {...props}
        style={{ strokeWidth: 1, stroke: "black", strokeDasharray: "5,5" }}
        markerEnd={"url(#open-arrow)"}
      />
    </>
  );
}

export function AuthorityRequirementPath(props: React.SVGProps<SVGPathElement>) {
  return (
    <>
      <path
        {...props}
        style={{ strokeWidth: 1, stroke: "black", strokeDasharray: "5,5" }}
        markerEnd={"url(#closed-circle)"}
      />
    </>
  );
}

export function AssociationPath(props: React.SVGProps<SVGPathElement>) {
  return (
    <>
      <path {...props} style={{ strokeWidth: 1, stroke: "black", strokeDasharray: "2,10" }} />
    </>
  );
}

//

export function InformationRequirementEdge(props: RF.EdgeProps) {
  const path = useKieEdgePath(props.source, props.target, props.data);
  return <InformationRequirementPath d={path} />;
}
export function KnowledgeRequirementEdge(props: RF.EdgeProps) {
  const path = useKieEdgePath(props.source, props.target, props.data);
  return <KnowledgeRequirementPath d={path} />;
}
export function AuthorityRequirementEdge(props: RF.EdgeProps) {
  const path = useKieEdgePath(props.source, props.target, props.data);
  return <AuthorityRequirementPath d={path} />;
}
export function AssociationEdge(props: RF.EdgeProps) {
  const path = useKieEdgePath(props.source, props.target, props.data);
  return <AuthorityRequirementPath d={path} />;
}
