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
import { useCallback, useEffect, useState } from "react";
import { useHistory } from "react-router";
import { AuthSourceKeys, useAuthSources, useSelectedAuthInfo as useAuthInfo } from "../authSources/AuthSourceHooks";
import { EditorPageErrorPage } from "../editor/EditorPageErrorPage";
import { useRoutes } from "../navigation/Hooks";
import { QueryParams } from "../navigation/Routes";
import { OnlineEditorPage } from "../pageTemplate/OnlineEditorPage";
import { useQueryParam } from "../queryParams/QueryParamsContext";
import { useSettingsDispatch } from "../settings/SettingsContext";
import { encoder } from "../workspace/encoderdecoder/EncoderDecoder";
import { PromiseStateStatus } from "../workspace/hooks/PromiseState";
import { LocalFile } from "../workspace/worker/api/LocalFile";
import { WorkspaceKind } from "../workspace/worker/api/WorkspaceOrigin";
import { useWorkspaces } from "../workspace/WorkspacesContext";
import { isSingleFile, UrlType, useClonableUrl, useImportableUrl } from "./ImportableUrlHooks";

export function NewWorkspaceFromUrlPage() {
  const workspaces = useWorkspaces();
  const routes = useRoutes();
  const history = useHistory();
  const authSources = useAuthSources();
  const settingsDispatch = useSettingsDispatch();

  const [importingError, setImportingError] = useState("");

  const queryParamUrl = useQueryParam(QueryParams.URL);
  const queryParamBranch = useQueryParam(QueryParams.BRANCH);
  const queryParamAuthSource = useQueryParam(QueryParams.AUTH_SOURCE);
  const queryParamConfirm = useQueryParam(QueryParams.CONFIRM);

  const { authInfo, authSource } = useAuthInfo(queryParamAuthSource);

  const importableUrl = useImportableUrl(queryParamUrl);
  const { clonableUrl, gitRefsPromise, selectedBranch } = useClonableUrl(queryParamUrl, authSource, queryParamBranch);

  const cloneGitRepository: typeof workspaces.createWorkspaceFromGitRepository = useCallback(
    async (args) => {
      const res = await workspaces.createWorkspaceFromGitRepository(args);

      const { workspace, suggestedFirstFile } = res;

      if (!suggestedFirstFile) {
        history.replace({ pathname: routes.home.path({}) });
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
              branch: selectedBranch ?? gitRefsPromise.data.defaultBranch,
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
              branch: selectedBranch ?? gitRefsPromise.data.defaultBranch,
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
              branch: queryParamBranch ?? selectedBranch ?? gitRefsPromise.data.defaultBranch,
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
    selectedBranch,
    authInfo,
    createWorkspaceForFile,
    settingsDispatch.github.octokit.repos,
    settingsDispatch.github.octokit.gists,
  ]);

  useEffect(() => {
    if (gitRefsPromise.status === PromiseStateStatus.PENDING) {
      return;
    }

    setImportingError("");
    doImport();
  }, [gitRefsPromise.status, doImport]);

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
          {!importingError && (
            <Bullseye>
              <TextContent>
                <Bullseye>
                  <Spinner />
                </Bullseye>
                <br />
                <Text component={TextVariants.p}>{`Importing from '${queryParamUrl}'`}</Text>
              </TextContent>
            </Bullseye>
          )}
        </PageSection>
      </OnlineEditorPage>
    </>
  );
}
