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
import { ChartVoronoiContainer } from "@patternfly/react-charts";
import { VictoryZoomContainer } from "victory-zoom-container";
import { DataSet } from "@kie-tools/dashbuilder-component-api";

export type ThemeColorType =
  | "blue"
  | "cyan"
  | "blue"
  | "gold"
  | "gray"
  | "green"
  | "multi"
  | "multi-ordered"
  | "multi-unordered"
  | "orange"
  | "purple";

export type ThemeVariantType = "light" | "dark";

export type LegendPosition = "bottom-left" | "bottom" | "right";
export type ChartType = "bar" | "area" | "line" | "donut" | "pie" | "stack" | "utilization-donut" | "scatter";
export type LegendOrientation = "horizontal" | "vertical";

export interface XYChartData {
  x: any;
  y: any;
}

export interface ChartSeries {
  name: string;
}

export interface XYChartDataLine extends XYChartData, ChartSeries {}

export interface XYChartSeries extends ChartSeries {
  data: XYChartData[];
}

export interface PieChartSerie extends ChartSeries {
  y: number;
}

export interface Grid {
  x: boolean;
  y: boolean;
}

export interface AnimationProp {
  enabled: boolean;
  duration?: number;
  easing?: any;
}

export interface LegendData {
  name: string;
}

export interface ChartProps {
  width: number;
  height: number;
  themeColor: ThemeColorType;
  dataSet: DataSet;
  legendPosition: LegendPosition;
  legendOrientation?: LegendOrientation;
  animation: AnimationProp;
  ariaTitle: string;
  ariaDescription: string;
  grid: Grid;
  padding: any;

  donutTitle?: string;
  donutSubTitle?: string;

  horizontalBars?: boolean;
  fixLabelsOverlap?: boolean;

  // Bar Chart configuration
  barWidth?: number;
  barOffset?: number;

  zoom?: boolean;
}

export abstract class BaseChart extends React.Component<ChartProps, any> {
  legendOrientation: LegendOrientation = "horizontal";
  legendData: LegendData[] = [];
  animationProp: any = false;

  containerComponent: React.ReactElement<any> = (
    // TODO: Explore options from CursorVoronoiContainer
    <ChartVoronoiContainer labels={({ datum }: any) => `${datum.x}: ${datum.y}`} constrainToVisibleArea={true} />
  );

  constructor(props: ChartProps) {
    super(props);
    this.buildLegendData();
    this.legendOrientation =
      this.props.legendOrientation || (this.props.legendPosition === "right" ? "vertical" : "horizontal");
    if (props.animation.enabled) {
      this.animationProp = {
        duration: props.animation.duration,
        easing: props.animation.easing,
      };
    }

    if (props.zoom) {
      this.containerComponent = <VictoryZoomContainer />;
    }
  }

  buildLegendData() {
    this.legendData = this.categories().map((name) => {
      return { name: name };
    });
  }

  abstract categories(): string[];
}
