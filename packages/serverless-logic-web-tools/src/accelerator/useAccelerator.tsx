/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import {
  GIT_DEFAULT_BRANCH,
  GIT_ORIGIN_REMOTE_NAME,
} from "@kie-tools-core/workspaces-git-fs/dist/constants/GitConstants";
import { WorkspaceFile, useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useWorkspacePromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import { join } from "path";
import { useCallback, useMemo } from "react";
import { v4 as uuid } from "uuid";
import { APP_GIT_USER, APP_NAME } from "../AppConstants";
import { Accelerator } from "./Accelerators";

interface Props {
  workspaceId: string;
  currentFile: WorkspaceFile;
  accelerator: Accelerator;
}

export type ApplyAcceleratorResult =
  | { backupBranchName: string; tempBranchName: string } & (
      | {
          success: true;
          currentFileAfterMoving: WorkspaceFile;
          movedFiles: WorkspaceFile[];
        }
      | {
          success: false;
          message: string;
        }
    );

export function useAccelerator(props: Props) {
  const workspaces = useWorkspaces();
  const workspacePromise = useWorkspacePromise(props.workspaceId);

  const canAcceleratorBeUsed = useMemo(() => {
    if (!workspacePromise.data) {
      return false;
    }
    return props.accelerator.canBeUsed(workspacePromise.data.files);
  }, [props.accelerator, workspacePromise.data]);

  const hasConflictsWithAccelerator = useMemo(() => {
    if (!workspacePromise.data) {
      return false;
    }
    return props.accelerator.hasConflicts(workspacePromise.data.files);
  }, [props.accelerator, workspacePromise.data]);

  const cleanUpTempResources = useCallback(
    async (args: { backupBranchName: string; tempBranchName: string }) => {
      try {
        await workspaces.deleteBranch({
          workspaceId: props.workspaceId,
          ref: args.backupBranchName,
        });
      } catch (e) {
        // Do nothing
      }
      try {
        await workspaces.deleteBranch({
          workspaceId: props.workspaceId,
          ref: args.tempBranchName,
        });
      } catch (e) {
        // Do nothing
      }
    },
    [props.workspaceId, workspaces]
  );

  const rollbackApply = useCallback(
    async (args: { backupBranchName: string; movedFiles: WorkspaceFile[] }) => {
      await workspaces.checkout({
        workspaceId: props.workspaceId,
        ref: GIT_DEFAULT_BRANCH,
        remote: GIT_ORIGIN_REMOTE_NAME,
      });

      await workspaces.checkoutFilesFromLocalHead({
        workspaceId: props.workspaceId,
        ref: args.backupBranchName,
        filepaths: ["."],
      });

      await Promise.all(
        args.movedFiles.map(
          async (file) =>
            (await workspaces.existsFile({
              workspaceId: props.workspaceId,
              relativePath: file.relativePath,
            })) && (await workspaces.deleteFile({ file }))
        )
      );
    },
    [props.workspaceId, workspaces]
  );

  const applyAccelerator = useCallback(async (): Promise<ApplyAcceleratorResult> => {
    const tempBranchName = `temp_branch__${uuid()}`;
    const backupBranchName = `backup_branch__${uuid()}`;

    if (!canAcceleratorBeUsed || !workspacePromise.data) {
      return {
        success: false,
        message: "This accelerator cannot be used along with the workspace files",
        backupBranchName,
        tempBranchName,
      };
    }

    let currentFileAfterMoving: WorkspaceFile | undefined;
    const movedFiles: WorkspaceFile[] = [];

    try {
      await workspaces.branch({
        workspaceId: props.workspaceId,
        name: backupBranchName,
        checkout: false,
      });

      await workspaces.commit({
        workspaceId: props.workspaceId,
        commitMessage: `${APP_NAME}: Backup files before applying Accelerator`,
        targetBranch: backupBranchName,
      });

      await workspaces.branch({
        workspaceId: props.workspaceId,
        name: tempBranchName,
        checkout: true,
      });

      movedFiles.push(
        ...(await Promise.all(
          workspacePromise.data.files.map(async (file) => {
            const destinationFolder = props.accelerator.fileBlockList.includes(file.relativePath)
              ? props.accelerator.folderToMoveBlockedFiles
              : props.accelerator.folderToMoveFiles;

            const movedFile = await workspaces.moveFile({
              file: file,
              newDirPath: join(destinationFolder, file.relativeDirPath),
            });

            await workspaces.stageFile({
              workspaceId: props.workspaceId,
              relativePath: movedFile.relativePath,
            });

            if (file.relativePath === props.currentFile.relativePath) {
              currentFileAfterMoving = movedFile;
            }

            return movedFile;
          })
        ))
      );

      if (!currentFileAfterMoving) {
        throw new Error(`Failed to find ${props.currentFile.name} after moving.`);
      }

      await workspaces.commit({
        workspaceId: props.workspaceId,
        commitMessage: "Move files to apply Accelerator",
        targetBranch: tempBranchName,
      });

      await workspaces.checkout({
        workspaceId: props.workspaceId,
        ref: GIT_DEFAULT_BRANCH,
        remote: GIT_ORIGIN_REMOTE_NAME,
      });

      await workspaces.addRemote({
        workspaceId: props.workspaceId,
        name: props.accelerator.remote,
        url: props.accelerator.url,
        force: true,
      });

      const fetchResult = await workspaces.fetch({
        workspaceId: props.workspaceId,
        remote: props.accelerator.remote,
        ref: props.accelerator.ref,
      });

      if (!fetchResult.fetchHead) {
        throw new Error("Failed to fetch Accelerator");
      }

      await workspaces.checkoutFilesFromLocalHead({
        workspaceId: props.workspaceId,
        ref: fetchResult.fetchHead,
        filepaths: ["."],
      });

      await workspaces.deleteRemote({
        workspaceId: props.workspaceId,
        name: props.accelerator.remote,
      });

      const acceleratorFiles = await workspaces.getFiles({ workspaceId: props.workspaceId });
      await Promise.all(
        acceleratorFiles.map(async (file) =>
          workspaces.stageFile({
            workspaceId: props.workspaceId,
            relativePath: file.relativePath,
          })
        )
      );

      await workspaces.checkoutFilesFromLocalHead({
        workspaceId: props.workspaceId,
        ref: tempBranchName,
        filepaths: movedFiles.map((file) => file.relativePath),
      });

      await Promise.all(
        movedFiles.map(async (file) => {
          return workspaces.stageFile({
            workspaceId: props.workspaceId,
            relativePath: file.relativePath,
          });
        })
      );

      await workspaces.createSavePoint({
        workspaceId: props.workspaceId,
        gitConfig: APP_GIT_USER,
        commitMessage: `Merge workspace and ${props.accelerator.name} Accelerator files together`,
        forceHasChanges: true,
      });

      return {
        success: true,
        currentFileAfterMoving,
        movedFiles,
        backupBranchName,
        tempBranchName,
      };
    } catch (error) {
      await rollbackApply({ backupBranchName, movedFiles });

      return {
        success: false,
        message: error,
        backupBranchName,
        tempBranchName,
      };
    }
  }, [canAcceleratorBeUsed, workspacePromise.data, workspaces, props, rollbackApply]);

  return { applyAccelerator, rollbackApply, canAcceleratorBeUsed, hasConflictsWithAccelerator, cleanUpTempResources };
}
