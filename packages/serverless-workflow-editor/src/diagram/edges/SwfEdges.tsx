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
import { useRef } from "react";
import * as RF from "reactflow";
import { SwfEdge } from "../graph/graph";
import { DEFAULT_INTRACTION_WIDTH } from "../maths/SwfMaths";
import { propsHaveSameValuesDeep } from "../memoization/memoization";
import { useIsHovered } from "../useIsHovered";
import { PotentialWaypoint, Waypoints } from "./Waypoints";
import { useAlwaysVisibleEdgeUpdatersAtNodeBorders } from "./useAlwaysVisibleEdgeUpdatersAtNodeBorders";
import { useKieEdgePath } from "./useKieEdgePath";
import { usePotentialWaypointControls } from "./usePotentialWaypointControls";
import { useSettings } from "../../settings/SwfEditorSettingsContext";
import { Specification } from "@serverlessworkflow/sdk-typescript";
import { Unpacked } from "../../tsExt/tsExt";

export type SwfDiagramEdgeData = {
  swfEdge: { index: number } | undefined;
  swfObject: SwfEdge["swfObject"];
  swfSource: Unpacked<Specification.States> | undefined;
  swfTarget: Unpacked<Specification.States> | undefined;
};

export const TransitionPath = React.memo(
  (_props: React.SVGProps<SVGPathElement> & { svgRef?: React.RefObject<SVGPathElement> }) => {
    const { svgRef, ...props } = _props;
    return (
      <>
        <path ref={svgRef} style={{ strokeWidth: 1, stroke: "black" }} markerEnd={"url(#closed-arrow)"} {...props} />
      </>
    );
  }
);

export const ErrorTransitionPath = React.memo(
  (_props: React.SVGProps<SVGPathElement> & { svgRef?: React.RefObject<SVGPathElement> }) => {
    const { svgRef, ...props } = _props;
    return (
      <>
        <path ref={svgRef} style={{ strokeWidth: 1, stroke: "red" }} markerEnd={"url(#closed-arrow)"} {...props} />
      </>
    );
  }
);

export const EventConditionTransitionPath = React.memo(
  (_props: React.SVGProps<SVGPathElement> & { svgRef?: React.RefObject<SVGPathElement> }) => {
    const { svgRef, ...props } = _props;
    return (
      <>
        <path ref={svgRef} style={{ strokeWidth: 1, stroke: "black" }} markerEnd={"url(#closed-arrow)"} {...props} />
      </>
    );
  }
);

export const DefaultConditionTransitionPath = React.memo(
  (_props: React.SVGProps<SVGPathElement> & { svgRef?: React.RefObject<SVGPathElement> }) => {
    const { svgRef, ...props } = _props;
    return (
      <>
        <path ref={svgRef} style={{ strokeWidth: 1, stroke: "green" }} markerEnd={"url(#closed-arrow)"} {...props} />
      </>
    );
  }
);

export const DataConditionTransitionPath = React.memo(
  (_props: React.SVGProps<SVGPathElement> & { svgRef?: React.RefObject<SVGPathElement> }) => {
    const { svgRef, ...props } = _props;
    return (
      <>
        <path ref={svgRef} style={{ strokeWidth: 1, stroke: "black" }} markerEnd={"url(#closed-arrow)"} {...props} />
      </>
    );
  }
);

export const CompensationTransitionPath = React.memo(
  (_props: React.SVGProps<SVGPathElement> & { svgRef?: React.RefObject<SVGPathElement> }) => {
    const { svgRef, ...props } = _props;
    return (
      <>
        <path
          ref={svgRef}
          style={{ strokeWidth: 1, stroke: "yellow", strokeDasharray: "5,5" }}
          markerEnd={"url(#closed-arrow)"}
          {...props}
        />
      </>
    );
  }
);

export function useEdgeClassName(isConnecting: boolean, isDraggingWaypoint: boolean) {
  if (isConnecting) {
    return "dimmed";
  }

  if (isDraggingWaypoint) {
    return "dragging-waypoint";
  }

  return "normal";
}

