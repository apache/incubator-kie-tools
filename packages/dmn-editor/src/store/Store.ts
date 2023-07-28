import { create } from "zustand";
import { immer } from "zustand/middleware/immer";
import { DMN14__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { DmnMarshaller, getMarshaller } from "@kie-tools/dmn-marshaller";
import {
  DMN14__tBusinessKnowledgeModel,
  DMN14__tDecision,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { Draft } from "immer";

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
}

export type Dispatch = {
  dmn: {
    reset: (xml: string) => void;
    set: (state: (state: Draft<{ definitions: DMN14__tDefinitions }>) => void) => any;
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
};

const EMPTY_DMN_14 = `<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="https://www.omg.org/spec/DMN/20211108/MODEL/">
</definitions>`;

const defaultMarshaller = getMarshaller(EMPTY_DMN_14);

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

// FIXME: Tiago --> Why is this happening?
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
export const useDmnEditor = create(
  immer<State>((set) => ({
    dmn: {
      marshaller: defaultMarshaller,
      model: defaultMarshaller.parser.parse(),
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
    dispatch: {
      dmn: {
        reset: (xml) => {
          set((state) => {
            const marshaller = getMarshaller(xml.trim().length <= 0 ? EMPTY_DMN_14 : xml);
            state.dmn.marshaller = marshaller;
            state.dmn.model = marshaller.parser.parse();
          });
        },
        set: (mutate) => {
          set((state) => {
            mutate(state.dmn.model);
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
    },
  }))
);
