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
import { Column, ComponentController, DataSet } from "@kie-tools/dashbuilder-component-api";
import { useState, useEffect, useCallback, useRef } from "react";
import { ECharts } from "@kie-tools/dashbuilder-component-echarts-base";

interface Props {
  controller: ComponentController;
}

const INIT_OPTION = {
  tooltip: {
    trigger: "axis",
    position: function (pt: any) {
      return [pt[0], "10%"];
    },
  },
  toolbox: {
    feature: {
      dataZoom: {},
      magicType: {
        type: ["line", "bar", "stack"],
      },
    },
  },
  title: {
    left: "center",
  },
  xAxis: {
    type: "time",
    boundaryGap: false,
  },
  yAxis: {
    type: "value",
    boundaryGap: [0, "100%"],
  },
  dataZoom: [],
  series: [],
};

const validate = (columns: Column[]): string | undefined => {
  if (columns.length < 3) {
    return "Data Set is invalid! You must provide at least 3 columns containing the series, timestamp and the value";
  }
};

const datasetToSeries = (dataset: DataSet): any[] => {
  const seriesMap = new Map();
  for (let i = 0; i < dataset.data.length; i++) {
    const row = dataset.data[i];

    const serieName = row[0];
    const data = seriesMap.get(serieName) || [];

    data.push([+row[1], +row[2]]);
    seriesMap.set(serieName, data);
  }

  const series: any[] = [];
  seriesMap.forEach((v, k) => {
    series.push({
      name: k,
      type: "line",
      smooth: false,
      symbol: "none",
      areaStyle: {},
      data: v,
    });
  });
  return series;
};

interface State {
  error?: string;
  option?: any;
  params?: Map<string, any>;
}

export function TimeSeriesComponent(props: Props) {
  const [state, setState] = useState<State>({});

  useEffect(() => {
    props.controller.setOnInit((params: Map<string, any>) => setState({ option: INIT_OPTION, params: params }));
    props.controller.setOnDataSet((_dataset: DataSet, params: Map<string, any>) => {
      const error = validate(_dataset.columns);
      setState({
        error: error,
        option: { series: datasetToSeries(_dataset) },
        params: params,
      });
    });
  }, [props.controller]);

  return <>{state.error ? <h3>{state.error}</h3> : <ECharts {...state} />}</>;
}
