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

import React, {
  ReactNode,
  SetStateAction,
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { AuthSessionSelectFilter } from "../../../authSessions/AuthSessionSelect";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { useAuthSession } from "../../../authSessions/AuthSessionsContext";
import { useAuthProvider } from "../../../authProviders/AuthProvidersContext";
import { useGitHubClient } from "../../../github/Hooks";
import { useBitbucketClient } from "../../../bitbucket/Hooks";
import {
  WorkspaceKind,
  isGistLikeWorkspaceKind,
} from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import {
  authSessionsSelectFilterCompatibleWithGistOrSnippetUrlDomain,
  authSessionsSelectFilterCompatibleWithGitUrlDomain,
  gitAuthSessionSelectFilter,
} from "../../../authSessions/CompatibleAuthSessions";
import type { RestEndpointMethodTypes as OctokitRestEndpointMethodTypes } from "@octokit/plugin-rest-endpoint-methods/dist-types/generated/parameters-and-response-types";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { ImportableUrl, UrlType, useImportableUrl } from "../../../importFromUrl/ImportableUrlHooks";
import {
  GIST_ORIGIN_REMOTE_NAME,
  GIT_ORIGIN_REMOTE_NAME,
} from "@kie-tools-core/workspaces-git-fs/dist/constants/GitConstants";
import { useNavigationBlockersBypass, useRoutes } from "../../../navigation/Hooks";
import { useHistory } from "react-router";
import { useGitIntegrationAlerts } from "./GitIntegrationAlerts";
import { AuthProviderGroup, isGistEnabledAuthProviderType } from "../../../authProviders/AuthProvidersApi";
import { useEditorToolbarDispatchContext } from "../EditorToolbarContextProvider";

export type GitIntegrationContextType = {
  workspaceImportableUrl: ImportableUrl;
  git: {
    canPushToGitRepository: boolean;
    canCreateGitRepository: boolean;
    pushNewBranch: (newBranchName: string) => Promise<void>;
    pushToGitRepository: () => Promise<void>;
    pullFromGitRepository: (args: { showAlerts: boolean }) => Promise<void>;
  };
  gistOrSnippet: {
    canCreateGistOrSnippet: boolean;
    canUpdateGistOrSnippet: boolean;
    canForkGitHubGist: boolean;
    forceUpdateGistOrSnippet: () => Promise<void>;
    updateGistOrSnippet: () => Promise<void>;
    forkGitHubGist: () => Promise<void>;
  };
  auth: {
    changeGitAuthSessionId: (
      newGitAuthSessionId: SetStateAction<string | undefined>,
      lastAuthSessionId: string | undefined
    ) => void;
    authSessionSelectFilter: AuthSessionSelectFilter;
  };
  alerts: ReturnType<typeof useGitIntegrationAlerts>;
};

export const GitIntegrationContext = createContext<GitIntegrationContextType>({} as any);

export type GitIntegrationContextProviderProps = {
  children: ReactNode;
  workspace: ActiveWorkspace;
};

export function GitIntegrationContextProvider(props: GitIntegrationContextProviderProps) {
  const workspaces = useWorkspaces();
  const routes = useRoutes();
  const history = useHistory();
  const { authSession, authInfo, gitConfig } = useAuthSession(props.workspace.descriptor.gitAuthSessionId);
  const authProvider = useAuthProvider(authSession);
  const workspaceImportableUrl = useImportableUrl(props.workspace.descriptor.origin.url?.toString());
  const alerts = useGitIntegrationAlerts(props.workspace);
  const navigationBlockersBypass = useNavigationBlockersBypass();
  const { setSyncGistOrSnippetDropdownOpen } = useEditorToolbarDispatchContext();

  const gitHubClient = useGitHubClient(authSession);
  const bitbucketClient = useBitbucketClient(authSession);

  const [isGistOrSnippetLoading, setGistOrSnippetLoading] = useState(false);
  const [gitHubGist, setGitHubGist] = useState<
    OctokitRestEndpointMethodTypes["gists"]["get"]["response"]["data"] | undefined
  >(undefined);
  const [bitbucketSnippet, setBitbucketSnippet] = useState<any>(undefined);

  const insecurelyDisableTlsCertificateValidation = useMemo(() => {
    if (authProvider?.group === AuthProviderGroup.GIT) {
      return authProvider.insecurelyDisableTlsCertificateValidation;
    }
    return props.workspace.descriptor.gitInsecurelyDisableTlsCertificateValidation;
  }, [authProvider, props.workspace]);

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (gitHubGist || workspaceImportableUrl.type !== UrlType.GIST_DOT_GITHUB_DOT_COM) {
          return;
        }

        const { gistId } = workspaceImportableUrl;

        if (!gistId) {
          return;
        }

        gitHubClient.gists.get({ gist_id: gistId }).then(({ data: gist }) => {
          if (canceled.get()) {
            return;
          }

          if (gist) {
            setGitHubGist(gist);
          }
        });
      },
      [gitHubGist, workspaceImportableUrl, gitHubClient.gists]
    )
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (bitbucketSnippet || workspaceImportableUrl.type !== UrlType.BITBUCKET_DOT_ORG_SNIPPET) {
          return;
        }

        const { snippetId, org } = workspaceImportableUrl;

        if (!snippetId || !org) {
          return;
        }

        bitbucketClient.getSnippet({ workspace: org, snippetId }).then(async (response) => {
          if (canceled.get()) {
            return;
          }
          const json = await response.json();
          setBitbucketSnippet(json);
        });
      },
      [bitbucketSnippet, workspaceImportableUrl, bitbucketClient]
    )
  );

  const authSessionSelectFilter = useMemo(() => {
    if (!props.workspace) {
      return gitAuthSessionSelectFilter();
    }

    if (props.workspace.descriptor.origin.kind === WorkspaceKind.LOCAL) {
      return gitAuthSessionSelectFilter();
    }

    if (props.workspace.descriptor.origin.kind === WorkspaceKind.GIT) {
      return authSessionsSelectFilterCompatibleWithGitUrlDomain(new URL(props.workspace.descriptor.origin.url).host);
    }

    if (props.workspace.descriptor.origin.kind === WorkspaceKind.GITHUB_GIST) {
      return authSessionsSelectFilterCompatibleWithGistOrSnippetUrlDomain(
        new URL(props.workspace.descriptor.origin.url).host,
        gitHubGist?.owner?.login
      );
    }

    if (props.workspace.descriptor.origin.kind === WorkspaceKind.BITBUCKET_SNIPPET) {
      return authSessionsSelectFilterCompatibleWithGistOrSnippetUrlDomain(
        new URL(props.workspace.descriptor.origin.url).host,
        bitbucketSnippet?.owner?.login
      );
    }

    return gitAuthSessionSelectFilter();
  }, [bitbucketSnippet?.owner?.login, gitHubGist?.owner?.login, props.workspace]);

  const changeGitAuthSessionId = useCallback(
    (newGitAuthSessionId: React.SetStateAction<string | undefined>, lastAuthSessionId: string | undefined) => {
      workspaces.changeGitAuthSessionId({
        workspaceId: props.workspace.descriptor.workspaceId,
        gitAuthSessionId:
          typeof newGitAuthSessionId === "function" ? newGitAuthSessionId(lastAuthSessionId) : newGitAuthSessionId,
        insecurelyDisableTlsCertificateValidation,
      });
    },
    [props.workspace.descriptor.workspaceId, workspaces, insecurelyDisableTlsCertificateValidation]
  );

  const workspaceHasNestedDirectories = useMemo(
    () => props.workspace.files.filter((f) => f.relativePath !== f.name).length !== 0,
    [props.workspace]
  );

  const canCreateGitHubRepository = useMemo(() => authProvider?.type === "github", [authProvider?.type]);

  const canCreateBitbucketRepository = useMemo(() => authProvider?.type === "bitbucket", [authProvider?.type]);

  const canCreateGitRepository = useMemo(
    () => canCreateGitHubRepository || canCreateBitbucketRepository,
    [canCreateGitHubRepository, canCreateBitbucketRepository]
  );

  const canPushToGitRepository = useMemo(
    () => authSession?.type === "git" && !!authProvider,
    [authProvider, authSession?.type]
  );

  const isGitHubGistOwner = useMemo(() => {
    return authInfo?.username && gitHubGist?.owner?.login === authInfo.username;
  }, [authInfo, gitHubGist]);

  const isBitbucketSnippetOwner = useMemo(() => {
    return authInfo?.uuid && bitbucketSnippet?.creator.uuid === authInfo.uuid;
  }, [authInfo, bitbucketSnippet]);

  const isGistOrSnippetOwner = isGitHubGistOwner || isBitbucketSnippetOwner;

  const canCreateGistOrSnippet = useMemo(
    () =>
      Boolean(
        authProvider &&
          isGistEnabledAuthProviderType(authProvider?.type) &&
          props.workspace.descriptor.origin.kind === WorkspaceKind.LOCAL &&
          !workspaceHasNestedDirectories
      ),
    [authProvider, props.workspace.descriptor.origin.kind, workspaceHasNestedDirectories]
  );

  const canUpdateGistOrSnippet = useMemo(
    () =>
      Boolean(
        authProvider &&
          isGistEnabledAuthProviderType(authProvider?.type) &&
          !!isGistOrSnippetOwner &&
          props.workspace &&
          isGistLikeWorkspaceKind(props.workspace.descriptor.origin.kind) &&
          !workspaceHasNestedDirectories
      ),
    [authProvider, isGistOrSnippetOwner, props.workspace, workspaceHasNestedDirectories]
  );

  const canForkGitHubGist = useMemo(
    () =>
      authProvider?.type === "github" &&
      !isGistOrSnippetOwner &&
      props.workspace.descriptor.origin.kind === WorkspaceKind.GITHUB_GIST &&
      !workspaceHasNestedDirectories,
    [authProvider?.type, isGistOrSnippetOwner, props.workspace.descriptor.origin.kind, workspaceHasNestedDirectories]
  );

  const pushNewBranch = useCallback(
    async (newBranchName: string) => {
      if (!authInfo || !props.workspace) {
        return;
      }

      try {
        alerts.pushingAlert.show();

        await workspaces.createSavePoint({
          workspaceId: props.workspace.descriptor.workspaceId,
          gitConfig,
        });

        await workspaces.branch({
          workspaceId: props.workspace.descriptor.workspaceId,
          checkout: false,
          name: newBranchName,
        });

        await workspaces.push({
          workspaceId: props.workspace.descriptor.workspaceId,
          remote: GIT_ORIGIN_REMOTE_NAME,
          remoteRef: `refs/heads/${newBranchName}`,
          ref: newBranchName,
          force: false,
          authInfo,
          insecurelyDisableTlsCertificateValidation,
        });

        history.push({
          pathname: routes.import.path({}),
          search: routes.import.queryString({
            url: `${props.workspace.descriptor.origin.url}`,
            branch: newBranchName,
            authSessionId: props.workspace.descriptor.gitAuthSessionId,
          }),
        });
      } catch (e) {
        alerts.pushErrorAlert.show();
      } finally {
        alerts.pushingAlert.close();
      }
    },
    [
      authInfo,
      props.workspace,
      alerts.pushingAlert,
      alerts.pushErrorAlert,
      workspaces,
      gitConfig,
      history,
      routes.import,
      insecurelyDisableTlsCertificateValidation,
    ]
  );

  const pullFromGitRepository = useCallback(
    async (args: { showAlerts: boolean }) => {
      alerts.pullingAlert.close();
      alerts.pullErrorAlert.close();
      alerts.pullSuccessAlert.close();

      if (args.showAlerts) {
        alerts.pullingAlert.show();
      }
      await workspaces.createSavePoint({
        workspaceId: props.workspace.descriptor.workspaceId,
        gitConfig,
      });

      try {
        await workspaces.pull({
          workspaceId: props.workspace.descriptor.workspaceId,
          authInfo,
          insecurelyDisableTlsCertificateValidation,
        });

        if (args.showAlerts) {
          alerts.pullSuccessAlert.show();
        }
      } catch (e) {
        console.error(e);
        if (args.showAlerts) {
          const randomString = (Math.random() + 1).toString(36).substring(7);
          const newBranchName = `${props.workspace.descriptor.origin.branch}-${randomString}`;
          alerts.pullErrorAlert.show({ newBranchName, canPushToGitRepository, pushNewBranch });
        }
      } finally {
        if (args.showAlerts) {
          alerts.pullingAlert.close();
        }
      }
    },
    [
      alerts.pullingAlert,
      alerts.pullErrorAlert,
      alerts.pullSuccessAlert,
      workspaces,
      props.workspace.descriptor.workspaceId,
      props.workspace.descriptor.origin.branch,
      gitConfig,
      authInfo,
      canPushToGitRepository,
      pushNewBranch,
      insecurelyDisableTlsCertificateValidation,
    ]
  );

  const pushToGitRepository = useCallback(async () => {
    alerts.pushingAlert.close();
    alerts.pushErrorAlert.close();
    alerts.pushSuccessAlert.close();

    if (!authInfo) {
      return;
    }

    alerts.pushingAlert.show();
    try {
      const workspaceId = props.workspace.descriptor.workspaceId;
      await workspaces.createSavePoint({
        workspaceId: workspaceId,
        gitConfig,
      });

      const workspace = await workspaces.getWorkspace({ workspaceId });
      await workspaces.push({
        workspaceId: props.workspace.descriptor.workspaceId,
        ref: workspace.origin.branch,
        remote: GIST_ORIGIN_REMOTE_NAME,
        remoteRef: `refs/heads/${workspace.origin.branch}`,
        force: false,
        authInfo,
        insecurelyDisableTlsCertificateValidation,
      });
      await pullFromGitRepository({ showAlerts: false });
      alerts.pushSuccessAlert.show();
    } catch (e) {
      console.error(e);
      alerts.pushErrorAlert.show();
    } finally {
      alerts.pushingAlert.close();
    }
  }, [
    alerts.pushingAlert,
    alerts.pushErrorAlert,
    alerts.pushSuccessAlert,
    authInfo,
    props.workspace.descriptor.workspaceId,
    workspaces,
    gitConfig,
    pullFromGitRepository,
    insecurelyDisableTlsCertificateValidation,
  ]);

  const forceUpdateGistOrSnippet = useCallback(async () => {
    try {
      if (!authInfo || !props.workspace) {
        return;
      }

      setGistOrSnippetLoading(true);

      await workspaces.push({
        workspaceId: props.workspace.descriptor.workspaceId,
        remote: GIST_ORIGIN_REMOTE_NAME,
        ref: props.workspace.descriptor.origin.branch,
        remoteRef: `refs/heads/${props.workspace.descriptor.origin.branch}`,
        force: true,
        authInfo,
      });

      await workspaces.pull({
        workspaceId: props.workspace.descriptor.workspaceId,
        authInfo,
        insecurelyDisableTlsCertificateValidation,
      });
    } catch (e) {
      alerts.errorAlert.show();
    } finally {
      setGistOrSnippetLoading(false);
      setSyncGistOrSnippetDropdownOpen(false);
    }

    alerts.successfullyUpdatedGistOrSnippetAlert.show();
  }, [
    alerts.errorAlert,
    alerts.successfullyUpdatedGistOrSnippetAlert,
    authInfo,
    props.workspace,
    setSyncGistOrSnippetDropdownOpen,
    workspaces,
    insecurelyDisableTlsCertificateValidation,
  ]);

  const updateGistOrSnippet = useCallback(async () => {
    try {
      if (
        !authInfo ||
        !props.workspace ||
        !isGistEnabledAuthProviderType(authProvider?.type) ||
        !isGistLikeWorkspaceKind(props.workspace.descriptor.origin.kind)
      ) {
        return;
      }
      setGistOrSnippetLoading(true);

      await workspaces.createSavePoint({
        workspaceId: props.workspace.descriptor.workspaceId,
        gitConfig,
      });

      await workspaces.push({
        workspaceId: props.workspace.descriptor.workspaceId,
        remote: GIST_ORIGIN_REMOTE_NAME,
        ref: props.workspace.descriptor.origin.branch,
        remoteRef: `refs/heads/${props.workspace.descriptor.origin.branch}`,
        force: false,
        authInfo,
        insecurelyDisableTlsCertificateValidation,
      });

      await workspaces.pull({
        workspaceId: props.workspace.descriptor.workspaceId,
        authInfo,
      });
    } catch (e) {
      alerts.errorPushingGistOrSnippet.show({ forceUpdateGistOrSnippet });
      throw e;
    } finally {
      setGistOrSnippetLoading(false);
      setSyncGistOrSnippetDropdownOpen(false);
    }

    alerts.successfullyUpdatedGistOrSnippetAlert.show();
  }, [
    alerts.successfullyUpdatedGistOrSnippetAlert,
    alerts.errorPushingGistOrSnippet,
    authInfo,
    props.workspace,
    authProvider?.type,
    workspaces,
    gitConfig,
    forceUpdateGistOrSnippet,
    setSyncGistOrSnippetDropdownOpen,
    insecurelyDisableTlsCertificateValidation,
  ]);

  const forkGitHubGist = useCallback(async () => {
    try {
      if (!authSession || !authInfo || !gitHubGist?.id || !props.workspace) {
        return;
      }

      setGistOrSnippetLoading(true);

      // Fork Gist
      const gist = await gitHubClient.gists.fork({
        gist_id: gitHubGist.id,
      });

      const remoteName = gist.data.id;

      // Adds forked gist remote to current one
      await workspaces.addRemote({
        workspaceId: props.workspace.descriptor.workspaceId,
        url: gist.data.git_push_url,
        name: remoteName,
        force: true,
      });

      // Commit
      await workspaces.createSavePoint({
        workspaceId: props.workspace.descriptor.workspaceId,
        gitConfig,
      });

      // Push to forked gist remote
      await workspaces.push({
        workspaceId: props.workspace.descriptor.workspaceId,
        remote: remoteName,
        ref: props.workspace.descriptor.origin.branch,
        remoteRef: `refs/heads/${props.workspace.descriptor.origin.branch}`,
        force: true,
        authInfo,
        insecurelyDisableTlsCertificateValidation,
      });

      // Redirect to import workspace
      navigationBlockersBypass.execute(() => {
        history.push({
          pathname: routes.import.path({}),
          search: routes.import.queryString({
            url: gist.data.html_url,
            authSessionId: authSession.id,
          }),
        });
      });
    } catch (err) {
      alerts.errorAlert.show();
      throw err;
    } finally {
      setGistOrSnippetLoading(false);
    }
  }, [
    authSession,
    authInfo,
    gitHubGist,
    props.workspace,
    gitHubClient.gists,
    workspaces,
    gitConfig,
    navigationBlockersBypass,
    history,
    routes.import,
    alerts.errorAlert,
    insecurelyDisableTlsCertificateValidation,
  ]);

  useEffect(() => {
    if (isGistOrSnippetLoading) {
      alerts.loadingGistOrSnippetAlert.show();
    } else {
      alerts.loadingGistOrSnippetAlert.close();
    }
  }, [alerts.loadingGistOrSnippetAlert, isGistOrSnippetLoading]);

  const value = useMemo(
    () => ({
      workspaceImportableUrl,
      git: {
        canPushToGitRepository,
        canCreateGitRepository,
        pushNewBranch,
        pushToGitRepository,
        pullFromGitRepository,
      },
      gistOrSnippet: {
        canCreateGistOrSnippet,
        canUpdateGistOrSnippet,
        canForkGitHubGist,
        forceUpdateGistOrSnippet,
        updateGistOrSnippet,
        forkGitHubGist,
      },
      auth: {
        authSessionSelectFilter,
        changeGitAuthSessionId,
      },
      alerts,
    }),
    [
      workspaceImportableUrl,
      canPushToGitRepository,
      canCreateGitRepository,
      pushNewBranch,
      pushToGitRepository,
      pullFromGitRepository,
      canCreateGistOrSnippet,
      canUpdateGistOrSnippet,
      canForkGitHubGist,
      forceUpdateGistOrSnippet,
      updateGistOrSnippet,
      forkGitHubGist,
      authSessionSelectFilter,
      changeGitAuthSessionId,
      alerts,
    ]
  );

  return <GitIntegrationContext.Provider value={value}>{props.children}</GitIntegrationContext.Provider>;
}

export function useGitIntegration() {
  return useContext(GitIntegrationContext);
}
