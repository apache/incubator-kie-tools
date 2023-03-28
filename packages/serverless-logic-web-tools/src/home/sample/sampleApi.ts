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
import { basename, extname } from "path";
import { encoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { Sample, SampleType } from "./SampleCard";

export const kieSamplesRepo = {
  org: "kiegroup",
  repo: "kie-samples",
  ref: "main",
  path: "samples",
};

const fileExtns = ["sw.yml", "sw.yaml", "sw.json", "sw.project", "dash.yml", "dash.yaml"];

export interface repoContentType {
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

export function fetchFile(octokit: Octokit, org: string, repo: string, ref: string, path: string) {
  return octokit.repos
    .getContent({
      repo: repo,
      owner: org,
      ref: ref,
      path: path,
    })
    .then((res) => res)
    .catch((e) => {
      console.debug(`Error fetching ${path} with Octokit. Fallback is 'raw.githubusercontent.com'.`);
      return fetch(`https://raw.githubusercontent.com/${org}/${repo}/${ref}/${path}`).then((res) =>
        res.ok ? res.text() : Promise.resolve(undefined)
      );
    });
}

export const fetchSampleDefinitions = async (octokit: Octokit): Promise<Sample[]> => {
  const res = await fetchFile(
    octokit,
    kieSamplesRepo.org,
    kieSamplesRepo.repo,
    kieSamplesRepo.ref,
    decodeURIComponent(kieSamplesRepo.path)
  );

  const sampleDirs = ((res as any)?.data).filter((sample: repoContentType) => sample.name !== "template");

  const promises = sampleDirs.map((sample: repoContentType) => {
    return fetchFile(
      octokit,
      kieSamplesRepo.org,
      kieSamplesRepo.repo,
      kieSamplesRepo.ref,
      decodeURIComponent(`${kieSamplesRepo.path}/${sample.name}`)
    );
  });

  return Promise.all(promises).then((promiseData) => {
    return Promise.all(
      promiseData.map((sampleData) => {
        return Promise.all(
          sampleData.data.map(async (files: repoContentType) => {
            let svgResponse;
            let definitionRes;
            const file = files.name.split(".");
            const type = `${file[1]}.${file[2]}` as unknown as SampleType;
            const fileName = file[0];

            if (files.name === "definition.json") {
              const rawUrl = new URL((files as repoContentType).download_url);
              definitionRes = await fetch(rawUrl.toString());
              if (!definitionRes.ok) {
                console.log(
                  `${definitionRes.status}${definitionRes.statusText ? `- ${definitionRes.statusText}` : ""}`
                );
                return;
              }
              const content = JSON.parse(await definitionRes?.text());
              return { name: content.title, description: content.description };
            } else if (files.name.split(".")[1] === "svg") {
              const rawUrl = new URL((files as repoContentType).download_url);
              svgResponse = await fetch(rawUrl.toString());
              if (!svgResponse.ok) {
                console.log(`${svgResponse.status}${svgResponse.statusText ? `- ${svgResponse.statusText}` : ""}`);
                return;
              }
              const svg = await svgResponse.text();
              if (svg) {
                return { svg };
              }
            } else if (fileExtns.includes(type)) {
              const repoUrl = new URL((files as repoContentType).download_url);
              return { type, fileName, repoUrl };
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
  const res = await fetchFile(
    args.octokit,
    kieSamplesRepo.org,
    kieSamplesRepo.repo,
    kieSamplesRepo.ref,
    decodeURIComponent(`${kieSamplesRepo.path}/${args.sampleId}`)
  );
  if (res === undefined) {
    throw new Error(`Sample ${args.sampleId} not found`);
  }

  const sampleFiles = [] as LocalFile[];

  return Promise.all(
    (res as any)?.data?.map(async (file: repoContentType) => {
      if (file.name === "definition.json" || file.name.split(".")[1] === "svg") {
        return;
      }
      const rawUrl = new URL((file as repoContentType).download_url);
      const response = await fetch(rawUrl.toString());
      if (!response.ok) {
        throw new Error(`${response.status}${response.statusText ? `- ${response.statusText}` : ""}`);
      }
      const content = await response?.text();

      sampleFiles.push({
        path: basename(decodeURIComponent(rawUrl.pathname)),
        fileContents: encoder.encode(content),
      });
    })
  ).then(() => sampleFiles);
  return sampleFiles;
};
