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

import { DC__Point } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { useLayoutEffect } from "react";
import * as RF from "reactflow";
import { getLineRectangleIntersectionPoint } from "../maths/DmnMaths";
import { scaleFromCenter } from "../maths/Maths";

const RADIUS = 5;
const HOVERED_RADIUS = 10;

export function useAlwaysVisibleEdgeUpdatersAtNodeBorders(
  interactionPathRef: React.RefObject<SVGPathElement>,
  source: string,
  target: string,
  snappedWaypoints: DC__Point[]
) {
  const sourceNodeX = RF.useStore((s) => (source ? s.nodeInternals.get(source)?.positionAbsolute?.x : undefined));
  const sourceNodeY = RF.useStore((s) => (source ? s.nodeInternals.get(source)?.positionAbsolute?.y : undefined));
  const sourceNodeWidth = RF.useStore((s) => (source ? s.nodeInternals.get(source)?.width : undefined));
  const sourceNodeHeight = RF.useStore((s) => (source ? s.nodeInternals.get(source)?.height : undefined));

  const targetNodeX = RF.useStore((s) => (target ? s.nodeInternals.get(target)?.positionAbsolute?.x : undefined));
  const targetNodeY = RF.useStore((s) => (target ? s.nodeInternals.get(target)?.positionAbsolute?.y : undefined));
  const targetNodeWidth = RF.useStore((s) => (target ? s.nodeInternals.get(target)?.width : undefined));
  const targetNodeHeight = RF.useStore((s) => (target ? s.nodeInternals.get(target)?.height : undefined));

  useLayoutEffect(() => {
    const edgeSvgGroup = interactionPathRef.current!.parentElement;

    const edgeUpdaterSource = edgeSvgGroup!.querySelector(".react-flow__edgeupdater-source") as SVGCircleElement;
    const edgeUpdaterTarget = edgeSvgGroup!.querySelector(".react-flow__edgeupdater-target") as SVGCircleElement;

    function onEnter(e: MouseEvent) {
      (e.target as any)?.setAttribute("r", `${HOVERED_RADIUS}`);
    }

    function onLeave(e: MouseEvent) {
      (e.target as any)?.setAttribute("r", `${RADIUS}`);
    }

    edgeUpdaterSource?.addEventListener("mouseenter", onEnter);
    edgeUpdaterSource?.addEventListener("mouseleave", onLeave);
    edgeUpdaterTarget?.addEventListener("mouseenter", onEnter);
    edgeUpdaterTarget?.addEventListener("mouseleave", onLeave);
    return () => {
      edgeUpdaterSource?.removeEventListener("mouseleave", onLeave);
      edgeUpdaterSource?.removeEventListener("mouseenter", onEnter);
      edgeUpdaterTarget?.removeEventListener("mouseleave", onLeave);
      edgeUpdaterTarget?.removeEventListener("mouseenter", onEnter);
    };
  }, [interactionPathRef]);

  useLayoutEffect(() => {
    const edgeSvgGroup = interactionPathRef.current!.parentElement;

    // Get fake scaled bounds to give the Edge Updaters some distance of the node.
    const scaledSourceNode = scaleFromCenter(HOVERED_RADIUS, {
      position: { x: sourceNodeX ?? 0, y: sourceNodeY ?? 0 },
      dimensions: { width: sourceNodeWidth, height: sourceNodeHeight },
    });
    const scaledTargetNode = scaleFromCenter(HOVERED_RADIUS, {
      position: { x: targetNodeX ?? 0, y: targetNodeY ?? 0 },
      dimensions: { width: targetNodeWidth, height: targetNodeHeight },
    });

    // Get the intersection point between the edge and the nodes. The Edge Updater must be visible at all times!
    //
    // FIXME: Sometimes the immediate next waypoint is hidden behind the node.
    //        Ideally, we would use the first waypoints that's outside of the node's bounds.
    const firstWaypointOutsideSourceNodeBounds = snappedWaypoints[1];
    const sourcePoint = getLineRectangleIntersectionPoint(firstWaypointOutsideSourceNodeBounds, snappedWaypoints[0], {
      x: scaledSourceNode.position.x ?? 0,
      y: scaledSourceNode.position.y ?? 0,
      width: scaledSourceNode.dimensions.width ?? 0,
      height: scaledSourceNode.dimensions.height ?? 0,
    });

    const firstWaypointOutsideTargetNodeBounds = snappedWaypoints[snappedWaypoints.length - 2];
    const targetPoint = getLineRectangleIntersectionPoint(
      firstWaypointOutsideTargetNodeBounds,
      snappedWaypoints[snappedWaypoints.length - 1],
      {
        x: scaledTargetNode.position.x ?? 0,
        y: scaledTargetNode.position.y ?? 0,
        width: scaledTargetNode.dimensions.width ?? 0,
        height: scaledTargetNode.dimensions.height ?? 0,
      }
    );

    // Update source Edge Updater
    const edgeUpdaterSource = edgeSvgGroup!.querySelector(".react-flow__edgeupdater-source") as SVGCircleElement;
    edgeUpdaterSource.setAttribute("cx", "" + sourcePoint!["@_x"]);
    edgeUpdaterSource.setAttribute("cy", "" + sourcePoint!["@_y"]);
    edgeUpdaterSource.setAttribute("r", `${RADIUS}`);

    // Update target Edge Updater
    const edgeUpdaterTarget = edgeSvgGroup!.querySelector(".react-flow__edgeupdater-target") as SVGCircleElement;
    edgeUpdaterTarget.setAttribute("cx", "" + targetPoint!["@_x"]);
    edgeUpdaterTarget.setAttribute("cy", "" + targetPoint!["@_y"]);
    edgeUpdaterTarget.setAttribute("r", `${RADIUS}`);
  }, [
    interactionPathRef,
    snappedWaypoints,
    sourceNodeHeight,
    sourceNodeWidth,
    sourceNodeX,
    sourceNodeY,
    targetNodeHeight,
    targetNodeWidth,
    targetNodeX,
    targetNodeY,
  ]);
}
