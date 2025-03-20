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

import * as Numeral from "numeral";
import { BaseChart, XYChartSeries, XYChartDataLine, XYChartData } from "./BaseChart";
import { Chart, ChartAxis, ChartGroup } from "@patternfly/react-charts";

export type ChartGroupType = typeof ChartGroup;

export abstract class XYChart extends BaseChart {
  render() {
    const { width, height, themeColor } = this.props;
    return (
      <Chart
        ariaDesc={this.props.ariaDescription}
        ariaTitle={this.props.ariaTitle}
        containerComponent={this.containerComponent}
        domainPadding={{ x: [30, 25] }}
        legendData={this.legendData}
        legendOrientation={this.legendOrientation}
        legendPosition={this.props.legendPosition}
        width={width}
        height={height}
        animate={this.animationProp}
        padding={this.props.padding}
        themeColor={themeColor}
      >
        <ChartAxis showGrid={this.props.grid.y} fixLabelOverlap={this.props.fixLabelsOverlap} />
        <ChartAxis
          dependentAxis
          showGrid={this.props.grid.x}
          tickFormat={(t: any) => Numeral(t).format(this.pattern())}
          fixLabelOverlap={this.props.fixLabelsOverlap}
        />
        {this.buildChartGroup()}
      </Chart>
    );
  }

  abstract buildChartGroup(): ChartGroupType;

  categories() {
    return this.props.dataSet.columns.slice(1).map((column: any) => column.settings["columnName"]);
  }

  pattern() {
    const pattern = this.props.dataSet.columns.slice(1).map((column: any) => column.settings.valuePattern)[0];
    return pattern?.replace(/#/g, "0");
  }

  dataSetToXYData(): XYChartSeries[] {
    const groupedLines: Map<string, XYChartData[]> = new Map();
    const categories = this.categories();
    const ds = this.props.dataSet;
    const rows = ds.data.length;
    const cols = ds.columns.length;
    const getcolumnExpression = this.props.dataSet.columns
      .slice(0)
      .map((column: any) => column.settings["valueExpression"]);
    const getcolumn1Expression = (Object as any).values(getcolumnExpression)[0].toString();
    const getExpression = this.props.dataSet.columns.slice(1).map((column: any) => column.settings["valueExpression"]);
    const expression = (Object as any).values(getExpression)[0].toString();
    const exp = expression.replace("value", "1");
    const series: XYChartSeries[] = [];

    categories.forEach((name: any) => groupedLines.set(name, []));

    for (let i = 0; i < rows; i++) {
      const name = ds.data[i][0];
      for (let j = 1; j < cols; j++) {
        const cat = categories[j - 1];
        const yValue = Numeral(+ds.data[i][j] * eval(exp)).format(this.pattern());
        groupedLines.get(cat)?.push({
          x: eval(getcolumn1Expression.replace("value", '"' + name + '"')),
          y: yValue,
        });
      }
    }
    groupedLines.forEach((lines, name) => series.push({ name: name, data: lines }));
    return series;
  }

  seriesLines(series: XYChartSeries): XYChartDataLine[] {
    return series.data.map((d) => {
      return { name: series.name, x: d.x, y: d.y, yVal: Numeral(d.y).value() };
    });
  }

  scatterLines(series: XYChartSeries): XYChartDataLine[] {
    return series.data.map((d) => {
      return { name: d.x, x: series.name, y: d.y, yVal: Numeral(d.y).value() };
    });
  }
}
