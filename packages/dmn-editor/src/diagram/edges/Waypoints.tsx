import * as React from "react";
import { DC__Point } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { deleteEdgeWaypoint } from "../../mutations/deleteEdgeWaypoint";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/Store";
import { useIsHovered } from "../useIsHovered";
import { drag } from "d3-drag";
import { select } from "d3-selection";
import { useEffect } from "react";
import { repositionEdgeWaypoint } from "../../mutations/repositionEdgeWaypoint";

export function Waypoints(props: { edgeId: string; edgeIndex: number; waypoints: DC__Point[] }) {
  return (
    <g className={"kie-dmn-editor--diagram-edge-waypoints"}>
      {props.waypoints.slice(1, -1).map((p, i) => (
        <Waypoint
          key={i}
          edgeIndex={props.edgeIndex}
          edgeId={props.edgeId}
          point={p}
          index={i + 1 /* Plus one because we're removing the 1st element of the array before iterating */}
        />
      ))}
    </g>
  );
}

export function Waypoint({
  edgeId,
  edgeIndex,
  index,
  point,
}: {
  edgeId: string;
  edgeIndex: number;
  index: number;
  point: DC__Point;
}) {
  const circleRef = React.useRef<SVGCircleElement>(null);
  const isHovered = useIsHovered(circleRef);
  const { dispatch } = useDmnEditorStore();
  const { setState } = useDmnEditorStoreApi();

  useEffect(() => {
    if (!circleRef.current) {
      return;
    }

    const selection = select(circleRef.current);
    const dragHandler = drag<SVGCircleElement, unknown>()
      .on("start", () => {
        setState((state) => dispatch.diagram.setEdgeStatus(state, edgeId, { draggingWaypoint: true }));
      })
      .on("drag", (e) => {
        setState((state) => {
          repositionEdgeWaypoint({
            definitions: state.dmn.model.definitions,
            edgeIndex,
            waypointIndex: index,
            waypoint: { "@_x": e.x, "@_y": e.y },
          });
        });
      })
      .on("end", () => {
        setState((state) => dispatch.diagram.setEdgeStatus(state, edgeId, { draggingWaypoint: false }));
      });

    selection.call(dragHandler);
    return () => {
      selection.on(".drag", null);
    };
  }, [dispatch.diagram, edgeId, edgeIndex, index, setState]);

  return (
    <circle
      ref={circleRef}
      cx={point["@_x"]}
      cy={point["@_y"]}
      stroke={isHovered ? "red" : undefined}
      r={1}
      onDoubleClick={(e) => {
        e.preventDefault();
        e.stopPropagation();

        setState((state) => {
          deleteEdgeWaypoint({
            definitions: state.dmn.model.definitions,
            edgeIndex,
            waypointIndex: index,
          });
        });
      }}
    />
  );
}
