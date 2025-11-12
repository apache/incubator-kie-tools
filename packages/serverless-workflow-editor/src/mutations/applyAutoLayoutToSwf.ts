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

import * as Elk from "elkjs/lib/elk.bundled.js";
import * as RF from "reactflow";
import { State } from "../store/Store";
import { FAKE_MARKER, visitNodeAndNested } from "../autolayout/autoLayoutInfo";

export function applyAutoLayoutToSwf({
  state,
  __readonly_autoLayoutedInfo,
}: {
  state: State;
  __readonly_autoLayoutedInfo: {
    isHorizontal: boolean;
    nodes: Elk.ElkNode[] | undefined;
    edges: Elk.ElkExtendedEdge[] | undefined;
  };
}) {
  // Nodes
  for (const topLevelElkNode of __readonly_autoLayoutedInfo.nodes ?? []) {
    visitNodeAndNested(topLevelElkNode, { x: 0, y: 0 }, (elkNode, positionOffset) => {
      if (elkNode.id.includes(FAKE_MARKER)) {
        return;
      }

      state
        .layout(state)
        .setNodePosition(elkNode.id, { x: elkNode.x! + positionOffset.x, y: elkNode.y! + positionOffset.y });
    });
  }

  // Edges always go from top to bottom, removing waypoints.
  for (const elkEdge of __readonly_autoLayoutedInfo.edges ?? []) {
    if (elkEdge.id.includes(FAKE_MARKER)) {
      continue;
    }

    const points: RF.XYPosition[] = [];

    // SWF do not support edges with multiple targets so there will be always a single section
    if (elkEdge.sections) {
      for (const section of elkEdge.sections ?? []) {
        //Start point
        points.push({ x: section.startPoint.x, y: section.startPoint.y });

        //Bend points
        for (const elkPoint of section.bendPoints ?? []) {
          points.push({ x: elkPoint.x, y: elkPoint.y });
        }

        //End point
        points.push({ x: section.endPoint.x - 15, y: section.endPoint.y });
      }
    }

    state.layout(state).setEdgeWaypoints(elkEdge.id, points);
  }

  state.dispatch(state).swf.reset(state.swf.model, true);
}
