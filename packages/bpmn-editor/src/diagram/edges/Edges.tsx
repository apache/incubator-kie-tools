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

import { useEdgeClassName } from "@kie-tools/xyflow-react-kie-diagram/dist/edges/Hooks";
import { PotentialWaypoint, Waypoints } from "@kie-tools/xyflow-react-kie-diagram/dist/waypoints/Waypoints";
import { useAlwaysVisibleEdgeUpdatersAtNodeBorders } from "@kie-tools/xyflow-react-kie-diagram/dist/edges/useAlwaysVisibleEdgeUpdatersAtNodeBorders";
import { usePathForEdgeWithWaypoints } from "@kie-tools/xyflow-react-kie-diagram/dist/edges/usePathForEdgeWithWaypoints";
import { usePotentialWaypointControls } from "@kie-tools/xyflow-react-kie-diagram/dist/waypoints/usePotentialWaypointControls";
import { DEFAULT_INTRACTION_WIDTH } from "@kie-tools/xyflow-react-kie-diagram/dist/maths/DcMaths";
import { propsHaveSameValuesDeep } from "@kie-tools/xyflow-react-kie-diagram/dist/memoization/memoization";
import { useIsHovered } from "@kie-tools/xyflow-react-kie-diagram/dist/reactExt/useIsHovered";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import * as RF from "reactflow";
import { AssociationPath, SequenceFlowPath } from "./EdgeSvgs";
import { BpmnDiagramEdgeData, MIN_NODE_SIZES } from "../BpmnDiagramDomain";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import {
  EditableNodeLabel,
  useEditableNodeLabel,
} from "@kie-tools/xyflow-react-kie-diagram/dist/nodes/EditableNodeLabel";
import { updateFlowElement } from "../../mutations/renameNode";
import { getEdgeLabelCenter, repositionEdgeLabel } from "../../mutations/repositionEdgeLabel";
import "./Edges.css";
import { useBpmnEditorI18n } from "../../i18n";

const interactionStrokeProps: Partial<React.SVGAttributes<SVGPathElement>> = {
  strokeOpacity: 1,
  markerEnd: undefined,
  style: undefined,
  className: "react-flow__edge-interaction",
  stroke: "transparent",
  strokeLinecap: "round",
};

