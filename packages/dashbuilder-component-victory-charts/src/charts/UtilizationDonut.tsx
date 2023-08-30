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
