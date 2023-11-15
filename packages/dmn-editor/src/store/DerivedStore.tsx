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
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { UniqueNameIndex } from "../Dmn15Spec";
import { ExternalPmmlsIndex, ExternalDmnsIndex } from "../DmnEditor";
import { builtInFeelTypeNames } from "../dataTypes/BuiltInFeelTypes";

export type DerivedStore = {
  selectedNodeTypes: Set<NodeType>;
  isDropTargetNodeValidForSelection: boolean;
  isDiagramEditingInProgress: boolean;
  nodes: RF.Node[];
  edges: RF.Edge[];
  nodesById: Map<string, RF.Node<DmnDiagramNodeData>>;
  edgesById: Map<string, RF.Edge<DmnDiagramEdgeData>>;
  selectedNodesById: Map<string, RF.Node<DmnDiagramNodeData>>;
  selectedEdgesById: Map<string, RF.Edge<DmnDiagramEdgeData>>;
  importsByNamespace: Map<string, DMN15__tImport>;
  dataTypesTree: DataType[];
  allDataTypesById: DataTypeIndex;
  allTopLevelDataTypesByFeelName: DataTypeIndex;
  dmnEdgesByDmnElementRef: Map<string, DMNDI15__DMNEdge & { index: number }>;
  dmnShapesByHref: Map<string, DMNDI15__DMNShape & { index: number }>;
  drgElementsWithoutVisualRepresentationOnCurrentDrd: string[];
  allFeelVariableUniqueNames: UniqueNameIndex;
  allTopLevelItemDefinitionUniqueNames: UniqueNameIndex;
  externalDmnsByNamespace: ExternalDmnsIndex;
  externalPmmlsByNamespace: ExternalPmmlsIndex;
};

const DmnEditorDerivedStoreContext = React.createContext<DerivedStore>({} as any);

export function useDmnEditorDerivedStore() {
  return React.useContext(DmnEditorDerivedStoreContext);
}

export function DmnEditorDerivedStoreContextProvider(props: React.PropsWithChildren<{}>) {
  const diagram = useDmnEditorStore((s) => s.diagram);
  const thisDmn = useDmnEditorStore((s) => s.dmn);
  const thisDmnsImports = useMemo(() => thisDmn.model.definitions.import ?? [], [thisDmn.model.definitions.import]);

  const thisDmnsImportsByNamespace = useMemo(() => {
    const ret = new Map<string, DMN15__tImport>();
    for (let i = 0; i < thisDmnsImports.length; i++) {
      ret.set(thisDmnsImports[i]["@_namespace"], thisDmnsImports[i]);
    }
    return ret;
  }, [thisDmnsImports]);

  const { externalModelsByNamespace } = useExternalModels();

  const { dmns: externalDmnsByNamespace, pmmls: externalPmmlsByNamespace } = useMemo<{
    dmns: ExternalDmnsIndex;
    pmmls: ExternalPmmlsIndex;
  }>(() => {
    return thisDmnsImports.reduce<{ dmns: ExternalDmnsIndex; pmmls: ExternalPmmlsIndex }>(
      (acc, _import) => {
        const externalModel = externalModelsByNamespace?.[_import["@_namespace"]];
        if (!externalModel) {
          console.warn(
            `DMN DIAGRAM: Can't index external model with namespace '${_import["@_namespace"]}' because it doesn't exist on the external models list.`
          );
          return acc;
        }

        if (externalModel.type === "dmn") {
          acc.dmns.set(_import["@_namespace"], externalModel);
        } else if (externalModel.type === "pmml") {
          acc.pmmls.set(_import["@_namespace"], externalModel);
        } else {
          console.warn("DMN EDITOR: Unknown external model type", externalModel);
        }

        return acc;
      },
      { dmns: new Map(), pmmls: new Map() }
    );
  }, [externalModelsByNamespace, thisDmnsImports]);

  const {
    nodes,
    edges,
    nodesById,
    edgesById,
    dmnEdgesByDmnElementRef,
    dmnShapesByHref,
    selectedNodesById,
    selectedEdgesById,
    selectedNodeTypes,
    drgElementsWithoutVisualRepresentationOnCurrentDrd,
  } = useDiagramData(externalDmnsByNamespace);

  const { dataTypesTree, allDataTypesById, allTopLevelDataTypesByFeelName } = useMemo(() => {
    const allDataTypesById: DataTypeIndex = new Map();
    const allTopLevelDataTypesByFeelName: DataTypeIndex = new Map();

    const externalDmnsDataTypeTree = [...externalDmnsByNamespace.values()].flatMap((externalDmn) => {
      return buildDataTypesTree(
        externalDmn.model.definitions.itemDefinition ?? [],
        thisDmnsImportsByNamespace,
        allDataTypesById,
        allTopLevelDataTypesByFeelName,
        undefined,
        new Set(),
        externalDmn.model.definitions["@_namespace"],
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
      dataTypesTree: [...thisDmnsDataTypeTree, ...externalDmnsDataTypeTree],
      allDataTypesById,
      allTopLevelDataTypesByFeelName,
    };
  }, [externalDmnsByNamespace, thisDmn.model.definitions, thisDmnsImportsByNamespace]);

  const allTopLevelItemDefinitionUniqueNames = useMemo(() => {
    const ret: UniqueNameIndex = new Map();

    for (const [k, v] of allTopLevelDataTypesByFeelName.entries()) {
      ret.set(k, v.itemDefinition["@_id"]!);
    }

    for (const type of builtInFeelTypeNames) {
      ret.set(type, type);
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

    return ret;
  }, [thisDmn.model.definitions.drgElement, thisDmnsImports]);

  const isDropTargetNodeValidForSelection =
    !!diagram.dropTargetNode &&
    _isValidContainment({
      nodeTypes: selectedNodeTypes,
      inside: diagram.dropTargetNode.type as NodeType,
      dmnObjectQName: diagram.dropTargetNode.data.dmnObjectQName,
    });

  const isDiagramEditingInProgress =
    diagram.draggingNodes.length > 0 ||
    diagram.resizingNodes.length > 0 ||
    diagram.draggingWaypoints.length > 0 ||
    diagram.movingDividerLines.length > 0;

  const value = useMemo(
    () => ({
      selectedNodeTypes,
      isDropTargetNodeValidForSelection,
      isDiagramEditingInProgress,
      nodes,
      edges,
      nodesById,
      edgesById,
      selectedNodesById,
      selectedEdgesById,
      importsByNamespace: thisDmnsImportsByNamespace,
      dmnEdgesByDmnElementRef,
      dmnShapesByHref,
      dataTypesTree,
      allDataTypesById,
      allTopLevelDataTypesByFeelName,
      allFeelVariableUniqueNames,
      allTopLevelItemDefinitionUniqueNames,
      externalDmnsByNamespace,
      externalPmmlsByNamespace,
      drgElementsWithoutVisualRepresentationOnCurrentDrd,
    }),
    [
      selectedNodeTypes,
      isDropTargetNodeValidForSelection,
      isDiagramEditingInProgress,
      nodes,
      edges,
      nodesById,
      edgesById,
      selectedNodesById,
      selectedEdgesById,
      thisDmnsImportsByNamespace,
      dmnEdgesByDmnElementRef,
      dmnShapesByHref,
      dataTypesTree,
      allDataTypesById,
      allTopLevelDataTypesByFeelName,
      allFeelVariableUniqueNames,
      allTopLevelItemDefinitionUniqueNames,
      externalDmnsByNamespace,
      externalPmmlsByNamespace,
      drgElementsWithoutVisualRepresentationOnCurrentDrd,
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
