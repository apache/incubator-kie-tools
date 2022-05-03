import * as React from "react";

import { ChartArea, ChartGroup } from "@patternfly/react-charts";
import "@patternfly/patternfly/patternfly-charts.css"; // Required for mix-blend-mode CSS property
import { XYChart } from "./XYChart";

export class AreaChart extends XYChart {
  buildChartGroup(): any {
    return (
      <ChartGroup>
        {this.dataSetToXYData()
          .map((line) => this.seriesLines(line))
          .map((lineData, i) => (
            <ChartArea key={i} data={lineData} interpolation="monotoneX" y={(d) => d.yVal} />
          ))}
      </ChartGroup>
    );
  }
}
