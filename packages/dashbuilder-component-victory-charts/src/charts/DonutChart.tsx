import * as React from "react";

import { ChartDonut } from "@patternfly/react-charts";
import { PieBaseChart } from "./PieBaseChart";

export class DonutChart extends PieBaseChart {
  render() {
    const { width, height, themeColor, themeVariant } = this.props;
    return (
      <ChartDonut
        ariaDesc={this.props.ariaDescription}
        ariaTitle={this.props.ariaTitle}
        constrainToVisibleArea={true}
        data={this.dataSetToPieChart()}
        labels={({ datum }) => `${datum.name}: ${datum.y}`}
        legendData={this.legendData}
        legendOrientation={this.legendOrientation}
        legendPosition={this.pieLegendPosition()}
        padding={this.props.padding}
        animate={this.animationProp}
        subTitle={this.props.donutSubTitle || ""}
        title={this.props.donutTitle || ""}
        themeColor={themeColor}
        themeVariant={themeVariant}
        width={width}
        height={height}
      />
    );
  }
}
