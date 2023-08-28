import {
  DMN15__tBusinessKnowledgeModel,
  DMN15__tDecision,
  DMN15__tDefinitions,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { createContext, useContext } from "react";
import { StoreApi, UseBoundStore, create, useStore as useZustandStore } from "zustand";
import { WithImmer, immer } from "zustand/middleware/immer";

export interface DmnEditorDiagramNodeStatus {
  selected: boolean;
  dragging: boolean;
  resizing: boolean;
}
export interface DmnEditorDiagramEdgeStatus {
  selected: boolean;
}

export interface SnapGrid {
  isEnabled: boolean;
  x: number;
  y: number;
}

export interface State {
  dispatch: Dispatch;
  dmn: {
    model: { definitions: DMN15__tDefinitions };
  };
  boxedExpressionEditor: {
    id: string | undefined;
  };
  propertiesPanel: {
    isOpen: boolean;
    elementId: string | undefined;
  };
  navigation: {
    tab: DmnEditorTab;
  };
  diagram: {
    overlaysPanel: {
      isOpen: boolean;
    };
    overlays: {
      enableNodeHierarchyHighlight: boolean;
      enableExecutionHitsHighlights: boolean;
      enableCustomNodeStyles: boolean;
      enableDataTypesOnNodes: boolean;
    };
    snapGrid: SnapGrid;
    selectedNodes: Array<string>;
    draggingNodes: Array<string>;
    resizingNodes: Array<string>;
    selectedEdges: Array<string>;
  };
}

export type Dispatch = {
  dmn: {
    reset: (model: State["dmn"]["model"]) => void;
  };
  boxedExpression: {
    open: (id: string) => void;
    close: () => void;
  };
  propertiesPanel: {
    open: () => void;
    close: () => void;
    toggle: () => void;
  };
  navigation: {
    setTab: (tab: DmnEditorTab) => void;
  };
  diagram: {
    toggleOverlaysPanel: (state: State) => void;
    setSnapGrid: (state: State, snap: SnapGrid) => void;
    setNodeStatus: (state: State, nodeId: string, status: Partial<DmnEditorDiagramNodeStatus>) => void;
    setEdgeStatus: (state: State, edgeId: string, status: Partial<DmnEditorDiagramEdgeStatus>) => void;
  };
};

export enum DmnEditorTab {
  EDITOR,
  DATA_TYPES,
  INCLUDED_MODELS,
  DOCUMENTATION,
}

export const NODE_LAYERS = {
  PARENT_NODES: 0,
  NODES: 1000, // We need a difference > 1000 here, since ReactFlow will add 1000 to the z-index when a node is selected.
  NESTED_NODES: 3000,
};

export function useDmnEditorStore() {
  return useZustandStore(useDmnEditorStoreApi());
}

export function useDmnEditorStoreApi() {
  return useContext(DmnEditorStoreApiContext);
}

export const DmnEditorStoreApiContext = createContext<StoreApiType>({} as any);

export type StoreApiType = UseBoundStore<WithImmer<StoreApi<State>>>;

export function createDmnEditorStore(model: State["dmn"]["model"]) {
  return create(
    immer<State>((set, get) => ({
      dmn: {
        model,
      },
      boxedExpressionEditor: {
        id: undefined,
      },
      propertiesPanel: {
        isOpen: false,
        elementId: undefined,
      },
      navigation: {
        tab: DmnEditorTab.EDITOR,
      },
      diagram: {
        overlaysPanel: {
          isOpen: false,
        },
        overlays: {
          enableNodeHierarchyHighlight: false,
          enableExecutionHitsHighlights: false,
          enableCustomNodeStyles: false,
          enableDataTypesOnNodes: false,
        },
        snapGrid: { isEnabled: true, x: 20, y: 20 },
        selectedNodes: [],
        draggingNodes: [],
        resizingNodes: [],
        selectedEdges: [],
      },
      dispatch: {
        dmn: {
          reset: (model) => {
            set((state) => {
              state.diagram.selectedNodes = [];
              state.diagram.draggingNodes = [];
              state.diagram.resizingNodes = [];
              state.navigation.tab = DmnEditorTab.EDITOR;
              state.boxedExpressionEditor.id = undefined;
            });
          },
        },
        boxedExpression: {
          open: (id) => {
            set((state) => {
              state.boxedExpressionEditor.id = id;
            });
          },
          close: () => {
            set((state) => {
              state.boxedExpressionEditor.id = undefined;
            });
          },
        },
        propertiesPanel: {
          open: () => {
            set((state) => {
              state.propertiesPanel.isOpen = true;
            });
          },
          close: () => {
            set((state) => {
              state.propertiesPanel.isOpen = false;
            });
          },
          toggle: () => {
            set((state) => {
              state.propertiesPanel.isOpen = !state.propertiesPanel.isOpen;
            });
          },
        },
        navigation: {
          setTab: (tab) => {
            set((state) => {
              state.navigation.tab = tab;
            });
          },
        },
        diagram: {
          toggleOverlaysPanel: (prev) => {
            prev.diagram.overlaysPanel.isOpen = !prev.diagram.overlaysPanel.isOpen;
          },
          setSnapGrid: (prev, snapGrid) => {
            prev.diagram.snapGrid = snapGrid;
          },
          setNodeStatus: (prev, nodeId, newStatus) => {
            //selected
            if (newStatus.selected !== undefined) {
              if (newStatus.selected) {
                prev.diagram.selectedNodes.push(nodeId);
              } else {
                prev.diagram.selectedNodes = prev.diagram.selectedNodes.filter((s) => s !== nodeId);
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
            if (newStatus.selected !== undefined) {
              if (newStatus.selected) {
                prev.diagram.selectedEdges.push(edgeId);
              } else {
                prev.diagram.selectedEdges = prev.diagram.selectedEdges.filter((s) => s !== edgeId);
              }
            }
          },
        },
      },
    }))
  );
}
