/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { Form, FormAlert, FormGroup, FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { Radio } from "@patternfly/react-core/dist/js/components/Radio";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";

import { UsersIcon } from "@patternfly/react-icons/dist/js/icons/users-icon";
import { LockIcon } from "@patternfly/react-icons/dist/js/icons/lock-icon";

import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers/constants";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { GIT_ORIGIN_REMOTE_NAME } from "@kie-tools-core/workspaces-git-fs/dist/constants/GitConstants";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { useAuthSession } from "../../../authSessions/AuthSessionsContext";
import { useBitbucketClient } from "../../../bitbucket/Hooks";
import { BitbucketIcon } from "@patternfly/react-icons/dist/js/icons/bitbucket-icon";
import { GithubIcon } from "@patternfly/react-icons/dist/js/icons/github-icon";
import { GitlabIcon } from "@patternfly/react-icons/dist/js/icons/gitlab-icon";
import { useGitHubClient } from "../../../github/Hooks";
import { AuthProviderGroup, isSupportedGitAuthProviderType } from "../../../authProviders/AuthProvidersApi";
import { useAuthProvider } from "../../../authProviders/AuthProvidersContext";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { useOnlineI18n } from "../../../i18n";
import { LoadOrganizationsSelect, SelectOptionObjectType } from "./LoadOrganizationsSelect";
import { useGitIntegration } from "./GitIntegrationContextProvider";
import { useGitlabClient } from "../../../gitlab/useGitlabClient";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";

export interface CreateRepositoryResponse {
  cloneUrl: string;
  htmlUrl: string;
}

const getSuggestedRepositoryName = (name: string) =>
  name
    .replaceAll(" ", "-")
    .toLocaleLowerCase()
    .replace(/[^._\-\w\d]/g, "");

export function CreateGitRepositoryModal(props: {
  workspace: WorkspaceDescriptor;
  isOpen: boolean;
  onClose: () => void;
  onSuccess?: (args: { url: string }) => void;
}) {
  const workspaces = useWorkspaces();
  const { authSession, gitConfig, authInfo } = useAuthSession(props.workspace.gitAuthSessionId);
  const authProvider = useAuthProvider(authSession);
  const bitbucketClient = useBitbucketClient(authSession);
  const gitHubClient = useGitHubClient(authSession);
  const gitlabClient = useGitlabClient(authSession);

  const [isPrivate, setPrivate] = useState(false);
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState<string | undefined>(undefined);
  const [name, setName] = useState(getSuggestedRepositoryName(props.workspace.name));
  const { i18n } = useOnlineI18n();
  const [selectedOrganization, setSelectedOrganization] = useState<SelectOptionObjectType>();

  const {
    alerts: { createRepositorySuccessAlert, errorAlert },
  } = useGitIntegration();

  useEffect(() => {
    setName(getSuggestedRepositoryName(props.workspace.name));
  }, [props.workspace.name]);

  const createBitbucketRepository = useCallback(async (): Promise<CreateRepositoryResponse> => {
    if (selectedOrganization?.kind !== "organization") {
      throw new Error("No workspace was selected for Bitbucket Repository.");
    }
    const bitbucketWorkspace = (selectedOrganization as SelectOptionObjectType<"organization">).selectedOption;
    const repoResponse = await bitbucketClient.createRepo({
      name,
      workspace: bitbucketWorkspace.name,
      isPrivate,
    });
    if (!repoResponse.ok) {
      throw new Error(
        `Bitbucket repository creation request failed with: ${repoResponse.status} ${repoResponse.statusText}`
      );
    }
    const repo = await repoResponse.json();

    if (!repo.links || !repo.links.clone || !Array.isArray(repo.links.clone)) {
      throw new Error("Unexpected contents of the Bitbucket repository creation response.");
    }

    const cloneLinks: any[] = repo.links.clone;
    const cloneUrl = cloneLinks.filter((e) => {
      return (e.name = "https" && e.href.startsWith("https"));
    })[0].href;
    return { cloneUrl, htmlUrl: repo.links.html.href };
  }, [bitbucketClient, isPrivate, name, selectedOrganization]);

  const createGitlabRepository = useCallback(async (): Promise<CreateRepositoryResponse> => {
    const gitlabGroup = (selectedOrganization as SelectOptionObjectType<"organization">).selectedOption;
    const repoResponse = await gitlabClient.createRepository({
      name,
      groupId: gitlabGroup.value,
      visibility: isPrivate ? "private" : "public",
    });
    if (!(repoResponse.status === 201)) {
      throw new Error(
        `Gitlab repository creation request failed with: ${repoResponse.status} ${repoResponse.statusText}`
      );
    }
    const repo = await repoResponse.json();
    if (!repo?.http_url_to_repo || !repo?.web_url) {
      throw new Error("Unexpected contents of the Gitlab repository creation response.");
    }
    return { cloneUrl: repo?.http_url_to_repo, htmlUrl: repo?.web_url };
  }, [gitlabClient, isPrivate, name, selectedOrganization]);

  const createGitHubRepository = useCallback(async (): Promise<CreateRepositoryResponse> => {
    const githubOrg = (selectedOrganization as SelectOptionObjectType<"organization">).selectedOption;
    const repo =
      selectedOrganization?.kind === "organization"
        ? await gitHubClient.repos.createInOrg({
            name,
            private: isPrivate,
            org: githubOrg.name,
          })
        : await gitHubClient.repos.createForAuthenticatedUser({ name, private: isPrivate });

    if (!repo.data.clone_url) {
      throw new Error("Repo creation failed.");
    }

    // The cloneUrl host is replaced with the authProvider domain because when GitHub is being proxied it will return
    // the original URL (with github.com) instead of the proxy URL.
    // This won't affect usaged when GitHub is not being proxied.
    const host = new URL(repo.data.clone_url).host;
    const cloneUrl = repo.data.clone_url.replace(host, authProvider?.domain ?? host);
    return { cloneUrl, htmlUrl: repo.data.html_url };
  }, [selectedOrganization, gitHubClient.repos, name, isPrivate, authProvider?.domain]);

  const pushEmptyCommitIntoBitbucket = useCallback(async (): Promise<void> => {
    if (selectedOrganization?.kind !== "organization") {
      throw new Error("No workspace was selected for Bitbucket Repository.");
    }
    const org = (selectedOrganization as SelectOptionObjectType<"organization">).selectedOption;
    // need an empty commit push through REST API first
    await bitbucketClient
      .pushEmptyCommit({
        repository: name,
        workspace: org.name,
        branch: props.workspace.origin.branch,
      })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`Initial commit push failed: ${response.status} ${response.statusText}`);
        }
      });
  }, [bitbucketClient, name, props.workspace.origin.branch, selectedOrganization]);

  const create = useCallback(async () => {
    try {
      if (!authInfo || !gitConfig || !isSupportedGitAuthProviderType(authProvider?.type)) {
        return;
      }

      const insecurelyDisableTlsCertificateValidation =
        authProvider?.group === AuthProviderGroup.GIT && authProvider.insecurelyDisableTlsCertificateValidation;

      const disableEncoding = authProvider?.group === AuthProviderGroup.GIT && authProvider.disableEncoding;

      setError(undefined);
      setLoading(true);

      const createRepositoryCommand: () => Promise<CreateRepositoryResponse> = switchExpression(authProvider?.type, {
        bitbucket: createBitbucketRepository,
        github: createGitHubRepository,
        gitlab: createGitlabRepository,
      });

      if (!createRepositoryCommand) {
        throw new Error("Undefined create repository command for auth type " + authProvider?.type);
      }
      const { cloneUrl, htmlUrl: websiteUrl } = await createRepositoryCommand();
      const initializeEmptyRepositoryCommand = switchExpression(authProvider?.type, {
        bitbucket: pushEmptyCommitIntoBitbucket,
        default: async (): Promise<void> => {},
      });
      await initializeEmptyRepositoryCommand();

      await workspaces.addRemote({
        workspaceId: props.workspace.workspaceId,
        url: cloneUrl,
        name: GIT_ORIGIN_REMOTE_NAME,
        force: true,
      });

      await workspaces.createSavePoint({
        workspaceId: props.workspace.workspaceId,
        gitConfig,
      });

      await workspaces.push({
        workspaceId: props.workspace.workspaceId,
        remote: GIT_ORIGIN_REMOTE_NAME,
        ref: props.workspace.origin.branch,
        remoteRef: `refs/heads/${props.workspace.origin.branch}`,
        force: switchExpression(authProvider?.type, {
          github: false,
          bitbucket: true,
          gitlab: true,
        }),
        authInfo,
        insecurelyDisableTlsCertificateValidation,
        disableEncoding,
      });

      await workspaces.initGitOnWorkspace({
        workspaceId: props.workspace.workspaceId,
        remoteUrl: new URL(cloneUrl),
        branch: props.workspace.origin.branch,
        insecurelyDisableTlsCertificateValidation,
        disableEncoding,
      });

      await workspaces.renameWorkspace({
        workspaceId: props.workspace.workspaceId,
        newName: new URL(websiteUrl).pathname.substring(1),
      });

      props.onClose();
      createRepositorySuccessAlert.show({ url: websiteUrl });
      props.onSuccess?.({ url: websiteUrl });
    } catch (err) {
      errorAlert.show();
      setError(err);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [
    authInfo,
    gitConfig,
    authProvider,
    createBitbucketRepository,
    createGitHubRepository,
    createGitlabRepository,
    pushEmptyCommitIntoBitbucket,
    workspaces,
    props,
    createRepositorySuccessAlert,
    errorAlert,
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

  if (!authProvider?.type || !isSupportedGitAuthProviderType(authProvider?.type)) {
    return <></>;
  }

  return (
    <Modal
      variant={ModalVariant.medium}
      aria-label={i18n.createGitRepositoryModal[authProvider.type].createRepository}
      isOpen={props.isOpen}
      onClose={() => {
        setError(undefined);
        props.onClose();
      }}
      title={i18n.createGitRepositoryModal[authProvider.type].createRepository}
      titleIconVariant={switchExpression(authProvider.type, {
        bitbucket: BitbucketIcon,
        github: GithubIcon,
        gitlab: GitlabIcon,
      })}
      description={i18n.createGitRepositoryModal[authProvider.type].description(props.workspace.name)}
      actions={[
        <Button
          isLoading={isLoading}
          key="create"
          variant="primary"
          onClick={create}
          isDisabled={switchExpression(authProvider.type, {
            bitbucket: !isNameValid || selectedOrganization === undefined,
            github: !isNameValid,
            gitlab: !isNameValid,
          })}
        >
          {i18n.createGitRepositoryModal.form.buttonCreate}
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
            <Alert
              variant="danger"
              title={i18n.createGitRepositoryModal[authProvider.type].error.formAlert(error)}
              isInline={true}
            />
            <br />
          </FormAlert>
        )}
        <FormGroup label={i18n.createGitRepositoryModal[authProvider.type].form.select.label} fieldId="organization">
          <LoadOrganizationsSelect
            workspace={props.workspace}
            onSelect={setSelectedOrganization}
            actionType="repository"
          />
          <FormHelperText>
            <HelperText>
              <HelperTextItem variant="default">
                {i18n.createGitRepositoryModal[authProvider.type].form.select.description}
              </HelperTextItem>
            </HelperText>
          </FormHelperText>
        </FormGroup>
        <FormGroup
          label={i18n.createGitRepositoryModal.form.nameField.label}
          isRequired={true}
          fieldId="repository-name"
        >
          <TextInput
            id={"repo-name"}
            validated={validated}
            isRequired={true}
            placeholder={i18n.createGitRepositoryModal.form.nameField.label}
            value={name}
            onChange={(_event, val) => setName(val)}
          />
          {validated === "error" ? (
            <FormHelperText>
              <HelperText>
                <HelperTextItem variant="error">{i18n.createGitRepositoryModal.form.nameField.hint}</HelperTextItem>
              </HelperText>
            </FormHelperText>
          ) : (
            <FormHelperText>
              <HelperText>
                <HelperTextItem variant="success"></HelperTextItem>
              </HelperText>
            </FormHelperText>
          )}
        </FormGroup>
        <Divider inset={{ default: "inset3xl" }} />
        <FormGroup fieldId="repo-visibility">
          <Radio
            isChecked={!isPrivate}
            id={"repository-public"}
            name={"repository-public"}
            label={
              <>
                <UsersIcon />
                &nbsp;&nbsp; {i18n.createGitRepositoryModal.form.visibility.public.label}
              </>
            }
            description={i18n.createGitRepositoryModal.form.visibility.public.description}
            onChange={() => setPrivate(false)}
          />
          <br />
          <Radio
            isChecked={isPrivate}
            id={"repository-private"}
            name={"repository-private"}
            label={
              <>
                <LockIcon />
                &nbsp;&nbsp; {i18n.createGitRepositoryModal.form.visibility.private.label}
              </>
            }
            description={i18n.createGitRepositoryModal.form.visibility.private.description}
            onChange={() => setPrivate(true)}
          />
        </FormGroup>
      </Form>
    </Modal>
  );
}
