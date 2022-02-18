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
import { useCallback, useMemo } from "react";
import { InputRow, useDmnRunnerDispatch, useDmnRunnerState } from "./DmnRunnerContext";
import { DmnRunnerMode } from "./DmnRunnerStatus";
import { DmnAutoTable } from "@kie-tools/unitables";
import { DecisionResult } from "@kie-tools/form/dist/dmn";
import { PanelId } from "../EditorPageDockDrawer";
import { useElementsThatStopKeyboardEventsPropagation } from "@kie-tools-core/keyboard-shortcuts/dist/channel";
import { WorkspaceFile } from "../../workspace/WorkspacesContext";
import { DmnRunnerLoading } from "./DmnRunnerLoading";
import { Holder, useCancelableEffect } from "../../reactExt/Hooks";

interface Props {
  isReady?: boolean;
  setPanelOpen: React.Dispatch<React.SetStateAction<PanelId>>;
  dmnRunnerResults: Array<DecisionResult[] | undefined>;
  setDmnRunnerResults: React.Dispatch<React.SetStateAction<Array<DecisionResult[] | undefined>>>;
  workspaceFile: WorkspaceFile;
}

export function DmnRunnerTabular(props: Props) {
  const dmnRunnerState = useDmnRunnerState();
  const dmnRunnerDispatch = useDmnRunnerDispatch();

  const updateDmnRunnerResults = useCallback(
    async (inputRows: Array<InputRow>, canceled: Holder<boolean>) => {
      if (!props.isReady) {
        dmnRunnerDispatch.setDidUpdateOutputRows(true);
        return;
      }

      try {
        if (canceled.get()) {
          return;
        }
        const payloads = await Promise.all(inputRows.map((data) => dmnRunnerDispatch.preparePayload(data)));
        const results = await Promise.all(
          payloads.map((payload) => {
            if (payload === undefined) {
              return;
            }
            return dmnRunnerState.service.result(payload);
          })
        );
        if (canceled.get()) {
          return;
        }

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
        dmnRunnerDispatch.setDidUpdateOutputRows(true);
      } catch (err) {
        dmnRunnerDispatch.setDidUpdateOutputRows(true);
        return undefined;
      }
    },
    [props.isReady, dmnRunnerDispatch, dmnRunnerState.service]
  );

  // Debounce to avoid multiple updates caused by uniforms library
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        const timeout = setTimeout(() => {
          updateDmnRunnerResults(dmnRunnerState.inputRows, canceled);
        }, 100);
        return () => {
          clearTimeout(timeout);
        };
      },
      [dmnRunnerState.inputRows, updateDmnRunnerResults]
    )
  );

  const openRow = useCallback(
    (rowIndex: number) => {
      dmnRunnerDispatch.setMode(DmnRunnerMode.FORM);
      dmnRunnerDispatch.setCurrentInputRowIndex(rowIndex);
      props.setPanelOpen(PanelId.NONE);
    },
    [dmnRunnerDispatch, props.setPanelOpen]
  );

  useElementsThatStopKeyboardEventsPropagation(
    window,
    useMemo(() => [".unitables--dmn-runner-drawer"], [])
  );

  return (
    <div style={{ height: "100%" }}>
      <DmnRunnerLoading>
        {dmnRunnerState.jsonSchema && (
          <DmnAutoTable
            jsonSchema={dmnRunnerState.jsonSchema}
            inputRows={dmnRunnerState.inputRows}
            setInputRows={dmnRunnerDispatch.setInputRows}
            results={props.dmnRunnerResults}
            error={dmnRunnerState.error}
            setError={dmnRunnerDispatch.setError}
            openRow={openRow}
          />
        )}
      </DmnRunnerLoading>
    </div>
  );
}
