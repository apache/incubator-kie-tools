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
export const SampleCategories: SampleCategory[] = ["serverless-workflow", "serverless-decision", "dashbuilder"];

const SUPPORTING_FILES_FOLDER = join(".github", "supporting-files");

const SAMPLE_DEFINITION_FILE = "definition.json";
const SAMPLE_DEFINITIONS_FILE = join(SUPPORTING_FILES_FOLDER, "sample-definitions.json");

const SAMPLE_TEMPLATE_FOLDER = "template";

const SVG_EXTENSION = ".svg";

const SAMPLE_FOLDER = "samples";

type SampleStatus = "ok" | "out of date" | "deprecated";

interface SampleSocial {
  network: string;
  id: string;
}

interface SampleAuthor {
  name: string;
  email: string;
  github: string;
  social: SampleSocial[];
}

interface SampleDefinition {
  category: SampleCategory;
  status: SampleStatus;
  title: string;
  description: string;
  cover: string;
  tags: string[];
  type: string;
  dependencies: string[];
  related_to: string[];
  resources: string[];
  authors: SampleAuthor[];
  sample_path: string;
}

interface ContentData {
  type: "dir" | "file";
  name: string;
  path: string;
}

export type Sample = {
  sampleId: string;
  definition: SampleDefinition;
};

export type SampleCoversHashtable = {
  [sampleId: string]: string;
};

export const KIE_SAMPLES_REPO: GitHubFileInfo = {
  owner: "kiegroup",
  repo: "kie-samples",
  ref: process.env["WEBPACK_REPLACE__samplesRepositoryRef"]!,
  path: SAMPLE_FOLDER,
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

async function fetchFolderContent(args: {
  octokit: Octokit;
  fileInfo: GitHubFileInfo;
  onlyFilesRecursively: boolean;
}): Promise<ContentData[] | undefined> {
  try {
    const folderContent = await args.octokit.repos.getContent({
      ...args.fileInfo,
    });

    if (!folderContent || !folderContent.data) {
      return;
    }

    const contents = folderContent.data as ContentData[];

    if (!args.onlyFilesRecursively) {
      return contents;
    }

    const promises: Promise<ContentData[] | undefined>[] = [];

    for (const content of contents) {
      if (content.type === "file") {
        promises.push(Promise.resolve([content]));
      } else if (content.type === "dir") {
        promises.push(
          fetchFolderContent({
            octokit: args.octokit,
            onlyFilesRecursively: true,
            fileInfo: { ...args.fileInfo, path: content.path },
          })
        );
      }
    }

    const resolvedPromises = await Promise.all(promises);
    return resolvedPromises.flat().filter((r): r is ContentData => r !== undefined);
  } catch (e) {
    console.debug(`Error fetching ${args.fileInfo.path} with Octokit.`);
  }
}

async function listSampleDefinitionFiles(args: {
  octokit: Octokit;
  repoInfo: GitHubRepoInfo;
}): Promise<SampleDefinitionFile[]> {
  const sampleFolders = await fetchFolderContent({
    octokit: args.octokit,
    fileInfo: {
      ...args.repoInfo,
      path: SAMPLE_FOLDER,
    },
    onlyFilesRecursively: false,
  });

  if (!sampleFolders) {
    throw new Error(
      `Cannot fetch samples folder at https://github.com/${KIE_SAMPLES_REPO.owner}/${KIE_SAMPLES_REPO.repo}`
    );
  }

  return sampleFolders
    .filter((folder) => folder.name !== SAMPLE_TEMPLATE_FOLDER)
    .map((folder) => ({ sampleId: folder.name, definitionPath: join(folder.path, SAMPLE_DEFINITION_FILE) }));
}

/**
 * fetch the Sample Definitions file from the repository.
 * @param args.octokit An instance of the Octokit GitHub API client.
 * @returns an array of Samples
 */
export async function fetchSampleDefinitions(octokit: Octokit): Promise<Sample[]> {
  const fileContent = await fetchFileContent({
    octokit,
    fileInfo: {
      ...KIE_SAMPLES_REPO,
      path: SAMPLE_DEFINITIONS_FILE,
    },
  });

  if (!fileContent) {
    console.error(`Could not read sample definitions`);
    return [];
  }

  const definitions = JSON.parse(fileContent) as SampleDefinition[];

  return definitions.map((definition) => ({
    sampleId: definition.sample_path.replace(new RegExp(`^${SAMPLE_FOLDER}/`), ""),
    definition,
  }));
}

/**
 * Fetches the cover of a given sample using the GitHub API.
 * @param args.octokit An instance of the Octokit GitHub API client.
 * @param args.sample The sample object for which the cover is being fetched.
 * @returns The content of the SVG cover file for the sample, or undefined if it could not be fetched.
 */
export async function fetchSampleCover(args: { octokit: Octokit; sample: Sample }): Promise<string | undefined> {
  const { sample } = args;

  const svgContent = await fetchFileContent({
    octokit: args.octokit,
    fileInfo: {
      ...KIE_SAMPLES_REPO,
      path: join(SAMPLE_FOLDER, sample.sampleId, sample.definition.cover),
    },
  });

  if (!svgContent) {
    console.error(`Could not read sample svg for ${sample.sampleId}`);
    return;
  }

  return svgContent;
}

export async function fetchSampleFiles(args: { octokit: Octokit; sampleId: string }): Promise<LocalFile[]> {
  const sampleFolderFiles = await fetchFolderContent({
    octokit: args.octokit,
    fileInfo: {
      ...KIE_SAMPLES_REPO,
      path: decodeURIComponent(`${KIE_SAMPLES_REPO.path}/${args.sampleId}`),
    },
    onlyFilesRecursively: true,
  });

  if (!sampleFolderFiles) {
    throw new Error(`Sample ${args.sampleId} not found`);
  }

  const sampleFiles = sampleFolderFiles
    .filter(
      (file) => file.name !== SAMPLE_DEFINITION_FILE && extname(file.name) !== SVG_EXTENSION && file.type === "file"
    )
    .map(async (file) => {
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

      return {
        path: decodeURIComponent(file.path).split(`${SAMPLE_FOLDER}/${args.sampleId}`)[1],
        fileContents: encoder.encode(fileContent),
      };
    });
  return Promise.all(sampleFiles);
}
