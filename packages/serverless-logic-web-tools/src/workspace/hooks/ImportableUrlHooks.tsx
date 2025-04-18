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

import { matchPath } from "react-router-dom";
import { extname } from "path";
import { useMemo } from "react";

export enum UrlType {
  GIT,
  GITHUB,
  GIST,
  GIST_FILE,
  GITHUB_FILE,
  FILE,
  ZIP,
  INVALID,
}

export type ImportableUrl =
  | {
      type: UrlType.ZIP;
      error?: string;
      url: URL;
    }
  | {
      type: UrlType.FILE;
      error?: string;
      url: URL;
    }
  | {
      type: UrlType.GIT;
      error?: string;
      url: URL;
    }
  | {
      type: UrlType.GIST;
      error?: string;
      gistId?: string;
      url: URL;
    }
  | {
      type: UrlType.GIST_FILE;
      error?: string;
      url: URL;
      gistId: string;
      fileName: string;
    }
  | {
      type: UrlType.GITHUB_FILE;
      error?: string;
      url: URL;
      org: string;
      repo: string;
      branch: string;
      filePath: string;
    }
  | {
      type: UrlType.GITHUB;
      error?: string;
      url: URL;
      branch?: string;
    }
  | {
      type: UrlType.INVALID;
      error: string;
      url: string;
    };

export function useImportableUrl(args: {
  isFileSupported: (path: string) => boolean;
  urlString?: string;
  allowedUrlTypes?: UrlType[];
}): ImportableUrl {
  return useMemo(() => {
    const ifAllowed = (url: ImportableUrl): ImportableUrl => {
      if (args.allowedUrlTypes && !args.allowedUrlTypes.includes(url.type)) {
        return { type: UrlType.INVALID, error: "URL not allowed", url: url.url.toString() };
      }

      return url;
    };

    if (!args.urlString) {
      return { type: UrlType.INVALID, error: "Empty URL", url: "" };
    }

    let url: URL;
    try {
      url = new URL(args.urlString);
    } catch (e) {
      return { type: UrlType.INVALID, error: "Invalid URL", url: args.urlString };
    }

    if (url.host === "github.com" || url.host === "raw.githubusercontent.com") {
      const defaultBranchMatch = matchPath({ path: "/:org/:repo", end: true, caseSensitive: false }, url.pathname);
      const customBranchMatch = matchPath(
        { path: "/:org/:repo/tree/:tree", end: true, caseSensitive: false },
        url.pathname
      );

      if (defaultBranchMatch) {
        return ifAllowed({ type: UrlType.GITHUB, url });
      }

      if (customBranchMatch) {
        const branch = customBranchMatch.params.tree;
        const customBranchUrl = new URL(args.urlString);
        customBranchUrl.pathname = customBranchUrl.pathname.replace(`/tree/${branch}`, "");
        return ifAllowed({ type: UrlType.GITHUB, url: customBranchUrl, branch });
      }

      const gitHubFileMatch = matchPath(
        { path: "/:org/:repo/blob/:tree/:path*", end: true, caseSensitive: false },
        url.pathname
      );

      if (
        gitHubFileMatch !== null &&
        gitHubFileMatch.params.org &&
        gitHubFileMatch.params.repo &&
        gitHubFileMatch.params.tree &&
        gitHubFileMatch.params["path*"]
      ) {
        return ifAllowed({
          type: UrlType.GITHUB_FILE,
          url: url,
          org: gitHubFileMatch.params.org,
          repo: gitHubFileMatch.params.repo,
          branch: gitHubFileMatch.params.tree,
          filePath: gitHubFileMatch.params["path*"],
        });
      }

      const gitHubRawFileMatch = matchPath(
        { path: "/:org/:repo/refs/heads/:tree/:path*", end: true, caseSensitive: false },
        url.pathname
      );

      if (
        gitHubRawFileMatch !== null &&
        gitHubRawFileMatch.params.org &&
        gitHubRawFileMatch.params.repo &&
        gitHubRawFileMatch.params.tree &&
        gitHubRawFileMatch.params["path*"]
      ) {
        return ifAllowed({
          type: UrlType.GITHUB_FILE,
          url: url,
          org: gitHubRawFileMatch.params.org,
          repo: gitHubRawFileMatch.params.repo,
          branch: gitHubRawFileMatch.params.tree,
          filePath: gitHubRawFileMatch.params["path*"],
        });
      }

      return { type: UrlType.INVALID, error: "Unsupported GitHub URL", url: args.urlString };
    }

    if (url.host === "gist.github.com" || url.host === "gist.githubusercontent.com") {
      const gistMatch = matchPath({ path: "/:user/:gistId", end: true }, url.pathname);
      const rawGistMatch = matchPath({ path: "/:user/:gistId/raw/:fileId/:fileName", end: true }, url.pathname);
      const directGistMatch = matchPath({ path: "/:gistId", end: true }, url.pathname);

      if (!gistMatch && !rawGistMatch && !directGistMatch) {
        return { type: UrlType.INVALID, error: "Unsupported Gist URL", url: args.urlString };
      }

      if (gistMatch !== null && gistMatch.params.gistId && url.hash) {
        return ifAllowed({
          type: UrlType.GIST_FILE,
          url: url,
          gistId: gistMatch.params.gistId,
          fileName: url.hash.replace("#file-", "").replace(/-([^-]*)$/, ".$1"),
        });
      }

      if (rawGistMatch !== null && rawGistMatch.params.gistId && rawGistMatch.params.fileName) {
        return ifAllowed({
          type: UrlType.GIST_FILE,
          url: url,
          gistId: rawGistMatch.params.gistId,
          fileName: rawGistMatch.params.fileName,
        });
      }

      return ifAllowed({
        type: UrlType.GIST,
        url,
        gistId: (gistMatch ?? directGistMatch)?.params?.gistId?.replace(".git", ""),
      });
    }

    const extension = extname(url.pathname).replace(".", "");
    if (!extension) {
      return { type: UrlType.INVALID, error: `Can't determine file extension from URL`, url: args.urlString };
    }

    if (extension === "git") {
      return ifAllowed({ type: UrlType.GIT, url });
    }

    if (!args.isFileSupported(url.pathname)) {
      return { type: UrlType.INVALID, error: `Unsupported extension for '${url.pathname}'`, url: args.urlString };
    }

    return ifAllowed({ type: UrlType.FILE, url });
  }, [args]);
}
