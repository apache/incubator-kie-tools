import * as React from "react";
import { PaddingProps, AnimationEasing, AnimatePropTypeInterface } from "victory-core";
import { VictoryZoomContainer } from "victory-zoom-container";
import { ChartVoronoiContainer } from "@patternfly/react-charts";
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
export type ChartType = "bar" | "area" | "line" | "donut" | "pie" | "stack" | "utilization-donut";
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

export type AnimationEasingType = AnimationEasing;

export interface AnimationProp {
  enabled: boolean;
  duration?: number;
  easing?: AnimationEasing;
}

export interface LegendData {
  name: string;
}

export interface ChartProps {
  width: number;
  height: number;
  themeColor: ThemeColorType;
  themeVariant: ThemeVariantType;
  zoom: boolean;
  dataSet: DataSet;
  legendPosition: LegendPosition;
  animation: AnimationProp;
  ariaTitle: string;
  ariaDescription: string;
  grid: Grid;
  padding: PaddingProps;

  donutTitle?: string;
  donutSubTitle?: string;

  horizontalBars?: boolean;
  fixLabelsOverlap?: boolean;
}

export abstract class BaseChart extends React.Component<ChartProps, any> {
  legendOrientation: LegendOrientation = "horizontal";
  legendData: LegendData[] = [];
  animationProp: boolean | AnimatePropTypeInterface = false;

  containerComponent: React.ReactElement<any> = (
    // TODO: Explore options from CursorVoronoiContainer
    <ChartVoronoiContainer labels={({ datum }) => `${datum.x}: ${datum.y}`} constrainToVisibleArea={true} />
  );

  constructor(props: ChartProps) {
    super(props);
    this.buildLegendData();
    this.legendOrientation = this.props.legendPosition === "right" ? "vertical" : "horizontal";
    if (props.zoom) {
      this.containerComponent = <VictoryZoomContainer />;
    }
    if (props.animation.enabled) {
      this.animationProp = {
        duration: props.animation.duration,
        easing: props.animation.easing,
      };
    }
  }

  buildLegendData() {
    this.legendData = this.categories().map((name) => {
      return { name: name };
    });
  }

  abstract categories(): string[];
}
