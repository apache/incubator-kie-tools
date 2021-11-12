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
import { useCallback, useEffect } from "react";
import { useDmnRunner, useDmnRunnerCallbacks } from "./DmnRunnerContext";
import { DmnRunnerMode } from "./DmnRunnerStatus";
import { DmnAutoTable } from "@kogito-tooling/unitables";
import { DecisionResult } from "@kogito-tooling/form/dist/dmn";
import { PanelId } from "../EditorPageDockDrawer";

interface Props {
  isReady?: boolean;
  setPanelOpen: React.Dispatch<React.SetStateAction<PanelId>>;
  dmnRunnerResults: Array<DecisionResult[] | undefined>;
  setDmnRunnerResults: React.Dispatch<React.SetStateAction<Array<DecisionResult[] | undefined>>>;
}

export function DmnRunnerTabular(props: Props) {
  const dmnRunner = useDmnRunner();
  const dmnRunnerCallbacks = useDmnRunnerCallbacks();

  const updateDmnRunnerResults = useCallback(
    async (tableData: any[]) => {
      if (!props.isReady) {
        return;
      }

      const payloads = await Promise.all(tableData.map((data) => dmnRunnerCallbacks.preparePayload(data)));

      try {
        const results = await Promise.all(
          payloads.map((payload) => {
            if (payload === undefined) {
              return;
            }
            return dmnRunner.service.result(payload);
          })
        );

        const runnerResults: Array<DecisionResult[] | undefined> = [];
        for (const result of results) {
          if (Object.hasOwnProperty.call(result, "details") && Object.hasOwnProperty.call(result, "stack")) {
            dmnRunnerCallbacks.setError(true);
            break;
          }
          if (result) {
            runnerResults.push(result.decisionResults);
          }
        }
        props.setDmnRunnerResults(runnerResults);
      } catch (err) {
        return undefined;
      }
    },
    [props.isReady, dmnRunnerCallbacks, dmnRunner.service]
  );

  useEffect(() => {
    updateDmnRunnerResults(dmnRunner.data);
  }, [dmnRunner.data]);

  const openRow = useCallback(
    (rowIndex: number) => {
      dmnRunnerCallbacks.setMode(DmnRunnerMode.DRAWER);
      dmnRunnerCallbacks.setDataIndex(rowIndex);
      props.setPanelOpen(PanelId.NONE);
    },
    [dmnRunnerCallbacks]
  );

  return (
    <div style={{ height: "100%" }}>
      <DmnAutoTable
        schema={dmnRunner.schema as any}
        data={dmnRunner.data}
        setData={dmnRunnerCallbacks.setData}
        results={props.dmnRunnerResults}
        error={dmnRunner.error}
        setError={dmnRunnerCallbacks.setError}
        openRow={openRow}
      />
    </div>
  );
}
