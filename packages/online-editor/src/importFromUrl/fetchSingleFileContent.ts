import { Octokit } from "@octokit/rest";
import { BitbucketClientApi } from "../bitbucket/Hooks";
import { ImportableUrl, UrlType } from "./ImportableUrlHooks";

export async function fetchSingleFileContent(
  importableUrl: ImportableUrl,
  gitHubClient: Octokit,
  bitbucketClient: BitbucketClientApi
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
    const repoResponse = await bitbucketClient.getRepositoryContents(
      importableUrl.org,
      importableUrl.repo,
      importableUrl.branch,
      decodeURIComponent(importableUrl.filePath),
      true
    );
    if (!repoResponse.ok) {
      throw new Error(`Couldn't get Bitbucket repository contents: ${repoResponse.status} ${repoResponse.statusText}`);
    }
    const json = await repoResponse.json();
    if (!json.links) {
      throw new Error("Unexpected contents of Bitbucket reponse - missing links property.");
    }
    rawUrl = new URL(json.links.self.href);
  }

  const response = await fetch(rawUrl.toString());
  if (!response.ok) {
    return { error: `${response.status}${response.statusText ? `- ${response.statusText}` : ""}` };
  }

  const content = await response.text();

  return { content, rawUrl, error: undefined };
}
