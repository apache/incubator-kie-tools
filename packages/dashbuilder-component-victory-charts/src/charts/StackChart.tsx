import * as React from "react";

import { ChartBar, ChartStack, ChartTooltip } from "@patternfly/react-charts";
import "@patternfly/patternfly/patternfly-charts.css"; // Required for mix-blend-mode CSS property
import { XYChart } from "./XYChart";

export class StackChart extends XYChart {
  buildChartGroup(): any {
    return (
      <ChartStack>
        {this.dataSetToXYData()
          .map((line) => this.seriesLines(line))
          .map((lineData, i) => (
            <ChartBar
              key={i}
              data={lineData}
              labelComponent={<ChartTooltip constrainToVisibleArea />}
              y={(d) => d.yVal}
            />
          ))}
      </ChartStack>
    );
  }
}
