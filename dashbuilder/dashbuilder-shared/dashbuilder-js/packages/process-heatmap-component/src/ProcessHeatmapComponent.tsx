/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { useEffect, useState, useCallback } from "react";
import { ColumnType, DataSet, FunctionCallRequest } from "@dashbuilder-js/component-api";
import { ComponentController } from "@dashbuilder-js/component-api/dist/controller/ComponentController";
import { SvgNodeValue, SvgHeatmap } from "@dashbuilder-js/heatmap-base";

const NOT_ENOUGH_COLUMNS_MSG = "Process Heatmap expects 2 columns: Node Id(LABEL or TEXT),Value (NUMBER).";
const FIRST_COLUMN_INVALID_MSG = "Wrong type for first column, it should be either LABEL or TEXT.";
const SECOND_COLUMN_INVALID_MSG = "Wrong type for second column, it should be NUMBER.";

enum Params {
  SERVER_TEMPLATE = "serverTemplate",
  CONTAINER_ID = "containerId",
  PROCESS_ID = "processId"
}

enum AppStateType {
  ERROR = "Error",
  INIT = "Initializing",
  LOADING_SVG = "Loading SVG",
  LOADED_SVG = "Loaded SVG",
  FINISHED = "Finished loading"
}

interface AppState {
  state: AppStateType;
  processesNodesValues: SvgNodeValue[];
  svgRequest?: FunctionCallRequest;
  processSVG?: string;
  configurationIssue: string;
  message?: string;
}

const isEmpty = (param?: string) => param === undefined || param.trim() === "";

const validateParams = (params: Map<string, any>): string | undefined => {
  if (isEmpty(params.get(Params.SERVER_TEMPLATE))) {
    return "Server template is required.";
  }
  if (isEmpty(params.get(Params.CONTAINER_ID))) {
    return "Container ID is required.";
  }
  if (isEmpty(params.get(Params.PROCESS_ID))) {
    return "Process ID is required.";
  }
};

const validateDataSet = (ds: DataSet): string | undefined => {
  if (ds.columns.length < 2) {
    return NOT_ENOUGH_COLUMNS_MSG;
  }
  if (ds.columns[0].type !== ColumnType.LABEL && ds.columns[0].type !== ColumnType.TEXT) {
    return FIRST_COLUMN_INVALID_MSG;
  }
  if (ds.columns[1].type !== ColumnType.NUMBER) {
    return SECOND_COLUMN_INVALID_MSG;
  }
};

interface Props {
  controller: ComponentController;
}

export function ProcessHeatmapComponent(props: Props) {
  const [appState, setAppState] = useState<AppState>({
    state: AppStateType.INIT,
    processesNodesValues: [],
    configurationIssue: ""
  });

  const onInit = useCallback(
    (params: Map<string, string>) => {
      const validationMessage = validateParams(params);
      if (validationMessage) {
        setAppState(previousAppState => ({
          ...previousAppState,
          state: AppStateType.ERROR,
          message: validationMessage,
          configurationIssue: validationMessage
        }));
      } else {
        setAppState(previousAppState => ({
          ...previousAppState,
          state: AppStateType.LOADING_SVG,
          svgRequest: {
            functionName: "ProcessSVGFunction",
            parameters: params
          },
          configurationIssue: ""
        }));
      }
    },
    [appState]
  );

  const onDataset = useCallback((ds: DataSet, params: Map<string, any>) => {
    const validationMessage = validateParams(params) || validateDataSet(ds);
    if (validationMessage) {
      setAppState(previousAppState => ({
        ...previousAppState,
        state: AppStateType.ERROR,
        message: validationMessage,
        configurationIssue: validationMessage
      }));
    } else {
      setAppState(previousAppState => ({
        ...previousAppState,
        processesNodesValues: ds.data.map(d => ({ nodeId: d[0], value: +d[1] })),
        state: AppStateType.FINISHED,
        configurationIssue: ""
      }));
    }
  }, []);

  useEffect(() => {
    props.controller.setOnInit(onInit);
    props.controller.setOnDataSet(onDataset);
  }, [appState]);

  useEffect(() => {
    if (appState.configurationIssue) {
      props.controller.requireConfigurationFix(appState.configurationIssue);
    } else {
      props.controller.configurationOk();
    }
  }, [appState.configurationIssue]);

  useEffect(() => {
    if (appState.svgRequest) {
      props.controller
        .callFunction(appState.svgRequest!)
        .then((result: any) =>
          setAppState(previousAppState => ({ ...previousAppState, state: AppStateType.LOADED_SVG, processSVG: result }))
        )
        .catch((errorMsg: string) =>
          setAppState(previousAppState => ({
            ...previousAppState,
            state: AppStateType.ERROR,
            message: `There was an error retrieving process SVG: ${errorMsg}`
          }))
        );
    }
  }, [appState.svgRequest]);

  return (
    <div style={{ width: "100%", height: "100%" }}>
      {(() => {
        switch (appState.state) {
          case AppStateType.ERROR:
            return <em style={{ color: "red" }}>{appState.message}</em>;
          case AppStateType.LOADED_SVG:
          case AppStateType.FINISHED:
            return <SvgHeatmap svgContent={appState.processSVG!} svgNodesValues={appState.processesNodesValues} />;
          default:
            return <em>Status: {appState.state}</em>;
        }
      })()}
    </div>
  );
}
