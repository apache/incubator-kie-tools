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

import * as React from "react";
import { DC__Point } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { deleteEdgeWaypoint } from "../../mutations/deleteEdgeWaypoint";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/StoreContext";
import { drag } from "d3-drag";
import { select } from "d3-selection";
import { useEffect } from "react";
import { repositionEdgeWaypoint } from "../../mutations/repositionEdgeWaypoint";
import { snapPoint } from "../SnapGrid";

export function PotentialWaypoint(props: { point: { x: number; y: number } }) {
  return <circle className={"kie-dmn-editor--edge-waypoint-potential"} r={5} cx={props.point.x} cy={props.point.y} />;
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
  const diagram = useDmnEditorStore((s) => s.diagram);
  const dispatch = useDmnEditorStore((s) => s.dispatch);
  const { setState } = useDmnEditorStoreApi();

  useEffect(() => {
    if (!circleRef.current) {
      return;
    }

    const selection = select(circleRef.current);
    const dragHandler = drag<SVGCircleElement, unknown>()
      .on("start", () => {
        setState((state) => state.dispatch(state).diagram.setEdgeStatus(edgeId, { draggingWaypoint: true }));
      })
      .on("drag", (e) => {
        setState((state) => {
          repositionEdgeWaypoint({
            definitions: state.dmn.model.definitions,
            drdIndex: diagram.drdIndex,
            edgeIndex,
            waypointIndex: index,
            waypoint: snapPoint(diagram.snapGrid, { "@_x": e.x, "@_y": e.y }),
          });
        });
      })
      .on("end", (e) => {
        onDragStop(e.sourceEvent);
        setState((state) => state.dispatch(state).diagram.setEdgeStatus(edgeId, { draggingWaypoint: false }));
      });

    selection.call(dragHandler);
    return () => {
      selection.on(".drag", null);
    };
  }, [diagram.drdIndex, diagram.snapGrid, edgeId, edgeIndex, index, onDragStop, setState]);

  return (
    <circle
      data-waypointindex={index}
      ref={circleRef}
      className={"kie-dmn-editor--diagram-edge-waypoint"}
      cx={point["@_x"]}
      cy={point["@_y"]}
      r={1}
      onDoubleClick={(e) => {
        e.preventDefault();
        e.stopPropagation();

        setState((state) => {
          deleteEdgeWaypoint({
            definitions: state.dmn.model.definitions,
            drdIndex: diagram.drdIndex,
            edgeIndex,
            waypointIndex: index,
          });
        });
      }}
    />
  );
}
