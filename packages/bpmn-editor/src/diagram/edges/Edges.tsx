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
import { useCallback, useMemo, useRef } from "react";
import * as RF from "reactflow";
import { AssociationPath, SequenceFlowPath } from "./EdgeSvgs";
import { BpmnDiagramEdgeData, MIN_NODE_SIZES } from "../BpmnDiagramDomain";
import { useBpmnEditorStoreApi } from "../../store/StoreContext";
import {
  EditableNodeLabel,
  useEditableNodeLabel,
} from "@kie-tools/xyflow-react-kie-diagram/dist/nodes/EditableNodeLabel";
import { updateFlowElement } from "../../mutations/renameNode";
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

  const labelStyle = useMemo(() => {
    return {
      transform: `translate(${labelPosition["@_x"]}px,${labelPosition["@_y"]}px)`,
    };
  }, [labelPosition]);

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
      />
      <SequenceFlowPath d={path} className={`xyflow-react-kie-diagram--edge ${className}`} />

      {props.data?.bpmnElement.__$$element === "sequenceFlow" &&
        (!!props.data.bpmnElement["@_name"] || isEditingLabel) && (
          <RF.EdgeLabelRenderer>
            <div
              style={labelStyle}
              className={`kie-bpmn-editor--floating-edge-label edge-label-renderer__custom-edge nodrag nopan ${props.selected ? "selected" : ""}`}
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
      />
      <AssociationPath d={path} className={`kie-bpmn-editor--edge ${className}`} />

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
