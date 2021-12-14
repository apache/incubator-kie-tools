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
import { useCallback, useEffect, useMemo } from "react";
import { useDmnRunnerState, useDmnRunnerDispatch, InputRow } from "./DmnRunnerContext";
import { DmnRunnerMode } from "./DmnRunnerStatus";
import { DmnAutoTable } from "@kogito-tooling/unitables";
import { DecisionResult } from "@kogito-tooling/form/dist/dmn";
import { PanelId } from "../EditorPageDockDrawer";
import { useElementsThatStopKeyboardEventsPropagation } from "@kie-tooling-core/keyboard-shortcuts/dist/channel";

interface Props {
  isReady?: boolean;
  setPanelOpen: React.Dispatch<React.SetStateAction<PanelId>>;
  dmnRunnerResults: Array<DecisionResult[] | undefined>;
  setDmnRunnerResults: React.Dispatch<React.SetStateAction<Array<DecisionResult[] | undefined>>>;
}

export function DmnRunnerTabular(props: Props) {
  const dmnRunnerState = useDmnRunnerState();
  const dmnRunnerDispatch = useDmnRunnerDispatch();

  const updateDmnRunnerResults = useCallback(
    async (inputRows: Array<InputRow>) => {
      if (!props.isReady) {
        return;
      }

      const payloads = await Promise.all(inputRows.map((data) => dmnRunnerDispatch.preparePayload(data)));

      try {
        const results = await Promise.all(
          payloads.map((payload) => {
            if (payload === undefined) {
              return;
            }
            return dmnRunnerState.service.result(payload);
          })
        );

        const runnerResults: Array<DecisionResult[] | undefined> = [];
        for (const result of results) {
          if (Object.hasOwnProperty.call(result, "details") && Object.hasOwnProperty.call(result, "stack")) {
            dmnRunnerDispatch.setError(true);
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
    [props.isReady, dmnRunnerDispatch, dmnRunnerState.service]
  );

  useEffect(() => {
    updateDmnRunnerResults(dmnRunnerState.inputRows);
  }, [dmnRunnerState.inputRows]);

  const openRow = useCallback(
    (rowIndex: number) => {
      dmnRunnerDispatch.setMode(DmnRunnerMode.FORM);
      dmnRunnerDispatch.setCurrentInputRowIndex(rowIndex);
      props.setPanelOpen(PanelId.NONE);
    },
    [dmnRunnerDispatch]
  );

  useElementsThatStopKeyboardEventsPropagation(
    window,
    useMemo(() => [".unitables--dmn-runner-drawer"], [])
  );

  return (
    <div style={{ height: "100%" }}>
      {dmnRunnerState.jsonSchema && (
        <DmnAutoTable
          jsonSchema={dmnRunnerState.jsonSchema}
          inputRows={dmnRunnerState.inputRows}
          setInputRows={dmnRunnerDispatch.updateInputRows}
          results={props.dmnRunnerResults}
          error={dmnRunnerState.error}
          setError={dmnRunnerDispatch.setError}
          openRow={openRow}
        />
      )}
    </div>
  );
}
