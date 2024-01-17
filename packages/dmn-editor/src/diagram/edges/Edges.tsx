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

import {
  DMN15__tDefinitions,
  DMNDI15__DMNEdge,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import * as React from "react";
import * as RF from "reactflow";
import { Unpacked } from "../../tsExt/tsExt";
import { PotentialWaypoint, Waypoints } from "./Waypoints";
import { useKieEdgePath } from "./useKieEdgePath";
import { useIsHovered } from "../useIsHovered";
import { usePotentialWaypointControls } from "./usePotentialWaypointControls";
import { useAlwaysVisibleEdgeUpdatersAtNodeBorders } from "./useAlwaysVisibleEdgeUpdatersAtNodeBorders";
import { DEFAULT_INTRACTION_WIDTH } from "../maths/DmnMaths";
import { propsHaveSameValuesDeep } from "../memoization/memoization";
import { useRef } from "react";

export type DmnDiagramEdgeData = {
  dmnEdge: (DMNDI15__DMNEdge & { index: number }) | undefined;
  dmnObject: {
    id: string;
    type:
      | Unpacked<DMN15__tDefinitions["artifact"]>["__$$element"]
      | Unpacked<DMN15__tDefinitions["drgElement"]>["__$$element"];
    requirementType: "informationRequirement" | "knowledgeRequirement" | "authorityRequirement" | "association";
    index: number;
  };
  dmnShapeSource: DMNDI15__DMNShape | undefined;
  dmnShapeTarget: DMNDI15__DMNShape | undefined;
};

export const InformationRequirementPath = React.memo(
  (_props: React.SVGProps<SVGPathElement> & { svgRef?: React.RefObject<SVGPathElement> }) => {
    const { svgRef, ...props } = _props;
    return (
      <>
        <path ref={svgRef} style={{ strokeWidth: 1, stroke: "black" }} markerEnd={"url(#closed-arrow)"} {...props} />
      </>
    );
  }
);

export const KnowledgeRequirementPath = React.memo(
  (__props: React.SVGProps<SVGPathElement> & { svgRef?: React.RefObject<SVGPathElement> }) => {
    const { svgRef, ...props } = __props;
    return (
      <>
        <path
          ref={svgRef}
          style={{ strokeWidth: 1, stroke: "black", strokeDasharray: "5,5" }}
          markerEnd={"url(#open-arrow)"}
          {...props}
        />
      </>
    );
  }
);

export const AuthorityRequirementPath = React.memo(
  (
    __props: React.SVGProps<SVGPathElement> & { centerToConnectionPoint: boolean | undefined } & {
      svgRef?: React.RefObject<SVGPathElement>;
    }
  ) => {
    const { centerToConnectionPoint: center, svgRef, ...props } = __props;
    return (
      <>
        <path
          ref={svgRef}
          style={{ strokeWidth: 1, stroke: "black", strokeDasharray: "5,5" }}
          markerEnd={center ? `url(#closed-circle-at-center)` : `url(#closed-circle-at-border)`}
          {...props}
        />
      </>
    );
  }
);

export const AssociationPath = React.memo(
  (__props: React.SVGProps<SVGPathElement> & { svgRef?: React.RefObject<SVGPathElement> }) => {
    const strokeWidth = __props.strokeWidth ?? 1.5;
    const { svgRef, ...props } = __props;
    return (
      <>
        <path
          ref={svgRef}
          strokeWidth={strokeWidth}
          strokeLinecap="butt"
          strokeLinejoin="round"
          style={{ stroke: "black", strokeDasharray: `${strokeWidth},10` }}
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

//

const interactionStrokeProps: Partial<React.SVGAttributes<SVGPathElement>> = {
  strokeOpacity: 1,
  markerEnd: undefined,
  style: undefined,
  className: "react-flow__edge-interaction",
  stroke: "transparent",
  strokeLinecap: "round",
};

export const InformationRequirementEdge = React.memo((props: RF.EdgeProps<DmnDiagramEdgeData>) => {
  const renderCount = useRef<number>(0);
  renderCount.current++;

  const { path, points: waypoints } = useKieEdgePath(props.source, props.target, props.data);

  const interactionPathRef = React.useRef<SVGPathElement>(null);
  const isHovered = useIsHovered(interactionPathRef);

  const { onMouseMove, onDoubleClick, potentialWaypoint, isDraggingWaypoint } = usePotentialWaypointControls(
    waypoints,
    props.selected,
    props.id,
    props.data?.dmnEdge?.index,
    interactionPathRef
  );

  const isConnecting = !!RF.useStore((s) => s.connectionNodeId);
  const className = useEdgeClassName(isConnecting, isDraggingWaypoint);

  useAlwaysVisibleEdgeUpdatersAtNodeBorders(interactionPathRef, props.source, props.target, waypoints);

  console.log(`re-rendering edge --> count: ${renderCount.current}`);

  return (
    <>
      <InformationRequirementPath
        svgRef={interactionPathRef}
        d={path}
        {...interactionStrokeProps}
        className={`${interactionStrokeProps.className} ${className}`}
        strokeWidth={props.interactionWidth ?? DEFAULT_INTRACTION_WIDTH}
        onMouseMove={onMouseMove}
        onDoubleClick={onDoubleClick}
      />
      <InformationRequirementPath d={path} className={`kie-dmn-editor--edge ${className}`} />

      {props.selected && !isConnecting && props.data?.dmnEdge && (
        <Waypoints
          edgeId={props.id}
          edgeIndex={props.data.dmnEdge.index}
          waypoints={waypoints}
          onDragStop={onMouseMove}
        />
      )}
      {isHovered && potentialWaypoint && <PotentialWaypoint point={potentialWaypoint.point} />}
    </>
  );
}, propsHaveSameValuesDeep);

export const KnowledgeRequirementEdge = React.memo((props: RF.EdgeProps<DmnDiagramEdgeData>) => {
  const renderCount = useRef<number>(0);
  renderCount.current++;

  const { path, points: waypoints } = useKieEdgePath(props.source, props.target, props.data);

  const interactionPathRef = React.useRef<SVGPathElement>(null);
  const isHovered = useIsHovered(interactionPathRef);

  const { onMouseMove, onDoubleClick, potentialWaypoint, isDraggingWaypoint } = usePotentialWaypointControls(
    waypoints,
    props.selected,
    props.id,
    props.data?.dmnEdge?.index,
    interactionPathRef
  );

  const isConnecting = !!RF.useStore((s) => s.connectionNodeId);
  const className = useEdgeClassName(isConnecting, isDraggingWaypoint);

  useAlwaysVisibleEdgeUpdatersAtNodeBorders(interactionPathRef, props.source, props.target, waypoints);

  console.log(`re-rendering edge --> count: ${renderCount.current}`);

  return (
    <>
      <KnowledgeRequirementPath
        svgRef={interactionPathRef}
        d={path}
        {...interactionStrokeProps}
        className={`${interactionStrokeProps.className} ${className}`}
        strokeWidth={props.interactionWidth ?? DEFAULT_INTRACTION_WIDTH}
        onMouseMove={onMouseMove}
        onDoubleClick={onDoubleClick}
      />
      <KnowledgeRequirementPath d={path} className={`kie-dmn-editor--edge ${className}`} />

      {props.selected && !isConnecting && props.data?.dmnEdge && (
        <Waypoints
          edgeId={props.id}
          edgeIndex={props.data.dmnEdge.index}
          waypoints={waypoints}
          onDragStop={onMouseMove}
        />
      )}
      {isHovered && potentialWaypoint && <PotentialWaypoint point={potentialWaypoint.point} />}
    </>
  );
}, propsHaveSameValuesDeep);

export const AuthorityRequirementEdge = React.memo((props: RF.EdgeProps<DmnDiagramEdgeData>) => {
  const renderCount = useRef<number>(0);
  renderCount.current++;

  const { path, points: waypoints } = useKieEdgePath(props.source, props.target, props.data);

  const interactionPathRef = React.useRef<SVGPathElement>(null);
  const isHovered = useIsHovered(interactionPathRef);

  const { onMouseMove, onDoubleClick, potentialWaypoint, isDraggingWaypoint } = usePotentialWaypointControls(
    waypoints,
    props.selected,
    props.id,
    props.data?.dmnEdge?.index,
    interactionPathRef
  );

  const isConnecting = !!RF.useStore((s) => s.connectionNodeId);
  const className = useEdgeClassName(isConnecting, isDraggingWaypoint);

  useAlwaysVisibleEdgeUpdatersAtNodeBorders(interactionPathRef, props.source, props.target, waypoints);

  console.log(`re-rendering edge --> count: ${renderCount.current}`);

  return (
    <>
      <AuthorityRequirementPath
        svgRef={interactionPathRef}
        d={path}
        centerToConnectionPoint={false}
        {...interactionStrokeProps}
        className={`${interactionStrokeProps.className} ${className}`}
        strokeWidth={props.interactionWidth ?? DEFAULT_INTRACTION_WIDTH}
        onMouseMove={onMouseMove}
        onDoubleClick={onDoubleClick}
      />
      <AuthorityRequirementPath
        d={path}
        className={`kie-dmn-editor--edge ${className}`}
        centerToConnectionPoint={false}
      />

      {props.selected && !isConnecting && props.data?.dmnEdge && (
        <Waypoints
          edgeId={props.id}
          edgeIndex={props.data.dmnEdge.index}
          waypoints={waypoints}
          onDragStop={onMouseMove}
        />
      )}
      {isHovered && potentialWaypoint && <PotentialWaypoint point={potentialWaypoint.point} />}
    </>
  );
}, propsHaveSameValuesDeep);

export const AssociationEdge = React.memo((props: RF.EdgeProps<DmnDiagramEdgeData>) => {
  const renderCount = useRef<number>(0);
  renderCount.current++;

  const { path, points: waypoints } = useKieEdgePath(props.source, props.target, props.data);

  const interactionPathRef = React.useRef<SVGPathElement>(null);
  const isHovered = useIsHovered(interactionPathRef);

  const { onMouseMove, onDoubleClick, potentialWaypoint, isDraggingWaypoint } = usePotentialWaypointControls(
    waypoints,
    props.selected,
    props.id,
    props.data?.dmnEdge?.index,
    interactionPathRef
  );

  const isConnecting = !!RF.useStore((s) => s.connectionNodeId);
  const className = useEdgeClassName(isConnecting, isDraggingWaypoint);

  useAlwaysVisibleEdgeUpdatersAtNodeBorders(interactionPathRef, props.source, props.target, waypoints);

  console.log(`re-rendering edge --> count: ${renderCount.current}`);

  return (
    <>
      <AssociationPath
        svgRef={interactionPathRef}
        d={path}
        {...interactionStrokeProps}
        className={`${interactionStrokeProps.className} ${className}`}
        strokeWidth={props.interactionWidth ?? DEFAULT_INTRACTION_WIDTH}
        onMouseMove={onMouseMove}
        onDoubleClick={onDoubleClick}
      />
      <AssociationPath d={path} className={`kie-dmn-editor--edge ${className}`} />

      {props.selected && !isConnecting && props.data?.dmnEdge && (
        <Waypoints
          edgeId={props.id}
          edgeIndex={props.data.dmnEdge.index}
          waypoints={waypoints}
          onDragStop={onMouseMove}
        />
      )}
      {isHovered && potentialWaypoint && <PotentialWaypoint point={potentialWaypoint.point} />}
    </>
  );
}, propsHaveSameValuesDeep);
