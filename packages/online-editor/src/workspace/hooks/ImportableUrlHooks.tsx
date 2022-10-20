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

import { matchPath } from "react-router";
import { extname } from "path";
import { useCallback, useMemo } from "react";
import { useEditorEnvelopeLocator } from "../../envelopeLocator/hooks/EditorEnvelopeLocatorContext";
import { useWorkspaces } from "../WorkspacesContext";
import { useSettings } from "../../settings/SettingsContext";
import { PromiseStateStatus, usePromiseState } from "./PromiseState";
import { useCancelableEffect } from "../../reactExt/Hooks";
import { GitServerRef } from "../worker/api/GitServerRef";
import { AuthSource, AuthSourceKeys } from "../../authSources/AuthSourceHooks";

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
      url: string;
    };

export function useImportableUrl(urlString?: string, allowedUrlTypes?: UrlType[]): ImportableUrl {
  const editorEnvelopeLocator = useEditorEnvelopeLocator();

  return useMemo(() => {
    const ifAllowed = (url: ImportableUrl): ImportableUrl => {
      if (allowedUrlTypes && !allowedUrlTypes.includes(url.type) && url.type !== UrlType.INVALID) {
        return { type: UrlType.NOT_SUPPORTED, error: `URL type not allowed (${url.type})`, url: url.url };
      }

      return url;
    };

    if (!urlString) {
      return { type: UrlType.INVALID, error: "Empty URL", url: "" };
    }

    let url: URL;
    try {
      url = new URL(urlString);
    } catch (e) {
      return { type: UrlType.INVALID, error: "Invalid URL", url: urlString };
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
  }, [editorEnvelopeLocator, urlString, allowedUrlTypes]);
}

export function useEnhancedImportableUrl(url: string | undefined, authSource: AuthSource) {
  const importableUrl = useImportableUrl(url);

  const { defaultBranch, gitRefsPromise, defaultRef } = useGitRefs(
    isPotentiallyGit(importableUrl.type) ? (importableUrl as { url: URL }).url : undefined,
    authSource
  );

  const potentialSelectedBranch = useMemo(() => {
    if (isPotentiallyGit(importableUrl.type)) {
      return (importableUrl as any).branch ?? defaultBranch;
    } else {
      return undefined;
    }
  }, [defaultBranch, importableUrl]);

  const selectedBranch = useMemo(() => {
    if (!potentialSelectedBranch) {
      return undefined;
    }

    const potentialSelectedBranchExists = gitRefsPromise.data?.some(
      ({ ref }) => ref === `refs/heads/${potentialSelectedBranch}`
    );

    if (potentialSelectedBranchExists) {
      return potentialSelectedBranch;
    } else {
      return undefined;
    }
  }, [potentialSelectedBranch, gitRefsPromise.data]);

  const enhancedImportableUrl: ImportableUrl = useMemo(() => {
    if (importableUrl.type === UrlType.INVALID || gitRefsPromise.status === PromiseStateStatus.PENDING) {
      return importableUrl;
    }

    if (!isPotentiallyGit(importableUrl.type)) {
      return importableUrl;
    }

    if (defaultBranch) {
      if (selectedBranch) {
        return importableUrl;
      } else {
        return {
          type: UrlType.INVALID,
          url: importableUrl.url.toString(),
          error: `Selected branch '${potentialSelectedBranch}' does not exist.`,
        };
      }
    }

    return {
      type: UrlType.INVALID,
      url: importableUrl.url.toString(),
      error: `Can't determine Git refs for '${importableUrl.url.toString()}'`,
    };
  }, [importableUrl, gitRefsPromise.status, defaultBranch, selectedBranch, potentialSelectedBranch]);

  return { importableUrl: enhancedImportableUrl, defaultBranch, gitRefsPromise, selectedBranch, defaultRef };
}

export function useGitRefs(url: URL | undefined, authSource: AuthSource) {
  const workspaces = useWorkspaces();
  const settings = useSettings();
  const [gitRefsPromise, setGitRefsPromise] = usePromiseState<GitServerRef[]>();

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!url) {
          setGitRefsPromise({ error: "Can't determine Git refs without URL." });
          return;
        }
        setGitRefsPromise({ loading: true });
        workspaces
          .getGitServerRefs({
            url: url.toString(),
            authInfo:
              authSource === AuthSourceKeys.GITHUB && settings.github.user && settings.github.token
                ? { username: settings.github.user.login, password: settings.github.token }
                : undefined,
          })
          .then((refs) => {
            if (canceled.get()) {
              return;
            }
            setGitRefsPromise({ data: refs });
          })
          .catch((e) => {
            if (canceled.get()) {
              return;
            }
            console.log(e);
            setGitRefsPromise({ error: e });
          });
      },
      [url, setGitRefsPromise, workspaces, authSource, settings.github.user, settings.github.token]
    )
  );

  const defaultRef = useMemo(
    () => gitRefsPromise.data && gitRefsPromise.data.filter((f) => f.ref === "HEAD").pop()?.target,
    [gitRefsPromise]
  );

  return { gitRefsPromise, defaultBranch: defaultRef?.replace("refs/heads/", ""), defaultRef };
}
