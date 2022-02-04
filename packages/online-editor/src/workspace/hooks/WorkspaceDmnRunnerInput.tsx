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

import { useCallback, useEffect, useState } from "react";
import { decoder, useWorkspaces, useWorkspacesDmnRunnerInputs, WorkspaceFile } from "../";
import { WorkspaceFileEvents } from "./WorkspaceFileHooks";
import { InputRow } from "../../editor/DmnRunner/DmnRunnerContext";
import { Holder, useCancelableEffect } from "../../reactExt/Hooks";

export function useWorkspaceDmnRunnerInputs(
  workspaceId: string | undefined,
  relativePath: string | undefined,
  workspaceFile: WorkspaceFile
) {
  const workspaces = useWorkspaces();
  const { dmnRunnerService } = useWorkspacesDmnRunnerInputs();
  const [inputRows, setInputRows] = useState<Array<InputRow>>([{}]);

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!relativePath || !workspaceId) {
          return;
        }

        const uniqueFileIdentifier = workspaces.getUniqueFileIdentifier({ workspaceId, relativePath });

        console.debug("Subscribing to " + uniqueFileIdentifier + "__dmn_runner_inputs");
        const broadcastChannel = new BroadcastChannel(uniqueFileIdentifier + "__dmn_runner_inputs");
        broadcastChannel.onmessage = ({ data }: MessageEvent<WorkspaceFileEvents>) => {
          // create new message related to the change of the input;
          console.debug(`EVENT::WORKSPACE_FILE: ${JSON.stringify(data)}`);
          if (data.type === "MOVE" || data.type == "RENAME") {
            if (canceled.get()) {
              return;
            }
            const newRelativePathWithoutExtension = data.newRelativePath.slice(
              0,
              data.newRelativePath.lastIndexOf(".")
            );
            dmnRunnerService?.renameDmnRunnerData(workspaceFile, newRelativePathWithoutExtension);
          }
          if (data.type === "DELETE") {
            if (canceled.get()) {
              return;
            }
            dmnRunnerService?.delete(workspaceId);
          }
        };

        return () => {
          console.debug("Unsubscribing to " + uniqueFileIdentifier);
          broadcastChannel.close();
        };
      },
      [relativePath, workspaceId, workspaces, dmnRunnerService, workspaceFile]
    )
  );

  const getInputRows = useCallback(() => {
    if (!workspaceFile || !dmnRunnerService) {
      return Promise.resolve([{}]);
    }
    return dmnRunnerService.getDmnRunnerData(workspaceFile).then((data) => {
      if (!data) {
        return [{}];
      }
      return data.getFileContents().then((content) => JSON.parse(decoder.decode(content)) as Array<InputRow>);
    });
  }, [dmnRunnerService, workspaceFile]);

  useEffect(() => {
    let runEffect = true;
    getInputRows().then((inputRows) => {
      // Avoid setState on unmounted component
      if (!runEffect) {
        return;
      }
      setInputRows(inputRows);
    });

    return () => {
      runEffect = false;
    };
  }, [getInputRows]);

  return inputRows;
}

export type WorkspaceDmnRunnerEvents =
  | { type: "MOVE"; newRelativePath: string; oldRelativePath: string }
  | { type: "RENAME"; newRelativePath: string; oldRelativePath: string }
  | { type: "UPDATE"; relativePath: string }
  | { type: "DELETE"; relativePath: string }
  | { type: "ADD"; relativePath: string }
  | { type: "INPUTS_UPDATED"; relativePath: string }
  | { type: "INPUTS_DELETED"; relativePath: string };
