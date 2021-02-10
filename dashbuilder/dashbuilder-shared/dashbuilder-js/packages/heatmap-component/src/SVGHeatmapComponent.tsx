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
import { useEffect, useState } from "react";

import { SvgHeatmap, SvgNodeValue } from "@dashbuilder-js/heatmap-base";
import { ColumnType, DataSet } from "@dashbuilder-js/component-api";
import { ComponentController } from "@dashbuilder-js/component-api/dist/controller/ComponentController";

const SVG_CONTENT_PARAM = "svgContent";
const SVG_URL_PARAM = "svgUrl";

const NOT_ENOUGH_COLUMNS_MSG = "Heatmap expects 2 columns: Node ID (TEXT or Label) and value (NUMBER)";
const INVALID_COLUMNS_TYPE_MSG = "Wrong columns type. First column should be TEXT or LABEL and second column NUMBER.";
const MISSING_PARAM_MSG = "You must provide either a SVG URL or the SVG Content.";

const notEmpty = (param?: string) => param !== undefined && param.trim() !== "";

const validateDataSet = (ds: DataSet): string | undefined => {
  if (ds.columns.length < 2) {
    return NOT_ENOUGH_COLUMNS_MSG;
  }
  if (
    (ds.columns[0].type !== ColumnType.TEXT && ds.columns[0].type !== ColumnType.LABEL) ||
    ds.columns[1].type !== ColumnType.NUMBER
  ) {
    return INVALID_COLUMNS_TYPE_MSG;
  }
};

const validateParams = (params: Map<string, string>) => {
  const svgContent = params.get(SVG_CONTENT_PARAM);
  const svgUrl = params.get(SVG_URL_PARAM) as string;
  if (!(svgContent || svgUrl)) {
    return MISSING_PARAM_MSG;
  }
};
const extractNodeInfo = (dataset: string[][]): SvgNodeValue[] =>
  dataset.map(row => ({
    nodeId: row[0],
    value: +row[1]
  }));

interface AppState {
  svgContent?: string;
  data: SvgNodeValue[];
  errorMessage?: string;
}

interface Props {
  controller: ComponentController;
}

export function SVGHeatmapComponent(props: Props) {
  const [appState, setAppState] = useState<AppState>({ data: [] });

  const onDataset = (ds: DataSet, params: Map<string, any>) => {
    const validationMessage = validateDataSet(ds) || validateParams(params);
    if (validationMessage) {
      props.controller.requireConfigurationFix(validationMessage);
      setAppState(previousState => ({
        ...previousState,
        errorMessage: validationMessage
      }));
      return;
    }
    props.controller.configurationOk();

    const userSvgContent = params.get(SVG_CONTENT_PARAM);
    const svgUrl = params.get(SVG_URL_PARAM);

    if (notEmpty(userSvgContent)) {
      setAppState(previousState => ({
        ...previousState,
        data: extractNodeInfo(ds.data),
        svgContent: userSvgContent
      }));
    } else {
      fetch(svgUrl)
        .then(r => r.text())
        .then(urlSvgContent =>
          setAppState(previousState => ({
            ...previousState,
            data: extractNodeInfo(ds.data),
            svgContent: urlSvgContent
          }))
        )
        .catch(e =>
          setAppState(previousState => ({
            ...previousState,
            data: [],
            svgContent: undefined,
            errorMessage: e
          }))
        );
    }
  };

  useEffect(() => props.controller.setOnDataSet(onDataset), [appState.data]);

  return appState?.errorMessage ? (
    <em>{appState.errorMessage}</em>
  ) : (
    <SvgHeatmap svgContent={appState.svgContent!} svgNodesValues={appState.data} />
  );
}
