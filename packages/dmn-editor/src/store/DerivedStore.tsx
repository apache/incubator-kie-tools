import * as React from "react";
import * as RF from "reactflow";
import { useMemo } from "react";
import { isValidContainment as _isValidContainment } from "../diagram/connections/isValidContainment";
import { NodeType } from "../diagram/connections/graphStructure";
import { useDmnEditorStore } from "./Store";
import { useDiagramData } from "./useDiagramData";
import {
  DMN15__tImport,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { DmnDiagramNodeData } from "../diagram/nodes/Nodes";
import { DmnDiagramEdgeData } from "../diagram/edges/Edges";

export type DerivedStore = {
  selectedNodeTypes: Set<NodeType>;
  isDropTargetNodeValidForSelection: boolean;
  isDiagramEditingInProgress: boolean;
  nodes: RF.Node[];
  edges: RF.Edge[];
  nodesById: Map<string, RF.Node<DmnDiagramNodeData>>;
  edgesById: Map<string, RF.Edge<DmnDiagramEdgeData>>;
  importsByNamespace: Map<string, DMN15__tImport>;
  dmnEdgesByDmnElementRef: Map<string, DMNDI15__DMNEdge & { index: number }>;
  dmnShapesByHref: Map<string, DMNDI15__DMNShape & { index: number }>;
};

const DmnEditorDerivedStoreContext = React.createContext<DerivedStore>({} as any);

export function useDmnEditorDerivedStore() {
  return React.useContext(DmnEditorDerivedStoreContext);
}

export function DmnEditorDerivedStoreContextProvider(props: React.PropsWithChildren<{}>) {
  const diagram = useDmnEditorStore((s) => s.diagram);
  const thisDmnsImports = useDmnEditorStore((s) => s.dmn.model.definitions.import ?? []);

  const importsByNamespace = useMemo(() => {
    const ret = new Map<string, DMN15__tImport>();
    for (let i = 0; i < thisDmnsImports.length; i++) {
      ret.set(thisDmnsImports[i]["@_namespace"], thisDmnsImports[i]);
    }
    return ret;
  }, [thisDmnsImports]);

  const { nodes, edges, nodesById, edgesById, dmnEdgesByDmnElementRef, dmnShapesByHref } = useDiagramData();

  const selectedNodeTypes = useMemo(() => {
    const ret = new Set<NodeType>();
    for (let i = 0; i < diagram.selectedNodes.length; i++) {
      ret.add(nodesById.get(diagram.selectedNodes[i])!.type as NodeType);
    }
    return ret;
  }, [diagram.selectedNodes, nodesById]);

  const isDropTargetNodeValidForSelection =
    !!diagram.dropTargetNode &&
    _isValidContainment({
      nodeTypes: selectedNodeTypes,
      inside: diagram.dropTargetNode.type as NodeType,
      dmnObjectQName: diagram.dropTargetNode.data.dmnObjectQName,
    });

  const isDiagramEditingInProgress =
    diagram.draggingNodes.length > 0 || diagram.resizingNodes.length > 0 || diagram.draggingWaypoints.length > 0;

  const value = useMemo(
    () => ({
      selectedNodeTypes,
      isDropTargetNodeValidForSelection,
      isDiagramEditingInProgress,
      nodes,
      edges,
      nodesById,
      edgesById,
      importsByNamespace,
      dmnEdgesByDmnElementRef,
      dmnShapesByHref,
    }),
    [
      selectedNodeTypes,
      isDropTargetNodeValidForSelection,
      isDiagramEditingInProgress,
      nodes,
      edges,
      nodesById,
      edgesById,
      importsByNamespace,
      dmnEdgesByDmnElementRef,
      dmnShapesByHref,
    ]
  );

  return <DmnEditorDerivedStoreContext.Provider value={value}>{props.children}</DmnEditorDerivedStoreContext.Provider>;
}
