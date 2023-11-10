import { createContext, useContext } from "react";
import * as RF from "reactflow";
import { StoreApi, UseBoundStore, create } from "zustand";
import { WithImmer, immer } from "zustand/middleware/immer";
import { useStoreWithEqualityFn } from "zustand/traditional";
import { DmnLatestModel } from "@kie-tools/dmn-marshaller";
import { DmnDiagramNodeData } from "../diagram/nodes/Nodes";

export interface DmnEditorDiagramNodeStatus {
  selected: boolean;
  dragging: boolean;
  resizing: boolean;
}
export interface DmnEditorDiagramEdgeStatus {
  selected: boolean;
  draggingWaypoint: boolean;
}

export interface DmnEditorDiagramDividerLineStatus {
  moving: boolean;
}

export interface SnapGrid {
  isEnabled: boolean;
  x: number;
  y: number;
}

export enum DiagramNodesPanel {
  NONE = "NONE",
  DRG_NODES = "DRG_NODES",
  EXTERNAL_NODES = "EXTERNAL_NODES",
}

export type DropTargetNode = undefined | RF.Node<DmnDiagramNodeData>;

export interface State {
  dispatch: Dispatch;
  dmn: { model: DmnLatestModel };
  boxedExpressionEditor: {
    activeDrgElementId: string | undefined;
    selectedObjectId: string | undefined;
    propertiesPanel: {
      isOpen: boolean;
    };
  };
  dataTypesEditor: {
    activeItemDefinitionId: string | undefined;
    expandedItemComponentIds: string[];
  };
  navigation: {
    tab: DmnEditorTab;
  };
  diagram: {
    drdIndex: number;
    edgeIdBeingUpdated: string | undefined;
    dropTargetNode: DropTargetNode;
    ongoingConnection: RF.OnConnectStartParams | undefined;
    propertiesPanel: {
      isOpen: boolean;
      elementId: string | undefined;
    };
    overlaysPanel: {
      isOpen: boolean;
    };
    openNodesPanel: DiagramNodesPanel;
    drdSelector: {
      isOpen: boolean;
    };
    overlays: {
      enableNodeHierarchyHighlight: boolean;
      enableExecutionHitsHighlights: boolean;
      enableCustomNodeStyles: boolean;
      enableDataTypesToolbarOnNodes: boolean;
    };
    snapGrid: SnapGrid;
    _selectedNodes: Array<string>;
    _selectedEdges: Array<string>;
    draggingNodes: Array<string>;
    resizingNodes: Array<string>;
    draggingWaypoints: Array<string>;
    movingDividerLines: Array<string>;
  };
}

export type Dispatch = {
  dmn: {
    reset: (model: State["dmn"]["model"]) => void;
  };
  boxedExpressionEditor: {
    open: (state: State, id: string) => void;
    close: (state: State) => void;
  };
  diagram: {
    setNodeStatus: (state: State, nodeId: string, status: Partial<DmnEditorDiagramNodeStatus>) => void;
    setEdgeStatus: (state: State, edgeId: string, status: Partial<DmnEditorDiagramEdgeStatus>) => void;
    setDividerLineStatus: (
      state: State,
      decisionServiceId: string,
      status: Partial<DmnEditorDiagramDividerLineStatus>
    ) => void;
  };
};

export enum DmnEditorTab {
  EDITOR,
  DATA_TYPES,
  INCLUDED_MODELS,
}

export const NODE_LAYERS = {
  GROUP_NODE: 0,
  NODES: 1000, // We need a difference > 1000 here, since ReactFlow will add 1000 to the z-index when a node is selected.
  DECISION_SERVICE_NODE: 2000, // We need a difference > 1000 here, since ReactFlow will add 1000 to the z-index when a node is selected.
  NESTED_NODES: 4000,
};

type ExtractState = StoreApi<State> extends { getState: () => infer T } ? T : never;

export function useDmnEditorStore<StateSlice = ExtractState>(
  selector: (state: State) => StateSlice,
  equalityFn?: (a: StateSlice, b: StateSlice) => boolean
) {
  const store = useContext(DmnEditorStoreApiContext);

  if (store === null) {
    throw new Error("Can't use DMN Editor Store outside of the DmnEditor component.");
  }

  return useStoreWithEqualityFn(store, selector, equalityFn);
}

export function useDmnEditorStoreApi() {
  return useContext(DmnEditorStoreApiContext);
}

export const DmnEditorStoreApiContext = createContext<StoreApiType>({} as any);

export type StoreApiType = UseBoundStore<WithImmer<StoreApi<State>>>;

