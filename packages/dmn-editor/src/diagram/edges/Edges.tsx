import * as React from "react";
import * as RF from "reactflow";
import { useCallback, useMemo } from "react";
import { DMNDI13__DMNEdge, DMNDI13__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { getSnappedMultiPointAnchoredEdgePath } from "./getSnappedMultiPointAnchoredEdgePath";

export type DmnEditorDiagramEdgeData = {
  dmnEdge: (DMNDI13__DMNEdge & { index: number }) | undefined;
  dmnObject: {
    type: string;
    index: number;
    requirementType: "ir" | "kr" | "ar" | "a";
    requirementIndex: number;
  };
  dmnShapeSource: DMNDI13__DMNShape | undefined;
  dmnShapeTarget: DMNDI13__DMNShape | undefined;
};

export function useKieEdgePath(source: string, target: string, data: DmnEditorDiagramEdgeData | undefined) {
  const sourceNode = RF.useStore(useCallback((store) => store.nodeInternals.get(source), [source]));
  const targetNode = RF.useStore(useCallback((store) => store.nodeInternals.get(target), [target]));
  const dmnEdge = data?.dmnEdge;
  const dmnShapeSource = data?.dmnShapeSource;
  const dmnShapeTarget = data?.dmnShapeTarget;

  const { path } = useMemo(
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

export const InformationRequirementPath = React.memo((props: React.SVGProps<SVGPathElement>) => {
  return (
    <>
      <path {...props} style={{ strokeWidth: 1, stroke: "black" }} markerEnd={"url(#closed-arrow)"} />
    </>
  );
});

export const KnowledgeRequirementPath = React.memo((props: React.SVGProps<SVGPathElement>) => {
  return (
    <>
      <path
        {...props}
        style={{ strokeWidth: 1, stroke: "black", strokeDasharray: "5,5" }}
        markerEnd={"url(#open-arrow)"}
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
          {...props}
          style={{ strokeWidth: 1, stroke: "black", strokeDasharray: "5,5" }}
          markerEnd={center ? `url(#closed-circle-at-center)` : `url(#closed-circle-at-border)`}
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
        {...props}
        strokeWidth={strokeWidth}
        strokeLinecap="butt"
        strokeLinejoin="round"
        style={{ stroke: "black", strokeDasharray: `${strokeWidth},10` }}
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

export const InformationRequirementEdge = React.memo((props: RF.EdgeProps<DmnEditorDiagramEdgeData>) => {
  const className = useEdgeClassName();
  const path = useKieEdgePath(props.source, props.target, props.data);
  return (
    <>
      <InformationRequirementPath d={path} className={`kie-dmn-editor--edge ${className}`} />
    </>
  );
});

export const KnowledgeRequirementEdge = React.memo((props: RF.EdgeProps<DmnEditorDiagramEdgeData>) => {
  const className = useEdgeClassName();
  const path = useKieEdgePath(props.source, props.target, props.data);
  return (
    <>
      <KnowledgeRequirementPath d={path} className={`kie-dmn-editor--edge ${className}`} />
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
    </>
  );
});

export const AssociationEdge = React.memo((props: RF.EdgeProps<DmnEditorDiagramEdgeData>) => {
  const className = useEdgeClassName();
  const path = useKieEdgePath(props.source, props.target, props.data);
  return (
    <>
      <AssociationPath d={path} className={`kie-dmn-editor--edge ${className}`} />
    </>
  );
});
