/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { decoder, useWorkspaces, WorkspaceFile } from "../workspace/WorkspacesContext";
import { DmnRunnerInputsService } from "./DmnRunnerInputsService";
import { InputRow } from "@kie-tools/form-dmn";
import { DmnRunnerInputsDispatchContext } from "./DmnRunnerInputsContext";
import { useCancelableEffect } from "../reactExt/Hooks";
import { WORKSPACES_BROADCAST_CHANNEL } from "../workspace/services/WorkspaceService";
import { WorkspacesEvents } from "../workspace/hooks/WorkspacesHooks";

export function DmnRunnerInputsContextProvider(props: React.PropsWithChildren<{}>) {
  const dmnRunnerInputsService = useMemo(() => {
    return new DmnRunnerInputsService();
  }, []);

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        console.debug("Subscribing to " + WORKSPACES_BROADCAST_CHANNEL);
        const broadcastChannel = new BroadcastChannel(WORKSPACES_BROADCAST_CHANNEL);
        broadcastChannel.onmessage = ({ data }: MessageEvent<WorkspacesEvents>) => {
          console.debug(`EVENT::WORKSPACE_FILE: ${JSON.stringify(data)}`);
          if (data.type === "DELETE_WORKSPACE") {
            if (canceled.get()) {
              return;
            }
            dmnRunnerInputsService.delete(data.workspaceId);
          }
        };

        return () => {
          console.debug("Unsubscribing to " + WORKSPACES_BROADCAST_CHANNEL);
          broadcastChannel.close();
        };
      },
      [dmnRunnerInputsService]
    )
  );

  const updatePersistedInputRows = useCallback(
    async (
      workspaceFile: WorkspaceFile,
      newInputRows: Array<InputRow> | ((previous: Array<InputRow>) => Array<InputRow>)
    ) => {
      if (typeof newInputRows === "function") {
        const inputs = await dmnRunnerInputsService.getDmnRunnerInputs(workspaceFile);
        const previousInputRows = await inputs
          ?.getFileContents()
          .then((content) => dmnRunnerInputsService.parseDmnRunnerInputs(decoder.decode(content)));
        await dmnRunnerInputsService.updateDmnRunnerInputs(
          workspaceFile,
          dmnRunnerInputsService.stringifyDmnRunnerInputs(newInputRows, previousInputRows)
        );
      } else {
        await dmnRunnerInputsService.updateDmnRunnerInputs(
          workspaceFile,
          dmnRunnerInputsService.stringifyDmnRunnerInputs(newInputRows)
        );
      }
    },
    [dmnRunnerInputsService]
  );

  const deletePersistedInputRows = useCallback(
    async (workspaceFile: WorkspaceFile) => {
      await dmnRunnerInputsService.deleteDmnRunnerInputs(workspaceFile);
    },
    [dmnRunnerInputsService]
  );

  const getUniqueFileIdentifier = useCallback(
    (args: { workspaceId: string; relativePath: string }) => dmnRunnerInputsService.getUniqueFileIdentifier(args),
    [dmnRunnerInputsService]
  );

  const getInputRowsForDownload = useCallback(
    async (workspaceFile: WorkspaceFile) => {
      const inputs = await dmnRunnerInputsService.getDmnRunnerInputs(workspaceFile);
      return await inputs?.getFileContents().then((content) => new Blob([content], { type: "application/json" }));
    },
    [dmnRunnerInputsService]
  );

  const uploadInputRows = useCallback(
    async (workspaceFile: WorkspaceFile, file: File) => {
      const content = await new Promise<string>((res) => {
        const reader = new FileReader();
        reader.onload = (event: ProgressEvent<FileReader>) => res(decoder.decode(event.target?.result as ArrayBuffer));
        reader.readAsArrayBuffer(file);
      });
      await dmnRunnerInputsService.createOrOverwriteDmnRunnerInputs(workspaceFile, content);
    },
    [dmnRunnerInputsService]
  );

  return (
    <DmnRunnerInputsDispatchContext.Provider
      value={{
        dmnRunnerInputsService,
        updatePersistedInputRows,
        getUniqueFileIdentifier,
        deletePersistedInputRows,
        getInputRowsForDownload,
        uploadInputRows,
      }}
    >
      {props.children}
    </DmnRunnerInputsDispatchContext.Provider>
  );
}
