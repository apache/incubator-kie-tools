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

import { extname } from "path";
import { useCallback, useMemo } from "react";
import { matchPath } from "react-router";
import { AuthSource, useSelectedAuthInfo } from "../authSources/AuthSourceHooks";
import { useEditorEnvelopeLocator } from "../envelopeLocator/hooks/EditorEnvelopeLocatorContext";
import { useCancelableEffect } from "../reactExt/Hooks";
import { PromiseStateStatus, usePromiseState } from "../workspace/hooks/PromiseState";
import { GitServerRef } from "../workspace/worker/api/GitServerRef";
import { useWorkspaces } from "../workspace/WorkspacesContext";

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

    if (url.host === "github.com" || url.host === "raw.githubusercontent.com") {
      const defaultBranchMatch = matchPath<{ org: string; repo: string }>(url.pathname, {
        path: "/:org/:repo",
        exact: true,
        strict: true,
        sensitive: false,
      });

      const customBranchMatch = matchPath<{ org: string; repo: string; tree: string }>(url.pathname, {
        path: "/:org/:repo/tree/:tree",
        exact: true,
        strict: true,
        sensitive: false,
      });

      if (defaultBranchMatch) {
        return ifAllowed({ type: UrlType.GITHUB_DOT_COM, url });
      }

      if (customBranchMatch) {
        const branch = customBranchMatch.params.tree;
        const customBranchUrl = new URL(urlString);
        customBranchUrl.pathname = customBranchUrl.pathname.replace(`/tree/${branch}`, "");
        return ifAllowed({ type: UrlType.GITHUB_DOT_COM, url: customBranchUrl, branch });
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

      return { type: UrlType.NOT_SUPPORTED, error: "Unsupported GitHub URL", url };
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
  authSource: AuthSource | undefined,
  branch: string | undefined
) {
  const importableUrl = useImportableUrl(url);

  const gitRefsPromise = useGitRefs(isPotentiallyGit(importableUrl.type) ? importableUrl.url : undefined, authSource);

  const branchFromUrl = useMemo(() => {
    return (importableUrl as any).branch ?? gitRefsPromise.data?.defaultBranch;
  }, [gitRefsPromise.data?.defaultBranch, importableUrl]);

  const selectedBranch = useMemo<string | undefined>(() => {
    if (branch) {
      const branchExists = gitRefsPromise.data?.refs.some(({ ref }) => ref === `refs/heads/${branch}`);
      if (branchExists) {
        return branch;
      }
    } else if (branchFromUrl) {
      const branchFromUrlExists = gitRefsPromise.data?.refs.some(({ ref }) => ref === `refs/heads/${branchFromUrl}`);
      if (branchFromUrlExists) {
        return branchFromUrl;
      }
    }

    return undefined;
  }, [branch, branchFromUrl, gitRefsPromise.data]);

  const clonableUrl: ImportableUrl = useMemo(() => {
    if (!isPotentiallyGit(importableUrl.type) || gitRefsPromise.status === PromiseStateStatus.PENDING) {
      return importableUrl;
    }

    if (gitRefsPromise.data?.defaultBranch) {
      if (selectedBranch) {
        return importableUrl;
      } else {
        return {
          type: UrlType.INVALID,
          error: `Selected branch '${branch ?? branchFromUrl}' does not exist.`,
        };
      }
    }

    return {
      type: UrlType.INVALID,
      error: `Can't determine Git refs for '${importableUrl.url?.toString()}'`,
    };
  }, [importableUrl, gitRefsPromise.status, gitRefsPromise.data?.defaultBranch, selectedBranch, branch, branchFromUrl]);

  return { clonableUrl, selectedBranch, gitRefsPromise };
}

export function useGitRefs(url: URL | undefined, authSource: AuthSource | undefined) {
  const workspaces = useWorkspaces();
  const { authInfo } = useSelectedAuthInfo(authSource);

  const gitRefsPromise = useLivePromiseState<{ refs: GitServerRef[]; defaultBranch: string; defaultRef: string }>(
    useMemo(() => {
      if (!url) {
        return { error: "Can't determine Git refs without URL." };
      }
      return async () => {
        const refs = await workspaces.getGitServerRefs({
          url: url.toString(),
          authInfo,
        });

        const defaultRef = refs.filter((f) => f.ref === "HEAD").pop()!.target!;

        const defaultBranch = defaultRef.replace("refs/heads/", "");

        return { refs, defaultBranch, defaultRef };
      };
    }, [authInfo, url, workspaces])
  );

  return gitRefsPromise;
}

export function useLivePromiseState<T>(promiseDelegate: (() => Promise<T>) | { error: string }) {
  const [state, setState] = usePromiseState<T>();
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (typeof promiseDelegate !== "function") {
          setState({ error: promiseDelegate.error });
          return;
        }
        setState({ loading: true });
        promiseDelegate()
          .then((refs) => {
            if (canceled.get()) {
              return;
            }
            setState({ data: refs });
          })
          .catch((e) => {
            if (canceled.get()) {
              return;
            }
            console.log(e);
            setState({ error: e });
          });
      },
      [promiseDelegate, setState]
    )
  );
  return state;
}
