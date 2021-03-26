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
import { LineChart, ChartProps } from "./LineChart";
import { options, series } from "./SampleData";
import { ComponentController, DataSet, ColumnType } from "@dashbuilder-js/component-api";
import { useState, useEffect, useCallback } from "react";
import { Options, SingleSeries } from "./Data";

// Default Values
export const NOT_ENOUGH_COLUMNS_MSG_NON_TRANSPOSED =
  "Time series component expects at least 2 columns: category(LABEL or TEXT or NUMBER or DATE) and one or more series(NUMBER).";
export const NOT_ENOUGH_COLUMNS_MSG_TRANSPOSED =
  "Time series component expects 3 columns: category(LABEL or TEXT or NUMBER or DATE), series(TEXT) and values(NUMBER).";
export const SECOND_COLUMN_INVALID_MSG_TRANSPOSED = "Wrong type for second column, it should be TEXT.";
export const THIRD_COLUMN_INVALID_MSG_TRANSPOSED = "Wrong type for third column, it should be NUMBER.";
export const CHARTNAME_VALIDATION = "Please remove all special characters and spaces in Chart Name";

export enum Params {
  TRANSPOSED = "transposed",
  CHARTNAME = "chartName",
  SHOWAREA = "showArea",
  XAXISTYPE = "xaxisType",
  DATALABELS = "dataLabels",
  ZOOMTYPE = "type",
  ZOOMENABLED = "enabled",
  ZOOMAUTOSCALEYAXIS = "autoScaleYaxis",
  TITLETEXT = "text",
  TITLEALIGN = "align",
  TOOLBARSHOW = "show",
  TOOLBARAUTOSELECTED = "autoSelected"
}

enum AppStateType {
  ERROR = "Error",
  INIT = "Initializing",
  LOADING_COMPONENT = "Loading Component",
  LOADED_COMPONENT = "Loaded Component",
  FINISHED = "Finished loading"
}

interface AppState {
  state: AppStateType;
  processesOptions: Options;
  processesSeries: SingleSeries[];
  configurationIssue: string;
  message?: string;
}

export const validateParams = (params: Map<string, string | number>): string | undefined => {
  if (!params.get(Params.TRANSPOSED)) {
    return "Transposed is required.";
  }
  if (!params.get(Params.CHARTNAME)) {
    return "Chart name is required.";
  } else {
    return validateChartName(params.get(Params.CHARTNAME));
  }
};

