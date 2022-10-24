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
import { AuthSourceKeys, useAuthSources, useSelectedAuthInfo } from "../authSources/AuthSourceHooks";
import { EditorPageErrorPage } from "../editor/EditorPageErrorPage";
import { useRoutes } from "../navigation/Hooks";
import { QueryParams } from "../navigation/Routes";
import { OnlineEditorPage } from "../pageTemplate/OnlineEditorPage";
import { useQueryParam, useQueryParams } from "../queryParams/QueryParamsContext";
import { AuthStatus, useSettings, useSettingsDispatch } from "../settings/SettingsContext";
import { encoder } from "../workspace/encoderdecoder/EncoderDecoder";
import { PromiseStateStatus } from "../workspace/hooks/PromiseState";
import { LocalFile } from "../workspace/worker/api/LocalFile";
import { WorkspaceKind } from "../workspace/worker/api/WorkspaceOrigin";
import { useWorkspaces } from "../workspace/WorkspacesContext";
import {
  isPotentiallyGit,
  isSingleFile,
  UrlType,
  useClonableUrl,
  useImportableUrl,
  useImportableUrlValidation,
} from "./ImportableUrlHooks";
import { AdvancedImportModal, AdvancedImportModalRef } from "./AdvancedImportModalContent";

export function NewWorkspaceFromUrlPage() {
  const workspaces = useWorkspaces();
  const routes = useRoutes();
  const history = useHistory();
  const authSources = useAuthSources();
  const settingsDispatch = useSettingsDispatch();

  const [importingError, setImportingError] = useState("");

  const queryParams = useQueryParams();

  const queryParamUrl = useQueryParam(QueryParams.URL);
  const queryParamBranch = useQueryParam(QueryParams.BRANCH);
  const queryParamAuthSource = useQueryParam(QueryParams.AUTH_SOURCE);
  const queryParamConfirm = useQueryParam(QueryParams.CONFIRM);

  const { authInfo, authSource } = useSelectedAuthInfo(queryParamAuthSource);

  const importableUrl = useImportableUrl(queryParamUrl);
  const clonableUrlObject = useClonableUrl(queryParamUrl, authSource, queryParamBranch);
  const { clonableUrl, selectedGitRefName: selectedGitRef, gitRefsPromise } = clonableUrlObject;

  const setAuthSource = useCallback(
    (newAuthSource) => {
      if (!newAuthSource) {
        history.replace({
          pathname: routes.import.path({}),
          search: queryParams.without(QueryParams.AUTH_SOURCE).toString(),
        });
        return;
      }
      history.replace({
        pathname: routes.import.path({}),
        search: queryParams
          .with(
            QueryParams.AUTH_SOURCE,
            typeof newAuthSource === "function" ? newAuthSource(authSource) : newAuthSource
          )
          .toString(),
      });
    },
    [authSource, history, queryParams, routes.import]
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

  const setBranch = useCallback(
    (newBranch) => {
      if (!newBranch) {
        history.replace({
          pathname: routes.import.path({}),
          search: queryParams.without(QueryParams.BRANCH).toString(),
        });
        return;
      }
      history.replace({
        pathname: routes.import.path({}),
        search: queryParams
          .with(QueryParams.BRANCH, typeof newBranch === "function" ? newBranch(selectedGitRef ?? "") : newBranch)
          .toString(),
      });
    },
    [history, queryParams, routes.import, selectedGitRef]
  );

  // Startup the page. Only import if those are set.
  useEffect(() => {
    if (!selectedGitRef) {
      return;
    }
    history.replace({
      pathname: routes.import.path({}),
      search: queryParams.with(QueryParams.BRANCH, selectedGitRef).with(QueryParams.AUTH_SOURCE, authSource).toString(),
    });
  }, [authSource, history, queryParams, routes.import, selectedGitRef, setBranch]);

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
      workspaces.createWorkspaceFromLocal({ localFiles: [file] }).then(({ workspace, suggestedFirstFile }) => {
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

  const doImport = useCallback(async () => {
    const singleFile = isSingleFile(importableUrl.type);

    try {
      if (queryParamAuthSource && !authSources.has(queryParamAuthSource as AuthSourceKeys)) {
        setImportingError(`Auth source '${queryParamAuthSource}' not found.`);
        return;
      }

      if (clonableUrl.type === UrlType.INVALID || clonableUrl.type === UrlType.NOT_SUPPORTED) {
        setImportingError(clonableUrl.error);
        return;
      }

      //unknown
      else if (importableUrl.type === UrlType.UNKNOWN) {
        if (gitRefsPromise.data?.defaultBranch) {
          await cloneGitRepository({
            origin: {
              kind: WorkspaceKind.GIT,
              url: importableUrl.url.toString(),
              branch: selectedGitRef ?? gitRefsPromise.data.defaultBranch,
            },
            gitConfig: authInfo,
            authInfo: authInfo,
          });
        } else {
          // TODO Tiago
          // Attempt importing single file.
        }
      }

      // git but not gist
      else if (importableUrl.type === UrlType.GITHUB_DOT_COM || importableUrl.type === UrlType.GIT) {
        if (gitRefsPromise.data?.defaultBranch) {
          await cloneGitRepository({
            origin: {
              kind: WorkspaceKind.GIT,
              url: importableUrl.url.toString(),
              branch: selectedGitRef ?? gitRefsPromise.data.defaultBranch,
            },
            gitConfig: authInfo,
            authInfo: authInfo,
          });
        } else {
          setImportingError(`Can't clone. ${gitRefsPromise.error}`);
          return;
        }
      }

      // gist
      else if (importableUrl.type === UrlType.GIST_DOT_GITHUB_DOT_COM) {
        importableUrl.url.hash = "";

        if (gitRefsPromise.data?.defaultBranch) {
          await cloneGitRepository({
            origin: {
              kind: WorkspaceKind.GITHUB_GIST,
              url: importableUrl.url.toString(),
              branch: queryParamBranch ?? selectedGitRef ?? gitRefsPromise.data.defaultBranch,
            },
            gitConfig: authInfo,
            authInfo: authInfo,
          });
        } else {
          setImportingError(`Can't clone. ${gitRefsPromise.error}`);
          return;
        }
      }

      // single file
      else if (singleFile) {
        let rawUrl = importableUrl.url as URL;

        if (importableUrl.type === UrlType.GITHUB_DOT_COM_FILE) {
          const res = await settingsDispatch.github.octokit.repos.getContent({
            repo: importableUrl.repo,
            owner: importableUrl.org,
            ref: importableUrl.branch,
            path: decodeURIComponent(importableUrl.filePath),
            headers: {
              "If-None-Match": "",
            },
          });
          rawUrl = new URL((res.data as any).download_url);
        }

        if (importableUrl.type === UrlType.GIST_DOT_GITHUB_DOT_COM_FILE) {
          const { data } = await settingsDispatch.github.octokit.gists.get({ gist_id: importableUrl.gistId });
          const fileName =
            Object.keys(data.files!).find((k) => k.toLowerCase() === importableUrl.fileName.toLowerCase()) ??
            Object.keys(data.files!)[0];
          rawUrl = new URL((data as any).files[fileName].raw_url);
        }

        const response = await fetch(rawUrl.toString());
        if (!response.ok) {
          setImportingError(`${response.status}${response.statusText ? `- ${response.statusText}` : ""}`);
          return;
        }

        const content = await response.text();

        await createWorkspaceForFile({
          path: basename(decodeURIComponent(rawUrl.pathname)),
          fileContents: encoder.encode(content),
        });
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
    authSources,
    queryParamAuthSource,
    clonableUrl.type,
    clonableUrl.error,
    gitRefsPromise.data?.defaultBranch,
    gitRefsPromise.error,
    cloneGitRepository,
    queryParamBranch,
    selectedGitRef,
    authInfo,
    createWorkspaceForFile,
    settingsDispatch.github.octokit.repos,
    settingsDispatch.github.octokit.gists,
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
    if ((!queryParamBranch || !queryParamAuthSource) && selectedGitRef) {
      return;
    }

    if (gitRefsPromise.status === PromiseStateStatus.PENDING) {
      return;
    }

    if (!queryParamUrl || (isPotentiallyGit(importableUrl.type) && queryParamConfirm === "true")) {
      advancedImportModalRef.current?.open();
      return;
    }

    setImportingError("");
    doImport();
  }, [
    gitRefsPromise.status,
    doImport,
    queryParamConfirm,
    importableUrl.type,
    queryParamUrl,
    queryParamBranch,
    queryParamAuthSource,
    selectedGitRef,
  ]);

  const validation = useImportableUrlValidation(authSource, queryParamUrl, queryParamBranch, clonableUrlObject);
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
                history.push({
                  pathname: routes.home.path({}),
                });
              }}
              clonableUrl={clonableUrlObject}
              validation={validation}
              authSource={authSource}
              url={queryParamUrl ?? ""}
              gitRefName={selectedGitRef ?? ""}
              setAuthSource={setAuthSource}
              setUrl={setUrl}
              setGitRefName={setBranch}
            />
          )}
        </PageSection>
      </OnlineEditorPage>
    </>
  );
}
