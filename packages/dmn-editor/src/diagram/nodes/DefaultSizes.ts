import { DC__Dimension } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { SnapGrid } from "../../store/Store";
import { MIN_SIZE_FOR_NODES, NODE_MIN_WIDTH } from "../SnapGrid";
import { NodeType } from "../connections/graphStructure";
import { NODE_TYPES } from "./NodeTypes";

export const DEFAULT_NODE_SIZES: Record<NodeType, (snapGrid: SnapGrid) => DC__Dimension> = {
  [NODE_TYPES.inputData]: (snapGrid) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid);
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.decision]: (snapGrid) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid);
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.bkm]: (snapGrid) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid);
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.knowledgeSource]: (snapGrid) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid);
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.decisionService]: (snapGrid) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid, NODE_MIN_WIDTH * 2, NODE_MIN_WIDTH * 2); // This is not a mistake, we want the DecisionService node to be a bigger square.
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.textAnnotation]: (snapGrid) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid, 200, 200);
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.group]: (snapGrid) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid, NODE_MIN_WIDTH * 2, NODE_MIN_WIDTH * 2); // This is not a mistake, we want the Group node to be a bigger square.
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
};
