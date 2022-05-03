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
  AnimationEasingType,
  ThemeVariantType,
  ChartProps,
} from "./charts/BaseChart";
import { validateDataSetForChart } from "./charts/PropsValidation";
import { UtilizationDonut } from "./charts/UtilizationDonut";
import { DataSet } from "@kie-tools/dashbuilder-component-api";

export interface VictoryChartProps {
  dataSet?: DataSet;

  width: number;
  height: number;

  type: ChartType;
  themeColor: ThemeColorType;
  themeVariant: ThemeVariantType;
  legendPosition?: LegendPosition;

  animate?: boolean;
  animationDuration?: number;
  animationEasing?: AnimationEasingType;

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
        themeVariant: props.themeVariant,
        dataSet: props.dataSet || EMPTY_DATASET,
        legendPosition: props.legendPosition || "bottom",
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
        zoom: props.zoom!,
        grid: {
          x: props.gridX!,
          y: props.gridY!,
        },
        donutTitle: props.donutTitle,
        donutSubTitle: props.donutSubTitle,
        horizontalBars: props.horizontalBars,
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
      }
    },
    [props]
  );
  return <>{selectChart(props.type)}</>;
};