export const SequenceFlowEdge = React.memo((props: RF.EdgeProps<BpmnDiagramEdgeData>) => {
  const { i18n } = useBpmnEditorI18n();
  const renderCount = useRef<number>(0);
  renderCount.current++;

  const { path, points: waypoints } = usePathForEdgeWithWaypoints(
    props.data?.bpmnEdge,
    props.data?.bpmnShapeSource,
    props.data?.bpmnShapeTarget,
    props.data?.bpmnSourceType ? MIN_NODE_SIZES[props.data.bpmnSourceType] : undefined,
    props.data?.bpmnTargetType ? MIN_NODE_SIZES[props.data.bpmnTargetType] : undefined
  );

  const interactionPathRef = React.useRef<SVGPathElement>(null);
  const isHovered = useIsHovered(interactionPathRef);

  const {
    onMouseMove: onMouseMoveOnEdge,
    onDoubleClick: onDoubleClickEdgeWaypoints,
    potentialWaypoint,
    isDraggingWaypoint,
  } = usePotentialWaypointControls(waypoints, props.selected, props.id, props.data?.bpmnEdgeIndex, interactionPathRef);

  const isConnecting = !!RF.useStore((s) => s.connectionNodeId);
  const className = useEdgeClassName(isConnecting, isDraggingWaypoint);

  useAlwaysVisibleEdgeUpdatersAtNodeBorders(interactionPathRef, props.source, props.target, waypoints);

  const labelPosition = useMemo(() => {
    if (waypoints.length > 2) {
      return waypoints[1];
    } else {
      return waypoints[0];
    }
  }, [waypoints]);

  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const id = props.data?.bpmnElement["@_id"] ?? props.id;

  const onChangeLabel = useCallback(
    (newName: string) => {
      bpmnEditorStoreApi.setState((s) => {
        updateFlowElement({
          definitions: s.bpmn.model.definitions,
          id: id,
          newFlowElement: { "@_name": newName },
        });
      });
    },
    [bpmnEditorStoreApi, id]
  );

  const { isEditingLabel, setEditingLabel, triggerEditing: triggerEditingLabel } = useEditableNodeLabel(props.id);

  // Label drag, saved on drop.

  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);
  const reactFlowStoreApi = RF.useStoreApi();
  const labelRef = useRef<HTMLDivElement>(null);
  const labelDragRef = useRef<EdgeLabelDrag | undefined>(undefined);
  const [draggedLabelCenter, setDraggedLabelCenter] = useState<Point | undefined>(undefined);
  const isDraggingLabel = draggedLabelCenter !== undefined;

  const pinnedLabelCenter = useMemo(
    () => getEdgeLabelCenter(props.data?.bpmnEdge?.["bpmndi:BPMNLabel"]?.["dc:Bounds"]),
    [props.data?.bpmnEdge]
  );

  const labelCenter = draggedLabelCenter ?? pinnedLabelCenter;

  const labelStyle = useMemo(() => {
    if (labelCenter) {
      return { transform: `translate(${labelCenter.x}px,${labelCenter.y}px) translate(-50%,-50%)` };
    }

    return {
      transform: `translate(${labelPosition["@_x"]}px,${labelPosition["@_y"]}px)`,
    };
  }, [labelCenter, labelPosition]);

  // screenToFlowPosition snaps to grid.
  const getFlowPosition = useCallback(
    (clientX: number, clientY: number): Point => {
      const { transform, domNode } = reactFlowStoreApi.getState();
      const domNodeRect = domNode?.getBoundingClientRect();
      return {
        x: (clientX - (domNodeRect?.left ?? 0) - transform[0]) / transform[2],
        y: (clientY - (domNodeRect?.top ?? 0) - transform[1]) / transform[2],
      };
    },
    [reactFlowStoreApi]
  );

  const onLabelPointerDown = useCallback(
    (e: React.PointerEvent<HTMLDivElement>) => {
      if (isReadOnly || isEditingLabel || e.button !== 0 || !labelRef.current) {
        return;
      }

      // Multi-selection happens on click.
      if (!reactFlowStoreApi.getState().multiSelectionActive) {
        bpmnEditorStoreApi.setState((s) => {
          const { _selectedNodes, _selectedEdges } = s.xyFlowReactKieDiagram;
          if (_selectedNodes.length > 0 || _selectedEdges.length !== 1 || _selectedEdges[0] !== props.id) {
            s.xyFlowReactKieDiagram._selectedNodes = [];
            s.xyFlowReactKieDiagram._selectedEdges = [props.id];
          }
        });
      }

      const labelRect = labelRef.current.getBoundingClientRect();
      labelDragRef.current = {
        pointerId: e.pointerId,
        startClientPosition: { x: e.clientX, y: e.clientY },
        startPointerPosition: getFlowPosition(e.clientX, e.clientY),
        startCenter: getFlowPosition(labelRect.left + labelRect.width / 2, labelRect.top + labelRect.height / 2),
        width: labelRef.current.offsetWidth,
        height: labelRef.current.offsetHeight,
        hasMoved: false,
      };

      try {
        e.currentTarget.setPointerCapture(e.pointerId);
      } catch (error) {
        // Inactive pointers can't be captured.
      }
    },
    [bpmnEditorStoreApi, getFlowPosition, isEditingLabel, isReadOnly, props.id, reactFlowStoreApi]
  );

  const onLabelPointerMove = useCallback(
    (e: React.PointerEvent<HTMLDivElement>) => {
      const drag = labelDragRef.current;
      if (!drag || drag.pointerId !== e.pointerId) {
        return;
      }

      if (!drag.hasMoved) {
        const distance = Math.hypot(e.clientX - drag.startClientPosition.x, e.clientY - drag.startClientPosition.y);
        if (distance < EDGE_LABEL_DRAG_THRESHOLD_IN_PX) {
          return; // Clicks never move labels.
        }
        drag.hasMoved = true;
      }

      setDraggedLabelCenter(getDraggedLabelCenter(drag, getFlowPosition(e.clientX, e.clientY)));
    },
    [getFlowPosition]
  );

  const edgeIndex = props.data?.bpmnEdgeIndex;

  const onLabelPointerUp = useCallback(
    (e: React.PointerEvent<HTMLDivElement>) => {
      const drag = labelDragRef.current;
      if (!drag || drag.pointerId !== e.pointerId) {
        return;
      }

      labelDragRef.current = undefined;

      const center = getDraggedLabelCenter(drag, getFlowPosition(e.clientX, e.clientY));
      const isBackWherePinned =
        !!pinnedLabelCenter &&
        Math.abs(pinnedLabelCenter.x - center.x) < 0.01 &&
        Math.abs(pinnedLabelCenter.y - center.y) < 0.01;

      if (
        !drag.hasMoved ||
        isBackWherePinned ||
        edgeIndex === undefined ||
        !Number.isFinite(center.x) ||
        !Number.isFinite(center.y)
      ) {
        setDraggedLabelCenter(undefined);
        return;
      }

      // Avoids flicker until model updates.
      setDraggedLabelCenter(center);
      try {
        bpmnEditorStoreApi.setState((s) => {
          repositionEdgeLabel({
            definitions: s.bpmn.model.definitions,
            __readonly_edgeIndex: edgeIndex,
            __readonly_bounds: {
              "@_x": center.x - drag.width / 2,
              "@_y": center.y - drag.height / 2,
              "@_width": drag.width,
              "@_height": drag.height,
            },
          });
        });
      } catch (error) {
        setDraggedLabelCenter(undefined);
        throw error;
      }
    },
    [bpmnEditorStoreApi, edgeIndex, getFlowPosition, pinnedLabelCenter]
  );

  // Edge changed; clear dragged position.
  useEffect(() => {
    if (!labelDragRef.current) {
      setDraggedLabelCenter(undefined);
    }
  }, [props.data?.bpmnEdge]);

  const cancelLabelDrag = useCallback(() => {
    labelDragRef.current = undefined;
    setDraggedLabelCenter(undefined);
  }, []);

  // Also fired after pointerup.
  const onLabelLostPointerCapture = useCallback(() => {
    if (labelDragRef.current) {
      cancelLabelDrag();
    }
  }, [cancelLabelDrag]);

  // Escape cancels the drag.
  useEffect(() => {
    if (!isDraggingLabel) {
      return;
    }

    const onKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        e.stopPropagation();
        cancelLabelDrag();
      }
    };

    window.addEventListener("keydown", onKeyDown, true);
    return () => {
      window.removeEventListener("keydown", onKeyDown, true);
    };
  }, [cancelLabelDrag, isDraggingLabel]);

  const onDoubleClickEdge = useCallback(
    (e: React.MouseEvent) => {
      if (e.metaKey) {
        triggerEditingLabel(e);
      } else {
        onDoubleClickEdgeWaypoints();
      }
    },
    [onDoubleClickEdgeWaypoints, triggerEditingLabel]
  );

  return (
    <>
      <SequenceFlowPath
        svgRef={interactionPathRef}
        d={path}
        {...interactionStrokeProps}
        className={`${interactionStrokeProps.className} ${className}`}
        strokeWidth={props.interactionWidth ?? DEFAULT_INTRACTION_WIDTH}
        onMouseMove={onMouseMoveOnEdge}
        onDoubleClick={onDoubleClickEdge}
        data-edgetype={"information-requirement"}
        data-testid={`kie-tools--bpmn-editor--edge-${props.id}`}
      />
      <SequenceFlowPath
        d={path}
        className={`xyflow-react-kie-diagram--edge ${className}`}
        data-testid={`kie-tools--bpmn-editor--edge-path-${props.id}`}
      />

      {props.data?.bpmnElement.__$$element === "sequenceFlow" &&
        (!!props.data.bpmnElement["@_name"] || isEditingLabel) && (
          <RF.EdgeLabelRenderer>
            <div
              ref={labelRef}
              style={labelStyle}
              className={`kie-bpmn-editor--floating-edge-label edge-label-renderer__custom-edge nodrag nopan ${props.selected ? "selected" : ""} ${labelCenter ? "pinned" : ""} ${isReadOnly ? "" : "movable"} ${isDraggingLabel ? "dragging" : ""}`}
              onPointerDown={isReadOnly ? undefined : onLabelPointerDown}
              onPointerMove={isReadOnly ? undefined : onLabelPointerMove}
              onPointerUp={isReadOnly ? undefined : onLabelPointerUp}
              onPointerCancel={isReadOnly ? undefined : cancelLabelDrag}
              onLostPointerCapture={isReadOnly ? undefined : onLabelLostPointerCapture}
            >
              <EditableNodeLabel
                id={props.id}
                name={props.data.bpmnElement["@_name"]}
                value={props.data.bpmnElement["@_name"]}
                onChange={onChangeLabel}
                placeholder={i18n.nodeLabel.placeHolder}
                position={"center-center"}
                isEditing={isEditingLabel}
                setEditing={setEditingLabel}
                validate={NO_VALIDATION}
              />
            </div>
          </RF.EdgeLabelRenderer>
        )}

      {props.selected && !isConnecting && props.data?.bpmnEdge && (
        <Waypoints
          edgeId={props.id}
          edgeIndex={props.data.bpmnEdgeIndex}
          waypoints={waypoints}
          onDragStop={onMouseMoveOnEdge}
        />
      )}
      {isHovered && potentialWaypoint && <PotentialWaypoint point={potentialWaypoint.point} />}
    </>
  );
}, propsHaveSameValuesDeep);