const interactionStrokeProps: Partial<React.SVGAttributes<SVGPathElement>> = {
  strokeOpacity: 1,
  markerEnd: undefined,
  style: undefined,
  className: "react-flow__edge-interaction",
  stroke: "transparent",
  strokeLinecap: "round",
};

export const TransitionEdge = React.memo((props: RF.EdgeProps<SwfDiagramEdgeData>) => {
  const renderCount = useRef<number>(0);
  renderCount.current++;

  const { path, points: waypoints } = useKieEdgePath(props.source, props.target, props.data);

  const interactionPathRef = React.useRef<SVGPathElement>(null);
  const isHovered = useIsHovered(interactionPathRef);
  const settings = useSettings();

  const { onMouseMove, onDoubleClick, potentialWaypoint, isDraggingWaypoint } = usePotentialWaypointControls(
    waypoints,
    props.selected,
    props.id,
    interactionPathRef
  );

  const isConnecting = !!RF.useStore((s) => s.connectionNodeId);
  const className = useEdgeClassName(isConnecting, isDraggingWaypoint);

  useAlwaysVisibleEdgeUpdatersAtNodeBorders(interactionPathRef, props.source, props.target, waypoints);

  return (
    <>
      <TransitionPath
        svgRef={interactionPathRef}
        d={path}
        {...interactionStrokeProps}
        className={`${interactionStrokeProps.className} ${className}`}
        strokeWidth={props.interactionWidth ?? DEFAULT_INTRACTION_WIDTH}
        onMouseMove={onMouseMove}
        onDoubleClick={onDoubleClick}
        data-edgetype={"information-requirement"}
        visibility={settings.isReadOnly ? "hidden" : undefined}
      />
      <TransitionPath d={path} className={`kie-swf-editor--edge ${className}`} />

      {!settings.isReadOnly && props.selected && !isConnecting && props.data?.swfEdge && (
        <Waypoints
          edgeId={props.id}
          edgeIndex={props.data.swfEdge.index}
          waypoints={waypoints}
          onDragStop={onMouseMove}
        />
      )}
      {!settings.isReadOnly && isHovered && potentialWaypoint && <PotentialWaypoint point={potentialWaypoint.point} />}
    </>
  );
}, propsHaveSameValuesDeep);

export const ErrorTransitionEdge = React.memo((props: RF.EdgeProps<SwfDiagramEdgeData>) => {
  const renderCount = useRef<number>(0);
  renderCount.current++;

  const { path, points: waypoints } = useKieEdgePath(props.source, props.target, props.data);

  const interactionPathRef = React.useRef<SVGPathElement>(null);
  const isHovered = useIsHovered(interactionPathRef);
  const settings = useSettings();

  const { onMouseMove, onDoubleClick, potentialWaypoint, isDraggingWaypoint } = usePotentialWaypointControls(
    waypoints,
    props.selected,
    props.id,
    interactionPathRef
  );

  const isConnecting = !!RF.useStore((s) => s.connectionNodeId);
  const className = useEdgeClassName(isConnecting, isDraggingWaypoint);

  useAlwaysVisibleEdgeUpdatersAtNodeBorders(interactionPathRef, props.source, props.target, waypoints);

  return (
    <>
      <ErrorTransitionPath
        svgRef={interactionPathRef}
        d={path}
        {...interactionStrokeProps}
        className={`${interactionStrokeProps.className} ${className}`}
        strokeWidth={props.interactionWidth ?? DEFAULT_INTRACTION_WIDTH}
        onMouseMove={onMouseMove}
        onDoubleClick={onDoubleClick}
        data-edgetype={"information-requirement"}
        visibility={settings.isReadOnly ? "hidden" : undefined}
      />
      <ErrorTransitionPath d={path} className={`kie-swf-editor--edge ${className}`} />

      {!settings.isReadOnly && props.selected && !isConnecting && props.data?.swfEdge && (
        <Waypoints
          edgeId={props.id}
          edgeIndex={props.data.swfEdge.index}
          waypoints={waypoints}
          onDragStop={onMouseMove}
        />
      )}
      {!settings.isReadOnly && isHovered && potentialWaypoint && <PotentialWaypoint point={potentialWaypoint.point} />}
    </>
  );
}, propsHaveSameValuesDeep);

