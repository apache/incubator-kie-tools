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
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { basename } from "path";
import { useCallback, useEffect, useRef, useState } from "react";
import { useHistory } from "react-router";
import { EditorPageErrorPage } from "../editor/EditorPageErrorPage";
import { useRoutes } from "../navigation/Hooks";
import { QueryParams } from "../navigation/Routes";
import { OnlineEditorPage } from "../pageTemplate/OnlineEditorPage";
import { useQueryParam, useQueryParams } from "../queryParams/QueryParamsContext";
import {
  ImportableUrl,
  isCertainlyGit,
  isPotentiallyGit,
  isSingleFile,
  UrlType,
  useClonableUrl,
  useImportableUrl,
  useImportableUrlValidation,
} from "./ImportableUrlHooks";
import { AdvancedImportModal, AdvancedImportModalRef } from "./AdvancedImportModalContent";
import { fetchSingleFileContent } from "./fetchSingleFileContent";
import { useGitHubClient } from "../github/Hooks";
import { AccountsDispatchActionKind, useAccountsDispatch } from "../accounts/AccountsContext";
import { useAuthSession, useAuthSessions } from "../authSessions/AuthSessionsContext";
import { useAuthProviders } from "../authProviders/AuthProvidersContext";
import { getCompatibleAuthSessionWithUrlDomain } from "../authSessions/CompatibleAuthSessions";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { LocalFile } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/LocalFile";
import { encoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { WorkspaceKind } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import { PromiseStateStatus } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { AUTH_SESSION_NONE } from "../authSessions/AuthSessionApi";
import { useBitbucketClient } from "../bitbucket/Hooks";

export function NewWorkspaceFromUrlPage() {
  const workspaces = useWorkspaces();
  const routes = useRoutes();
  const history = useHistory();
  const accountsDispatch = useAccountsDispatch();

  const [importingError, setImportingError] = useState("");
  const queryParams = useQueryParams();
  const queryParamUrl = useQueryParam(QueryParams.URL);
  const queryParamBranch = useQueryParam(QueryParams.BRANCH);
  const queryParamAuthSessionId = useQueryParam(QueryParams.AUTH_SESSION_ID);
  const queryParamConfirm = useQueryParam(QueryParams.CONFIRM);

  const authProviders = useAuthProviders();
  const { authSessions, authSessionStatus } = useAuthSessions();
  const { authSession, gitConfig, authInfo } = useAuthSession(queryParamAuthSessionId);

  const importableUrl = useImportableUrl(queryParamUrl);
  const clonableUrlObject = useClonableUrl(queryParamUrl, authInfo, queryParamBranch);
  const { clonableUrl, selectedGitRefName, gitServerRefsPromise } = clonableUrlObject;

  const setAuthSessionId = useCallback(
    (newAuthSessionId: React.SetStateAction<string | undefined>) => {
      if (!newAuthSessionId) {
        history.replace({
          pathname: routes.import.path({}),
          search: queryParams.without(QueryParams.AUTH_SESSION_ID).toString(),
        });
        return;
      }
      history.replace({
        pathname: routes.import.path({}),
        search: queryParams
          .with(
            QueryParams.AUTH_SESSION_ID,
            typeof newAuthSessionId === "function" ? newAuthSessionId(queryParamAuthSessionId) : newAuthSessionId
          )
          .toString(),
      });

      accountsDispatch({ kind: AccountsDispatchActionKind.CLOSE });
    },
    [accountsDispatch, history, queryParamAuthSessionId, queryParams, routes.import]
  );

  const setUrl = useCallback(
    (newUrl) => {
      if (!newUrl) {
        history.replace({
          pathname: routes.import.path({}),
          search: queryParams.without(QueryParams.URL).toString(),
        });
        return;
      }
      history.replace({
        pathname: routes.import.path({}),
        search: queryParams
          .with(QueryParams.URL, typeof newUrl === "function" ? newUrl(queryParamUrl ?? "") : newUrl)
          .toString(),
      });
    },
    [history, queryParamUrl, queryParams, routes.import]
  );

  const setGitRefName = useCallback(
    (newGitRefName) => {
      if (!newGitRefName) {
        history.replace({
          pathname: routes.import.path({}),
          search: queryParams.without(QueryParams.BRANCH).toString(),
        });
        return;
      }
      history.replace({
        pathname: routes.import.path({}),
        search: queryParams
          .with(
            QueryParams.BRANCH,
            typeof newGitRefName === "function" ? newGitRefName(selectedGitRefName ?? "") : newGitRefName
          )
          .toString(),
      });
    },
    [history, queryParams, routes.import, selectedGitRefName]
  );

  // Startup the page. Only import if those are set.
  useEffect(() => {
    const urlDomain = importableUrl.url?.hostname;
    const { compatible } = getCompatibleAuthSessionWithUrlDomain({
      authProviders,
      authSessions,
      authSessionStatus,
      urlDomain,
    });
    setAuthSessionId(compatible[0].id);

    if (compatible[0].id === AUTH_SESSION_NONE.id && !selectedGitRefName) {
      history.replace({
        pathname: routes.import.path({}),
        search: queryParams.with(QueryParams.CONFIRM, "true").toString(),
      });
    } else {
      history.replace({
        pathname: routes.import.path({}),
        search: queryParams
          .without(QueryParams.CONFIRM)
          .with(QueryParams.BRANCH, selectedGitRefName)
          .with(QueryParams.AUTH_SESSION_ID, compatible[0].id)
          .toString(),
      });
    }
  }, [
    authProviders,
    authSessionStatus,
    authSessions,
    history,
    importableUrl.url?.hostname,
    queryParams,
    routes.import,
    selectedGitRefName,
    setAuthSessionId,
  ]);

  const cloneGitRepository: typeof workspaces.createWorkspaceFromGitRepository = useCallback(
    async (args) => {
      const res = await workspaces.createWorkspaceFromGitRepository(args);

      const { workspace, suggestedFirstFile } = res;

      if (!suggestedFirstFile) {
        history.replace({
          pathname: routes.home.path({}),
          search: routes.home.queryString({ expand: workspace.workspaceId }),
        });
        return res;
      }

      history.replace({
        pathname: routes.workspaceWithFilePath.path({
          workspaceId: workspace.workspaceId,
          fileRelativePath: suggestedFirstFile.relativePathWithoutExtension,
          extension: suggestedFirstFile.extension,
        }),
      });
      return res;
    },
    [routes, history, workspaces]
  );

  const createWorkspaceForFile = useCallback(
    async (file: LocalFile) => {
      workspaces
        .createWorkspaceFromLocal({
          localFiles: [file],
          gitAuthSessionId: AUTH_SESSION_NONE.id,
        })
        .then(({ workspace, suggestedFirstFile }) => {
          if (!suggestedFirstFile) {
            return;
          }
          history.replace({
            pathname: routes.workspaceWithFilePath.path({
              workspaceId: workspace.workspaceId,
              fileRelativePath: suggestedFirstFile.relativePathWithoutExtension,
              extension: suggestedFirstFile.extension,
            }),
          });
        });
    },
    [routes, history, workspaces]
  );

  const gitHubClient = useGitHubClient(authSession);
  const bitbucketClient = useBitbucketClient(authSession);

  const doImportAsSingleFile = useCallback(
    async (importableUrl: ImportableUrl) => {
      const singleFileContent = await fetchSingleFileContent(importableUrl, gitHubClient, bitbucketClient);

      if (singleFileContent.error) {
        setImportingError(singleFileContent.error);
        return;
      }

      await createWorkspaceForFile({
        path: basename(decodeURIComponent(singleFileContent.rawUrl!.pathname)),
        fileContents: encoder.encode(singleFileContent.content!),
      });
    },
    [bitbucketClient, createWorkspaceForFile, gitHubClient]
  );

  const doImport = useCallback(async () => {
    const singleFile = isSingleFile(importableUrl.type);

    try {
      if (queryParamAuthSessionId && !authSession) {
        setImportingError(`Auth session '${queryParamAuthSessionId}' not found.`);
        return;
      }

      if (!queryParamBranch && isCertainlyGit(importableUrl.type) && queryParamAuthSessionId !== AUTH_SESSION_NONE.id) {
        return;
      }

      if (clonableUrl.type === UrlType.INVALID || clonableUrl.type === UrlType.NOT_SUPPORTED) {
        setImportingError(clonableUrl.error);
        return;
      }

      //unknown
      else if (importableUrl.type === UrlType.UNKNOWN) {
        if (gitServerRefsPromise.data?.defaultBranch) {
          await cloneGitRepository({
            origin: {
              kind: WorkspaceKind.GIT,
              url: importableUrl.url.toString(),
              branch: selectedGitRefName ?? gitServerRefsPromise.data.defaultBranch,
            },
            gitConfig,
            authInfo,
            gitAuthSessionId: queryParamAuthSessionId,
          });
        } else {
          await doImportAsSingleFile(importableUrl);
        }
      }

      // git but not gist or snippet
      else if (
        importableUrl.type === UrlType.GITHUB_DOT_COM ||
        importableUrl.type === UrlType.BITBUCKET_DOT_ORG ||
        importableUrl.type === UrlType.GIT
      ) {
        if (gitServerRefsPromise.data?.defaultBranch) {
          await cloneGitRepository({
            origin: {
              kind: WorkspaceKind.GIT,
              url: importableUrl.url.toString(),
              branch: selectedGitRefName ?? gitServerRefsPromise.data.defaultBranch,
            },
            gitAuthSessionId: queryParamAuthSessionId,
            gitConfig,
            authInfo,
          });
        } else {
          setImportingError(`Can't clone. ${gitServerRefsPromise.error}`);
          return;
        }
      }

      // gist
      else if (importableUrl.type === UrlType.GIST_DOT_GITHUB_DOT_COM) {
        importableUrl.url.hash = "";

        if (gitServerRefsPromise.data?.defaultBranch) {
          await cloneGitRepository({
            origin: {
              kind: WorkspaceKind.GITHUB_GIST,
              url: importableUrl.url.toString(),
              branch: queryParamBranch ?? selectedGitRefName ?? gitServerRefsPromise.data.defaultBranch,
            },
            gitAuthSessionId: queryParamAuthSessionId,
            gitConfig,
            authInfo,
          });
        } else {
          setImportingError(`Can't clone. ${gitServerRefsPromise.error}`);
          return;
        }
      }

      // snippet
      else if (importableUrl.type === UrlType.BITBUCKET_DOT_ORG_SNIPPET) {
        importableUrl.url.hash = "";
        if (gitServerRefsPromise.data?.defaultBranch) {
          await cloneGitRepository({
            origin: {
              kind: WorkspaceKind.BITBUCKET_SNIPPET,
              url: importableUrl.url.toString(),
              branch: queryParamBranch ?? selectedGitRefName ?? gitServerRefsPromise.data.defaultBranch,
            },
            gitAuthSessionId: queryParamAuthSessionId,
            gitConfig,
            authInfo,
          });
        } else {
          setImportingError(`Can't clone. ${gitServerRefsPromise.error}`);
          return;
        }
      }

      // single file
      else if (singleFile) {
        doImportAsSingleFile(importableUrl);
      } else {
        throw new Error("Invalid UrlType " + importableUrl.type);
      }
    } catch (e) {
      console.error(e);
      setImportingError(e.toString());
      return;
    }
  }, [
    importableUrl,
    queryParamAuthSessionId,
    authSession,
    clonableUrl.type,
    clonableUrl.error,
    gitServerRefsPromise.data?.defaultBranch,
    gitServerRefsPromise.error,
    cloneGitRepository,
    selectedGitRefName,
    gitConfig,
    authInfo,
    doImportAsSingleFile,
    queryParamBranch,
  ]);

  useEffect(() => {
    if (!queryParamUrl) {
      history.replace({
        pathname: routes.import.path({}),
        search: queryParams.with(QueryParams.CONFIRM, "true").toString(),
      });
    }
  }, [history, queryParamUrl, queryParams, routes.import]);

  useEffect(() => {
    if ((!queryParamBranch || !queryParamAuthSessionId) && selectedGitRefName) {
      return;
    }

    if (gitServerRefsPromise.status === PromiseStateStatus.PENDING) {
      return;
    }

    if (!queryParamUrl || (isPotentiallyGit(importableUrl.type) && queryParamConfirm === "true")) {
      advancedImportModalRef.current?.open();
      return;
    }
    setImportingError("");
    doImport();
  }, [
    gitServerRefsPromise.status,
    doImport,
    queryParamConfirm,
    importableUrl.type,
    queryParamUrl,
    queryParamBranch,
    queryParamAuthSessionId,
    selectedGitRefName,
  ]);

  const validation = useImportableUrlValidation(authSession, queryParamUrl, queryParamBranch, clonableUrlObject);
  const advancedImportModalRef = useRef<AdvancedImportModalRef>(null);

  return (
    <>
      <OnlineEditorPage>
        <PageSection variant={"light"} isFilled={true} padding={{ default: "noPadding" }}>
          {importingError && (
            <EditorPageErrorPage
              title={`Can't import`}
              path={importableUrl.url?.toString() ?? ""}
              errors={[importingError]}
            />
          )}
          {!importingError && queryParamConfirm !== "true" && queryParamUrl && (
            <Bullseye>
              <TextContent>
                <Bullseye>
                  <Spinner />
                </Bullseye>
                <br />
                <Text component={TextVariants.p}>{`Importing '${queryParamUrl}'`}</Text>
              </TextContent>
            </Bullseye>
          )}
          {(queryParamConfirm === "true" || !queryParamUrl) && (
            <AdvancedImportModal
              ref={advancedImportModalRef}
              onSubmit={() => {
                history.replace({
                  pathname: routes.import.path({}),
                  search: queryParams.without(QueryParams.CONFIRM).toString(),
                });
              }}
              onClose={() => {
                history.push({ pathname: routes.home.path({}) });
              }}
              clonableUrl={clonableUrlObject}
              validation={validation}
              authSessionId={queryParamAuthSessionId}
              url={queryParamUrl ?? ""}
              gitRefName={selectedGitRefName ?? ""}
              setAuthSessionId={setAuthSessionId}
              setUrl={setUrl}
              setGitRefName={setGitRefName}
            />
          )}
        </PageSection>
      </OnlineEditorPage>
    </>
  );
}
