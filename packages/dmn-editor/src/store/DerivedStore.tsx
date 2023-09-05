import * as React from "react";
import * as RF from "reactflow";
import { useMemo } from "react";
import { isValidContainment as _isValidContainment } from "../diagram/connections/isValidContainment";
import { NodeType } from "../diagram/connections/graphStructure";
import { useDmnEditorStore } from "./Store";
import { useDiagramData } from "./useDiagramData";
import { DMNDI15__DMNEdge, DMNDI15__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export type DerivedStore = {
  selectedNodeTypes: Set<NodeType>;
  isDropTargetNodeValidForSelection: boolean;
  nodes: RF.Node[];
  edges: RF.Edge[];
  nodesById: Map<string, RF.Node>;
  dmnEdgesByDmnRefId: Map<string, DMNDI15__DMNEdge & { index: number }>;
  dmnShapesByDmnRefId: Map<string, DMNDI15__DMNShape & { index: number }>;
};

const DmnEditorDerivedStoreContext = React.createContext<DerivedStore>({} as any);

export function useDmnEditorDerivedStore() {
  return React.useContext(DmnEditorDerivedStoreContext);
}

export function DmnEditorDerivedStoreContextProvider(props: React.PropsWithChildren<{}>) {
  const diagram = useDmnEditorStore((s) => s.diagram);

  const { nodes, edges, nodesById, dmnEdgesByDmnRefId, dmnShapesByDmnRefId } = useDiagramData();

  const selectedNodeTypes = useMemo(() => {
    const ret = new Set<NodeType>();
    for (let i = 0; i < diagram.selectedNodes.length; i++) {
      ret.add(nodesById.get(diagram.selectedNodes[i])!.type as NodeType);
    }
    return ret;
  }, [diagram.selectedNodes, nodesById]);

  const isDropTargetNodeValidForSelection =
    !!diagram.dropTargetNode &&
    _isValidContainment({ nodeTypes: selectedNodeTypes, inside: diagram.dropTargetNode.type as NodeType });

  const value = useMemo(
    () => ({
      selectedNodeTypes,
      isDropTargetNodeValidForSelection,
      nodes,
      edges,
      nodesById,
      dmnEdgesByDmnRefId,
      dmnShapesByDmnRefId,
    }),
    [
      edges,
      dmnEdgesByDmnRefId,
      isDropTargetNodeValidForSelection,
      nodes,
      nodesById,
      selectedNodeTypes,
      dmnShapesByDmnRefId,
    ]
  );

  return <DmnEditorDerivedStoreContext.Provider value={value}>{props.children}</DmnEditorDerivedStoreContext.Provider>;
}
