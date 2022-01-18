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
): [Array<InputRow>, (value: Array<InputRow> | ((previous: Array<InputRow>) => Array<InputRow>)) => void] {
  const workspaces = useWorkspaces();
  const { workspaceDmnRunnerInputs } = useWorkspacesDmnRunnerInputs();
  const [inputRows, setInputRows] = useState<Array<InputRow>>([{}]);

  const refresh = useCallback(
    async (workspaceId: string, relativePath: string, canceled: Holder<boolean>) => {
      workspaceDmnRunnerInputs?.getDmnRunnerData(workspaceFile).then((workspaceFile) => {
        if (canceled.get()) {
          return;
        }

        workspaceFile?.getFileContents().then((inputRows) => {
          if (canceled.get()) {
            return;
          }

          setInputRows(JSON.parse(decoder.decode(inputRows)));
        });
      });
    },
    [workspaceDmnRunnerInputs, workspaceFile]
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!relativePath || !workspaceId) {
          return;
        }
        refresh(workspaceId, relativePath, canceled);
      },
      [relativePath, workspaceId, refresh]
    )
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!relativePath || !workspaceId) {
          return;
        }

        const uniqueFileIdentifier = workspaces.getUniqueFileIdentifier({ workspaceId, relativePath });

        console.debug("Subscribing to " + uniqueFileIdentifier);
        const broadcastChannel = new BroadcastChannel(uniqueFileIdentifier);
        broadcastChannel.onmessage = ({ data }: MessageEvent<WorkspaceFileEvents>) => {
          console.debug(`EVENT::WORKSPACE_FILE: ${JSON.stringify(data)}`);
          if (data.type === "MOVE" || data.type == "RENAME") {
            if (canceled.get()) {
              return;
            }
            const newRelativePathWithoutExtension = data.newRelativePath.slice(
              0,
              data.newRelativePath.lastIndexOf(".")
            );
            workspaceDmnRunnerInputs?.renameDmnRunnerData(workspaceFile, newRelativePathWithoutExtension);
          }
          if (data.type === "DELETE") {
            if (canceled.get()) {
              return;
            }
            workspaceDmnRunnerInputs?.delete(workspaceId);
          }
        };

        return () => {
          console.debug("Unsubscribing to " + uniqueFileIdentifier);
          broadcastChannel.close();
        };
      },
      [relativePath, workspaceId, workspaces, workspaceDmnRunnerInputs, workspaceFile]
    )
  );

  const getInputRows = useCallback(() => {
    if (!workspaceFile || !workspaceDmnRunnerInputs) {
      return Promise.resolve([{}]);
    }
    return workspaceDmnRunnerInputs.getDmnRunnerData(workspaceFile).then((data) => {
      if (!data) {
        return [{}];
      }
      return data.getFileContents().then((content) => JSON.parse(decoder.decode(content)) as Array<object>);
    });
  }, [workspaceDmnRunnerInputs, workspaceFile]);

  const updateInputRows = useCallback(
    async (newInputRows: Array<object> | ((previous: Array<object>) => Array<object>)) => {
      if (!workspaceFile || !workspaceDmnRunnerInputs) {
        return;
      }
      if (typeof newInputRows === "function") {
        const data = await workspaceDmnRunnerInputs.getDmnRunnerData(workspaceFile);
        const currentInputRows = await data
          ?.getFileContents()
          .then((content) => JSON.parse(decoder.decode(content)) as Array<object>);
        await workspaceDmnRunnerInputs.createOrOverwriteDmnRunnerData(
          workspaceFile,
          newInputRows(currentInputRows ?? [{}])
        );
      } else {
        // Change to updateDmnRunnerInput;
        await workspaceDmnRunnerInputs.createOrOverwriteDmnRunnerData(workspaceFile, newInputRows);
      }
    },
    [workspaceDmnRunnerInputs, workspaceFile]
  );

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

  return [inputRows, updateInputRows];
}
