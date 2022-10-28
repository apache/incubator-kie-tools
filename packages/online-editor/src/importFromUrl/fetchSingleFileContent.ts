import { Octokit } from "@octokit/rest";
import { ImportableUrl, UrlType } from "./ImportableUrlHooks";

export async function fetchSingleFileContent(
  importableUrl: ImportableUrl,
  octokit: Octokit
): Promise<{ rawUrl?: URL; content?: string; error?: string }> {
  let rawUrl = importableUrl.url as URL;
  if (importableUrl.type === UrlType.GITHUB_DOT_COM_FILE) {
    const res = await octokit.repos.getContent({
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
    const { data } = await octokit.gists.get({ gist_id: importableUrl.gistId });
    const fileName =
      Object.keys(data.files!).find((k) => k.toLowerCase() === importableUrl.fileName.toLowerCase()) ??
      Object.keys(data.files!)[0];
    rawUrl = new URL((data as any).files[fileName].raw_url);
  }

  const response = await fetch(rawUrl.toString());
  if (!response.ok) {
    return { error: `${response.status}${response.statusText ? `- ${response.statusText}` : ""}` };
  }

  const content = await response.text();

  return { content, rawUrl, error: undefined };
}
