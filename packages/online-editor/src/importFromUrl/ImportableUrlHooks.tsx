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

import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { FormHelperText } from "@patternfly/react-core/dist/js/components/Form";

import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers/constants";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";

import { extname } from "path";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { matchPath } from "react-router-dom";
import { AuthProviderIcon } from "../authProviders/AuthProviderIcon";
import { useAuthProvider, useAuthProviders } from "../authProviders/AuthProvidersContext";
import { AuthSession, AUTH_SESSION_NONE } from "../authSessions/AuthSessionApi";
import { AuthInfo, useAuthSessions } from "../authSessions/AuthSessionsContext";
import { useEditorEnvelopeLocator } from "../envelopeLocator/hooks/EditorEnvelopeLocatorContext";
import { AdvancedImportModalRef } from "./AdvancedImportModalContent";
import { getGitRefName, getGitRefType } from "../gitRefs/GitRefs";
import { PromiseStateStatus, useLivePromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { GitServerRef } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/GitServerRef";
import { GitRefTypeIcon } from "../gitRefs/GitRefTypeIcon";
import { parseGitLabUrl } from "../gitlab/ParseGitLabUrl";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { getCompatibleAuthSessionWithUrlDomain } from "../authSessions/CompatibleAuthSessions";
import { AuthProviderType } from "../authProviders/AuthProvidersApi";

export enum UrlType {
  //git
  GIT,
  GITHUB_DOT_COM,
  GIST_DOT_GITHUB_DOT_COM,
  BITBUCKET_DOT_ORG,
  BITBUCKET_DOT_ORG_SNIPPET,
  GITLAB_DOT_COM,
  GITLAB_DOT_COM_SNIPPET,

  //single file
  GIST_DOT_GITHUB_DOT_COM_FILE,
  GITHUB_DOT_COM_FILE,
  BITBUCKET_DOT_ORG_FILE,
  BITBUCKET_DOT_ORG_SNIPPET_FILE,
  GITLAB_DOT_COM_FILE,
  GITLAB_DOT_COM_SNIPPET_FILE,
  FILE,

  //other
  UNKNOWN,
  NOT_SUPPORTED,
  INVALID,
}

export function isCertainlyGit(urlType: UrlType): boolean {
  return (
    urlType === UrlType.GIT ||
    urlType === UrlType.GITHUB_DOT_COM ||
    urlType === UrlType.GIST_DOT_GITHUB_DOT_COM ||
    urlType === UrlType.BITBUCKET_DOT_ORG ||
    urlType === UrlType.BITBUCKET_DOT_ORG_SNIPPET ||
    urlType === UrlType.GITLAB_DOT_COM ||
    urlType === UrlType.GITLAB_DOT_COM_SNIPPET
  );
}

export function isPotentiallyGit(urlType: UrlType): boolean {
  return isCertainlyGit(urlType) || urlType === UrlType.UNKNOWN;
}

export function isSingleFile(urlType: UrlType) {
  return (
    urlType === UrlType.FILE ||
    urlType === UrlType.GIST_DOT_GITHUB_DOT_COM_FILE ||
    urlType === UrlType.GITHUB_DOT_COM_FILE ||
    urlType === UrlType.BITBUCKET_DOT_ORG_FILE ||
    urlType === UrlType.BITBUCKET_DOT_ORG_SNIPPET_FILE ||
    urlType === UrlType.GITLAB_DOT_COM_FILE ||
    urlType === UrlType.GITLAB_DOT_COM_SNIPPET_FILE
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
      type: UrlType.BITBUCKET_DOT_ORG;
      error?: undefined;
      url: URL;
      branch?: string;
    }
  | {
      type: UrlType.BITBUCKET_DOT_ORG_FILE;
      error?: undefined;
      url: URL;
      org: string;
      repo: string;
      branch: string;
      filePath: string;
    }
  | {
      type: UrlType.BITBUCKET_DOT_ORG_SNIPPET;
      error?: undefined;
      snippetId: string;
      snippetName: string;
      url: URL;
      org: string;
    }
  | {
      type: UrlType.BITBUCKET_DOT_ORG_SNIPPET_FILE;
      error?: undefined;
      snippetId: string;
      snippetName: string;
      url: URL;
      org: string;
      filename: string;
    }
  | {
      type: UrlType.GITLAB_DOT_COM;
      project: string;
      url: URL;
      group?: string;
      branch?: string;
      error?: undefined;
    }
  | {
      type: UrlType.GITLAB_DOT_COM_FILE;
      error?: undefined;
      url: URL;
      group?: string;
      project: string;
      branch: string;
      filePath: string;
    }
  | {
      type: UrlType.GITLAB_DOT_COM_SNIPPET;
      url: URL;
      snippetId: string;
      group?: string;
      project?: string;
      branch?: string;
      error?: undefined;
    }
  | {
      type: UrlType.GITLAB_DOT_COM_SNIPPET_FILE;
      url: URL;
      snippetId: string;
      error?: undefined;
      group?: string;
      project?: string;
      branch?: string;
      filePath?: string;
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
  const { authSessions, authSessionStatus } = useAuthSessions();
  const authProviders = useAuthProviders();

  const ifAllowed = useCallback(
    (url: ImportableUrl): ImportableUrl => {
      if (allowedUrlTypes && !allowedUrlTypes.includes(url.type) && url.type !== UrlType.INVALID) {
        return { type: UrlType.NOT_SUPPORTED, error: `URL type not allowed (${url.type})`, url: url.url };
      }

      return url;
    },
    [allowedUrlTypes]
  );

  const isGitRemoteDomainAllowed = useCallback(
    (
      url: URL,
      hostname: string | string[],
      authProvider: AuthProviderType.bitbucket | AuthProviderType.github | AuthProviderType.gitlab
    ) => {
      const hostnames = Array.isArray(hostname) ? hostname : [hostname];
      // allow if exact match
      if (hostnames.includes(url.host)) {
        return true;
      }
      // Step 1: Determine which auth sessions are compatible with the given URL domain
      const { compatible } = getCompatibleAuthSessionWithUrlDomain({
        authProviders,
        authSessions,
        authSessionStatus,
        urlDomain: url.host,
      });
      // Step 2: Find the first compatible auth provider (if any)
      const selectedAuthProvider = authProviders?.find(
        ({ id }) => compatible?.[0]?.type !== "none" && id === compatible?.[0]?.authProviderId
      );
      if (!selectedAuthProvider || selectedAuthProvider.type !== AuthProviderType[authProvider]) {
        return false;
      }
      // Step 3: Validate whether the hostname is allowed for the selected provider
      return (
        selectedAuthProvider?.domain === url.host || // match primary domain
        selectedAuthProvider?.supportedGitRemoteDomains?.some((supportedDomain) => supportedDomain === url.host) // match against supported domains
      );
    },
    [authProviders, authSessionStatus, authSessions]
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
      const defaultBranchMatch = matchPath(
        {
          path: "/:org/:repo",
          end: true,
          caseSensitive: true,
        },
        url.pathname
      );

      if (defaultBranchMatch) {
        return ifAllowed({ type: UrlType.GITHUB_DOT_COM, url });
      }

      const customRefNameMatch = matchPath(
        {
          path: "/:org/:repo/tree/:tree",
          end: true,
          caseSensitive: true,
        },
        url.pathname
      );

      if (customRefNameMatch) {
        const gitRefName = customRefNameMatch.params.tree;
        const customGitRefNameUrl = new URL(urlString);
        customGitRefNameUrl.pathname = customGitRefNameUrl.pathname.replace(`/tree/${gitRefName}`, "");
        return ifAllowed({ type: UrlType.GITHUB_DOT_COM, url: customGitRefNameUrl, branch: gitRefName });
      }

      const gitHubFileMatch = matchPath(
        {
          path: "/:org/:repo/blob/:tree/*",
          end: true,
          caseSensitive: true,
        },
        url.pathname
      );

      if (
        gitHubFileMatch !== null &&
        gitHubFileMatch.params.org &&
        gitHubFileMatch.params.repo &&
        gitHubFileMatch.params.tree &&
        gitHubFileMatch.params["*"]
      ) {
        return ifAllowed({
          type: UrlType.GITHUB_DOT_COM_FILE,
          url: url,
          org: gitHubFileMatch.params.org,
          repo: gitHubFileMatch.params.repo,
          branch: gitHubFileMatch.params.tree,
          filePath: gitHubFileMatch.params["*"],
        });
      }

      return { type: UrlType.NOT_SUPPORTED, error: "Unsupported GitHub URL", url };
    }

    if (url.host === "bitbucket.org") {
      const defaultBranchMatch = matchPath(
        {
          path: "/:org/:repo",
          end: true,
          caseSensitive: true,
        },
        url.pathname
      );

      if (defaultBranchMatch) {
        return ifAllowed({ type: UrlType.BITBUCKET_DOT_ORG, url });
      }

      const customRefNameMatch = matchPath(
        {
          path: "/:org/:repo/src/:tree",
          end: true,
          caseSensitive: true,
        },
        url.pathname
      );

      if (customRefNameMatch) {
        const gitRefName = customRefNameMatch.params.tree;
        const customGitRefNameUrl = new URL(urlString);
        customGitRefNameUrl.pathname = customGitRefNameUrl.pathname.replace(`/src/${gitRefName}`, "");
        return ifAllowed({ type: UrlType.BITBUCKET_DOT_ORG, url: customGitRefNameUrl, branch: gitRefName });
      }

      const bitbucketFileMatch = matchPath(
        {
          path: "/:org/:repo/src/:tree/*",
          end: true,
          caseSensitive: true,
        },
        url.pathname
      );

      if (
        bitbucketFileMatch !== null &&
        bitbucketFileMatch.params.org &&
        bitbucketFileMatch.params.repo &&
        bitbucketFileMatch.params.tree &&
        bitbucketFileMatch.params["*"]
      ) {
        return ifAllowed({
          type: UrlType.BITBUCKET_DOT_ORG_FILE,
          url: url,
          org: bitbucketFileMatch.params.org,
          repo: bitbucketFileMatch.params.repo,
          branch: bitbucketFileMatch.params.tree,
          filePath: bitbucketFileMatch.params["*"],
        });
      }

      const snippetMatch = matchPath(
        {
          path: "/:org/workspace/snippets/:snippetId/:snippetName",
          end: true,
          caseSensitive: true,
        },
        url.pathname
      );
      if (
        snippetMatch !== null &&
        snippetMatch.params.org &&
        snippetMatch.params.snippetId &&
        snippetMatch.params.snippetName &&
        url.hash
      ) {
        return ifAllowed({
          type: UrlType.BITBUCKET_DOT_ORG_SNIPPET_FILE,
          url: url,
          snippetId: snippetMatch.params.snippetId,
          snippetName: snippetMatch.params.snippetName,
          org: snippetMatch.params.org,
          filename: url.hash.replace("#file-", ""),
        });
      } else if (
        snippetMatch !== null &&
        snippetMatch.params.org &&
        snippetMatch.params.snippetId &&
        snippetMatch.params.snippetName
      ) {
        const newURL = new URL(url);
        newURL.pathname = `/snippets/${snippetMatch.params.org}/${snippetMatch.params.snippetId}/${snippetMatch.params.snippetName}.git`;
        return ifAllowed({
          type: UrlType.BITBUCKET_DOT_ORG_SNIPPET,
          url: newURL,
          snippetId: snippetMatch.params.snippetId,
          snippetName: snippetMatch.params.snippetName,
          org: snippetMatch.params.org,
        });
      }
      const snippetCloneUrlMatch = matchPath(
        {
          path: "/snippets/:org/:snippetId/:snippetName",
          end: true,
          caseSensitive: true,
        },
        url.pathname
      );
      if (
        snippetCloneUrlMatch !== null &&
        snippetCloneUrlMatch.params.org &&
        snippetCloneUrlMatch.params.snippetId &&
        snippetCloneUrlMatch.params.snippetName
      ) {
        return ifAllowed({
          type: UrlType.BITBUCKET_DOT_ORG_SNIPPET,
          url: url,
          snippetId: snippetCloneUrlMatch.params.snippetId,
          snippetName: snippetCloneUrlMatch.params.snippetName,
          org: snippetCloneUrlMatch.params.org,
        });
      }

      return { type: UrlType.NOT_SUPPORTED, error: "Unsupported Bitbucket URL", url };
    }

    if (url.host === "raw.githubusercontent.com") {
      const gitHubRawFileMatch = matchPath(
        {
          path: "/:org/:repo/refs/heads/:tree/*",
          end: true,
          caseSensitive: true,
        },
        url.pathname
      );

      if (
        gitHubRawFileMatch !== null &&
        gitHubRawFileMatch.params.org &&
        gitHubRawFileMatch.params.repo &&
        gitHubRawFileMatch.params.tree &&
        gitHubRawFileMatch.params["*"]
      ) {
        return ifAllowed({
          type: UrlType.GITHUB_DOT_COM_FILE,
          url: url,
          org: gitHubRawFileMatch.params.org,
          repo: gitHubRawFileMatch.params.repo,
          branch: gitHubRawFileMatch.params.tree,
          filePath: gitHubRawFileMatch.params["*"],
        });
      }

      return { type: UrlType.NOT_SUPPORTED, error: "Unsupported GitHub raw URL", url };
    }

    if (url.host === "gist.github.com" || url.host === "gist.githubusercontent.com") {
      const gistMatch = matchPath(
        {
          path: "/:user/:gistId",
          end: true,
          caseSensitive: true,
        },
        url.pathname
      );

      const rawGistMatch = matchPath(
        {
          path: "/:user/:gistId/raw/:fileId/:fileName",
          end: true,
          caseSensitive: true,
        },
        url.pathname
      );

      const directGistMatch = matchPath(
        {
          path: "/:gistId",
          end: true,
          caseSensitive: true,
        },
        url.pathname
      );

      if (!gistMatch && !rawGistMatch && !directGistMatch) {
        return { type: UrlType.NOT_SUPPORTED, error: "Unsupported Gist URL", url };
      }

      if (gistMatch !== null && gistMatch.params.gistId && url.hash) {
        return ifAllowed({
          type: UrlType.GIST_DOT_GITHUB_DOT_COM_FILE,
          url: url,
          gistId: gistMatch.params.gistId,
          fileName: url.hash.replace("#file-", "").replace(/-([^-]*)$/, ".$1"),
        });
      }

      if (rawGistMatch !== null && rawGistMatch.params.gistId && rawGistMatch.params.fileName) {
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
        gistId: (gistMatch ?? directGistMatch)?.params?.gistId?.replace(".git", ""),
      });
    }

    // Gitlab
    if (isGitRemoteDomainAllowed(url, "gitlab.com", AuthProviderType.gitlab)) {
      const gitlabImportUrl = parseGitLabUrl(url);
      return ifAllowed(gitlabImportUrl);
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
  }, [urlString, ifAllowed, editorEnvelopeLocator, isGitRemoteDomainAllowed]);
}

