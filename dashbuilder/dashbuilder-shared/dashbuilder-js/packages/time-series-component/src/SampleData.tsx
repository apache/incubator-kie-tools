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

import { Options } from "./Data";

export const options: Options = {
  chart: {
    id: "apexchart-example",
    zoom: {
      type: 'x',
      enabled: true,
      autoScaleYaxis: true
    },
    toolbar:{
      show: true,
      autoSelected: 'zoom'
    }
  },
  title:{
    text: "Increase of series with time",
    align : "left"
  },
  xaxis: {
    type: "category",
    categories: [1991, 1992, 1993, 1994, 1995, 1996, 1997, 1998, 1999]
  },
  dataLabels: { enabled: false }
};
export const series = [
  {
    name: "series-1",
    data: [30, 40, 35, 50, 49, 60, 70, 91, 125]
  }
];
