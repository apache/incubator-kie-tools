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
import { useEffect, useState } from "react";

import { SvgHeatmap, SvgNodeValue } from "./SvgHeatmap";
import { ColumnType, DataSet, ComponentController } from "@kie-tools/dashbuilder-component-api";

const SVG_PARAM = "svg";
const BLUR_PARAM = "blur";
const OPACITY_PARAM = "opacity";
const SIZE_PARAM = "size";
const CONTAINS_ID_PARAM = "containsId";

const NOT_ENOUGH_COLUMNS_MSG = "Heatmap expects 2 columns: Node ID (TEXT or Label) and value (NUMBER)";
const INVALID_COLUMNS_TYPE_MSG = "Wrong columns type. First column should be TEXT or LABEL and second column NUMBER.";
const MISSING_PARAM_MSG = "You must provide either a SVG URL or the SVG Content using the parameter 'svg'.";
const INVALID_SVG_PARAM = "SVG parameter is not valid. It should be either a URL or a SVG content";

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

const isUrl = (param: string) => {
  return param && (param.trim().startsWith("http") || param.trim().startsWith("file:"));
};

const isSvg = (param: string) => {
  return param && param.trim().startsWith("<svg");
};

const validateParams = (params: Map<string, string>) => {
  const svg = params.get(SVG_PARAM);
  if (!svg) {
    return MISSING_PARAM_MSG;
  }

  if (!(isUrl(svg) || isSvg(svg))) {
    return INVALID_SVG_PARAM;
  }
};
const extractNodeInfo = (dataset: string[][]): SvgNodeValue[] =>
  dataset.map((row) => ({
    nodeId: row[0],
    value: +row[1],
  }));

interface AppState {
  svgContent: string;
  svgNodesValues: SvgNodeValue[];
  containsId?: boolean;
  errorMessage?: string;
  blur?: number;
  sizeFactor?: number;
  opacity?: number;
}

interface Props {
  controller: ComponentController;
}

export function SVGHeatmapComponent(props: Props) {
  const [appState, setAppState] = useState<AppState>({ svgNodesValues: [], svgContent: "" });

  const onDataset = (ds: DataSet, params: Map<string, any>) => {
    const validationMessage = validateDataSet(ds) || validateParams(params);
    if (validationMessage) {
      props.controller.requireConfigurationFix(validationMessage);
      setAppState((previousState) => ({
        ...previousState,
        errorMessage: validationMessage,
      }));
      return;
    }
    props.controller.configurationOk();

    const htParams = {
      svg: params.get(SVG_PARAM),
      blur: +params.get(BLUR_PARAM) || undefined,
      opacity: +params.get(OPACITY_PARAM) || undefined,
      sizeFactor: +params.get(SIZE_PARAM) || 1.0,
      containsId: params.get(CONTAINS_ID_PARAM) === "true",
    };

    if (isSvg(htParams.svg)) {
      setAppState((previousState) => ({
        ...previousState,
        svgContent: htParams.svg,
        svgNodesValues: extractNodeInfo(ds.data),
        ...htParams,
      }));
    } else if (isUrl(htParams.svg)) {
      fetch(htParams.svg)
        .then((r) => r.text())
        .then((urlSvgContent) =>
          setAppState((previousState) => ({
            ...previousState,
            ...htParams,
            svgNodesValues: extractNodeInfo(ds.data),
            svgContent: urlSvgContent,
          }))
        )
        .catch((e) =>
          setAppState((previousState) => ({
            ...previousState,
            svgNodesValues: [],
            svgContent: "",
            errorMessage: e,
          }))
        );
    }
  };

  useEffect(() => props.controller.setOnDataSet(onDataset), [appState.svgNodesValues]);

  return <>{appState?.errorMessage ? <em>{appState.errorMessage}</em> : <SvgHeatmap {...appState} />};</>;
}
