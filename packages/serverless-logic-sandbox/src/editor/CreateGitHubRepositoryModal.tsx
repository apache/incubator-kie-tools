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
import { dirname, join } from "path";
import { useHistory } from "react-router";
import { useRoutes } from "../navigation/Hooks";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { isProject } from "../project";

const getSuggestedRepositoryName = (name: string) =>
  name
    .replaceAll(" ", "-")
    .toLocaleLowerCase()
    .replace(/[^._\-\w\d]/g, "");

const KOGITO_QUARKUS_TEMPLATE = {
  url: "https://github.com/kiegroup/serverless-logic-sandbox-deployment",
  remoteName: "KOGITO_QUARKUS_SKELETON",
  branch: "quarkus-accelerator",
};

const RESOURCES_FOLDER = "/src/main/resources";

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

  const [isPrivate, setPrivate] = useState(false);
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState<string | undefined>(undefined);
  const [name, setName] = useState(getSuggestedRepositoryName(props.workspace.descriptor.name));
  const [shouldUseQuarkusAccelerator, setShouldUseQuarkusAccelerator] = useState(false);
  const isProjectStructure = useMemo(() => isProject(props.workspace.files), [props.workspace.files]);

  useEffect(() => {
    setName(getSuggestedRepositoryName(props.workspace.descriptor.name));
  }, [props.workspace.descriptor.name]);

  const create = useCallback(async () => {
    try {
      if (!githubAuthInfo) {
        return;
      }

      setError(undefined);
      setLoading(true);
      const repo = await settingsDispatch.github.octokit.request("POST /user/repos", {
        name,
        private: isPrivate,
      });

      if (!repo.data.clone_url) {
        throw new Error("Repo creation failed.");
      }

      const cloneUrl = repo.data.clone_url;

      let currentFileAfterMoving: WorkspaceFile | undefined;

      if (!isProjectStructure && shouldUseQuarkusAccelerator) {
        await workspaces.addRemote({
          workspaceId: props.workspace.descriptor.workspaceId,
          url: KOGITO_QUARKUS_TEMPLATE.url,
          name: KOGITO_QUARKUS_TEMPLATE.remoteName,
          force: true,
        });

        await workspaces.fetch({
          workspaceId: props.workspace.descriptor.workspaceId,
          remote: KOGITO_QUARKUS_TEMPLATE.remoteName,
          ref: KOGITO_QUARKUS_TEMPLATE.branch,
        });

        for (const file of props.workspace.files) {
          const movedFile = await workspaces.moveFile({
            file: file,
            newDirPath: join(RESOURCES_FOLDER, dirname(file.relativePath)),
          });

          if (file.relativePath === props.currentFile.relativePath) {
            currentFileAfterMoving = movedFile;
          }
        }

        if (!currentFileAfterMoving) {
          throw new Error("Failed to find current file after moving.");
        }

        await workspaces.checkout({
          workspaceId: props.workspace.descriptor.workspaceId,
          ref: KOGITO_QUARKUS_TEMPLATE.branch,
          remote: KOGITO_QUARKUS_TEMPLATE.remoteName,
        });

        await workspaces.deleteRemote({
          workspaceId: props.workspace.descriptor.workspaceId,
          name: KOGITO_QUARKUS_TEMPLATE.remoteName,
        });
      }

      await workspaces.addRemote({
        workspaceId: props.workspace.descriptor.workspaceId,
        url: cloneUrl,
        name: GIT_ORIGIN_REMOTE_NAME,
        force: true,
      });

      await workspaces.createSavePoint({
        workspaceId: props.workspace.descriptor.workspaceId,
        gitConfig: {
          name: githubAuthInfo.name,
          email: githubAuthInfo.email,
        },
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
        remoteUrl: new URL(cloneUrl),
      });

      await workspaces.renameWorkspace({
        workspaceId: props.workspace.descriptor.workspaceId,
        newName: new URL(repo.data.html_url).pathname.substring(1),
      });

      props.onClose();
      props.onSuccess({ url: repo.data.html_url });

      if (currentFileAfterMoving) {
        history.replace({
          pathname: routes.workspaceWithFilePath.path({
            workspaceId: props.workspace.descriptor.workspaceId,
            fileRelativePath: currentFileAfterMoving.relativePathWithoutExtension,
            extension: currentFileAfterMoving.extension,
          }),
        });
      }
    } catch (err) {
      setError(err);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [
    githubAuthInfo,
    settingsDispatch.github.octokit,
    name,
    isPrivate,
    workspaces,
    props,
    isProjectStructure,
    shouldUseQuarkusAccelerator,
    history,
    routes.workspaceWithFilePath,
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
            content={
              "Quarkus accelerator cannot be used since your workspace already seems to contain a project structure."
            }
            trigger={isProjectStructure ? "mouseenter click" : ""}
          >
            <Checkbox
              id="check-use-quarkus-accelerator"
              label="Use Quarkus Accelerator"
              description={
                "Create a base Quarkus project in the repository and place workspace files in src/main/resources folder."
              }
              isChecked={shouldUseQuarkusAccelerator}
              onChange={(checked) => setShouldUseQuarkusAccelerator(checked)}
              isDisabled={isProjectStructure}
            />
          </Tooltip>
        </FormGroup>
      </Form>
    </Modal>
  );
}
