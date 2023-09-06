import * as RF from "reactflow";
import { useCallback, useMemo } from "react";
import { getSnappedMultiPointAnchoredEdgePath } from "./getSnappedMultiPointAnchoredEdgePath";
import { useDmnEditorStore } from "../../store/Store";
import { DmnDiagramEdgeData } from "./Edges";

export function useKieEdgePath(
  source: string | undefined,
  target: string | undefined,
  data: DmnDiagramEdgeData | undefined
) {
  const diagram = useDmnEditorStore((s) => s.diagram);
  const sourceNode = RF.useStore(
    useCallback((store) => (source ? store.nodeInternals.get(source) : undefined), [source])
  );
  const targetNode = RF.useStore(
    useCallback((store) => (target ? store.nodeInternals.get(target) : undefined), [target])
  );
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