export function useClonableUrl(
  url: string | undefined,
  authInfo: AuthInfo | undefined,
  gitRefName: string | undefined,
  insecurelyDisableTlsCertificateValidation?: boolean,
  disableEncoding?: boolean
) {
  const importableUrl = useImportableUrl(url);

  const gitServerRefsPromise = useGitServerRefs(
    isPotentiallyGit(importableUrl.type) ? importableUrl.url : undefined,
    authInfo,
    insecurelyDisableTlsCertificateValidation,
    disableEncoding
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

export function useGitServerRefs(
  url: URL | undefined,
  authInfo: AuthInfo | undefined,
  insecurelyDisableTlsCertificateValidation?: boolean,
  disableEncoding?: boolean
) {
  const workspaces = useWorkspaces();

  const [gitServerRefsPromise] = useLivePromiseState<{ refs: GitServerRef[]; defaultBranch: string; headRef: string }>(
    useMemo(() => {
      if (!url) {
        return { error: "Can't determine Git refs without URL." };
      }
      return async () => {
        const refs = await workspaces.getGitServerRefs({
          url: url.toString(),
          authInfo,
          insecurelyDisableTlsCertificateValidation,
          disableEncoding,
        });

        const headRef = refs.filter((f) => f.ref === "HEAD").pop()!.target!;

        const defaultBranch = getGitRefName(headRef);

        return { refs, defaultBranch, headRef };
      };
    }, [authInfo, url, workspaces, insecurelyDisableTlsCertificateValidation, disableEncoding])
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
        helperText: (
          <FormHelperText>
            <Icon size="sm" isInline style={{ display: "none" }}>
              <Spinner />
            </Icon>
          </FormHelperText>
        ),
      };
    }

    if (clonableUrl.gitServerRefsPromise.status === PromiseStateStatus.PENDING) {
      return {
        option: ValidatedOptions.default,
        helperText: (
          <FormHelperText>
            <Icon size="sm" isInline>
              <Spinner />
            </Icon>
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

    if (!(authSession?.type === "git" || authSession?.type === AUTH_SESSION_NONE.type)) {
      return {
        option: ValidatedOptions.error,
        helperTextInvalid: `Incompatible AuthSession type '${authSession?.type}'.`,
      };
    }
    return {
      option: ValidatedOptions.success,
      helperText: (
        <FormHelperText style={gitRefName ? { display: "flex", flexWrap: "nowrap" } : { visibility: "hidden" }}>
          <Flex justifyContent={{ default: "justifyContentFlexStart" }} style={{ display: "inline-flex" }}>
            <FlexItem style={{ minWidth: 0 }}>
              <GitRefTypeIcon type={getGitRefType(gitRefName)} />
              &nbsp;&nbsp;
              {getGitRefName(gitRefName)}
            </FlexItem>
            <FlexItem style={{ minWidth: 0 }}>
              <AuthProviderIcon authProvider={authProvider} size="md" />
              &nbsp;&nbsp;
              {authSession?.login}
            </FlexItem>
            <FlexItem style={{ minWidth: 0 }}>
              <Button
                size="sm"
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
    authSession,
    advancedImportModalRef,
  ]);
}