export const defaultStaticState = () => ({
  boxedExpressionEditor: {
    activeDrgElementId: undefined,
    selectedObjectId: undefined,
    propertiesPanel: {
      isOpen: false,
    },
  },
  navigation: {
    tab: DmnEditorTab.EDITOR,
  },
  diagram: {
    drdIndex: 0,
    edgeIdBeingUpdated: undefined,
    dropTargetNode: undefined,
    ongoingConnection: undefined,
    propertiesPanel: {
      isOpen: false,
      elementId: undefined,
    },
    overlaysPanel: {
      isOpen: false,
    },

    openNodesPanel: DiagramNodesPanel.NONE,
    drdSelector: {
      isOpen: false,
    },
    overlays: {
      enableNodeHierarchyHighlight: false,
      enableExecutionHitsHighlights: false,
      enableCustomNodeStyles: false,
      enableDataTypesToolbarOnNodes: true,
    },
    snapGrid: { isEnabled: true, x: 20, y: 20 },
    _selectedNodes: [],
    _selectedEdges: [],
    draggingNodes: [],
    resizingNodes: [],
    draggingWaypoints: [],
    movingDividerLines: [],
  },
});

export function createDmnEditorStore(model: State["dmn"]["model"]) {
  return create(
    immer<State>((set, get) => ({
      dmn: {
        model,
      },
      dataTypesEditor: {
        activeItemDefinitionId: undefined,
        expandedItemComponentIds: [],
      },
      ...defaultStaticState(),
      dispatch: {
        dmn: {
          reset: (model) => {
            set((state) => {
              state.diagram._selectedNodes = [];
              state.diagram.draggingNodes = [];
              state.diagram.resizingNodes = [];
              state.navigation.tab = DmnEditorTab.EDITOR;
              state.boxedExpressionEditor.activeDrgElementId = undefined;
              state.boxedExpressionEditor.selectedObjectId = undefined;
            });
          },
        },
        boxedExpressionEditor: {
          open: (state, id) => {
            state.boxedExpressionEditor.activeDrgElementId = id;
            state.boxedExpressionEditor.selectedObjectId = undefined;
            state.boxedExpressionEditor.propertiesPanel.isOpen = state.diagram.propertiesPanel.isOpen;
          },
          close: (state) => {
            state.diagram.propertiesPanel.isOpen = state.boxedExpressionEditor.propertiesPanel.isOpen;
            state.boxedExpressionEditor.activeDrgElementId = undefined;
            state.boxedExpressionEditor.selectedObjectId = undefined;
          },
        },
        diagram: {
          setNodeStatus: (prev, nodeId, newStatus) => {
            //selected
            if (newStatus.selected !== undefined) {
              if (newStatus.selected) {
                prev.diagram._selectedNodes.push(nodeId);
              } else {
                prev.diagram._selectedNodes = prev.diagram._selectedNodes.filter((s) => s !== nodeId);
              }
            }
            //dragging
            if (newStatus.dragging !== undefined) {
              if (newStatus.dragging) {
                prev.diagram.draggingNodes.push(nodeId);
              } else {
                prev.diagram.draggingNodes = prev.diagram.draggingNodes.filter((s) => s !== nodeId);
              }
            }
            // resizing
            if (newStatus.resizing !== undefined) {
              if (newStatus.resizing) {
                prev.diagram.resizingNodes.push(nodeId);
              } else {
                prev.diagram.resizingNodes = prev.diagram.resizingNodes.filter((s) => s !== nodeId);
              }
            }
          },
          setEdgeStatus: (prev, edgeId, newStatus) => {
            //selected
            if (newStatus.selected !== undefined) {
              if (newStatus.selected) {
                prev.diagram._selectedEdges.push(edgeId);
              } else {
                prev.diagram._selectedEdges = prev.diagram._selectedEdges.filter((s) => s !== edgeId);
              }
            }
            //dragging
            if (newStatus.draggingWaypoint !== undefined) {
              if (newStatus.draggingWaypoint) {
                prev.diagram.draggingWaypoints.push(edgeId);
              } else {
                prev.diagram.draggingWaypoints = prev.diagram.draggingWaypoints.filter((s) => s !== edgeId);
              }
            }
          },
          setDividerLineStatus: (prev, decisionServiceId, newStatus) => {
            //dragging
            if (newStatus.moving !== undefined) {
              if (newStatus.moving) {
                prev.diagram.movingDividerLines.push(decisionServiceId);
              } else {
                prev.diagram.movingDividerLines = prev.diagram.movingDividerLines.filter(
                  (s) => s !== decisionServiceId
                );
              }
            }
          },
        },
      },
    }))
  );
}
