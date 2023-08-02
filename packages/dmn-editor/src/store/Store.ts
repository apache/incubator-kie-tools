import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { DmnMarshaller, getMarshaller } from "@kie-tools/dmn-marshaller";
import {
  DMN14__tBusinessKnowledgeModel,
  DMN14__tDecision,
  DMN14__tDefinitions,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { createContext, useContext } from "react";
import { StoreApi, UseBoundStore, create, useStore as useZustandStore } from "zustand";
import { WithImmer, immer } from "zustand/middleware/immer";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";

export interface DmnEditorDiagramNodeStatus {
  selected: boolean;
  dragging: boolean;
  resizing: boolean;
}

export interface State {
  dispatch: Dispatch;
  dmn: {
    model: { definitions: DMN14__tDefinitions };
    marshaller: DmnMarshaller;
  };
  boxedExpression: {
    node: DmnNodeWithExpression | undefined;
  };
  propertiesPanel: {
    isOpen: boolean;
    elementId: string | undefined;
  };
  navigation: {
    tab: DmnEditorTab;
  };
  diagram: {
    selected: Array<string>;
    dragging: Array<string>;
    resizing: Array<string>;
  };
}

export type Dispatch = {
  dmn: {
    reset: (xml: string) => void;
  };
  boxedExpression: {
    open: (nodeWithExpression: DmnNodeWithExpression) => void;
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
    setNodeStatus(state: State, nodeId: string, status: Partial<DmnEditorDiagramNodeStatus>): void;
  };
};

const EMPTY_DMN_14 = () => `<?xml version="1.0" encoding="UTF-8"?>
<definitions
  xmlns="https://www.omg.org/spec/DMN/20211108/MODEL/"
  expressionLanguage="http://www.omg.org/spec/DMN/20180521/FEEL/"
  namespace="https://kiegroup.org/dmn/${generateUuid()}"
  id="${generateUuid()}"
  name="DMN${generateUuid()}">
</definitions>`;

export enum DmnEditorTab {
  EDITOR,
  DATA_TYPES,
  INCLUDED_MODELS,
  DOCUMENTATION,
}

export type DmnNodeWithExpression =
  | {
      type: typeof NODE_TYPES.bkm;
      content: DMN14__tBusinessKnowledgeModel;
    }
  | {
      type: typeof NODE_TYPES.decision;
      content: DMN14__tDecision;
    };

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

export function createDmnEditorStore(xml: string) {
  const initialMarshaller = getMarshaller(xml || EMPTY_DMN_14());
  const initialModel = initialMarshaller.parser.parse();

  return create(
    immer<State>((set, get) => ({
      dmn: {
        marshaller: initialMarshaller,
        model: initialModel,
      },
      boxedExpression: {
        node: undefined,
      },
      propertiesPanel: {
        isOpen: false,
        elementId: undefined,
      },
      navigation: {
        tab: DmnEditorTab.EDITOR,
      },
      diagram: {
        selected: [],
        dragging: [],
        resizing: [],
      },
      dispatch: {
        dmn: {
          reset: (xml) => {
            set((state) => {
              xml = (xml ?? "").trim().length <= 0 ? EMPTY_DMN_14() : xml;
              const marshaller = getMarshaller(xml);
              state.dmn.marshaller = marshaller;
              state.dmn.model = marshaller.parser.parse();
              state.diagram.selected = [];
              state.diagram.dragging = [];
              state.diagram.resizing = [];
              state.navigation.tab = DmnEditorTab.EDITOR;
              state.boxedExpression.node = undefined;
            });
          },
        },
        boxedExpression: {
          open: (node) => {
            set((state) => {
              state.boxedExpression.node = node;
            });
          },
          close: () => {
            set((state) => {
              state.boxedExpression.node = undefined;
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
