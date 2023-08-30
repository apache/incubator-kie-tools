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
import { BaseChart, PieChartSerie } from "./BaseChart";

export type PieLegendPositionType = "bottom" | "right";

export abstract class PieBaseChart extends BaseChart {
  categories(): string[] {
    const data = this.props.dataSet.data;
    const categories: string[] = [];
    for (let i = 0; i < data.length; i++) {
      categories.push(data[i][0]);
    }
    return categories;
  }

  dataSetToPieChart(): PieChartSerie[] {
    const ds = this.props.dataSet;
    const rows = this.props.dataSet.data.length;
    const series: PieChartSerie[] = [];

    for (let i = 0; i < rows; i++) {
      series.push({
        name: ds.data[i][0],
        y: +ds.data[i][1],
      });
    }
    return series;
  }

  pieLegendPosition(): PieLegendPositionType {
    const currentPos = this.props.legendPosition;
    if (currentPos === "bottom" || currentPos) {
      return currentPos as PieLegendPositionType;
    }

    return "right";
  }
}
