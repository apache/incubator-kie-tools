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

import { Octokit } from "@octokit/rest";
import { BitbucketClientApi } from "../bitbucket/Hooks";
import { ImportableUrl, UrlType } from "./ImportableUrlHooks";
import { GitlabClientApi } from "../gitlab/GitlabClient";

export async function fetchSingleFileContent(
  importableUrl: ImportableUrl,
  gitHubClient: Octokit,
  bitbucketClient: BitbucketClientApi,
  gitlabClient: GitlabClientApi
): Promise<{ rawUrl?: URL; content?: string; error?: string }> {
  let rawUrl = importableUrl.url as URL;
  if (importableUrl.type === UrlType.GITHUB_DOT_COM_FILE) {
    const res = await gitHubClient.repos.getContent({
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
    const { data } = await gitHubClient.gists.get({ gist_id: importableUrl.gistId });
    const fileName =
      Object.keys(data.files!).find((k) => k.toLowerCase() === importableUrl.fileName.toLowerCase()) ??
      Object.keys(data.files!)[0];
    rawUrl = new URL((data as any).files[fileName].raw_url);
  }

  if (importableUrl.type === UrlType.BITBUCKET_DOT_ORG_FILE) {
    const repoResponse = await bitbucketClient.getRepositoryContents({
      workspace: importableUrl.org,
      repository: importableUrl.repo,
      ref: importableUrl.branch,
      path: decodeURIComponent(importableUrl.filePath),
      meta: true,
    });
    if (!repoResponse.ok) {
      throw new Error(`Couldn't get Bitbucket repository contents: ${repoResponse.status} ${repoResponse.statusText}`);
    }
    const json = await repoResponse.json();
    if (!json.links) {
      throw new Error("Unexpected contents of Bitbucket response - missing links property.");
    }
    rawUrl = new URL(json.links.self.href);
  }
  if (importableUrl.type === UrlType.BITBUCKET_DOT_ORG_SNIPPET_FILE) {
    const snippetResponse = await bitbucketClient.getSnippet({
      workspace: importableUrl.org,
      snippetId: importableUrl.snippetId,
    });
    if (!snippetResponse.ok) {
      throw new Error(
        `Couldn't get Bitbucket snippet contents: ${snippetResponse.status} ${snippetResponse.statusText}`
      );
    }
    const json = await snippetResponse.json();
    if (!json.files) {
      throw new Error("Unexpected contents of Bitbucket response - missing files property.");
    }
    if (!(importableUrl.filename in json.files)) {
      throw new Error(
        `Unexpected contents of Bitbucket response - file ${importableUrl.filename} is not in the response.`
      );
    }
    const links = json.files[importableUrl.filename].links;
    if (!links) {
      throw new Error("Unexpected contents of Bitbucket response - missing links property.");
    }
    rawUrl = new URL(links.self.href);
  }
  if (importableUrl.type === UrlType.GITLAB_DOT_COM_FILE) {
    const repoResponse = await gitlabClient.getRepositoryContents({
      group: importableUrl.group,
      project: importableUrl.project,
      ref: importableUrl.branch,
      path: decodeURIComponent(importableUrl.filePath),
    });

    if (!repoResponse.ok) {
      throw new Error(`Couldn't get Gitlab repository contents: ${repoResponse.status} ${repoResponse.statusText}`);
    }
    const content = await repoResponse?.text();
    if (!content) {
      throw new Error(`Unexpected contents of Gitlab response`);
    }
    return { content, rawUrl: new URL(importableUrl?.url), error: undefined };
  }
  if (importableUrl.type === UrlType.GITLAB_DOT_COM_SNIPPET_FILE) {
    const snippetResponse = await gitlabClient.getSnippetFileRaw({
      group: importableUrl.group,
      filePath: decodeURIComponent(importableUrl.filePath!),
      snippetId: importableUrl.snippetId,
      branch: importableUrl?.branch,
      project: importableUrl?.project,
    });
    if (!snippetResponse.ok) {
      throw new Error(`Couldn't get Gitlab snippet contents: ${snippetResponse.status} ${snippetResponse.statusText}`);
    }
    const content = await snippetResponse?.text();
    if (!content) {
      throw new Error(`Unexpected contents of Gitlab response`);
    }
    return { content, rawUrl: new URL(importableUrl?.url), error: undefined };
  }

  const response = await fetch(rawUrl.toString());
  if (!response.ok) {
    return { error: `${response.status}${response.statusText ? `- ${response.statusText}` : ""}` };
  }

  const content = await response.text();

  return { content, rawUrl, error: undefined };
}
