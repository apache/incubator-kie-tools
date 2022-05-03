import * as React from "react";

import { ChartGroup, ChartLine } from "@patternfly/react-charts";
import { XYChart } from "./XYChart";

export class LineChart extends XYChart {
  buildChartGroup(): any {
    return (
      <ChartGroup>
        {this.dataSetToXYData()
          .map((line) => this.seriesLines(line))
          .map((lineData, i) => (
            <ChartLine key={i} data={lineData} y={(d) => d.yVal} />
          ))}
      </ChartGroup>
    );
  }
}