export const EventConditionTransitionEdge = React.memo((props: RF.EdgeProps<SwfDiagramEdgeData>) => {
  const renderCount = useRef<number>(0);
  renderCount.current++;

  const { path, points: waypoints } = useKieEdgePath(props.source, props.target, props.data);

  const interactionPathRef = React.useRef<SVGPathElement>(null);
  const isHovered = useIsHovered(interactionPathRef);
  const settings = useSettings();

  const { onMouseMove, onDoubleClick, potentialWaypoint, isDraggingWaypoint } = usePotentialWaypointControls(
    waypoints,
    props.selected,
    props.id,
    interactionPathRef
  );

  const isConnecting = !!RF.useStore((s) => s.connectionNodeId);
  const className = useEdgeClassName(isConnecting, isDraggingWaypoint);

  useAlwaysVisibleEdgeUpdatersAtNodeBorders(interactionPathRef, props.source, props.target, waypoints);

  return (
    <>
      <EventConditionTransitionPath
        svgRef={interactionPathRef}
        d={path}
        {...interactionStrokeProps}
        className={`${interactionStrokeProps.className} ${className}`}
        strokeWidth={props.interactionWidth ?? DEFAULT_INTRACTION_WIDTH}
        onMouseMove={onMouseMove}
        onDoubleClick={onDoubleClick}
        data-edgetype={"information-requirement"}
        visibility={settings.isReadOnly ? "hidden" : undefined}
      />
      <EventConditionTransitionPath d={path} className={`kie-swf-editor--edge ${className}`} />

      {!settings.isReadOnly && props.selected && !isConnecting && props.data?.swfEdge && (
        <Waypoints
          edgeId={props.id}
          edgeIndex={props.data.swfEdge.index}
          waypoints={waypoints}
          onDragStop={onMouseMove}
        />
      )}
      {!settings.isReadOnly && isHovered && potentialWaypoint && <PotentialWaypoint point={potentialWaypoint.point} />}
    </>
  );
}, propsHaveSameValuesDeep);

export const DefaultConditionTransitionEdge = React.memo((props: RF.EdgeProps<SwfDiagramEdgeData>) => {
  const renderCount = useRef<number>(0);
  renderCount.current++;

  const { path, points: waypoints } = useKieEdgePath(props.source, props.target, props.data);

  const interactionPathRef = React.useRef<SVGPathElement>(null);
  const isHovered = useIsHovered(interactionPathRef);
  const settings = useSettings();

  const { onMouseMove, onDoubleClick, potentialWaypoint, isDraggingWaypoint } = usePotentialWaypointControls(
    waypoints,
    props.selected,
    props.id,
    interactionPathRef
  );

  const isConnecting = !!RF.useStore((s) => s.connectionNodeId);
  const className = useEdgeClassName(isConnecting, isDraggingWaypoint);

  useAlwaysVisibleEdgeUpdatersAtNodeBorders(interactionPathRef, props.source, props.target, waypoints);

  return (
    <>
      <DefaultConditionTransitionPath
        svgRef={interactionPathRef}
        d={path}
        {...interactionStrokeProps}
        className={`${interactionStrokeProps.className} ${className}`}
        strokeWidth={props.interactionWidth ?? DEFAULT_INTRACTION_WIDTH}
        onMouseMove={onMouseMove}
        onDoubleClick={onDoubleClick}
        data-edgetype={"information-requirement"}
        visibility={settings.isReadOnly ? "hidden" : undefined}
      />
      <DefaultConditionTransitionPath d={path} className={`kie-swf-editor--edge ${className}`} />

      {!settings.isReadOnly && props.selected && !isConnecting && props.data?.swfEdge && (
        <Waypoints
          edgeId={props.id}
          edgeIndex={props.data.swfEdge.index}
          waypoints={waypoints}
          onDragStop={onMouseMove}
        />
      )}
      {!settings.isReadOnly && isHovered && potentialWaypoint && <PotentialWaypoint point={potentialWaypoint.point} />}
    </>
  );
}, propsHaveSameValuesDeep);

