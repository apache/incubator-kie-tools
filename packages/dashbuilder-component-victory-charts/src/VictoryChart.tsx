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
import { useState, useEffect, useCallback } from "react";
import { AreaChart } from "./charts/AreaChart";
import { BarChart } from "./charts/BarChart";
import { LineChart } from "./charts/LineChart";
import { DonutChart } from "./charts/DonutChart";
import { PieChart } from "./charts/PieChart";
import { StackChart } from "./charts/StackChart";
import {
  ThemeColorType,
  ChartType,
  LegendPosition,
  ThemeVariantType,
  ChartProps,
  LegendOrientation,
} from "./charts/BaseChart";
import { validateDataSetForChart } from "./charts/PropsValidation";
import { UtilizationDonut } from "./charts/UtilizationDonut";
import { DataSet } from "@kie-tools/dashbuilder-component-api";
import { ScatterChart } from "./charts/ScatterChart";

export interface VictoryChartProps {
  dataSet?: DataSet;

  width: number;
  height: number;

  type: ChartType;
  themeColor: ThemeColorType;
  legendPosition?: LegendPosition;
  legendOrientation?: LegendOrientation;

  animate?: boolean;
  animationDuration?: number;
  animationEasing?: any;

  paddingTop?: number;
  paddingLeft?: number;
  paddingBottom?: number;
  paddingRight?: number;

  title?: string;
  description?: string;

  zoom?: boolean;
  gridX?: boolean;
  gridY?: boolean;
  fixLabelsOverlap?: boolean;

  donutTitle?: string;
  donutSubTitle?: string;

  backgroundColor?: string;
  staticTitle?: boolean;

  horizontalBars?: boolean;
  barWidth?: number;
  barOffset?: number;

  onValidationError?: (message: string) => void;
  onValidationSuccess?: () => void;
}

const EMPTY_DATASET: DataSet = {
  columns: [],
  data: [],
};

export const VictoryChart = (props: VictoryChartProps) => {
  useEffect(() => {
    const validation = validateDataSetForChart(props.type, props.dataSet || EMPTY_DATASET);
    if (validation.isValid && props.onValidationSuccess) {
      props.onValidationSuccess!();
    } else {
      props.onValidationError!(validation.message!);
    }
  }, [props, props.dataSet, props.onValidationSuccess, props.onValidationError]);

  const selectChart = useCallback(
    (type: ChartType) => {
      const victoryProps: ChartProps = {
        width: props.width || 600,
        height: props.height || 400,
        themeColor: props.themeColor,
        dataSet: props.dataSet || EMPTY_DATASET,
        legendPosition: props.legendPosition || "bottom",
        legendOrientation: props.legendOrientation,
        animation: {
          easing: props.animationEasing,
          duration: props.animationDuration,
          enabled: props.animate!,
        },
        fixLabelsOverlap: props.fixLabelsOverlap,
        ariaTitle: props.title || "",
        ariaDescription: props.description || "",
        padding: {
          bottom: props.paddingBottom || 0,
          left: props.paddingLeft || 0,
          top: props.paddingTop || 0,
          right: props.paddingRight || 0,
        },
        grid: {
          x: props.gridX!,
          y: props.gridY!,
        },
        donutTitle: props.donutTitle,
        donutSubTitle: props.donutSubTitle,
        horizontalBars: props.horizontalBars,
        barWidth: props.barWidth,
        barOffset: props.barOffset,
        zoom: props.zoom,
      };
      switch (type) {
        case "area":
          return <AreaChart {...victoryProps} />;
        case "bar":
          return <BarChart {...victoryProps} />;
        case "line":
          return <LineChart {...victoryProps} />;
        case "donut":
          return <DonutChart {...victoryProps} />;
        case "pie":
          return <PieChart {...victoryProps} />;
        case "stack":
          return <StackChart {...victoryProps} />;
        case "utilization-donut":
          return <UtilizationDonut {...victoryProps} />;
        case "scatter":
          return <ScatterChart {...victoryProps} />;
      }
    },
    [props]
  );
  return <>{selectChart(props.type)}</>;
};
