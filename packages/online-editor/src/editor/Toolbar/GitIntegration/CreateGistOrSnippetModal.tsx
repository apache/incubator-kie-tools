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

import * as React from "react";
import { useCallback, useState } from "react";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { Form, FormAlert, FormGroup, FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { Radio } from "@patternfly/react-core/dist/js/components/Radio";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { UsersIcon } from "@patternfly/react-icons/dist/js/icons/users-icon";
import { LockIcon } from "@patternfly/react-icons/dist/js/icons/lock-icon";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { GIST_ORIGIN_REMOTE_NAME } from "@kie-tools-core/workspaces-git-fs/dist/constants/GitConstants";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { useAuthSession } from "../../../authSessions/AuthSessionsContext";
import { useBitbucketClient } from "../../../bitbucket/Hooks";
import { BitbucketIcon } from "@patternfly/react-icons/dist/js/icons/bitbucket-icon";
import { GithubIcon } from "@patternfly/react-icons/dist/js/icons/github-icon";
import { useGitHubClient } from "../../../github/Hooks";
import {
  AuthProviderType,
  GistEnabledAuthProviderType,
  isGistEnabledAuthProviderType,
  isSupportedGitAuthProviderType,
} from "../../../authProviders/AuthProvidersApi";
import { useAuthProvider } from "../../../authProviders/AuthProvidersContext";
import { switchExpression } from "../../../switchExpression/switchExpression";
import { useOnlineI18n } from "../../../i18n";
import { LoadOrganizationsSelect, SelectOptionObjectType } from "./LoadOrganizationsSelect";
import { useGitIntegration } from "./GitIntegrationContextProvider";
import { useEnv } from "../../../env/hooks/EnvContext";

export interface CreateGistOrSnippetResponse {
  cloneUrl: string;
  htmlUrl: string;
}
export const CreateGistOrSnippetModal = (props: {
  workspace: WorkspaceDescriptor;
  isOpen: boolean;
  onClose: () => void;
  onSuccess?: (args: { url: string }) => void;
  onError?: () => void;
}) => {
  const { env } = useEnv();
  const workspaces = useWorkspaces();
  const { authSession, gitConfig, authInfo } = useAuthSession(props.workspace.gitAuthSessionId);
  const authProvider = useAuthProvider(authSession);
  const bitbucketClient = useBitbucketClient(authSession);
  const gitHubClient = useGitHubClient(authSession);

  const [isPrivate, setPrivate] = useState(false);
  const [error, setError] = useState<string | undefined>(undefined);
  const { i18n } = useOnlineI18n();
  const [selectedOrganization, setSelectedOrganization] = useState<SelectOptionObjectType>();
  const [isGistOrSnippetLoading, setGistOrSnippetLoading] = useState(false);

  const {
    alerts: { successfullyUpdatedGistOrSnippetAlert, errorAlert },
  } = useGitIntegration();

  const createGitHubGist: () => Promise<CreateGistOrSnippetResponse> = useCallback(async () => {
    const gist = await gitHubClient.gists.create({
      description: props.workspace.name ?? "",
      public: !isPrivate,

      // This file is used just for creating the Gist. The `push -f` overwrites it.
      files: {
        "README.md": {
          content: `
This Gist was created from ${env.KIE_SANDBOX_APP_NAME}.

This file is temporary and you should not be seeing it.
If you are, it means that creating this Gist failed and it can safely be deleted.
`,
        },
      },
    });

    if (!gist.data.git_push_url || !gist.data.html_url) {
      throw new Error("Gist creation failed.");
    }
    return { cloneUrl: gist.data.git_push_url, htmlUrl: gist.data.html_url };
  }, [env.KIE_SANDBOX_APP_NAME, gitHubClient.gists, isPrivate, props.workspace.name]);

  const createBitbucketSnippet: () => Promise<CreateGistOrSnippetResponse> = useCallback(async () => {
    if (selectedOrganization?.kind !== "organization") {
      throw new Error("No workspace was selected for Bitbucket Snippet.");
    }
    const response = await bitbucketClient.createSnippet({
      workspace: selectedOrganization.value,
      title: props.workspace.name ?? `${env.KIE_SANDBOX_APP_NAME} Snippet`,
      files: {
        "README.md": {
          content: `
This Snippet was created from ${env.KIE_SANDBOX_APP_NAME}.

This file is temporary and you should not be seeing it.
If you are, it means that creating this Snippet failed and it can safely be deleted.
`,
        },
      },
      isPrivate,
    });
    const json = await response.json();

    if (!json.links || !json.links.clone) {
      throw new Error("Unexpected contents of the snippet creation request.");
    }

    const cloneLinks: any[] = json.links.clone;
    const cloneUrl = cloneLinks.filter((e) => {
      return (e.name = "https" && e.href.startsWith("https"));
    })[0].href;

    return { cloneUrl, htmlUrl: json.links.html };
  }, [bitbucketClient, env.KIE_SANDBOX_APP_NAME, isPrivate, props.workspace.name, selectedOrganization]);

  const createGistOrSnippet = useCallback(async () => {
    try {
      if (!authInfo || !isGistEnabledAuthProviderType(authProvider?.type)) {
        return;
      }
      const gistEnabledAuthProvider = authProvider?.type as GistEnabledAuthProviderType;
      setGistOrSnippetLoading(true);

      const createGistOrSnippetCommand: () => Promise<CreateGistOrSnippetResponse> = switchExpression(
        gistEnabledAuthProvider,
        {
          github: createGitHubGist,
          bitbucket: createBitbucketSnippet,
        }
      );
      const gistOrSnippet = await createGistOrSnippetCommand();

      const gistOrSnippetDefaultBranch = (
        await workspaces.getGitServerRefs({
          url: new URL(gistOrSnippet.cloneUrl).toString(),
          authInfo,
        })
      )
        .find((serverRef) => serverRef.ref === "HEAD")!
        .target!.replace("refs/heads/", "");

      const initWorkspaceCommand: (args: { workspaceId: string; remoteUrl: URL; branch: string }) => Promise<void> =
        switchExpression(gistEnabledAuthProvider, {
          github: workspaces.initGistOnWorkspace,
          bitbucket: workspaces.initSnippetOnWorkspace,
        });
      await initWorkspaceCommand({
        workspaceId: props.workspace.workspaceId,
        remoteUrl: new URL(gistOrSnippet.cloneUrl),
        branch: gistOrSnippetDefaultBranch,
      });

      await workspaces.addRemote({
        workspaceId: props.workspace.workspaceId,
        url: gistOrSnippet.cloneUrl,
        name: GIST_ORIGIN_REMOTE_NAME,
        force: true,
      });

      await workspaces.branch({
        workspaceId: props.workspace.workspaceId,
        checkout: true,
        name: gistOrSnippetDefaultBranch,
      });

      await workspaces.createSavePoint({
        workspaceId: props.workspace.workspaceId,
        gitConfig,
      });
      await workspaces.push({
        workspaceId: props.workspace.workspaceId,
        remote: GIST_ORIGIN_REMOTE_NAME,
        ref: gistOrSnippetDefaultBranch,
        remoteRef: `refs/heads/${gistOrSnippetDefaultBranch}`,
        force: true,
        authInfo,
      });
      await workspaces.pull({
        workspaceId: props.workspace.workspaceId,
        authInfo,
      });

      props.onClose();
      successfullyUpdatedGistOrSnippetAlert.show({ url: gistOrSnippet.cloneUrl });
      props.onSuccess?.({ url: gistOrSnippet.cloneUrl });
      return;
    } catch (err) {
      setError(err);
      errorAlert.show();
      props.onError?.();
      throw err;
    } finally {
      setGistOrSnippetLoading(false);
    }
  }, [
    authInfo,
    authProvider?.type,
    createGitHubGist,
    createBitbucketSnippet,
    workspaces,
    props,
    gitConfig,
    successfullyUpdatedGistOrSnippetAlert,
    errorAlert,
  ]);

  if (!authProvider?.type || !isSupportedGitAuthProviderType(authProvider?.type)) {
    return <></>;
  }
  return (
    <Modal
      variant={ModalVariant.medium}
      aria-label={i18n.createGistOrSnippetModal[authProvider.type].create}
      isOpen={props.isOpen}
      onClose={props.onClose}
      title={i18n.createGistOrSnippetModal[authProvider.type].create}
      titleIconVariant={switchExpression(authProvider.type, {
        bitbucket: BitbucketIcon,
        github: GithubIcon,
      })}
      description={i18n.createGistOrSnippetModal[authProvider.type].description(props.workspace.name)}
      actions={[
        <Button
          isLoading={isGistOrSnippetLoading}
          key="create"
          variant="primary"
          onClick={createGistOrSnippet}
          isDisabled={selectedOrganization === undefined}
        >
          {i18n.createGistOrSnippetModal.form.buttonCreate}
        </Button>,
      ]}
    >
      <br />
      <Form
        style={{ padding: "0 16px 0 16px" }}
        onSubmit={(e) => {
          e.preventDefault();
          e.stopPropagation();

          return createGistOrSnippet();
        }}
      >
        {error && (
          <FormAlert>
            <Alert
              variant="danger"
              title={i18n.createGistOrSnippetModal[authProvider.type].error.formAlert(error)}
              isInline={true}
            />
            <br />
          </FormAlert>
        )}
        <FormGroup
          label={i18n.createGistOrSnippetModal[authProvider.type].form.select.label}
          helperText={i18n.createGistOrSnippetModal[authProvider.type].form.select.description}
          fieldId="organization"
        >
          <LoadOrganizationsSelect
            workspace={props.workspace}
            onSelect={setSelectedOrganization}
            readonly={authProvider.type === AuthProviderType.github}
          />
        </FormGroup>
        <FormGroup
          helperText={<FormHelperText icon={<CheckCircleIcon />} isHidden={false} style={{ visibility: "hidden" }} />}
          helperTextInvalidIcon={<ExclamationCircleIcon />}
          fieldId="gist-or-snippet-visibility"
        >
          <Radio
            isChecked={!isPrivate}
            id={"gist-or-snippet-public"}
            name={"gist-or-snippet-public"}
            label={
              <>
                <UsersIcon />
                &nbsp;&nbsp; {i18n.createGistOrSnippetModal.form.visibility.public.label}
              </>
            }
            description={i18n.createGistOrSnippetModal.form.visibility.public.description}
            onChange={() => setPrivate(false)}
          />
          <br />
          <Radio
            isChecked={isPrivate}
            id={"gist-or-snippet-private"}
            name={"gist-or-snippet-private"}
            label={
              <>
                <LockIcon />
                &nbsp;&nbsp; {i18n.createGistOrSnippetModal.form.visibility.private.label}
              </>
            }
            description={i18n.createGistOrSnippetModal.form.visibility.private.description}
            onChange={() => setPrivate(true)}
          />
        </FormGroup>
      </Form>
    </Modal>
  );
};
