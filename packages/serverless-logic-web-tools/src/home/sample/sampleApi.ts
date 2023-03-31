/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import { LocalFile } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/LocalFile";
import { Octokit } from "@octokit/rest";
import { extname, join } from "path";
import { decoder, encoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";

interface FileData {
  content: string;
  encoding: BufferEncoding;
}

interface SampleDefinitionFile {
  sampleId: string;
  definitionPath: string;
}

interface GitHubRepoInfo {
  owner: string;
  repo: string;
  ref: string;
}

type GitHubFileInfo = GitHubRepoInfo & { path: string };

export type SampleCategory = "serverless-workflow" | "serverless-decision" | "dashbuilder";

const SAMPLE_DEFINITION_FILE = "definition.json";

const SAMPLE_TEMPLATE_FOLDER = "template";

const SVG_EXTENSION = ".svg";

const SAMPLE_FOLDER = "samples";

type SampleStatus = "ok" | "out of date" | "deprecated";

interface SampleDefinition {
  category: SampleCategory;
  status: SampleStatus;
  title: string;
  description: string;
  cover: string;
  tags: string[];
}

interface ContentData {
  type: string;
  download_url: string;
  git_url: string;
  html_url: string;
  name: string;
  path: string;
  sha: string;
  size: number;
  url: string;
}

export type Sample = {
  sampleId: string;
  definition: SampleDefinition;
  svgContent: string;
};

export const KIE_SAMPLES_REPO: GitHubFileInfo = {
  owner: "kiegroup",
  repo: "kie-samples",
  ref: process.env["WEBPACK_REPLACE__samplesRepositoryRef"]!,
  path: "samples",
};

async function fetchFileContent(args: { octokit: Octokit; fileInfo: GitHubFileInfo }): Promise<string | undefined> {
  try {
    const content = await args.octokit.repos.getContent({ ...args.fileInfo });
    if (content) {
      const fileData = content.data as FileData;
      return decoder.decode(new Uint8Array(Buffer.from(fileData.content, fileData.encoding)));
    }
  } catch (e) {
    console.debug(`Error fetching ${args.fileInfo.path} with Octokit. Fallback is 'raw.githubusercontent.com'.`);
    const rawFileResponse = await fetch(
      `https://raw.githubusercontent.com/${args.fileInfo.owner}/${args.fileInfo.repo}/${args.fileInfo.ref}/${args.fileInfo.path}`
    );
    if (rawFileResponse.ok) {
      return await rawFileResponse.text();
    }
  }
}

async function fetchFolderFiles(args: {
  octokit: Octokit;
  fileInfo: GitHubFileInfo;
}): Promise<ContentData[] | undefined> {
  try {
    const folderContent = await args.octokit.repos.getContent({
      ...args.fileInfo,
    });

    if (!folderContent || !folderContent.data) {
      return;
    }

    return folderContent.data as ContentData[];
  } catch (e) {
    console.debug(`Error fetching ${args.fileInfo.path} with Octokit.`);
  }
}

async function listSampleDefinitionFiles(args: {
  octokit: Octokit;
  repoInfo: GitHubRepoInfo;
}): Promise<SampleDefinitionFile[]> {
  const samplesFolders = await fetchFolderFiles({
    octokit: args.octokit,
    fileInfo: {
      ...args.repoInfo,
      path: SAMPLE_FOLDER,
    },
  });

  if (!samplesFolders) {
    throw new Error("Cannot fetch samples folder");
  }

  return samplesFolders
    .filter((folder) => folder.name !== SAMPLE_TEMPLATE_FOLDER)
    .map((folder) => ({ sampleId: folder.name, definitionPath: join(folder.path, SAMPLE_DEFINITION_FILE) }));
}

export async function fetchSampleDefinitions(octokit: Octokit): Promise<Sample[]> {
  const sampleDefinitionFiles = await listSampleDefinitionFiles({
    octokit,
    repoInfo: { ...KIE_SAMPLES_REPO },
  });

  const samples: Sample[] = [];
  for (const definitionFile of sampleDefinitionFiles) {
    const fileContent = await fetchFileContent({
      octokit,
      fileInfo: {
        ...KIE_SAMPLES_REPO,
        path: definitionFile.definitionPath,
      },
    });

    if (!fileContent) {
      console.error(`Could not read sample definition for ${definitionFile.sampleId}`);
      continue;
    }

    const definition = JSON.parse(fileContent) as SampleDefinition;
    const svgContent = await fetchFileContent({
      octokit,
      fileInfo: {
        ...KIE_SAMPLES_REPO,
        path: join("samples", definitionFile.sampleId, definition.cover),
      },
    });

    if (!svgContent) {
      console.error(`Could not read sample svg for ${definitionFile.sampleId}`);
      continue;
    }

    samples.push({
      sampleId: definitionFile.sampleId,
      definition,
      svgContent,
    });
  }

  if (samples.length === 0) {
    throw new Error("No samples could be loaded.");
  }

  return samples;
}

export async function fetchSampleFiles(args: { octokit: Octokit; sampleId: string }): Promise<LocalFile[]> {
  const sampleFolderFiles = await fetchFolderFiles({
    octokit: args.octokit,
    fileInfo: {
      ...KIE_SAMPLES_REPO,
      path: decodeURIComponent(`${KIE_SAMPLES_REPO.path}/${args.sampleId}`),
    },
  });

  if (!sampleFolderFiles) {
    throw new Error(`Sample ${args.sampleId} not found`);
  }

  const sampleFiles: LocalFile[] = [];
  for (const file of sampleFolderFiles) {
    if (file.name === SAMPLE_DEFINITION_FILE || extname(file.name) === SVG_EXTENSION) {
      continue;
    }

    const fileContent = await fetchFileContent({
      octokit: args.octokit,
      fileInfo: {
        ...KIE_SAMPLES_REPO,
        path: file.path,
      },
    });

    if (!fileContent) {
      throw new Error(`Could not get file contents for ${file.path}`);
    }
    sampleFiles.push({
      path: decodeURIComponent(file.path).split(`${SAMPLE_FOLDER}/${args.sampleId}`)[1],
      fileContents: encoder.encode(fileContent),
    });
  }
  return sampleFiles;
}
