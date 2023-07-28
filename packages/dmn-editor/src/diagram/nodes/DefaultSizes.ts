import { DC__Dimension } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { MIN_SIZE_FOR_NODES, SNAP_GRID } from "../SnapGrid";
import { NODE_TYPES } from "./NodeTypes";
import { NodeType } from "../connections/graphStructure";

export const DEFAULT_NODE_SIZES: Record<NodeType, DC__Dimension> = {
  [NODE_TYPES.inputData]: {
    "@_width": MIN_SIZE_FOR_NODES.width,
    "@_height": MIN_SIZE_FOR_NODES.height,
  },
  [NODE_TYPES.decision]: {
    "@_width": MIN_SIZE_FOR_NODES.width,
    "@_height": MIN_SIZE_FOR_NODES.height,
  },
  [NODE_TYPES.bkm]: {
    "@_width": MIN_SIZE_FOR_NODES.width,
    "@_height": MIN_SIZE_FOR_NODES.height,
  },
  [NODE_TYPES.knowledgeSource]: {
    "@_width": MIN_SIZE_FOR_NODES.width,
    "@_height": MIN_SIZE_FOR_NODES.height,
  },
  [NODE_TYPES.decisionService]: {
    "@_width": MIN_SIZE_FOR_NODES.width * 2,
    "@_height": MIN_SIZE_FOR_NODES.width * 2,
  },
  [NODE_TYPES.textAnnotation]: {
    "@_width": SNAP_GRID.x * 10,
    "@_height": SNAP_GRID.y * 10,
  },
  [NODE_TYPES.group]: {
    "@_width": MIN_SIZE_FOR_NODES.width,
    "@_height": MIN_SIZE_FOR_NODES.height,
  },
};
