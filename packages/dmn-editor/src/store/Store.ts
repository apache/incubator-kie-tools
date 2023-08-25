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
    selected: Array<string>;
    dragging: Array<string>;
    resizing: Array<string>;
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
          enableNodeHierarchyHighlight: true,
          enableExecutionHitsHighlights: true,
          enableCustomNodeStyles: true,
          enableDataTypesOnNodes: true,
        },
        snapGrid: { isEnabled: true, x: 20, y: 20 },
        selected: [],
        dragging: [],
        resizing: [],
      },
      dispatch: {
        dmn: {
          reset: (model) => {
            set((state) => {
              state.diagram.selected = [];
              state.diagram.dragging = [];
              state.diagram.resizing = [];
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
                prev.diagram.selected.push(nodeId);
              } else {
                prev.diagram.selected = prev.diagram.selected.filter((s) => s !== nodeId);
              }
            }
            //dragging
            if (newStatus.dragging !== undefined) {
              if (newStatus.dragging) {
                prev.diagram.dragging.push(nodeId);
              } else {
                prev.diagram.dragging = prev.diagram.dragging.filter((s) => s !== nodeId);
              }
            }
            // resizing
            if (newStatus.resizing !== undefined) {
              if (newStatus.resizing) {
                prev.diagram.resizing.push(nodeId);
              } else {
                prev.diagram.resizing = prev.diagram.resizing.filter((s) => s !== nodeId);
              }
            }
          },
        },
      },
    }))
  );
}
