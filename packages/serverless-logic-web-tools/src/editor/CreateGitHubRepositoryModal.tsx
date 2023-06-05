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
import { useCallback, useEffect, useMemo, useState } from "react";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { GithubIcon } from "@patternfly/react-icons/dist/js/icons/github-icon";
import { Form, FormAlert, FormGroup, FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { Radio } from "@patternfly/react-core/dist/js/components/Radio";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { UsersIcon } from "@patternfly/react-icons/dist/js/icons/users-icon";
import { LockIcon } from "@patternfly/react-icons/dist/js/icons/lock-icon";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers/constants";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useSettingsDispatch } from "../settings/SettingsContext";
import { useGitHubAuthInfo } from "../settings/github/Hooks";
import {
  GIT_DEFAULT_BRANCH,
  GIT_ORIGIN_REMOTE_NAME,
} from "@kie-tools-core/workspaces-git-fs/dist/constants/GitConstants";
import { useHistory } from "react-router";
import { useRoutes } from "../navigation/Hooks";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ApplyAcceleratorResult, useAccelerator } from "../accelerator/useAccelerator";
import { KOGITO_QUARKUS_ACCELERATOR } from "../accelerator/Accelerators";

const getSuggestedRepositoryName = (name: string) =>
  name
    .replaceAll(" ", "-")
    .toLocaleLowerCase()
    .replace(/[^._\-\w\d]/g, "");

const ACCELERATOR_TO_USE = KOGITO_QUARKUS_ACCELERATOR;

