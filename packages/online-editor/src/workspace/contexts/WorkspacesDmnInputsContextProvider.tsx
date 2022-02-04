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
import { useCallback, useMemo, useState } from "react";
import { WorkspacesDmnInputsContext } from "./WorkspacesDmnInputsContext";
import { WorkspaceDmnRunnerInputsService } from "../services/WorkspaceDmnRunnerInputsService";
import { decoder, encoder, useWorkspaces, WorkspaceFile } from "./WorkspacesContext";
import { InputRow } from "../../editor/DmnRunner/DmnRunnerContext";
import { WorkspaceDmnRunnerEvents } from "../hooks/WorkspaceDmnRunnerInput";
import KieSandboxFs from "@kie-tools/kie-sandbox-fs";

export function WorkspacesDmnInputsContextProvider(props: React.PropsWithChildren<{}>) {
  const workspaces = useWorkspaces();

  const dmnRunnerService = useMemo(() => {
    return new WorkspaceDmnRunnerInputsService(workspaces.storageService);
  }, [workspaces.storageService]);

  // const updateInputRows = useCallback(
  //   (workspaceFile: WorkspaceFile) =>
  //     async (newInputRows: Array<InputRow> | ((previous: Array<InputRow>) => Array<InputRow>)) => {
  //       if (!workspaceFile || !dmnRunnerService) {
  //         return;
  //       }
  //       if (typeof newInputRows === "function") {
  //         const data = await dmnRunnerService.getDmnRunnerData(workspaceFile);
  //         const previousInputRows = await data
  //           ?.getFileContents()
  //           .then((content) => JSON.parse(decoder.decode(content)) as Array<InputRow>);
  //         const currentInputRows = newInputRows(previousInputRows ?? [{}]);
  //         await dmnRunnerService.createOrOverwriteDmnRunnerData(workspaceFile, currentInputRows);
  //         const broadcastChannel = new BroadcastChannel(workspaceFile.workspaceId + "__dmn_runner_inputs");
  //         const workspaceEvent: WorkspaceDmnRunnerEvents = { type: "UPDATE", relativePath: workspaceFile.relativePath };
  //         broadcastChannel.postMessage(workspaceEvent);
  //       } else {
  //         await dmnRunnerService.createOrOverwriteDmnRunnerData(workspaceFile, newInputRows);
  //       }
  //     },
  //   [dmnRunnerService]
  // );
  // : () => Promise.resolve(encoder.encode(JSON.stringify(dmnRunnerData)))

  const updateInputRows = useCallback(
    (workspaceFile: WorkspaceFile) =>
      async (newInputRows: Array<InputRow> | ((previous: Array<InputRow>) => Array<InputRow>)) => {
        if (typeof newInputRows === "function") {
          const data = await dmnRunnerService.getDmnRunnerData(workspaceFile);
          const previousInputRows = await data
            ?.getFileContents()
            .then((content) => JSON.parse(decoder.decode(content)) as Array<InputRow>);
          await dmnRunnerService.updateDmnRunnerInputs(
            workspaceFile,
            () => Promise.resolve(JSON.stringify(newInputRows(previousInputRows ?? [{}]))),
            { broadcast: true }
          );
        } else {
          await dmnRunnerService.updateDmnRunnerInputs(
            workspaceFile,
            () => Promise.resolve(JSON.stringify(newInputRows)),
            { broadcast: true }
          );
        }
      },
    [dmnRunnerService]
  );

  return (
    <WorkspacesDmnInputsContext.Provider value={{ dmnRunnerService, updateInputRows }}>
      {props.children}
    </WorkspacesDmnInputsContext.Provider>
  );
}
