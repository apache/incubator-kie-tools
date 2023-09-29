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
import { DataTypeIndex, DataType } from "../dataTypes/DataTypes";
import { buildFeelQNameFromNamespace } from "../feel/buildFeelQName";
import { useOtherDmns } from "../includedModels/DmnEditorDependenciesContext";
import { UniqueNameIndex } from "../Spec";

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
  allDataTypesById: DataTypeIndex;
  allTopLevelDataTypesByFeelName: DataTypeIndex;
  dmnEdgesByDmnElementRef: Map<string, DMNDI15__DMNEdge & { index: number }>;
  dmnShapesByHref: Map<string, DMNDI15__DMNShape & { index: number }>;
  allFeelVariableUniqueNames: UniqueNameIndex;
  allTopLevelItemDefinitionUniqueNames: UniqueNameIndex;
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

  const { dataTypesTree, allDataTypesById, allTopLevelDataTypesByFeelName } = useMemo(() => {
    const allDataTypesById: DataTypeIndex = new Map();
    const allTopLevelDataTypesByFeelName: DataTypeIndex = new Map();

    const otherDmnsDataTypeTree = thisDmnsImports.flatMap((_import) => {
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
        allDataTypesById,
        allTopLevelDataTypesByFeelName,
        undefined,
        new Set(),
        otherDmn.model.definitions["@_namespace"],
        thisDmn.model.definitions["@_namespace"]
      );
    });

    // Purposefully do thisDmn's after. This will make sure thisDmn's ItemDefintiions
    // take precedence over any external ones imported to the default namespace.
    const thisDmnsDataTypeTree = buildDataTypesTree(
      thisDmn.model.definitions.itemDefinition ?? [],
      thisDmnsImportsByNamespace,
      allDataTypesById,
      allTopLevelDataTypesByFeelName,
      undefined,
      new Set(),
      thisDmn.model.definitions["@_namespace"],
      thisDmn.model.definitions["@_namespace"]
    );

    return {
      dataTypesTree: [...thisDmnsDataTypeTree, ...otherDmnsDataTypeTree],
      allDataTypesById,
      allTopLevelDataTypesByFeelName,
    };
  }, [otherDmnsByNamespace, thisDmn.model.definitions, thisDmnsImports, thisDmnsImportsByNamespace]);

  const allTopLevelItemDefinitionUniqueNames = useMemo(() => {
    const ret: UniqueNameIndex = new Map();

    for (const [k, v] of allTopLevelDataTypesByFeelName.entries()) {
      ret.set(k, v.itemDefinition["@_id"]!);
    }

    return ret;
  }, [allTopLevelDataTypesByFeelName]);

  const allFeelVariableUniqueNames = useMemo(() => {
    const ret: UniqueNameIndex = new Map();

    const drgElements = thisDmn.model.definitions.drgElement ?? [];
    for (let i = 0; i < drgElements.length; i++) {
      const drgElement = drgElements[i];
      ret.set(drgElement["@_name"]!, drgElement["@_id"]!);
    }

    for (let i = 0; i < thisDmnsImports.length; i++) {
      const _import = thisDmnsImports[i];
      ret.set(_import["@_name"], _import["@_id"]!);
    }

    // FIXME: Tiago --> Add the names of external nodes here that come from imports namespaced with "". Inlcude all names, or only what is referenced into `thisDmn`?

    return ret;
  }, [thisDmn.model.definitions.drgElement, thisDmnsImports]);

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
      allDataTypesById,
      allTopLevelDataTypesByFeelName,
      allFeelVariableUniqueNames,
      allTopLevelItemDefinitionUniqueNames,
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
      allDataTypesById,
      allTopLevelDataTypesByFeelName,
      allFeelVariableUniqueNames,
      allTopLevelItemDefinitionUniqueNames,
    ]
  );

  return <DmnEditorDerivedStoreContext.Provider value={value}>{props.children}</DmnEditorDerivedStoreContext.Provider>;
}

function buildDataTypesTree(
  items: DMN15__tItemDefinition[],
  importsByNamespace: Map<string, DMN15__tImport>,
  allDataTypesById: DataTypeIndex,
  allTopLevelDataTypesByFeelName: DataTypeIndex,
  parentId: string | undefined,
  parents: Set<string>,
  namespace: string,
  relativeToNamespace: string
) {
  const dataTypesTree: DataType[] = [];

  for (let i = 0; i < items.length; i++) {
    const itemDefinition = items[i];

    const feelName = buildFeelQNameFromNamespace({
      importsByNamespace,
      namedElement: itemDefinition,
      namespace,
      relativeToNamespace,
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
        allDataTypesById,
        allTopLevelDataTypesByFeelName,
        itemDefinition["@_id"],
        new Set([...parents, itemDefinition["@_id"]!]),
        namespace,
        relativeToNamespace
      ),
    };

    dataTypesTree.push(dataType);
    allDataTypesById.set(itemDefinition["@_id"]!, dataType);

    if (parentId === undefined) {
      allTopLevelDataTypesByFeelName.set(feelName, dataType);
    }
  }

  return dataTypesTree;
}
