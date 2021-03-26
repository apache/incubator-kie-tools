/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { Options, SingleSeries } from "./Data";
import Chart from "react-apexcharts";

export type ChartType = "line" | "area";

export interface ChartProps {
  type?: ChartType;
  options: Options;
  series: SingleSeries[];
}

export function LineChart(props: ChartProps) {
  return <Chart type={props.type || "line"} options={props.options} series={props.series} />;
}
