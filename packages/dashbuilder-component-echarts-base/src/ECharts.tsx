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
import { useState, useEffect, useCallback, useRef } from "react";
import { EChartOption, EChartsType, init } from "echarts";

const OPTION_PARAM = "option";
const DATASET_PARAM = "dataSet";
const INIT_OPTIONS: EChartOption = {
  tooltip: {},
  xAxis: { type: "category" },
  yAxis: {},
  series: [],
};

export interface Props {
  option?: any;
  params?: Map<string, any>;
  theme?: string;
  refresh?: boolean;
}

type EChartsTypeWithTheme = EChartsType & { theme?: string };

export function ECharts(props: Props) {
  const container = useRef<HTMLDivElement>(null);
  const [chart, setChart] = useState<EChartsTypeWithTheme | undefined>();

  useEffect(() => {
    if (container.current && !chart) {
      const _chart = init(container.current, props.theme) as EChartsTypeWithTheme;
      _chart.setOption(INIT_OPTIONS);
      _chart.theme = props.theme;
      setChart(_chart);
    }
  }, [chart, props]);

  window.onresize = useCallback(() => {
    if (chart) chart.resize();
  }, [chart]);

  useEffect(() => {
    if (!chart) {
      return;
    }
    if (chart.theme != props.theme) {
      chart.dispose();
      setChart(undefined);
    } else {
      console.log(props);
      let option = props.option || {};
      if (props.params) {
        props.params.delete(DATASET_PARAM);
        option = fillProperties(props.params, option);
      }
      // replicate first series configuration if a single serie configuration object is provided
      const nColumns = option.dataset?.source[0]?.length || 0;
      if (option.series && !option.series.length && nColumns > 1) {
        const series = Array(nColumns - 1).fill(option.series);
        option.series = series;
      }
      chart.setOption(option);
    }
  }, [props, chart]);

  return (
    <>
      <div style={{ width: "100%", height: "100%" }} ref={container}></div>
    </>
  );
}

export const fillProperties = (props: Map<string, any>, option?: any): any => {
  if (!option) {
    option = {};
  }
  const optionStr = props.get(OPTION_PARAM);
  if (optionStr) {
    try {
      const parsedOption: Object = JSON.parse(optionStr);
      option = { ...option, ...parsedOption };
    } catch (e) {
      console.log("Not able to parse option property");
    }
    props.delete(OPTION_PARAM);
  }
  props.forEach((value, key) => setPropertyOnObject(key, value, option));
  return option;
};

const setPropertyOnObject = (prop: string, value: any, obj: any) => {
  if (!prop || !value) {
    return obj;
  }
  const props = prop.split(".");
  let parent = obj;
  for (let i = 0; i < props.length; i++) {
    const name = props[i];
    if (i === props.length - 1) {
      parent[name] = value;
    } else {
      parent[name] = parent[name] || {};
      parent = parent[name];
    }
  }
  return obj;
};
