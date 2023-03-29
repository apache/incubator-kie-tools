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
import { extname, dirname, join } from "path";
import { encoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { Sample } from "./SampleCard";

interface GitHubFileInfo {
  org: string;
  repo: string;
  ref: string;
  path: string;
}

export const kieSamplesRepo: GitHubFileInfo = {
  org: "kiegroup",
  repo: "kie-samples",
  ref: process.env["WEBPACK_REPLACE__samplesRepositoryRef"]!,
  path: "samples",
};

export type SampleCategory = "serverless-workflow" | "serverless-decision" | "dashbuilder";

const SAMPLE_DEFINITION_FILE = "definition.json";

const SAMPLE_TEMPLATE_FOLDER = "template";

const SVG_EXTENSION = ".svg";

type SampleStatus = "ok" | "out of date" | "deprecated";

interface SampleDefinition {
  category: SampleCategory;
  status: SampleStatus;
  title: string;
  description: string;
  cover: string;
  tags: string[];
}

export interface RepoContentType {
  download_url: string;
  git_url: string;
  html_url: string;
  name: string;
  path: string;
  sha: string;
  size: number;
  type: string;
  url: string;
  _links: {
    self: string;
    git: string;
    html: string;
  };
}

export function fetchFile(args: { octokit: Octokit; fileInfo: GitHubFileInfo }) {
  return args.octokit.repos
    .getContent({
      repo: args.fileInfo.repo,
      owner: args.fileInfo.org,
      ref: args.fileInfo.ref,
      path: args.fileInfo.path,
    })
    .then((res) => res)
    .catch((e) => {
      console.debug(`Error fetching ${args.fileInfo.path} with Octokit. Fallback is 'raw.githubusercontent.com'.`);
      return fetch(
        `https://raw.githubusercontent.com/${args.fileInfo.org}/${args.fileInfo.repo}/${args.fileInfo.ref}/${args.fileInfo.path}`
      ).then((res) => (res.ok ? res.text() : Promise.resolve(undefined)));
    });
}

export const fetchSampleDefinitions = async (octokit: Octokit): Promise<Sample[]> => {
  const res = await fetchFile({
    octokit,
    fileInfo: {
      org: kieSamplesRepo.org,
      repo: kieSamplesRepo.repo,
      ref: kieSamplesRepo.ref,
      path: decodeURIComponent(kieSamplesRepo.path),
    },
  });

  const sampleDirs = ((res as any)?.data).filter((sample: RepoContentType) => sample.name !== SAMPLE_TEMPLATE_FOLDER);

  const promises = sampleDirs.map((sample: RepoContentType) => {
    return fetchFile({
      octokit,
      fileInfo: {
        org: kieSamplesRepo.org,
        repo: kieSamplesRepo.repo,
        ref: kieSamplesRepo.ref,
        path: decodeURIComponent(`${kieSamplesRepo.path}/${sample.name}`),
      },
    });
  });

  return Promise.all(promises).then((promiseData) => {
    return Promise.all(
      promiseData.map((sampleData) => {
        return Promise.all(
          sampleData.data.map(async (files: RepoContentType) => {
            let svgResponse;
            let definitionRes;

            if (files.name === SAMPLE_DEFINITION_FILE) {
              const rawUrl = new URL((files as RepoContentType).download_url);
              definitionRes = await fetch(rawUrl.toString());
              if (!definitionRes.ok) {
                throw new Error(
                  `Sample definition Error ${definitionRes.status}${
                    definitionRes.statusText ? `- ${definitionRes.statusText}` : ""
                  }`
                );
              }
              const content = JSON.parse(await definitionRes?.text()) as SampleDefinition;

              if (content) {
                const sampleDirPath = dirname(rawUrl.href);
                const sampleId = sampleDirPath.split("/").pop()!;
                const svgUrl = new URL(join(sampleDirPath, content.cover));
                svgResponse = await fetch(svgUrl.toString());
                if (!svgResponse.ok) {
                  throw new Error(
                    `SVG Error ${svgResponse.status}${svgResponse.statusText ? `- ${svgResponse.statusText}` : ""}`
                  );
                }
                const svgContent = await svgResponse.text();

                return {
                  sampleId,
                  name: content.title,
                  description: content.description,
                  category: content.category,
                  svg: svgContent,
                };
              }
            }
          })
        ).then((data) => {
          return data?.reduce((r: any, c: any) => Object.assign(r, c), {});
        });
      })
    );
  });
};

export const fetchSample = async (args: { octokit: Octokit; sampleId: string }) => {
  const res = await fetchFile({
    octokit: args.octokit,
    fileInfo: {
      org: kieSamplesRepo.org,
      repo: kieSamplesRepo.repo,
      ref: kieSamplesRepo.ref,
      path: decodeURIComponent(`${kieSamplesRepo.path}/${args.sampleId}`),
    },
  });
  if (res === undefined) {
    throw new Error(`Sample ${args.sampleId} not found`);
  }

  const sampleFiles = [] as LocalFile[];

  return Promise.all(
    (res as any)?.data?.map(async (file: RepoContentType) => {
      if (file.name === SAMPLE_DEFINITION_FILE || extname(file.name) === SVG_EXTENSION) {
        return;
      }
      const rawUrl = new URL((file as RepoContentType).download_url);
      const response = await fetch(rawUrl.toString());
      if (!response.ok) {
        throw new Error(`${response.status}${response.statusText ? `- ${response.statusText}` : ""}`);
      }
      const content = await response?.text();

      sampleFiles.push({
        path: decodeURIComponent(rawUrl.pathname),
        fileContents: encoder.encode(content),
      });
    })
  ).then(() => sampleFiles);
};
