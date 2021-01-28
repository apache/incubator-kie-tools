/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import * as React from "react";
import { Chart, ChartAxis, ChartGroup, ChartLabel, ChartLine, ChartVoronoiContainer } from "@patternfly/react-charts";

export class Line {
  //y=mx+c
  public readonly m: number;
  public readonly c: number;
  public readonly title: string;

  constructor(m: number, c: number, title: string) {
    this.m = m;
    this.c = c;
    this.title = title;
  }
}

export class Range {
  public readonly min: number;
  public readonly max: number;

  constructor(min: number, max: number) {
    this.min = min;
    this.max = max;
  }
}

interface LinearRegressionViewProps {
  modelName: string;
  independentAxisTitle: string;
  dependentAxisTitle: string;
  width?: number;
  height?: number;
  lines: Line[];
  rangeX: Range;
  rangeY: Range;
}

const roundedToFixed = (_float: number, _digits: number): string => {
  const rounded = Math.pow(10, _digits);
  return (Math.round(_float * rounded) / rounded).toFixed(_digits);
};

const getTicks = (range: Range, count: number): number[] => {
  const start: number = range.min;
  const end: number = range.max;
  const step: number = (end - start) / count;
  const ticks: number[] = new Array<number>();
  let v: number = start;
  while (v <= end) {
    ticks.push(v);
    v = v + step;
  }
  if (ticks[ticks.length - 1] !== end) {
    ticks.push(end);
  }
  return ticks;
};

export const LinearRegressionView = (props: LinearRegressionViewProps) => {
  const legendData: any = [];
  props.lines.forEach(line => {
    legendData.push({ name: line.title });
  });

  const { modelName = "undefined", width = 500, height = 500 } = props;

  return (
    <div style={{ height: height, width: width }}>
      <Chart
        ariaTitle={modelName}
        containerComponent={
          <ChartVoronoiContainer
            labels={({ datum }) => `${roundedToFixed(datum._x, 2)}, ${roundedToFixed(datum._y, 2)}`}
            constrainToVisibleArea={true}
          />
        }
        legendData={legendData}
        legendOrientation="horizontal"
        legendPosition="bottom"
        padding={{
          bottom: 100,
          left: 50,
          right: 50,
          top: 50
        }}
        height={height}
        width={width}
      >
        <ChartLabel text={modelName} x={width / 2} y={30} textAnchor="middle" />
        <ChartAxis
          label={props.independentAxisTitle}
          showGrid={true}
          tickValues={getTicks(props.rangeX, 8)}
          tickFormat={x => roundedToFixed(x, 2)}
        />
        <ChartAxis
          label={props.dependentAxisTitle}
          dependentAxis={true}
          showGrid={true}
          tickValues={getTicks(props.rangeY, 8)}
          tickFormat={x => roundedToFixed(x, 2)}
        />
        <ChartGroup>
          {props.lines.map(line => {
            return (
              <ChartLine
                key={line.title}
                samples={100}
                domain={{
                  x: [props.rangeX.min, props.rangeX.max],
                  y: [props.rangeY.min, props.rangeY.max]
                }}
                y={(datum: any) => line.m * datum.x + line.c}
              />
            );
          })}
        </ChartGroup>
      </Chart>
    </div>
  );
};