export function CreateGitHubRepositoryModal(props: {
  workspace: ActiveWorkspace;
  isOpen: boolean;
  onClose: () => void;
  onSuccess: (args: { url: string }) => void;
  currentFile: WorkspaceFile;
}) {
  const history = useHistory();
  const routes = useRoutes();
  const workspaces = useWorkspaces();
  const settingsDispatch = useSettingsDispatch();
  const githubAuthInfo = useGitHubAuthInfo();

  const { applyAccelerator, rollbackApply, canAcceleratorBeUsed, hasConflictsWithAccelerator, cleanUpTempResources } =
    useAccelerator({
      workspaceId: props.workspace.descriptor.workspaceId,
      currentFile: props.currentFile,
      accelerator: ACCELERATOR_TO_USE,
    });

  const [isPrivate, setPrivate] = useState(false);
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState<string | undefined>(undefined);
  const [name, setName] = useState(getSuggestedRepositoryName(props.workspace.descriptor.name));
  const [shouldUseAccelerator, setShouldUseAccelerator] = useState(false);

  useEffect(() => {
    setName(getSuggestedRepositoryName(props.workspace.descriptor.name));
  }, [props.workspace.descriptor.name]);

  useEffect(() => {
    if (props.isOpen) {
      setShouldUseAccelerator(false);
    }
  }, [props.isOpen]);

  const create = useCallback(async () => {
    let applyAcceleratorResult: ApplyAcceleratorResult | undefined;
    if (!githubAuthInfo) {
      return;
    }

    setError(undefined);
    setLoading(true);

    try {
      const repo = await settingsDispatch.github.octokit.repos.createForAuthenticatedUser({ name, private: isPrivate });

      if (!repo.data.clone_url) {
        throw new Error("Repository creation failed");
      }

      if (canAcceleratorBeUsed && shouldUseAccelerator) {
        applyAcceleratorResult = await applyAccelerator();

        if (!applyAcceleratorResult.success) {
          throw new Error(applyAcceleratorResult.message);
        }
      } else {
        await workspaces.createSavePoint({
          workspaceId: props.workspace.descriptor.workspaceId,
          gitConfig: {
            name: githubAuthInfo.name,
            email: githubAuthInfo.email,
          },
        });
      }

      await workspaces.addRemote({
        workspaceId: props.workspace.descriptor.workspaceId,
        url: repo.data.clone_url,
        name: GIT_ORIGIN_REMOTE_NAME,
        force: true,
      });

      await workspaces.push({
        workspaceId: props.workspace.descriptor.workspaceId,
        remote: GIT_ORIGIN_REMOTE_NAME,
        ref: GIT_DEFAULT_BRANCH,
        remoteRef: `refs/heads/${GIT_DEFAULT_BRANCH}`,
        force: false,
        authInfo: githubAuthInfo,
      });

      await workspaces.initGitOnWorkspace({
        workspaceId: props.workspace.descriptor.workspaceId,
        remoteUrl: new URL(repo.data.clone_url),
      });

      await workspaces.renameWorkspace({
        workspaceId: props.workspace.descriptor.workspaceId,
        newName: new URL(repo.data.html_url).pathname.substring(1),
      });

      props.onClose();
      props.onSuccess({ url: repo.data.html_url });

      if (applyAcceleratorResult?.success) {
        history.replace({
          pathname: routes.workspaceWithFilePath.path({
            workspaceId: props.workspace.descriptor.workspaceId,
            fileRelativePath: applyAcceleratorResult.currentFileAfterMoving.relativePathWithoutExtension,
            extension: applyAcceleratorResult.currentFileAfterMoving.extension,
          }),
        });
      }
    } catch (err) {
      if (applyAcceleratorResult?.success) {
        await rollbackApply({ ...applyAcceleratorResult });
      }

      history.replace({
        pathname: routes.workspaceWithFilePath.path({
          extension: props.currentFile.extension,
          fileRelativePath: props.currentFile.relativePathWithoutExtension,
          workspaceId: props.workspace.descriptor.workspaceId,
        }),
      });

      console.error(err);
      setError(err);
    } finally {
      setLoading(false);

      if (applyAcceleratorResult) {
        await cleanUpTempResources({
          backupBranchName: applyAcceleratorResult.backupBranchName,
          tempBranchName: applyAcceleratorResult.tempBranchName,
        });
      }
    }
  }, [
    githubAuthInfo,
    canAcceleratorBeUsed,
    shouldUseAccelerator,
    settingsDispatch.github.octokit.repos,
    name,
    isPrivate,
    workspaces,
    props,
    applyAccelerator,
    history,
    routes.workspaceWithFilePath,
    rollbackApply,
    cleanUpTempResources,
  ]);

  const isNameValid = useMemo(() => {
    return name.match(/^[._\-\w\d]+$/g);
  }, [name]);

  const validated = useMemo(() => {
    if (isNameValid) {
      return ValidatedOptions.success;
    } else {
      return ValidatedOptions.error;
    }
  }, [isNameValid]);

  return (
    <Modal
      variant={ModalVariant.medium}
      aria-label={"Create a new GitHub repository"}
      isOpen={props.isOpen}
      onClose={props.onClose}
      title={"Create GitHub repository"}
      titleIconVariant={GithubIcon}
      description={`The contents of '${props.workspace.descriptor.name}' will be all in the new GitHub Repository.`}
      actions={[
        <Button isLoading={isLoading} key="create" variant="primary" onClick={create} isDisabled={!isNameValid}>
          Create
        </Button>,
      ]}
    >
      <br />
      <Form
        style={{ padding: "0 16px 0 16px" }}
        onSubmit={(e) => {
          e.preventDefault();
          e.stopPropagation();

          return create();
        }}
      >
        {error && (
          <FormAlert>
            <Alert variant="danger" title={"Error creating GitHub Repository. " + error} isInline={true} />
            <br />
          </FormAlert>
        )}
        <FormGroup
          label="Name"
          isRequired={true}
          helperTextInvalid={
            "Invalid name. Only letters, numbers, dashes (-), dots (.), and underscores (_) are allowed."
          }
          helperText={<FormHelperText icon={<CheckCircleIcon />} isHidden={false} style={{ visibility: "hidden" }} />}
          helperTextInvalidIcon={<ExclamationCircleIcon />}
          fieldId="github-repository-name"
          validated={validated}
        >
          <TextInput
            id={"github-repo-name"}
            validated={validated}
            isRequired={true}
            placeholder={"Name"}
            value={name}
            onChange={setName}
          />
        </FormGroup>
        <Divider inset={{ default: "inset3xl" }} />
        <FormGroup
          helperText={<FormHelperText icon={<CheckCircleIcon />} isHidden={false} style={{ visibility: "hidden" }} />}
          helperTextInvalidIcon={<ExclamationCircleIcon />}
          fieldId="github-repo-visibility"
        >
          <Radio
            isChecked={!isPrivate}
            id={"github-repository-public"}
            name={"github-repository-public"}
            label={
              <>
                <UsersIcon />
                &nbsp;&nbsp; Public
              </>
            }
            description={"Anyone on the internet can see this repository. You choose who can commit."}
            onChange={() => setPrivate(false)}
          />
          <br />
          <Radio
            isChecked={isPrivate}
            id={"github-repository-private"}
            name={"github-repository-private"}
            label={
              <>
                <LockIcon />
                &nbsp;&nbsp; Private
              </>
            }
            description={"You choose who can see and commit to this repository."}
            onChange={() => setPrivate(true)}
          />
          <br />

          <Tooltip
            content={`${ACCELERATOR_TO_USE.name} Accelerator cannot be applied to this workspace.`}
            trigger={!canAcceleratorBeUsed ? "mouseenter click" : ""}
          >
            <Checkbox
              id="check-use-accelerator"
              label={`Use ${ACCELERATOR_TO_USE.name} Accelerator`}
              description={
                <>
                  {`A project structure is created and the workspace files are placed in '${ACCELERATOR_TO_USE.folderToMoveFiles}' folder.`}
                  <br />
                  {canAcceleratorBeUsed &&
                    hasConflictsWithAccelerator &&
                    `Note that conflicting files will be moved to '${ACCELERATOR_TO_USE.folderToMoveBlockedFiles}' folder.`}
                </>
              }
              isChecked={shouldUseAccelerator}
              onChange={(checked) => setShouldUseAccelerator(checked)}
              isDisabled={!canAcceleratorBeUsed}
            />
          </Tooltip>
        </FormGroup>
      </Form>
    </Modal>
  );
}