export const AssociationEdge = React.memo((props: RF.EdgeProps<BpmnDiagramEdgeData>) => {
  const renderCount = useRef<number>(0);
  renderCount.current++;

  const { path, points: waypoints } = usePathForEdgeWithWaypoints(
    props.data?.bpmnEdge,
    props.data?.bpmnShapeSource,
    props.data?.bpmnShapeTarget,
    props.data?.bpmnSourceType ? MIN_NODE_SIZES[props.data.bpmnSourceType] : undefined,
    props.data?.bpmnTargetType ? MIN_NODE_SIZES[props.data.bpmnTargetType] : undefined
  );

  const interactionPathRef = React.useRef<SVGPathElement>(null);
  const isHovered = useIsHovered(interactionPathRef);

  const {
    onMouseMove: onMouseMoveOnEdge,
    onDoubleClick: onDoubleClickEdge,
    potentialWaypoint,
    isDraggingWaypoint,
  } = usePotentialWaypointControls(waypoints, props.selected, props.id, props.data?.bpmnEdgeIndex, interactionPathRef);

  const isConnecting = !!RF.useStore((s) => s.connectionNodeId);
  const className = useEdgeClassName(isConnecting, isDraggingWaypoint);

  useAlwaysVisibleEdgeUpdatersAtNodeBorders(interactionPathRef, props.source, props.target, waypoints);

  const association = props.data?.bpmnElement?.__$$element === "association" ? props.data?.bpmnElement : undefined;

  return (
    <>
      <AssociationPath
        svgRef={interactionPathRef}
        d={path}
        {...interactionStrokeProps}
        className={`${interactionStrokeProps.className} ${className}`}
        strokeWidth={props.interactionWidth ?? DEFAULT_INTRACTION_WIDTH}
        onMouseMove={onMouseMoveOnEdge}
        onDoubleClick={onDoubleClickEdge}
        data-edgetype={"association"}
        data-testid={`kie-tools--bpmn-editor--edge-${props.id}`}
        direction={association?.["@_associationDirection"]}
      />
      <AssociationPath
        d={path}
        className={`kie-bpmn-editor--edge ${className}`}
        data-testid={`kie-tools--bpmn-editor--edge-path-${props.id}`}
        direction={association?.["@_associationDirection"]}
      />

      {props.selected && !isConnecting && props.data?.bpmnEdge && (
        <Waypoints
          edgeId={props.id}
          edgeIndex={props.data.bpmnEdgeIndex}
          waypoints={waypoints}
          onDragStop={onMouseMoveOnEdge}
        />
      )}
      {isHovered && potentialWaypoint && <PotentialWaypoint point={potentialWaypoint.point} />}
    </>
  );
}, propsHaveSameValuesDeep);

const NO_VALIDATION = () => true;

const EDGE_LABEL_DRAG_THRESHOLD_IN_PX = 3;

type Point = { x: number; y: number };

type EdgeLabelDrag = {
  pointerId: number;
  startClientPosition: Point;
  startPointerPosition: Point;
  startCenter: Point;
  width: number;
  height: number;
  hasMoved: boolean;
};

function getDraggedLabelCenter(drag: EdgeLabelDrag, pointerPosition: Point): Point {
  return {
    x: drag.startCenter.x + pointerPosition.x - drag.startPointerPosition.x,
    y: drag.startCenter.y + pointerPosition.y - drag.startPointerPosition.y,
  };
}
