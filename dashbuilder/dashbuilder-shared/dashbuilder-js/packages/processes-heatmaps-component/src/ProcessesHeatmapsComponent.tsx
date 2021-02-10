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

import { ColumnType, DataSet } from "@dashbuilder-js/component-api";
import { ComponentController } from "@dashbuilder-js/component-api/dist/controller/ComponentController";
import { SvgNodeValue, SvgHeatmap } from "@dashbuilder-js/heatmap-base";
import { ProcessSelector } from "./ProcessSelector";

const NOT_ENOUGH_COLUMNS_MSG =
  "All Processes Heatmaps expects 4 columns: Container Id (or External Id), Process Id, Node Id ,Value (NUMBER).";
const INVALID_TEXT_COLUMN = "Wrong type for column {0}, it should be either LABEL or TEXT.";
const VALUE_COLUMN_INVALID_MSG = "Wrong type for node value column, it should be NUMBER.";
const NO_DATA_MESSAGE = "Dataset is empty. Please provide data with container id, process id, node id and value.";
enum Params {
  SERVER_TEMPLATE = "serverTemplate",
  SHOW_STATUS = "showStatus",
  SHOW_PROCESS_SELECTOR = "showProcessSelector"
}
enum AppStateType {
  ERROR = "Error",
  INIT = "Initializing",
  WAITING_DATA = "Waiting Data",
  LOADING_SVG = "Loading SVG",
  LOADED_SVG = "Loaded SVG",
  FINISHED = "Finished loading"
}

interface NodeData {
  nodeid: string;
  value: number;
}

interface ProcessData {
  processId: string;
  nodeValues: NodeData[];
}

interface ContainerData {
  containerId: string;
  processData: ProcessData[];
}

interface AppState {
  state: AppStateType;
  nodesValues: SvgNodeValue[];
  serverTemplate?: string;
  processSvg?: string;
  containerData: ContainerData[];
  message?: string;
  selectedContainer?: string;
  selectedProcess?: string;
  showStatus?: boolean;
}

const isEmpty = (param?: string) => param === undefined || param.trim() === "";

const validateParams = (params: Map<string, any>): string | undefined => {
  if (isEmpty(params.get(Params.SERVER_TEMPLATE))) {
    return "Server template is required. (Component Properties)";
  }
};

const validateDataSet = (ds: DataSet): string | undefined => {
  if (ds.columns.length < 4) {
    return NOT_ENOUGH_COLUMNS_MSG;
  }

  for (let i = 0; i < ds.columns.length; i++) {
    const column = ds.columns[i];
    const columnType = column.type;
    if (i < 3 && columnType !== ColumnType.LABEL && columnType !== ColumnType.TEXT) {
      return INVALID_TEXT_COLUMN.replace("{0}", column.name);
    }
    if (i === 3 && columnType !== ColumnType.NUMBER) {
      return VALUE_COLUMN_INVALID_MSG;
    }
  }
};

interface Props {
  controller: ComponentController;
}

export function ProcessesHeatmapsComponent(props: Props) {
  const [appState, setAppState] = useState<AppState>({
    state: AppStateType.INIT,
    nodesValues: [],
    containerData: [],
    showStatus: false
  });

  const onDataset = useCallback(
    (ds: DataSet, params: Map<string, any>) => {
      const validation = validateParams(params) || validateDataSet(ds);
      if (validation) {
        setAppState(previousState => ({
          ...previousState,
          state: AppStateType.ERROR,
          message: validation
        }));
        props.controller.requireConfigurationFix(validation);
        return;
      }
      if (ds.data.length === 0) {
        setAppState(previousState => ({
          ...previousState,
          state: AppStateType.ERROR,
          message: NO_DATA_MESSAGE
        }));
        props.controller.requireConfigurationFix(NO_DATA_MESSAGE);
        return;
      }

      props.controller.configurationOk();

      const allContainerData: ContainerData[] = [];
      ds.data.map(d => {
        const cid = d[0];
        const pid = d[1];
        const nid = d[2];
        const nodeValue = +d[3];

        let containerData = allContainerData.filter(c => c.containerId === cid)[0];
        if (!containerData) {
          containerData = { containerId: cid, processData: [] };
          allContainerData.push(containerData);
        }
        const processData = containerData.processData.filter(p => p.processId === pid)[0];
        if (processData) {
          processData.nodeValues.push({ nodeid: nid, value: nodeValue });
        } else {
          containerData.processData.push({ processId: pid, nodeValues: [{ nodeid: nid, value: nodeValue }] });
        }
      });

      setAppState(previousState => ({
        ...previousState,
        nodesValues: ds.data.map((d: string[]) => {
          return { nodeId: d[2], value: +d[3] };
        }),
        state: AppStateType.LOADING_SVG,
        containerData: allContainerData,
        serverTemplate: params.get(Params.SERVER_TEMPLATE),
        showStatus: params.get(Params.SHOW_STATUS) === "true",
        selectedContainer: appState.selectedContainer || allContainerData[0].containerId,
        selectedProcess: appState.selectedProcess || allContainerData[0].processData[0].processId
      }));
    },
    [appState]
  );

  const onProcessSelected = useCallback(
    (containerId: string, processId: string) => {
      if (
        !appState.serverTemplate ||
        (containerId === appState.selectedContainer && processId === appState.selectedProcess)
      ) {
        return;
      }
      setAppState(previousState => ({
        ...previousState,
        state: AppStateType.LOADING_SVG,
        selectedContainer: containerId,
        selectedProcess: processId
      }));
    },
    [appState.serverTemplate, appState.selectedContainer, appState.selectedProcess]
  );

  useEffect(() => props.controller.setOnDataSet(onDataset), [appState]);

  useEffect(() => {
    if (appState.serverTemplate && appState.selectedContainer && appState.selectedProcess) {
      const params = new Map<string, string>();
      params.set(Params.SERVER_TEMPLATE, appState.serverTemplate!);
      params.set("containerId", appState.selectedContainer!);
      params.set("processId", appState.selectedProcess!);
      props.controller
        .callFunction({
          functionName: "ProcessSVGFunction",
          parameters: params
        })
        .then((result: any) =>
          setAppState(previousState => ({ ...previousState, state: AppStateType.LOADED_SVG, processSvg: result }))
        )
        .catch((errorMsg: string) =>
          setAppState(previousState => ({
            ...previousState,
            state: AppStateType.ERROR,
            processSvg: undefined,
            message: `Error loading SVG for process "${appState.selectedProcess}" from container "${appState.selectedContainer}". 
            Please make sure the process SVG exists. Error: ${errorMsg}`
          }))
        );
    }
  }, [appState.serverTemplate, appState.selectedContainer, appState.selectedProcess]);

  return (
    <div className="allProcessHeatmapsComponent">
      {appState.state !== AppStateType.ERROR && appState.processSvg && (
        <SvgHeatmap svgNodesValues={appState.nodesValues} svgContent={appState.processSvg!} />
      )}
      {appState.containerData.length > 0 && (
        <ProcessSelector
          containers={appState.containerData.map(c => {
            return {
              id: c.containerId,
              processes: c.processData.map(p => p.processId)
            };
          })}
          onContainerProcessSelected={onProcessSelected}
          selectedContainer={appState.selectedContainer}
          selectedProcess={appState.selectedProcess}
        />
      )}
      {appState.state === AppStateType.ERROR && <p className="errorMessage">{appState.message}</p>}
      {appState.showStatus && (
        <div className="statusContainer">
          <em className="statusLabel">
            {appState.state} {appState.message ? `: ${appState.message}` : ""}
          </em>
        </div>
      )}
    </div>
  );
}
