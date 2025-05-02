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
import { ComponentController, DataSet } from "@kie-tools/dashbuilder-component-api";
import { useState, useEffect } from "react";
import { VictoryChart, VictoryChartProps } from "./VictoryChart";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { ChartType, ThemeColorType, ThemeVariantType } from "./charts/BaseChart";

interface Props {
  controller: ComponentController;
}

export function VictoryChartComponent(props: Props) {
  const [victoryChartProps, setVictoryChartProps] = useState<VictoryChartProps>({
    width: 0,
    height: 0,
    type: "bar",
    themeColor: "blue",
    gridX: false,
    onValidationError: (message: string) => props.controller.requireConfigurationFix(message),
    onValidationSuccess: () => props.controller.configurationOk(),
  });

  useEffect(() => {
    props.controller.setOnInit((params: Map<string, any>) => {
      const mode = params.get("mode");
      if (mode) {
        document.body.setAttribute("mode", mode.toLowerCase());
      }
      setVictoryChartProps((props: VictoryChartProps) => {
        return {
          ...props,
          width: +(params.get("width") || 0),
          height: +(params.get("height") || 0),
          type: (params.get("chartType") as ChartType) || "bar",
          themeColor: (params.get("themeColor") as ThemeColorType) || "multi",

          ariaTitle: params.get("title"),
          ariaDescription: params.get("description"),

          title: params.get("title"),
          description: params.get("description"),

          legendPosition: params.get("legendPosition") || "bottom",
          legendOrientation: params.get("legendOrientation") || "horizontal",

          paddingBottom: +(params.get("paddingBottom") || 50),
          paddingRight: +(params.get("paddingRight") || 0),
          paddingLeft: +(params.get("paddingLeft") || 90),
          paddingTop: +(params.get("paddingTop") || 0),

          zoom: params.get("zoom") === "true",
          gridX: params.get("gridx") === "true",
          gridY: params.get("gridy") === "true",
          fixLabelsOverlap: params.get("fixLabelsOverlap") === "true",

          animate: params.get("animate") === "true",
          animationDuration: +(params.get("animationDuration") || 200),
          animationEasing: params.get("animationEasing") || "linear",

          donutTitle: (params.get("donutTitle") as string) || "",
          donutSubTitle: (params.get("donutSubTitle") as string) || "",

          barWidth: +params.get("barWidth"),
          barOffset: +params.get("barOffset"),
          horizontalBars: params.get("horizontalBars") === "true",

          dataset: null,
        };
      });
    });

    props.controller.setOnDataSet((_dataset: DataSet) => {
      setVictoryChartProps((props: VictoryChartProps) => {
        return { ...props, dataSet: _dataset };
      });
    });
  }, [props.controller]);

  return (
    <>
      {victoryChartProps.dataSet ? (
        <VictoryChart {...victoryChartProps} />
      ) : (
        <Alert variant="warning" title="No Data" />
      )}
    </>
  );
}
