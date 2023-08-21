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
import { useCallback, useEffect, createRef, useState } from "react";
import * as heatmap from "heatmap.js";

export interface SvgNodeValue {
  nodeId: string;
  value: number;
}

interface NodeInfo {
  x: number;
  y: number;
  size: number;
}

interface HeatData {
  x: number;
  y: number;
  value: number;
  radius?: number;
}

const getNode = (id: string, useContains?: boolean) =>
  useContains ? (document.querySelector(`[id*='${id}']`) as HTMLElement) : document.getElementById(id);

const getNodeInfo = (el: HTMLElement): NodeInfo => {
  const bounds = el.getBoundingClientRect();
  const radius = Math.sqrt((bounds.width * bounds.height) / 4);
  return {
    x: (bounds.left + bounds.right) / 2,
    y: (bounds.top + bounds.bottom) / 2,
    size: radius,
  };
};

export interface SvgHeatmapProps {
  svgNodesValues: SvgNodeValue[];
  svgContent: string;
  containsId?: boolean;
  blur?: number;
  opacity?: number;
  maxOpacity?: number;
  sizeFactor?: number;
  width?: string;
  height?: string;
}

export function SvgHeatmap(props: SvgHeatmapProps) {
  const parentRef = createRef<HTMLDivElement>();
  const [svgHeatmap, setSvgHeatmap] = useState<heatmap.Heatmap<any, any, any>>();
  const [repaint, setRepaint] = useState(false);

  useEffect(() => {
    if (props.svgContent) {
      const heatmapContainer = parentRef.current!;
      heatmapContainer.innerHTML = props.svgContent;
      const svg = heatmapContainer.querySelector("svg")!;
      svg.style.width = "100%";
      svg.style.height = "100%";
      setSvgHeatmap(
        heatmap
          .create({
            container: heatmapContainer,
            blur: props.blur && props.blur > 0 ? props.blur : undefined,
            opacity: props.opacity && props.opacity > 0 ? props.opacity : undefined,
          })
          .setData({
            max: 0,
            min: 0,
            data: [],
          })
      );
    }
  }, [props]);

  useEffect(() => {
    if (svgHeatmap && props.svgNodesValues && props.svgNodesValues.length > 0) {
      const values = props.svgNodesValues
        .filter((n) => getNode(n.nodeId, props.containsId))
        .map((nodeValue) => {
          const node = getNode(nodeValue.nodeId, props.containsId);
          const nodeInfo = getNodeInfo(node!);
          return {
            x: Math.ceil(nodeInfo.x),
            y: Math.ceil(nodeInfo.y),
            radius: nodeInfo.size * (props.sizeFactor && props.sizeFactor > 0 ? props.sizeFactor : 1),
            value: nodeValue.value,
          };
        });

      if (values.length > 0) {
        svgHeatmap.setData({
          min: values.map((d) => d.value).reduce((d1, d2) => Math.min(d1, d2)),
          max: values.map((d) => d.value).reduce((d1, d2) => Math.max(d1, d2)),
          data: values,
        });
      }
      svgHeatmap.repaint();
    }
  }, [svgHeatmap, props.svgNodesValues, props.sizeFactor, repaint]);

  const onResize = useCallback(() => setRepaint((previous) => !previous), [repaint]);

  useEffect(() => {
    window.addEventListener("resize", onResize, false);
    return () => window.removeEventListener("resize", onResize, false);
  }, [repaint]);

  return (
    <>
      <div style={{ width: props.width || "100%", height: props.height || "100%" }} ref={parentRef} />
    </>
  );
}
