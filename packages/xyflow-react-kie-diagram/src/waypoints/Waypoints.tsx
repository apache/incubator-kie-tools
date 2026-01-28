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

import { drag } from "d3-drag";
import { select } from "d3-selection";
import { DC__Point } from "../maths/model";
import * as React from "react";
import { useEffect } from "react";
import { useXyFlowReactKieDiagramStore, useXyFlowReactKieDiagramStoreApi } from "../store/Store";
import "./Waypoints.css";
import { useWaypointsActions } from "./WaypointActionsContext";
import { snapPoint } from "../snapgrid/SnapGrid";

export function PotentialWaypoint(props: { point: { x: number; y: number } }) {
  return (
    <circle
      className={"xyflow-react-kie-diagram--edge-waypoint-potential"}
      r={5}
      cx={props.point.x}
      cy={props.point.y}
    />
  );
}

export function Waypoints(props: {
  edgeId: string;
  edgeIndex: number;
  waypoints: DC__Point[];
  onDragStop: (e: React.MouseEvent) => void;
}) {
  return (
    <>
      {props.waypoints.slice(1, -1).map((p, i) => (
        <Waypoint
          onDragStop={props.onDragStop}
          key={i}
          edgeIndex={props.edgeIndex}
          edgeId={props.edgeId}
          point={p}
          index={i + 1 /* Plus one because we're removing the 1st element of the array before iterating */}
        />
      ))}
    </>
  );
}

export function Waypoint({
  edgeId,
  edgeIndex,
  index,
  point,
  onDragStop,
}: {
  edgeId: string;
  edgeIndex: number;
  index: number;
  point: DC__Point;
  onDragStop: (e: React.MouseEvent) => void;
}) {
  const circleRef = React.useRef<SVGCircleElement>(null);

  const xyFlowReactKieDiagramStoreApi = useXyFlowReactKieDiagramStoreApi();

  const { onWaypointDeleted, onWaypointRepositioned } = useWaypointsActions();

  const snapGrid = useXyFlowReactKieDiagramStore((s) => ({
    isEnabled: s.xyFlowReactKieDiagram.snapGrid.isEnabled,
    x: s.xyFlowReactKieDiagram.snapGrid.x / 2,
    y: s.xyFlowReactKieDiagram.snapGrid.y / 2,
  }));

  useEffect(() => {
    if (!circleRef.current) {
      return;
    }

    const selection = select(circleRef.current);
    const dragHandler = drag<SVGCircleElement, unknown>()
      .on("start", (e) => {
        xyFlowReactKieDiagramStoreApi.setState((state) =>
          state.dispatch(state).setEdgeStatus(edgeId, { draggingWaypoint: true })
        );
      })
      .on("drag", (e) => {
        console.log("XYFLOW-DIAGRAM: Waypoint repositioned");
        onWaypointRepositioned({
          edgeIndex,
          waypointIndex: index,
          waypoint: snapPoint(snapGrid, { "@_x": e.x, "@_y": e.y }),
        });
      })
      .on("end", (e) => {
        onDragStop(e);
        xyFlowReactKieDiagramStoreApi.setState((state) =>
          state.dispatch(state).setEdgeStatus(edgeId, { draggingWaypoint: false })
        );
      });

    selection.call(dragHandler);
    return () => {
      selection.on(".drag", null);
    };
  }, [edgeId, edgeIndex, index, onDragStop, onWaypointRepositioned, snapGrid, xyFlowReactKieDiagramStoreApi]);

  return (
    <circle
      data-waypointindex={index}
      ref={circleRef}
      className={"xyflow-react-kie-diagram--diagram-edge-waypoint"}
      cx={point["@_x"]}
      cy={point["@_y"]}
      r={1}
      onDoubleClick={(e) => {
        e.preventDefault();
        e.stopPropagation();

        console.log("XYFLOW-DIAGRAM: Waypoint deleted");
        onWaypointDeleted({
          edgeIndex,
          waypointIndex: index,
        });
      }}
    />
  );
}
