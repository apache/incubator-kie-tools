/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { ComponentController, DataSet } from "@kie-tools/dashbuilder-component-api";
import { useState, useEffect, useCallback, useRef } from "react";
import { EChartsType, init } from "echarts";

interface Props {
  controller: ComponentController;
}

export function EChartsComponent(props: Props) {
  const container = useRef<HTMLDivElement>(null);
  const [chart, setChart] = useState<EChartsType | undefined>();

  useEffect(() => {
    props.controller.setOnInit((params: Map<string, any>) => {});

    props.controller.setOnDataSet((_dataset: DataSet, params: Map<string, any>) => {
      const options = fillProperties(params);
      options.dataset = { source: [_dataset.columns.map((c) => c.settings.columnName), ..._dataset.data] };
      // LIMITATION: have to set all series for the same type because the parameters is in a map
      if (options.series && _dataset.columns.length > 1) {
        const series = Array(_dataset.columns.length - 1).fill(options.series);
        options.series = series;
      }
      chart?.setOption(options, false);
    });
  }, [props.controller, chart]);

  useEffect(() => {
    if (container.current) {
      const chart = init(container.current);
      chart.setOption({
        tooltip: {},
        xAxis: { type: "category" },
        yAxis: {},
        series: [],
      });
      setChart(chart);
    }
  }, []);

  window.onresize = useCallback(() => {
    if (chart) chart.resize();
  }, [chart]);
  return (
    <>
      <div style={{ width: "100%", height: "100%" }} ref={container}></div>
    </>
  );
}

const fillProperties = (props: Map<string, any>): any => {
  let option = {};
  const optionStr = props.get("option");
  if (optionStr) {
    try {
      option = JSON.parse(optionStr);
    } catch (e) {
      console.log("Not able to parse option property");
    }
    props.delete("option");
  }
  props.forEach((value, key) => {
    if (key !== "dataSet") {
      setPropertyOnObject(key, value, option);
    }
  });
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
