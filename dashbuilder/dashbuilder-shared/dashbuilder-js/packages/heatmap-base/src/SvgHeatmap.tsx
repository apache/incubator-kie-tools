/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

const reduce = (data: HeatData[], reducer: (v1: number, v2: number) => number) =>
  data.length > 0 ? data.map(d => d.value).reduce((d1, d2) => reducer(d1, d2)) : 0;

function createHeatmap(parent: HTMLElement, heatData: HeatData[]) {
  return heatmap
    .create({
      container: parent
    })
    .setData({
      max: reduce(heatData, Math.max),
      min: reduce(heatData, Math.min),
      data: heatData
    });
}

const getNodeInfo = (el: HTMLElement): NodeInfo => {
  const bounds = el.getBoundingClientRect();
  const radius = Math.sqrt((bounds.width * bounds.height) / 4);
  return {
    x: (bounds.left + bounds.right) / 2,
    y: (bounds.top + bounds.bottom) / 2,
    size: radius
  };
};

export interface SvgHeatmapProps {
  svgNodesValues: SvgNodeValue[];
  svgContent: string;
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
      svg.style.height = "auto";
      setSvgHeatmap(createHeatmap(heatmapContainer, []));
    }
  }, [props.svgContent]);

  useEffect(() => {
    if (svgHeatmap && props.svgNodesValues && props.svgNodesValues.length > 0) {
      const values = props.svgNodesValues
        .filter(n => document.getElementById(n.nodeId))
        .map(nodeValue => {
          const node = document.getElementById(nodeValue.nodeId);
          const nodeInfo = getNodeInfo(node!);
          return {
            x: Math.ceil(nodeInfo.x),
            y: Math.ceil(nodeInfo.y),
            radius: nodeInfo.size,
            value: nodeValue.value
          };
        });

      if (values.length > 0) {
        svgHeatmap.setData({
          min: values.map(d => d.value).reduce((d1, d2) => Math.min(d1, d2)),
          max: values.map(d => d.value).reduce((d1, d2) => Math.max(d1, d2)),
          data: values
        });
      }
      svgHeatmap.repaint();
    }
  }, [svgHeatmap, props.svgNodesValues, repaint]);

  const onResize = useCallback(() => setRepaint(previous => !previous), [repaint]);

  useEffect(() => {
    window.addEventListener("resize", onResize, false);
    return () => window.removeEventListener("resize", onResize, false);
  }, [repaint]);

  return <div style={{ width: props.width || "100%", height: props.height || "100%" }} ref={parentRef} />;
}
