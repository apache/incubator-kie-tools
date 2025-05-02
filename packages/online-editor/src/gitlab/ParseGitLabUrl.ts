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

import { matchPath } from "react-router";
import { ImportableUrl, UrlType } from "../importFromUrl/ImportableUrlHooks";

function ensureGitExtension(pathname: string): string {
  // Remove any trailing slash first
  const cleanPath = pathname.endsWith("/") ? pathname.slice(0, -1) : pathname;

  // Add `.git` if not already present
  return cleanPath.endsWith(".git") ? cleanPath : `${cleanPath}.git`;
}

export function parseGitLabUrl(url: URL): ImportableUrl | undefined {
  const host = url.host;

  const isGitLab = /gitlab\.com$/.test(host) || host.includes("gitlab");
  if (!isGitLab) return;

  // /-/ is mostly used in GitLab web URLs, not the REST API.
  // replace all instances of /-/ with /
  const pathname = (url.pathname = url?.pathname?.replace(/\/-\//g, "/"));

  // 1. Standalone Snippet: https://gitlab.com/snippets/123
  const standaloneSnippet = matchPath<{ snippetId: string }>(pathname, {
    path: "/snippets/:snippetId",
    exact: true,
    strict: true,
  });

  if (standaloneSnippet) {
    const customStandaloneSnippetUrl = new URL(url);
    // Without .git on GitLab → Error (not a git repository or unexpected server response)
    // Ensure the pathname ends with `.git`
    customStandaloneSnippetUrl.pathname = ensureGitExtension(customStandaloneSnippetUrl.pathname);
    return {
      type: UrlType.GITLAB_DOT_COM_SNIPPET,
      snippetId: standaloneSnippet?.params?.snippetId,
      url: customStandaloneSnippetUrl,
    };
  }

  // 2. Standalone Snippet File: https://gitlab.com/snippets/123/raw/main/code.js
  const standaloneSnippetFile = matchPath<{ snippetId: string; tree: string; fileName: string }>(pathname, {
    path: "/snippets/:snippetId/raw/:tree/:fileName",
    exact: true,
    strict: true,
  });

  if (standaloneSnippetFile) {
    const {
      params: { fileName, snippetId, tree },
    } = standaloneSnippetFile;
    return { type: UrlType.GITLAB_DOT_COM_SNIPPET_FILE, snippetId, branch: tree, filePath: fileName, url };
  }

  // 3. Project Snippet: https://gitlab.com/group/project/-/snippets/123
  const projectSnippet = matchPath<{ group: string; project: string; snippetId: string }>(pathname, {
    path: "/:group*/:project/snippets/:snippetId",
    exact: true,
    strict: true,
  });

  if (projectSnippet) {
    const {
      params: { group, project, snippetId },
    } = projectSnippet;
    const customProjectSnippetUrl = new URL(url);
    // Without .git on GitLab → Error (not a git repository or unexpected server response)
    // Ensure the pathname ends with `.git`
    customProjectSnippetUrl.pathname = ensureGitExtension(customProjectSnippetUrl.pathname);
    return { type: UrlType.GITLAB_DOT_COM_SNIPPET, snippetId, group, project, url: customProjectSnippetUrl };
  }

  // 4. Project Snippet File: https://gitlab.com/group/project/-/snippets/123/raw/main/code.js
  const projectSnippetFile = matchPath<{
    group: string;
    project: string;
    snippetId: string;
    tree: string;
    path: string;
  }>(pathname, {
    path: "/:group*/:project/snippets/:snippetId/raw/:tree/:path*",
    exact: true,
    strict: true,
  });

  if (projectSnippetFile) {
    const {
      params: { group, project, snippetId, path, tree },
    } = projectSnippetFile;
    return { type: UrlType.GITLAB_DOT_COM_SNIPPET_FILE, snippetId, group, project, branch: tree, filePath: path, url };
  }

  // 5. Specific branch/ref: https://gitlab.com/group/project/-/tree/main
  const repoTreeView = matchPath<{ group: string; project: string; tree: string }>(pathname, {
    path: "/:group*/:project/tree/:tree",
    exact: true,
    strict: true,
  });

  if (repoTreeView) {
    const {
      params: { group, project, tree },
    } = repoTreeView;
    const customGitRefNameUrl = new URL(url);
    customGitRefNameUrl.pathname = customGitRefNameUrl.pathname.replace(`/tree/${tree}`, "");
    // Without .git on GitLab → Error (not a git repository or unexpected server response)
    // Ensure the pathname ends with `.git`
    customGitRefNameUrl.pathname = ensureGitExtension(customGitRefNameUrl.pathname);
    return { type: UrlType.GITLAB_DOT_COM, group, project, branch: tree, url: customGitRefNameUrl };
  }

  // 6. File in branch: https://gitlab.com/group/project/-/blob/main/src/index.js
  const repoFileView = matchPath<{ group: string; project: string; tree: string; path: string }>(pathname, {
    path: "/:group*/:project/blob/:tree/:path*",
    exact: true,
    strict: true,
  });

  if (repoFileView) {
    const {
      params: { group, project, tree, path },
    } = repoFileView;
    return { type: UrlType.GITLAB_DOT_COM_FILE, group, project, branch: tree, filePath: path, url };
  }

  // 7. Default repo view: https://gitlab.com/group/project
  const repoDefaultView = matchPath<{ group: string; project: string }>(pathname, {
    path: "/:group*/:project",
    exact: true,
    strict: true,
  });

  if (repoDefaultView) {
    const {
      params: { group, project },
    } = repoDefaultView;
    const customGitRepoUrl = new URL(url);
    // Without .git on GitLab → Error (not a git repository or unexpected server response)
    // Ensure the pathname ends with `.git`
    customGitRepoUrl.pathname = ensureGitExtension(customGitRepoUrl.pathname);
    return { type: UrlType.GITLAB_DOT_COM, group, project, url: customGitRepoUrl };
  }

  return { type: UrlType.NOT_SUPPORTED, error: "Unsupported GitLab URL", url };
}
