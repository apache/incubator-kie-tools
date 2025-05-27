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
import { ImportableUrl, UrlType } from "../importFromUrl/ImportableUrlHooks";

const GITLAB_NAMESPACE_REGEX = "([^\\/]+(?:\\/[^\\/]+)*)"; // group or nested group(s)
const NAMESPACE = GITLAB_NAMESPACE_REGEX;
const PROJECT = "([^\\/]+)"; // project name

const GITLAB_PROJECT_SNIPPET_REGEX = new RegExp(`^/${NAMESPACE}/${PROJECT}/snippets/(\\d+)(?:\\.git)?/?$`);
const GITLAB_PROJECT_SNIPPET_FILE_REGEX = new RegExp(`^/${NAMESPACE}/${PROJECT}/snippets/([^/]+)/raw/([^/]+)/(.+)$`);
const GITLAB_REPO_TREE_REGEX = new RegExp(`^/${NAMESPACE}/${PROJECT}/tree/([^/]+)$`);
const GITLAB_REPO_FILE_REGEX = new RegExp(`^/${NAMESPACE}/${PROJECT}/blob/([^/]+)/(.*)$`);
const GITLAB_DEFAULT_REPO_REGEX = new RegExp(`^/${NAMESPACE}/${PROJECT}$`);

function ensureGitExtension(pathname: string): string {
  // Remove any trailing slash first
  const cleanPath = pathname.endsWith("/") ? pathname.slice(0, -1) : pathname;

  // Add `.git` if not already present
  return cleanPath.endsWith(".git") ? cleanPath : `${cleanPath}.git`;
}

export function parseGitLabUrl(url: URL): ImportableUrl {
  // /-/ is mostly used in GitLab web URLs, not the REST API.
  // replace all instances of /-/ with /
  const pathname = (url.pathname = url?.pathname?.replace(/\/-\//g, "/"));

  // 1. Standalone Snippet: eg: https://gitlab.com/snippets/123
  const standaloneSnippet = matchPath(
    {
      path: "/snippets/:snippetId",
      end: true,
      caseSensitive: true,
    },
    pathname
  );

  if (standaloneSnippet !== null && standaloneSnippet.params.snippetId) {
    const customStandaloneSnippetUrl = new URL(url);
    // Without .git on GitLab → Error (not a git repository or unexpected server response)
    // Ensure the pathname ends with `.git`
    customStandaloneSnippetUrl.pathname = ensureGitExtension(customStandaloneSnippetUrl.pathname);
    return {
      type: UrlType.GITLAB_DOT_COM_SNIPPET,
      snippetId: standaloneSnippet.params.snippetId,
      url: customStandaloneSnippetUrl,
    };
  }

  // 2. Standalone Snippet File: eg: https://gitlab.com/snippets/123/raw/main/sample.dmn
  const standaloneSnippetFile = matchPath(
    {
      path: "/snippets/:snippetId/raw/:tree/:fileName",
      end: true,
      caseSensitive: true,
    },
    pathname
  );

  if (
    standaloneSnippetFile !== null &&
    standaloneSnippetFile.params.fileName &&
    standaloneSnippetFile.params.snippetId &&
    standaloneSnippetFile.params.tree
  ) {
    const {
      params: { fileName, snippetId, tree },
    } = standaloneSnippetFile;
    return { type: UrlType.GITLAB_DOT_COM_SNIPPET_FILE, snippetId, branch: tree, filePath: fileName, url };
  }

  // 3. Project Snippet: eg: https://gitlab.com/group/project/-/snippets/123
  const projectSnippetMatch = pathname.match(GITLAB_PROJECT_SNIPPET_REGEX);
  if (projectSnippetMatch) {
    const [, group, project, snippetId] = projectSnippetMatch ?? [];
    if (group && project && snippetId) {
      const customProjectSnippetUrl = new URL(url);
      // Without .git on GitLab → Error (not a git repository or unexpected server response)
      // Ensure the pathname ends with `.git`
      customProjectSnippetUrl.pathname = ensureGitExtension(customProjectSnippetUrl.pathname);
      return {
        type: UrlType.GITLAB_DOT_COM_SNIPPET,
        snippetId,
        group: group,
        project,
        url: customProjectSnippetUrl,
      };
    }
  }

  // 4. Project Snippet File: eg: https://gitlab.com/group/project/-/snippets/123/raw/main/sample.dmn
  const projectSnippetFileMatch = pathname.match(GITLAB_PROJECT_SNIPPET_FILE_REGEX);
  if (projectSnippetFileMatch) {
    const [, group, project, snippetId, tree, path] = projectSnippetFileMatch ?? [];
    if (group && project && snippetId && tree && path) {
      return {
        type: UrlType.GITLAB_DOT_COM_SNIPPET_FILE,
        snippetId,
        group,
        project,
        branch: tree,
        filePath: path,
        url,
      };
    }
  }

  // 5. Specific branch/ref: eg: https://gitlab.com/group/project/-/tree/main
  const repoTreeViewMatch = pathname.match(GITLAB_REPO_TREE_REGEX);
  if (repoTreeViewMatch) {
    const [, group, project, tree] = repoTreeViewMatch ?? [];
    if (group && project && tree) {
      const customGitRefNameUrl = new URL(url);
      customGitRefNameUrl.pathname = customGitRefNameUrl.pathname.replace(`/tree/${tree}`, "");
      // Without .git on GitLab → Error (not a git repository or unexpected server response)
      // Ensure the pathname ends with `.git`
      customGitRefNameUrl.pathname = ensureGitExtension(customGitRefNameUrl.pathname);
      return {
        type: UrlType.GITLAB_DOT_COM,
        group,
        project,
        branch: tree,
        url: customGitRefNameUrl,
      };
    }
  }

  // 6. File in branch: eg: https://gitlab.com/group/project/-/blob/main/src/sample.dmn
  const repoFileViewMatch = pathname.match(GITLAB_REPO_FILE_REGEX);
  if (repoFileViewMatch) {
    const [, group, project, tree, path] = repoFileViewMatch ?? [];
    if (group && project && tree && path) {
      return {
        type: UrlType.GITLAB_DOT_COM_FILE,
        group: group,
        project,
        branch: tree,
        filePath: path,
        url,
      };
    }
  }

  // 7. Default repo view: eg: https://gitlab.com/group/project
  const repoDefaultViewMatch = pathname.match(GITLAB_DEFAULT_REPO_REGEX);
  if (repoDefaultViewMatch) {
    const [, group, project] = repoDefaultViewMatch ?? [];
    if (group && project) {
      const customGitRepoUrl = new URL(url);
      // Without .git on GitLab → Error (not a git repository or unexpected server response)
      // Ensure the pathname ends with `.git`
      customGitRepoUrl.pathname = ensureGitExtension(customGitRepoUrl.pathname);
      return {
        type: UrlType.GITLAB_DOT_COM,
        group,
        project,
        url: customGitRepoUrl,
      };
    }
  }

  return { type: UrlType.NOT_SUPPORTED, error: "Unsupported GitLab URL", url };
}
