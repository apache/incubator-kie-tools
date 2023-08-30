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
import { ComponentController, DataSet, MessageProperty } from "@kie-tools/dashbuilder-component-api";
import { useState, useEffect, useCallback, useRef } from "react";
import { ECharts, Props as EChartsProps } from "@kie-tools/dashbuilder-component-echarts-base";

interface Props {
  controller: ComponentController;
}

export function EChartsComponent(props: Props) {
  const [echartsProps, setEchartsProps] = useState<EChartsProps>();
  useEffect(() => {
    props.controller.setOnDataSet((_dataset: DataSet, params: Map<string, any>) => {
      const option: any = {
        dataset: {
          source: [_dataset.columns.map((c) => c.settings.columnName), ..._dataset.data],
        },
      };
      setEchartsProps({ option: option, params: params, theme: params.get(MessageProperty.MODE) });
    });
  }, [props.controller]);

  return (
    <>
      <ECharts {...echartsProps} />
    </>
  );
}
