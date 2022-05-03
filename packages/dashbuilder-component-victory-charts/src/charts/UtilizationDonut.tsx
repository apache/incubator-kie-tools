import * as React from "react";
import { PieBaseChart } from "./PieBaseChart";
import { ChartDonutUtilization } from "@patternfly/react-charts";

export class UtilizationDonut extends PieBaseChart {
  render() {
    return (
      <ChartDonutUtilization
        ariaDesc={this.props.ariaDescription}
        ariaTitle={this.props.ariaTitle}
        constrainToVisibleArea={true}
        themeColor={this.props.themeColor}
        data={this.buildData()}
        labels={({ datum }) => (datum.x ? `${datum.x}: ${datum.y}%` : null)}
        subTitle={this.props.donutSubTitle || "Utilization"}
        title={this.props.donutTitle || "Total"}
        width={this.props.width}
        height={this.props.height}
        padding={this.props.padding}
        legendData={this.buildLegendData()}
        legendOrientation={this.legendOrientation}
        legendPosition={this.pieLegendPosition()}
        themeVariant={this.props.themeVariant}
      />
    );
  }

  buildLegendData() {
    const ds = this.props.dataSet;
    const legendName = `${ds.columns[0].name}: ${ds.data[0][0]}%`;
    return [
      {
        name: legendName,
      },
    ];
  }
  buildData() {
    const ds = this.props.dataSet;
    return {
      x: ds.columns[0].name,
      y: +ds.data[0][0],
    };
  }
}
