import * as React from "react";

import { ChartBar, ChartGroup } from "@patternfly/react-charts";
import { XYChart } from "./XYChart";

export class BarChart extends XYChart {
  buildChartGroup(): any {
    return (
      <ChartGroup offset={10} horizontal={this.props.horizontalBars}>
        {this.dataSetToXYData()
          .map((line) => {
            return this.seriesLines(line);
          })
          .map((lineData, i) => {
            return <ChartBar key={i} data={lineData} y={(d) => d.yVal} />;
          })}
      </ChartGroup>
    );
  }
}
