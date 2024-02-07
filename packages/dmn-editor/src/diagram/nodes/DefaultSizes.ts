/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { DC__Dimension } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { SnapGrid } from "../../store/Store";
import { snapPoint } from "../SnapGrid";
import { NodeType } from "../connections/graphStructure";
import { NODE_TYPES } from "./NodeTypes";
import { CONTAINER_NODES_DESIRABLE_PADDING } from "../maths/DmnMaths";

export type NodeSizes<T extends NodeType = NodeType> = {
  [K in T]: K extends typeof NODE_TYPES.inputData
    ? (args: { snapGrid: SnapGrid; isAlternativeInputDataShape: boolean }) => DC__Dimension
    : (args: { snapGrid: SnapGrid }) => DC__Dimension;
};

export const MIN_NODE_SIZES: NodeSizes = {
  [NODE_TYPES.inputData]: ({ snapGrid, isAlternativeInputDataShape }) => {
    if (isAlternativeInputDataShape) {
      const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid, NODE_MIN_WIDTH / 2, NODE_MIN_HEIGHT + 20);
      return {
        "@_width": snappedMinSize.width,
        "@_height": snappedMinSize.height,
      };
    }
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid);
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.decision]: ({ snapGrid }) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid);
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.bkm]: ({ snapGrid }) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid);
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.knowledgeSource]: ({ snapGrid }) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid);
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.decisionService]: ({ snapGrid }) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(
      snapGrid,
      NODE_MIN_WIDTH + CONTAINER_NODES_DESIRABLE_PADDING * 2,
      NODE_MIN_HEIGHT * 2 + CONTAINER_NODES_DESIRABLE_PADDING * 2
    );
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.textAnnotation]: ({ snapGrid }) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid, 200, 60);
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.group]: ({ snapGrid }) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(
      snapGrid,
      NODE_MIN_WIDTH + CONTAINER_NODES_DESIRABLE_PADDING * 2,
      NODE_MIN_HEIGHT + CONTAINER_NODES_DESIRABLE_PADDING * 2
    );
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.unknown]: ({ snapGrid }) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid);
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
};

export const DEFAULT_NODE_SIZES: NodeSizes = {
  [NODE_TYPES.inputData]: ({ snapGrid, isAlternativeInputDataShape }) => {
    if (isAlternativeInputDataShape) {
      const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid, NODE_MIN_WIDTH / 2, NODE_MIN_HEIGHT + 20);
      return {
        "@_width": snappedMinSize.width,
        "@_height": snappedMinSize.height,
      };
    }
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid);
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.decision]: ({ snapGrid }) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid);
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.bkm]: ({ snapGrid }) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid);
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.knowledgeSource]: ({ snapGrid }) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid);
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.decisionService]: ({ snapGrid }) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid, NODE_MIN_WIDTH * 2, NODE_MIN_WIDTH * 2); // This is not a mistake, we want the DecisionService node to be a bigger square.
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.textAnnotation]: ({ snapGrid }) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid, 200, 200);
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.group]: ({ snapGrid }) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid, NODE_MIN_WIDTH * 2, NODE_MIN_WIDTH * 2); // This is not a mistake, we want the Group node to be a bigger square.
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
  [NODE_TYPES.unknown]: ({ snapGrid }) => {
    const snappedMinSize = MIN_SIZE_FOR_NODES(snapGrid);
    return {
      "@_width": snappedMinSize.width,
      "@_height": snappedMinSize.height,
    };
  },
};

export const DECISION_SERVICE_COLLAPSED_DIMENSIONS = {
  width: 300,
  height: 100,
};

export const NODE_MIN_WIDTH = 160;
export const NODE_MIN_HEIGHT = 80;

const MIN_SIZE_FOR_NODES = (grid: SnapGrid, width = NODE_MIN_WIDTH, height = NODE_MIN_HEIGHT) => {
  const snapped = snapPoint(grid, { "@_x": width, "@_y": height }, "ceil");
  return { width: snapped["@_x"], height: snapped["@_y"] };
};
