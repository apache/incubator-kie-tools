import * as React from "react";
import * as RF from "reactflow";
import { useCallback, useMemo } from "react";
import { DMNDI13__DMNEdge, DMNDI13__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { getSnappedMultiPointAnchoredEdgePath } from "./getSnappedMultiPointAnchoredEdgePath";
import { useTargetStatus } from "../nodes/Nodes";

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

export function AuthorityRequirementPath(
  _props: React.SVGProps<SVGPathElement> & { centerToConnectionPoint: boolean | undefined }
) {
  const { centerToConnectionPoint: center, ...props } = _props;
  return (
    <>
      <path
        {...props}
        style={{ strokeWidth: 1, stroke: "black", strokeDasharray: "5,5" }}
        markerEnd={center ? `url(#closed-circle-at-center)` : `url(#closed-circle-at-border)`}
      />
    </>
  );
}

export function AssociationPath(props: React.SVGProps<SVGPathElement>) {
  const strokeWidth = props.strokeWidth ?? 1.5;
  return (
    <>
      <path
        {...props}
        strokeWidth={strokeWidth}
        strokeLinecap="butt"
        strokeLinejoin="round"
        style={{ stroke: "black", strokeDasharray: `${strokeWidth},10` }}
      />
    </>
  );
}

//

export function InformationRequirementEdge(props: RF.EdgeProps) {
  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const path = useKieEdgePath(props.source, props.target, props.data);
  return (
    <>
      <InformationRequirementPath d={path} className={`kie-dmn-editor--edge ${isConnecting ? "dimmed" : "normal"}`} />
    </>
  );
}
export function KnowledgeRequirementEdge(props: RF.EdgeProps) {
  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const path = useKieEdgePath(props.source, props.target, props.data);
  return (
    <>
      <KnowledgeRequirementPath d={path} className={`kie-dmn-editor--edge ${isConnecting ? "dimmed" : "normal"}`} />
    </>
  );
}
export function AuthorityRequirementEdge(props: RF.EdgeProps) {
  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const path = useKieEdgePath(props.source, props.target, props.data);
  return (
    <>
      <AuthorityRequirementPath
        d={path}
        className={`kie-dmn-editor--edge ${isConnecting ? "dimmed" : "normal"}`}
        centerToConnectionPoint={false}
      />
    </>
  );
}
export function AssociationEdge(props: RF.EdgeProps) {
  const isConnecting = !!RF.useStore(useCallback((state) => state.connectionNodeId, []));
  const path = useKieEdgePath(props.source, props.target, props.data);
  return (
    <>
      <AssociationPath d={path} className={`kie-dmn-editor--edge ${isConnecting ? "dimmed" : "normal"}`} />
    </>
  );
}
