import * as React from "react";
import * as RF from "reactflow";
import { useMemo } from "react";
import { isValidContainment as _isValidContainment } from "../diagram/connections/isValidContainment";
import { NodeType } from "../diagram/connections/graphStructure";
import { useDmnEditorStore } from "./Store";
import { useDiagramData } from "./useDiagramData";
import {
  DMN15__tImport,
  DMN15__tItemDefinition,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { DmnDiagramNodeData } from "../diagram/nodes/Nodes";
import { DmnDiagramEdgeData } from "../diagram/edges/Edges";
import { DataTypesById as DataTypeIndex, DataType } from "../dataTypes/DataTypes";
import { buildFeelQNameFromNamespace } from "../feel/buildFeelQName";
import { useOtherDmns } from "../includedModels/DmnEditorDependenciesContext";

export type DerivedStore = {
  selectedNodeTypes: Set<NodeType>;
  isDropTargetNodeValidForSelection: boolean;
  isDiagramEditingInProgress: boolean;
  nodes: RF.Node[];
  edges: RF.Edge[];
  nodesById: Map<string, RF.Node<DmnDiagramNodeData>>;
  edgesById: Map<string, RF.Edge<DmnDiagramEdgeData>>;
  importsByNamespace: Map<string, DMN15__tImport>;
  dataTypesTree: DataType[];
  dataTypesById: DataTypeIndex;
  dataTypesByFeelName: DataTypeIndex;
  dmnEdgesByDmnElementRef: Map<string, DMNDI15__DMNEdge & { index: number }>;
  dmnShapesByHref: Map<string, DMNDI15__DMNShape & { index: number }>;
};

const DmnEditorDerivedStoreContext = React.createContext<DerivedStore>({} as any);

export function useDmnEditorDerivedStore() {
  return React.useContext(DmnEditorDerivedStoreContext);
}

export function DmnEditorDerivedStoreContextProvider(props: React.PropsWithChildren<{}>) {
  const diagram = useDmnEditorStore((s) => s.diagram);
  const thisDmn = useDmnEditorStore((s) => s.dmn ?? []);
  const thisDmnsImports = useDmnEditorStore((s) => s.dmn.model.definitions.import ?? []);

  const thisDmnsImportsByNamespace = useMemo(() => {
    const ret = new Map<string, DMN15__tImport>();
    for (let i = 0; i < thisDmnsImports.length; i++) {
      ret.set(thisDmnsImports[i]["@_namespace"], thisDmnsImports[i]);
    }
    return ret;
  }, [thisDmnsImports]);

  const { nodes, edges, nodesById, edgesById, dmnEdgesByDmnElementRef, dmnShapesByHref } = useDiagramData();

  const { otherDmnsByNamespace } = useOtherDmns();

  const { dataTypesTree, dataTypesById, dataTypesByFeelName } = useMemo(() => {
    const dataTypesById: DataTypeIndex = new Map();
    const dataTypesByFeelName: DataTypeIndex = new Map();

    const otherDmnsDataTypes = thisDmnsImports.flatMap((_import) => {
      const otherDmn = otherDmnsByNamespace[_import["@_namespace"]];
      if (!otherDmn) {
        console.warn(
          `DMN DIAGRAM: Can't determine External Data Types for model with namespace '${_import["@_namespace"]}' because it doesn't exist on the dependencies object.`
        );
        return [];
      }

      return buildDataTypesTree(
        otherDmn.model.definitions.itemDefinition ?? [],
        thisDmnsImportsByNamespace,
        dataTypesById,
        dataTypesByFeelName,
        undefined,
        new Set(),
        otherDmn.model.definitions["@_namespace"],
        thisDmn.model.definitions["@_namespace"]
      );
    });

    const thisDmnsDataTypes = buildDataTypesTree(
      thisDmn.model.definitions.itemDefinition ?? [],
      thisDmnsImportsByNamespace,
      dataTypesById,
      dataTypesByFeelName,
      undefined,
      new Set(),
      thisDmn.model.definitions["@_namespace"],
      thisDmn.model.definitions["@_namespace"]
    );

    return {
      dataTypesTree: [...thisDmnsDataTypes, ...otherDmnsDataTypes],
      dataTypesById,
      dataTypesByFeelName,
    };
  }, [otherDmnsByNamespace, thisDmn.model.definitions, thisDmnsImports, thisDmnsImportsByNamespace]);

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
      importsByNamespace: thisDmnsImportsByNamespace,
      dmnEdgesByDmnElementRef,
      dmnShapesByHref,
      dataTypesTree,
      dataTypesById,
      dataTypesByFeelName,
    }),
    [
      selectedNodeTypes,
      isDropTargetNodeValidForSelection,
      isDiagramEditingInProgress,
      nodes,
      edges,
      nodesById,
      edgesById,
      thisDmnsImportsByNamespace,
      dmnEdgesByDmnElementRef,
      dmnShapesByHref,
      dataTypesTree,
      dataTypesById,
      dataTypesByFeelName,
    ]
  );

  return <DmnEditorDerivedStoreContext.Provider value={value}>{props.children}</DmnEditorDerivedStoreContext.Provider>;
}

function buildDataTypesTree(
  items: DMN15__tItemDefinition[],
  importsByNamespace: Map<string, DMN15__tImport>,
  dataTypesById: DataTypeIndex,
  dataTypesByFeelName: DataTypeIndex,
  parentId: string | undefined,
  parents: Set<string>,
  namespace: string,
  thisDmnsNamespace: string
) {
  const dataTypesTree: DataType[] = [];

  for (let i = 0; i < items.length; i++) {
    const itemDefinition = items[i];

    const feelName = buildFeelQNameFromNamespace({
      importsByNamespace,
      namedElement: itemDefinition,
      namespace,
      thisDmnsNamespace,
    }).full;

    const dataType: DataType = {
      itemDefinition,
      index: i,
      parentId,
      parents,
      feelName,
      namespace,
      children: buildDataTypesTree(
        itemDefinition.itemComponent ?? [],
        importsByNamespace,
        dataTypesById,
        dataTypesByFeelName,
        itemDefinition["@_id"],
        new Set([...parents, itemDefinition["@_id"]!]),
        namespace,
        thisDmnsNamespace
      ),
    };

    dataTypesTree.push(dataType);
    dataTypesById.set(itemDefinition["@_id"]!, dataType);
    dataTypesByFeelName.set(feelName, dataType);
  }

  return dataTypesTree;
}
