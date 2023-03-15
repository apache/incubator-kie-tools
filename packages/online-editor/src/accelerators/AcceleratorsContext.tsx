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

import React, { useCallback, useMemo, useState } from "react";
import * as yaml from "js-yaml";
import { useEnv } from "../env/hooks/EnvContext";
import {
  ACCELERATOR_CONFIG_FILE_NAME,
  ACCELERATOR_CONFIG_FILE_EXTENSION,
  KIE_SANDBOX_PROJECT_FILES_PATH,
  AcceleratorConfig,
  validateAcceleratorDestinationFolderPaths,
  ACCELERATOR_CONFIG_FILE_RELATIVE_PATH,
} from "./AcceleratorsApi";
import { WorkspaceFile, useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { dirname, join } from "path";
import { useHistory } from "react-router";
import { useRoutes } from "../navigation/Hooks";
import { useGlobalAlert } from "../alerts";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { useOnlineI18n } from "../i18n";
import { isBusinessProcessModel, isDecision, isScorecard, isTestScenario } from "../extension";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { useAuthSession } from "../authSessions/AuthSessionsContext";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { useWorkspaceFilePromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceFileHooks";
import {
  GIT_DEFAULT_BRANCH,
  GIT_ORIGIN_REMOTE_NAME,
} from "@kie-tools-core/workspaces-git-fs/dist/constants/GitConstants";

const TEMP_ACCELERATOR_REMOTE_NAME = "accelerator-remote";
const BACKUP_BRANCH_NAME = "pre-accelerator-backup-branch";
const MOVED_FILES_BRANCH_NAME = "moved-files-accelerator-branch";

export function useAvailableAccelerators() {
  const { env } = useEnv();
  return useMemo<AcceleratorConfig[]>(() => env.KIE_SANDBOX_ACCELERATORS, [env.KIE_SANDBOX_ACCELERATORS]);
}

export function useApplyAccelerators(workspace: ActiveWorkspace) {
  const workspaces = useWorkspaces();
  const history = useHistory();
  const routes = useRoutes();
  const { i18n } = useOnlineI18n();
  const { gitConfig } = useAuthSession(workspace.descriptor.gitAuthSessionId);

  const apllyingAcceleratorAlert = useGlobalAlert(
    useCallback(
      (_, staticArgs: { acceleratorName: string }) => (
        <Alert
          className={"kogito--alert"}
          variant="info"
          title={
            <>
              <Spinner size={"sm"} />
              &nbsp;&nbsp; {i18n.accelerators.loadingAlert(staticArgs.acceleratorName)}
            </>
          }
        />
      ),
      [i18n]
    )
  );

  const applyAcceleratorSuccessAlert = useGlobalAlert(
    useCallback(
      (_, staticArgs: { acceleratorName: string }) => (
        <Alert
          className={"kogito--alert"}
          variant="success"
          title={i18n.accelerators.successAlert(staticArgs.acceleratorName)}
        />
      ),
      [i18n]
    ),
    { durationInSeconds: 4 }
  );

  const applyAcceleratorFailAlert = useGlobalAlert(
    useCallback(
      (_, staticArgs: { acceleratorName: string }) => (
        <Alert
          className={"kogito--alert"}
          variant="danger"
          title={i18n.accelerators.failAlert(staticArgs.acceleratorName)}
        />
      ),
      [i18n]
    ),
    { durationInSeconds: 4 }
  );

  const attemptToDeleteTemporaryBranches = useCallback(async () => {
    const workspaceId = workspace.descriptor.workspaceId;
    try {
      await workspaces.deleteBranch({ workspaceId, ref: BACKUP_BRANCH_NAME });
    } catch (e) {
      // Do nothing
    }
    try {
      await workspaces.deleteBranch({ workspaceId, ref: MOVED_FILES_BRANCH_NAME });
    } catch (e) {
      // Do nothing
    }
  }, [workspace.descriptor.workspaceId, workspaces]);

  const applyAcceleratorToWorkspace = useCallback(
    async (accelerator: AcceleratorConfig, currentFile: WorkspaceFile) => {
      apllyingAcceleratorAlert.show({ acceleratorName: accelerator.name });

      const workspaceId = workspace.descriptor.workspaceId;

      let movedFiles: WorkspaceFile[] = [];

      // Attempt to delete branches
      // This is a workaround, as isomorphic-git doesn't necessarilly deletes the branch, even though it resolves
      attemptToDeleteTemporaryBranches();

      let configFile: WorkspaceFile | undefined = undefined;

      try {
        const destinationPathValidation = {
          dmnDestinationFolder: validateAcceleratorDestinationFolderPaths(accelerator.dmnDestinationFolder),
          bpmnDestinationFolder: validateAcceleratorDestinationFolderPaths(accelerator.bpmnDestinationFolder),
          otherFilesDestinationFolder: validateAcceleratorDestinationFolderPaths(
            accelerator.otherFilesDestinationFolder
          ),
        };

        if (
          destinationPathValidation.dmnDestinationFolder ||
          destinationPathValidation.bpmnDestinationFolder ||
          destinationPathValidation.otherFilesDestinationFolder
        ) {
          throw destinationPathValidation;
        }

        const workspaceFiles = await workspaces.getFiles({ workspaceId });

        // Create new temporary branch with current files, but stay on main
        await workspaces.branch({ workspaceId, name: BACKUP_BRANCH_NAME, checkout: false });

        // Commit moved files to moved files branch
        await workspaces.commit({
          workspaceId,
          commitMessage: "Backup files",
          targetBranch: BACKUP_BRANCH_NAME,
        });

        // Create new temporary branch for moved files, but stay on main
        await workspaces.branch({ workspaceId, name: MOVED_FILES_BRANCH_NAME, checkout: false });

        // Checkout to moved files branch
        await workspaces.checkout({
          workspaceId,
          ref: MOVED_FILES_BRANCH_NAME,
          remote: GIT_ORIGIN_REMOTE_NAME,
          force: true,
        });

        // Move files
        let currentFileAfterAccelerator: WorkspaceFile | undefined;
        movedFiles = await Promise.all(
          workspaceFiles.map(async (file) => {
            let fileNewDestination: string;

            if (isDecision(file.relativePath) || isScorecard(file.relativePath) || isTestScenario(file.relativePath)) {
              fileNewDestination = accelerator.dmnDestinationFolder;
            } else if (isBusinessProcessModel(file.relativePath)) {
              fileNewDestination = accelerator.bpmnDestinationFolder;
            } else {
              fileNewDestination = accelerator.otherFilesDestinationFolder;
            }

            const movedFile = await workspaces.moveFile({
              file,
              newDirPath: join(fileNewDestination, dirname(file.relativePath)),
            });

            await workspaces.stageFile({ workspaceId, relativePath: movedFile.relativePath });

            if (file.relativePath === currentFile.relativePath) {
              currentFileAfterAccelerator = movedFile;
            }
            return movedFile;
          })
        );

        const movedFilesPaths = movedFiles.map((file) => file.relativePath);

        if (!currentFileAfterAccelerator) {
          throw new Error("Failed to find current file after moving.");
        }

        // Commit moved files to moved files branch
        await workspaces.commit({
          workspaceId,
          commitMessage: "Moving files to apply Accelerator.",
          targetBranch: MOVED_FILES_BRANCH_NAME,
        });

        // Go back to main
        await workspaces.checkout({ workspaceId, ref: GIT_DEFAULT_BRANCH, remote: GIT_ORIGIN_REMOTE_NAME });

        // Add Accelerator remote and fetch it
        await workspaces.addRemote({
          workspaceId,
          name: TEMP_ACCELERATOR_REMOTE_NAME,
          url: accelerator.gitRepositoryUrl,
          force: true,
        });

        await workspaces.fetch({
          workspaceId,
          remote: TEMP_ACCELERATOR_REMOTE_NAME,
          ref: accelerator.gitRepositoryGitRef,
        });

        // Checkout Accelerator files, wiping everything else
        await workspaces.checkout({
          workspaceId,
          ref: `remotes/${TEMP_ACCELERATOR_REMOTE_NAME}/${accelerator.gitRepositoryGitRef}`,
          remote: TEMP_ACCELERATOR_REMOTE_NAME,
          force: true,
          noUpdateHead: true,
          filepaths: ["."],
        });

        // Delete Accelerator remote
        await workspaces.deleteRemote({ workspaceId, name: TEMP_ACCELERATOR_REMOTE_NAME });

        // Stage all Accelerator files
        const acceleratorFiles = await workspaces.getFiles({ workspaceId });
        await Promise.all(
          acceleratorFiles.map(async (file) => {
            return workspaces.stageFile({
              workspaceId,
              relativePath: file.relativePath,
            });
          })
        );

        // Bring moved user files back
        await workspaces.checkout({
          workspaceId,
          ref: MOVED_FILES_BRANCH_NAME,
          remote: GIT_ORIGIN_REMOTE_NAME,
          force: true,
          noUpdateHead: true,
          filepaths: movedFilesPaths,
        });

        // Stage all moved files
        await Promise.all(
          movedFiles.map(async (file) => {
            return workspaces.stageFile({
              workspaceId,
              relativePath: file.relativePath,
            });
          })
        );

        // Add Accelerator YAML config
        const content = `# This file was automatically created by ${
          i18n.names.businessModeler.online
        }. Please don't modify it.\n${yaml.dump({ ...accelerator, appliedAt: new Date().toISOString() })}`;
        configFile = await workspaces.addFile({
          workspaceId,
          name: ACCELERATOR_CONFIG_FILE_NAME,
          destinationDirRelativePath: KIE_SANDBOX_PROJECT_FILES_PATH,
          content,
          extension: ACCELERATOR_CONFIG_FILE_EXTENSION,
        });

        await workspaces.stageFile({
          workspaceId,
          relativePath: ACCELERATOR_CONFIG_FILE_RELATIVE_PATH,
        });

        // Create commit
        await workspaces.createSavePoint({
          workspaceId,
          gitConfig,
          commitMessage: i18n.accelerators.commitMessage(accelerator.name),
          forceHasChanges: true,
        });

        apllyingAcceleratorAlert.close();

        applyAcceleratorSuccessAlert.show({ acceleratorName: accelerator.name });

        attemptToDeleteTemporaryBranches();

        history.replace({
          pathname: routes.workspaceWithFilePath.path({
            extension: currentFileAfterAccelerator.extension,
            fileRelativePath: currentFileAfterAccelerator.relativePathWithoutExtension,
            workspaceId,
          }),
        });
      } catch (e) {
        apllyingAcceleratorAlert.close();
        applyAcceleratorFailAlert.show({ acceleratorName: accelerator.name });

        console.error(e);

        // Delete config file
        if (configFile) {
          await workspaces.deleteFile({ file: configFile });
        }

        // Return to main
        await workspaces.checkout({ workspaceId, ref: GIT_DEFAULT_BRANCH, remote: GIT_ORIGIN_REMOTE_NAME });

        // Revert repo
        await workspaces.checkout({
          workspaceId,
          ref: BACKUP_BRANCH_NAME,
          remote: GIT_ORIGIN_REMOTE_NAME,
          force: true,
          noUpdateHead: true,
          filepaths: ["."],
        });

        attemptToDeleteTemporaryBranches();

        // Delete moved files
        await Promise.all(
          movedFiles.map(
            async (file) =>
              (await workspaces.existsFile({ workspaceId, relativePath: file.relativePath })) &&
              (await workspaces.deleteFile({ file }))
          )
        );

        // Reload to currentFile file
        history.replace({
          pathname: routes.workspaceWithFilePath.path({
            extension: currentFile.extension,
            fileRelativePath: currentFile.relativePathWithoutExtension,
            workspaceId,
          }),
        });
      }
    },
    [
      apllyingAcceleratorAlert,
      applyAcceleratorFailAlert,
      applyAcceleratorSuccessAlert,
      attemptToDeleteTemporaryBranches,
      gitConfig,
      history,
      i18n.accelerators,
      i18n.names.businessModeler.online,
      routes.workspaceWithFilePath,
      workspace.descriptor.workspaceId,
      workspaces,
    ]
  );

  return applyAcceleratorToWorkspace;
}

export function useCurrentAccelerator(workspaceId: string) {
  const [currentAccelerator, setCurrentAccelerator] = useState<AcceleratorConfig>();

  const acceleratorConfigFile = useWorkspaceFilePromise(workspaceId, ACCELERATOR_CONFIG_FILE_RELATIVE_PATH);

  const getCurrentAccelerator = useCallback(async () => {
    if (!acceleratorConfigFile?.data) {
      return;
    }

    return yaml.load(await acceleratorConfigFile.data.workspaceFile.getFileContentsAsString()) as AcceleratorConfig;
  }, [acceleratorConfigFile.data]);

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        getCurrentAccelerator().then((accelerator) => {
          if (canceled.get()) {
            return;
          }
          setCurrentAccelerator(accelerator);
        });
      },
      [getCurrentAccelerator]
    )
  );

  return currentAccelerator;
}