export const CompensationTransitionEdge = React.memo((props: RF.EdgeProps<SwfDiagramEdgeData>) => {
  const renderCount = useRef<number>(0);
  renderCount.current++;

  const { path, points: waypoints } = useKieEdgePath(props.source, props.target, props.data);

  const interactionPathRef = React.useRef<SVGPathElement>(null);
  const isHovered = useIsHovered(interactionPathRef);
  const settings = useSettings();

  const { onMouseMove, onDoubleClick, potentialWaypoint, isDraggingWaypoint } = usePotentialWaypointControls(
    waypoints,
    props.selected,
    props.id,
    interactionPathRef
  );

  const isConnecting = !!RF.useStore((s) => s.connectionNodeId);
  const className = useEdgeClassName(isConnecting, isDraggingWaypoint);

  useAlwaysVisibleEdgeUpdatersAtNodeBorders(interactionPathRef, props.source, props.target, waypoints);

  return (
    <>
      <CompensationTransitionPath
        svgRef={interactionPathRef}
        d={path}
        {...interactionStrokeProps}
        className={`${interactionStrokeProps.className} ${className}`}
        strokeWidth={props.interactionWidth ?? DEFAULT_INTRACTION_WIDTH}
        onMouseMove={onMouseMove}
        onDoubleClick={onDoubleClick}
        data-edgetype={"authority-requirement"}
        visibility={settings.isReadOnly ? "hidden" : undefined}
      />
      <CompensationTransitionPath d={path} className={`kie-swf-editor--edge ${className}`} />

      {!settings.isReadOnly && props.selected && !isConnecting && props.data?.swfEdge && (
        <Waypoints
          edgeId={props.id}
          edgeIndex={props.data.swfEdge.index}
          waypoints={waypoints}
          onDragStop={onMouseMove}
        />
      )}
      {!settings.isReadOnly && isHovered && potentialWaypoint && <PotentialWaypoint point={potentialWaypoint.point} />}
    </>
  );
}, propsHaveSameValuesDeep);

export const DataConditionTransitionEdge = React.memo((props: RF.EdgeProps<SwfDiagramEdgeData>) => {
  const renderCount = useRef<number>(0);
  renderCount.current++;

  const { path, points: waypoints } = useKieEdgePath(props.source, props.target, props.data);

  const interactionPathRef = React.useRef<SVGPathElement>(null);
  const isHovered = useIsHovered(interactionPathRef);
  const settings = useSettings();

  const { onMouseMove, onDoubleClick, potentialWaypoint, isDraggingWaypoint } = usePotentialWaypointControls(
    waypoints,
    props.selected,
    props.id,
    interactionPathRef
  );

  const isConnecting = !!RF.useStore((s) => s.connectionNodeId);
  const className = useEdgeClassName(isConnecting, isDraggingWaypoint);

  useAlwaysVisibleEdgeUpdatersAtNodeBorders(interactionPathRef, props.source, props.target, waypoints);

  return (
    <>
      <DataConditionTransitionPath
        svgRef={interactionPathRef}
        d={path}
        {...interactionStrokeProps}
        className={`${interactionStrokeProps.className} ${className}`}
        strokeWidth={props.interactionWidth ?? DEFAULT_INTRACTION_WIDTH}
        onMouseMove={onMouseMove}
        onDoubleClick={onDoubleClick}
        data-edgetype={"information-requirement"}
        visibility={settings.isReadOnly ? "hidden" : undefined}
      />
      <DataConditionTransitionPath d={path} className={`kie-swf-editor--edge ${className}`} />

      {!settings.isReadOnly && props.selected && !isConnecting && props.data?.swfEdge && (
        <Waypoints
          edgeId={props.id}
          edgeIndex={props.data.swfEdge.index}
          waypoints={waypoints}
          onDragStop={onMouseMove}
        />
      )}
      {!settings.isReadOnly && isHovered && potentialWaypoint && <PotentialWaypoint point={potentialWaypoint.point} />}
    </>
  );
}, propsHaveSameValuesDeep);
