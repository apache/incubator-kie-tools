import * as React from "react";
import * as Numeral from "numeral";
import { BaseChart, XYChartSeries, XYChartDataLine, XYChartData } from "./BaseChart";
import { Chart, ChartAxis, ChartGroup } from "@patternfly/react-charts";

export type ChartGroupType = typeof ChartGroup;

export abstract class XYChart extends BaseChart {
  render() {
    const { width, height, themeColor, themeVariant } = this.props;
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
        themeVariant={themeVariant}
      >
        <ChartAxis showGrid={this.props.grid.y} fixLabelOverlap={this.props.fixLabelsOverlap} />
        <ChartAxis
          dependentAxis
          showGrid={this.props.grid.x}
          tickFormat={(t) => Numeral(t).format(this.pattern())}
          fixLabelOverlap={this.props.fixLabelsOverlap}
        />
        {this.buildChartGroup()}
      </Chart>
    );
  }

  abstract buildChartGroup(): ChartGroupType;

  categories() {
    return this.props.dataSet.columns.slice(1).map((column) => column.settings["columnName"]);
  }

  pattern() {
    const pattern = this.props.dataSet.columns.slice(1).map((column) => column.settings.valuePattern)[0];
    return pattern?.replace(/#/g, "0");
  }

  dataSetToXYData(): XYChartSeries[] {
    const groupedLines: Map<string, XYChartData[]> = new Map();
    const categories = this.categories();
    const ds = this.props.dataSet;
    const rows = ds.data.length;
    const cols = ds.columns.length;
    const getcolumnExpression = this.props.dataSet.columns.slice(0).map((column) => column.settings["valueExpression"]);
    const getcolumn1Expression = Object.values(getcolumnExpression)[0].toString();
    const getExpression = this.props.dataSet.columns.slice(1).map((column) => column.settings["valueExpression"]);
    const expression = Object.values(getExpression)[0].toString();
    const exp = expression.replace("value", "1");
    const series: XYChartSeries[] = [];

    categories.forEach((name) => groupedLines.set(name, []));

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
}