export const validateChartName = (chartName: string | number | undefined): string | undefined => {
  const format = /[ `!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]/;
  if (typeof chartName === "string") {
    return format.test(chartName) ? CHARTNAME_VALIDATION : "";
  }
};

const validateDataSet = (ds: DataSet, transposed: boolean): string | undefined => {
  return transposed ? validateTransposedDataset(ds) : validateNonTransposedDataset(ds);
};

export const validateNonTransposedDataset = (ds: DataSet): string | undefined => {
  if (ds.columns.length < 2) {
    return NOT_ENOUGH_COLUMNS_MSG_NON_TRANSPOSED;
  }
  for (let i = 1; i < ds.columns.length; i++) {
    if (ds.columns[i].type !== ColumnType.NUMBER) {
      return "Wrong type for column " + (i + 1) + ", it should be NUMBER";
    }
  }
};

export const validateTransposedDataset = (ds: DataSet): string | undefined => {
  if (ds.columns.length < 3) {
    return NOT_ENOUGH_COLUMNS_MSG_TRANSPOSED;
  }
  if (ds.columns[1].type !== ColumnType.TEXT && ds.columns[1].type !== ColumnType.LABEL) {
    return SECOND_COLUMN_INVALID_MSG_TRANSPOSED;
  }
  if (ds.columns[2].type !== ColumnType.NUMBER) {
    return THIRD_COLUMN_INVALID_MSG_TRANSPOSED;
  }
};

interface Props {
  controller: ComponentController;
}

function getSeries(dataset: DataSet, transposed: boolean): SingleSeries[] {
  return transposed ? getSeriesforTransposedDataset(dataset) : getSeriesforNonTransposedDataset(dataset);
}

export function getSeriesforNonTransposedDataset(dataset: DataSet): SingleSeries[] {
  const arrayseries: SingleSeries[] = [];
  for (let i = 1; i < dataset.columns.length; i++) {
    arrayseries.push({
      name: dataset.columns[i].name,
      data: dataset.data.map((d: Array<string | number>) => +d[i])
    });
  }
  return arrayseries;
}

export function getSeriesforTransposedDataset(dataset: DataSet): SingleSeries[] {
  const arrayseries: SingleSeries[] = [];
  const names: string[] = dataset.data.map((d: string[]) => d[1]);
  const datum: Array<string | number> = dataset.data.map((d: Array<string | number>) => d[2]);
  const newnames: string[] = [];
  for (const i of names) {
    if (newnames.indexOf(i) === -1) {
      newnames.push(i);
    }
  }
  for (const i of newnames) {
    const newdata: Array<number | string> = [];
    for (let j = 0; j < names.length; j++) {
      if (i === names[j]) {
        newdata.push(+datum[j]);
      }
    }
    arrayseries.push({ name: i, data: newdata });
  }
  return arrayseries;
}

export function getOptions(
  dataset: DataSet,
  transposed: boolean,
  chartName: string,
  type: string,
  enabled: boolean,
  autoScaleYaxis: boolean,
  text: string,
  align: string,
  show: boolean,
  autoSelected: string,
  xaxisType: string
): Options {
  const newoptions: Options = {
    chart: {
      id: chartName,
      zoom: { type, enabled, autoScaleYaxis },
      toolbar: { show, autoSelected }
    },
    title: { text, align },
    xaxis: { type: xaxisType, categories: dataset.data.map((d: Array<string | number>) => d[0]) },
    dataLabels: { enabled: false }
  };
  if (transposed) {
    newoptions.xaxis.categories = Array.from(new Set(newoptions.xaxis.categories));
  }
  return newoptions;
}

export function Chart(props: Props) {
  const [chartProps, setChartProps] = useState<ChartProps>({
    options,
    series
  });
  const [appState, setAppState] = useState<AppState>({
    state: AppStateType.INIT,
    processesOptions: {
      chart: {
        id: "",
        zoom: { type: "", enabled: false, autoScaleYaxis: false },
        toolbar: { show: false, autoSelected: "" }
      },
      xaxis: { type: "", categories: [] },
      dataLabels: { enabled: false },
      title: { text: "", align: "" }
    },
    processesSeries: [{ name: "", data: [] }],
    configurationIssue: ""
  });
  const onDataset = useCallback((ds: DataSet, params: Map<string, any>) => {
    const transposed = params.get(Params.TRANSPOSED) === "true";
    const validationMessage = validateParams(params) || validateDataSet(ds, transposed);
    if (validationMessage) {
      setAppState(previousAppState => ({
        ...previousAppState,
        state: AppStateType.ERROR,
        message: validationMessage,
        configurationIssue: validationMessage
      }));
    } else {
      const op = getOptions(
        ds,
        transposed,
        params.get(Params.CHARTNAME),
        params.get(Params.ZOOMTYPE),
        params.get(Params.ZOOMENABLED),
        params.get(Params.ZOOMAUTOSCALEYAXIS),
        params.get(Params.TITLETEXT),
        params.get(Params.TITLEALIGN),
        params.get(Params.TOOLBARSHOW),
        params.get(Params.TOOLBARAUTOSELECTED),
        params.get(Params.XAXISTYPE),
      );
      op.dataLabels.enabled = params.get(Params.DATALABELS) === "true";
      op.chart.zoom.enabled = params.get(Params.ZOOMENABLED) === "true";
      op.chart.zoom.autoScaleYaxis = params.get(Params.ZOOMAUTOSCALEYAXIS) === "true";
      op.chart.toolbar.show = params.get(Params.TOOLBARSHOW) === "true";
      setAppState(previousAppState => ({
        ...previousAppState,
        processesOptions: op,
        processesSeries: getSeries(ds, transposed),
        state: AppStateType.FINISHED,
        configurationIssue: ""
      }));
    }
  }, []);
  useEffect(() => {
    props.controller.setOnInit(componentProps => {
      setChartProps({
        type: componentProps.get(Params.SHOWAREA) === "true" ? "area" : "line",
        options: appState.processesOptions,
        series: appState.processesSeries
      });
    });
    props.controller.setOnDataSet(onDataset);
  }, [appState]);

  useEffect(() => {
    if (appState.configurationIssue) {
      props.controller.requireConfigurationFix(appState.configurationIssue);
    } else {
      props.controller.configurationOk();
    }
  }, [appState.configurationIssue]);
  return (
    <div style={{ width: "100%", height: "100%" }}>
      {(() => {
        switch (appState.state) {
          case AppStateType.ERROR:
            return <em style={{ color: "red" }}>{appState.message}</em>;
          case AppStateType.LOADED_COMPONENT:
          case AppStateType.FINISHED:
            return <LineChart {...chartProps} options={appState.processesOptions} series={appState.processesSeries} />;
          default:
            return <em>Status: {appState.state}</em>;
        }
      })()}
    </div>
  );
}
