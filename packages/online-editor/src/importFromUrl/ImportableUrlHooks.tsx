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

import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers/constants";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { IconSize } from "@patternfly/react-icons/dist/js/createIcon";
import CheckCircleIcon from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { extname } from "path";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { matchPath } from "react-router";
import { AuthProviderIcon } from "../accounts/authProviders/AuthProviderIcon";
import { useAuthProvider, useAuthProviders } from "../accounts/authProviders/AuthProvidersContext";
import { AuthSession } from "../accounts/authSessions/AuthSessionApi";
import { AuthInfo } from "../accounts/authSessions/AuthSessionsContext";
import { useEditorEnvelopeLocator } from "../envelopeLocator/hooks/EditorEnvelopeLocatorContext";
import { AdvancedImportModalRef } from "./AdvancedImportModalContent";
import { getGitRefName, getGitRefType } from "../gitRefs/GitRefs";
import { PromiseStateStatus, useLivePromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { GitServerRef } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/GitServerRef";
import { GitRefTypeIcon } from "../gitRefs/GitRefTypeIcon";

export enum UrlType {
  //git
  GIT,
  GITHUB_DOT_COM,
  GIST_DOT_GITHUB_DOT_COM,

  //single file
  GIST_DOT_GITHUB_DOT_COM_FILE,
  GITHUB_DOT_COM_FILE,
  FILE,

  //other
  UNKNOWN,
  NOT_SUPPORTED,
  INVALID,
}

export function isCertainlyGit(urlType: UrlType) {
  return urlType === UrlType.GIT || urlType === UrlType.GITHUB_DOT_COM || urlType === UrlType.GIST_DOT_GITHUB_DOT_COM;
}

export function isPotentiallyGit(urlType: UrlType) {
  return isCertainlyGit(urlType) || urlType === UrlType.UNKNOWN;
}

export function isSingleFile(urlType: UrlType) {
  return (
    urlType === UrlType.FILE ||
    urlType === UrlType.GIST_DOT_GITHUB_DOT_COM_FILE ||
    urlType === UrlType.GITHUB_DOT_COM_FILE
  );
}

export type ImportableUrl =
  | {
      type: UrlType.FILE;
      error?: undefined;
      url: URL;
    }
  | {
      type: UrlType.GIT;
      error?: undefined;
      url: URL;
    }
  | {
      type: UrlType.GIST_DOT_GITHUB_DOT_COM;
      error?: undefined;
      gistId?: string;
      url: URL;
    }
  | {
      type: UrlType.GIST_DOT_GITHUB_DOT_COM_FILE;
      error?: undefined;
      url: URL;
      gistId: string;
      fileName: string;
    }
  | {
      type: UrlType.GITHUB_DOT_COM_FILE;
      error?: undefined;
      url: URL;
      org: string;
      repo: string;
      branch: string;
      filePath: string;
    }
  | {
      type: UrlType.GITHUB_DOT_COM;
      error?: undefined;
      url: URL;
      branch?: string;
    }
  | {
      type: UrlType.NOT_SUPPORTED;
      error: string;
      url: URL;
    }
  | {
      type: UrlType.UNKNOWN;
      error?: string;
      url: URL;
    }
  | {
      type: UrlType.INVALID;
      error: string;
      url?: undefined;
    };

export function useImportableUrl(urlString?: string, allowedUrlTypes?: UrlType[]): ImportableUrl {
  const editorEnvelopeLocator = useEditorEnvelopeLocator();

  const ifAllowed = useCallback(
    (url: ImportableUrl): ImportableUrl => {
      if (allowedUrlTypes && !allowedUrlTypes.includes(url.type) && url.type !== UrlType.INVALID) {
        return { type: UrlType.NOT_SUPPORTED, error: `URL type not allowed (${url.type})`, url: url.url };
      }

      return url;
    },
    [allowedUrlTypes]
  );

  return useMemo(() => {
    if (!urlString) {
      return { type: UrlType.INVALID, error: "Empty URL" };
    }

    let url: URL;
    try {
      url = new URL(urlString);
    } catch (e) {
      return { type: UrlType.INVALID, error: "Invalid URL" };
    }

    if (url.host === "github.com") {
      const defaultBranchMatch = matchPath<{ org: string; repo: string }>(url.pathname, {
        path: "/:org/:repo",
        exact: true,
        strict: true,
        sensitive: false,
      });

      if (defaultBranchMatch) {
        return ifAllowed({ type: UrlType.GITHUB_DOT_COM, url });
      }

      const customRefNameMatch = matchPath<{ org: string; repo: string; tree: string }>(url.pathname, {
        path: "/:org/:repo/tree/:tree",
        exact: true,
        strict: true,
        sensitive: false,
      });

      if (customRefNameMatch) {
        const gitRefName = customRefNameMatch.params.tree;
        const customGitRefNameUrl = new URL(urlString);
        customGitRefNameUrl.pathname = customGitRefNameUrl.pathname.replace(`/tree/${gitRefName}`, "");
        return ifAllowed({ type: UrlType.GITHUB_DOT_COM, url: customGitRefNameUrl, branch: gitRefName });
      }

      const gitHubFileMatch = matchPath<{ org: string; repo: string; tree: string; path: string }>(url.pathname, {
        path: "/:org/:repo/blob/:tree/:path*",
        exact: true,
        strict: true,
        sensitive: false,
      });

      if (gitHubFileMatch) {
        return ifAllowed({
          type: UrlType.GITHUB_DOT_COM_FILE,
          url: url,
          org: gitHubFileMatch.params.org,
          repo: gitHubFileMatch.params.repo,
          branch: gitHubFileMatch.params.tree,
          filePath: gitHubFileMatch.params.path,
        });
      }

      return { type: UrlType.NOT_SUPPORTED, error: "Unsupported GitHub URL", url };
    }

    if (url.host === "raw.githubusercontent.com") {
      const gitHubRawFileMatch = matchPath<{ org: string; repo: string; tree: string; path: string }>(url.pathname, {
        path: "/:org/:repo/:tree/:path*",
        exact: true,
        strict: true,
        sensitive: false,
      });

      if (gitHubRawFileMatch) {
        return ifAllowed({
          type: UrlType.GITHUB_DOT_COM_FILE,
          url: url,
          org: gitHubRawFileMatch.params.org,
          repo: gitHubRawFileMatch.params.repo,
          branch: gitHubRawFileMatch.params.tree,
          filePath: gitHubRawFileMatch.params.path,
        });
      }

      return { type: UrlType.NOT_SUPPORTED, error: "Unsupported GitHub raw URL", url };
    }

    if (url.host === "gist.github.com" || url.host === "gist.githubusercontent.com") {
      const gistMatch = matchPath<{ user: string; gistId: string }>(url.pathname, {
        path: "/:user/:gistId",
        exact: true,
        strict: true,
      });

      const rawGistMatch = matchPath<{ user: string; gistId: string; fileId: string; fileName: string }>(url.pathname, {
        path: "/:user/:gistId/raw/:fileId/:fileName",
        exact: true,
        strict: true,
      });

      const directGistMatch = matchPath<{ gistId: string }>(url.pathname, {
        path: "/:gistId",
        exact: true,
        strict: true,
      });

      if (!gistMatch && !rawGistMatch && !directGistMatch) {
        return { type: UrlType.NOT_SUPPORTED, error: "Unsupported Gist URL", url };
      }

      if (gistMatch && url.hash) {
        return ifAllowed({
          type: UrlType.GIST_DOT_GITHUB_DOT_COM_FILE,
          url: url,
          gistId: gistMatch.params.gistId,
          fileName: url.hash.replace("#file-", "").replace(/-([^-]*)$/, ".$1"),
        });
      }

      if (rawGistMatch) {
        return ifAllowed({
          type: UrlType.GIST_DOT_GITHUB_DOT_COM_FILE,
          url: url,
          gistId: rawGistMatch.params.gistId,
          fileName: rawGistMatch.params.fileName,
        });
      }

      return ifAllowed({
        type: UrlType.GIST_DOT_GITHUB_DOT_COM,
        url,
        gistId: (gistMatch ?? directGistMatch)?.params.gistId.replace(".git", ""),
      });
    }

    const extension = extname(url.pathname).replace(".", "");
    if (extension) {
      if (extension === "git") {
        return ifAllowed({ type: UrlType.GIT, url });
      }

      if (!editorEnvelopeLocator.hasMappingFor(url.pathname)) {
        return { type: UrlType.NOT_SUPPORTED, error: `Unsupported extension for '${url.pathname}'`, url };
      }

      return ifAllowed({ type: UrlType.FILE, url });
    }

    return { type: UrlType.UNKNOWN, url };
  }, [urlString, ifAllowed, editorEnvelopeLocator]);
}

export function useClonableUrl(
  url: string | undefined,
  authInfo: AuthInfo | undefined,
  gitRefName: string | undefined
) {
  const importableUrl = useImportableUrl(url);

  const gitServerRefsPromise = useGitServerRefs(
    isPotentiallyGit(importableUrl.type) ? importableUrl.url : undefined,
    authInfo
  );

  const gitRefNameFromUrl = useMemo(() => {
    return (importableUrl as any).branch ?? gitServerRefsPromise.data?.defaultBranch;
  }, [gitServerRefsPromise.data?.defaultBranch, importableUrl]);

  const selectedGitRefName = useMemo<string | undefined>(() => {
    if (gitRefName) {
      const gitRefNameExists = gitServerRefsPromise.data?.refs.some(({ ref }) => getGitRefName(ref) === gitRefName);
      if (gitRefNameExists) {
        return gitRefName;
      }
    } else if (gitRefNameFromUrl) {
      const gitRefFromUrlExists = gitServerRefsPromise.data?.refs.some(
        ({ ref }) => getGitRefName(ref) === gitRefNameFromUrl
      );
      if (gitRefFromUrlExists) {
        return gitRefNameFromUrl;
      }
    }

    return undefined;
  }, [gitRefName, gitRefNameFromUrl, gitServerRefsPromise.data]);

  const clonableUrl: ImportableUrl = useMemo(() => {
    if (
      !isPotentiallyGit(importableUrl.type) ||
      gitServerRefsPromise.status === PromiseStateStatus.PENDING ||
      importableUrl.type === UrlType.INVALID
    ) {
      return importableUrl;
    }

    if (gitServerRefsPromise.data?.defaultBranch) {
      if (selectedGitRefName) {
        return importableUrl;
      } else {
        return {
          type: UrlType.NOT_SUPPORTED,
          url: importableUrl.url,
          error: `Selected ref '${gitRefName || gitRefNameFromUrl}' does not exist.`,
        };
      }
    }

    return {
      type: UrlType.NOT_SUPPORTED,
      url: importableUrl.url,
      error: `Can't determine Git refs for '${importableUrl.url.toString()}'`,
    };
  }, [
    importableUrl,
    gitServerRefsPromise.status,
    gitServerRefsPromise.data?.defaultBranch,
    selectedGitRefName,
    gitRefName,
    gitRefNameFromUrl,
  ]);

  return { clonableUrl, selectedGitRefName, gitServerRefsPromise };
}

export function useGitServerRefs(url: URL | undefined, authInfo: AuthInfo | undefined) {
  const workspaces = useWorkspaces();

  const gitServerRefsPromise = useLivePromiseState<{ refs: GitServerRef[]; defaultBranch: string; headRef: string }>(
    useMemo(() => {
      if (!url) {
        return { error: "Can't determine Git refs without URL." };
      }
      return async () => {
        const refs = await workspaces.getGitServerRefs({
          url: url.toString(),
          authInfo,
        });

        const headRef = refs.filter((f) => f.ref === "HEAD").pop()!.target!;

        const defaultBranch = getGitRefName(headRef);

        return { refs, defaultBranch, headRef };
      };
    }, [authInfo, url, workspaces])
  );

  return gitServerRefsPromise;
}

export function useImportableUrlValidation(
  authSession: AuthSession | undefined,
  url: string | undefined,
  gitRefName: string | undefined,
  clonableUrl: ReturnType<typeof useClonableUrl>,
  advancedImportModalRef?: React.RefObject<AdvancedImportModalRef>
) {
  const authProvider = useAuthProvider(authSession);

  return useMemo(() => {
    if (!url) {
      return {
        option: ValidatedOptions.default,
        helperText: <FormHelperText isHidden={true} icon={<Spinner size={"sm"} />} />,
      };
    }

    if (clonableUrl.gitServerRefsPromise.status === PromiseStateStatus.PENDING) {
      return {
        option: ValidatedOptions.default,
        helperText: (
          <FormHelperText isHidden={false} icon={<Spinner size={"sm"} />}>
            Loading...
          </FormHelperText>
        ),
      };
    }

    if (clonableUrl.clonableUrl.error) {
      return {
        option: ValidatedOptions.error,
        helperTextInvalid: clonableUrl.clonableUrl.error,
      };
    }

    return {
      option: ValidatedOptions.success,
      helperText: (
        <FormHelperText
          isHidden={false}
          icon={<CheckCircleIcon style={{ visibility: "hidden", width: 0 }} />}
          style={gitRefName ? { display: "flex", flexWrap: "nowrap" } : { visibility: "hidden" }}
        >
          <Flex justifyContent={{ default: "justifyContentFlexStart" }} style={{ display: "inline-flex" }}>
            <FlexItem style={{ minWidth: 0 }}>
              <GitRefTypeIcon type={getGitRefType(gitRefName)} />
              &nbsp;&nbsp;
              {getGitRefName(gitRefName)}
            </FlexItem>
            <FlexItem style={{ minWidth: 0 }}>
              <AuthProviderIcon authProvider={authProvider} size={IconSize.sm} />
              &nbsp;&nbsp;
              {authSession?.login}
            </FlexItem>
            <FlexItem style={{ minWidth: 0 }}>
              <Button
                isSmall={true}
                variant={ButtonVariant.link}
                style={{ padding: 0 }}
                onClick={() => advancedImportModalRef?.current?.open()}
              >
                Change...
              </Button>
            </FlexItem>
          </Flex>
        </FormHelperText>
      ),
    };
  }, [
    url,
    clonableUrl.gitServerRefsPromise.status,
    clonableUrl.clonableUrl.error,
    gitRefName,
    authProvider,
    authSession?.login,
    advancedImportModalRef,
  ]);
}
