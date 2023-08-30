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
import {
  FetchFileContentResponse,
  FetchFolderContentResponse,
  FetchSampleDefinitionsResponse,
  FetchSampleFilesResponse,
  GitHubContentData,
  GitHubFileData,
  GitHubFileInfo,
  Sample,
  SampleDefinition,
  SamplesRepositoryInfo,
} from "./types";
import { LocalFile } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/LocalFile";
import { extname, join } from "path";
import { decoder, encoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";

const SVG_EXTENSION = ".svg";
const SAMPLE_DEFINITION_FILE = "definition.json";
const GITHUB_HEADER_RATE_LIMIT_REMAINING = "x-ratelimit-remaining";

const HttpErrors = {
  FORBIDDEN: 403,
  NOT_FOUND: 404,
} as const;

export class SampleService {
  constructor(private readonly octokit: Octokit, private readonly repositoryInfo: SamplesRepositoryInfo) {}

  /**
   * Fetch the sample definitions.
   * @returns Response contining either the sample definitions or error details.
   */
  public async fetchDefinitions(): Promise<FetchSampleDefinitionsResponse> {
    const fileContentResponse = await this.fetchFileContent({
      owner: this.repositoryInfo.org,
      repo: this.repositoryInfo.name,
      ref: this.repositoryInfo.ref,
      path: this.repositoryInfo.paths.sampleDefinitionsJson,
    });

    if (!fileContentResponse.success) {
      return { ...fileContentResponse };
    }

    try {
      const definitions = JSON.parse(fileContentResponse.content) as SampleDefinition[];
      const samples = definitions.map((definition) => ({
        sampleId: definition.sample_path.replace(new RegExp(`^${this.repositoryInfo.paths.samplesFolder}/`), ""),
        definition,
      }));

      return { success: true, samples };
    } catch (e) {
      return {
        success: false,
        error: "Generic",
        message: e.message,
      };
    }
  }

  /**
   * Fetch the cover of a given sample.
   * @param sample The sample object for which the cover is being fetched.
   * @returns Response containing either the content of the SVG cover file for the sample or error details.
   */
  public async fetchCover(sample: Sample): Promise<FetchFileContentResponse> {
    const svgContentResponse = await this.fetchFileContent({
      owner: this.repositoryInfo.org,
      repo: this.repositoryInfo.name,
      ref: this.repositoryInfo.ref,
      path: join(this.repositoryInfo.paths.samplesFolder, sample.sampleId, sample.definition.cover),
    });

    if (!svgContentResponse.success) {
      return { ...svgContentResponse };
    }

    return {
      success: true,
      path: svgContentResponse.path,
      content: svgContentResponse.content,
    };
  }

  /**
   * Fetch the files of a given sample
   * @param sampleId The id of the sample being fetched.
   * @returns Response containing either the files of the sample or error details.
   */
  public async fetchFiles(sampleId: string): Promise<FetchSampleFilesResponse> {
    const sampleFolderFilesResponse = await this.fetchFolderContent({
      fileInfo: {
        owner: this.repositoryInfo.org,
        repo: this.repositoryInfo.name,
        ref: this.repositoryInfo.ref,
        path: decodeURIComponent(`${this.repositoryInfo.paths.samplesFolder}/${sampleId}`),
      },
      onlyFilesRecursively: true,
    });

    if (!sampleFolderFilesResponse.success) {
      return { ...sampleFolderFilesResponse };
    }

    try {
      const promises = sampleFolderFilesResponse.contents
        .filter(
          (file) => file.name !== SAMPLE_DEFINITION_FILE && extname(file.name) !== SVG_EXTENSION && file.type === "file"
        )
        .map((file) =>
          this.fetchFileContent({
            owner: this.repositoryInfo.org,
            repo: this.repositoryInfo.name,
            ref: this.repositoryInfo.ref,
            path: file.path,
          })
        );

      const resolvedPromises = await Promise.all(promises);

      const firstNotSucceeded = resolvedPromises.find((response) => !response.success);
      if (firstNotSucceeded && !firstNotSucceeded.success) {
        return { ...firstNotSucceeded };
      }

      const filesArray: LocalFile[] = resolvedPromises
        .map((response) => {
          if (response.success) {
            return {
              path: decodeURIComponent(response.path).split(
                `${this.repositoryInfo.paths.samplesFolder}/${sampleId}`
              )[1],
              fileContents: encoder.encode(response.content),
            };
          }
        })
        .flat()
        .filter((r): r is LocalFile => r !== undefined);

      return { success: true, files: filesArray };
    } catch (e) {
      return {
        success: false,
        error: "Generic",
        message: e.message,
      };
    }
  }

  /**
   * Fetch the content of a file using GitHub raw URL.
   * @param fileInfo Information of the file to be fetched.
   * @returns Response containing either the file content or error details.
   */
  private async fetchRawFileContent(fileInfo: GitHubFileInfo): Promise<FetchFileContentResponse> {
    try {
      const rawFileResponse = await fetch(
        `https://raw.githubusercontent.com/${fileInfo.owner}/${fileInfo.repo}/${fileInfo.ref}/${fileInfo.path}`
      );
      const content = await rawFileResponse.text();
      return { success: true, content, path: fileInfo.path };
    } catch (e) {
      return {
        success: false,
        error: "Generic",
        message: e.message,
      };
    }
  }

  /**
   * Fetch the content of a file.
   * @param fileInfo Information of the file to be fetched.
   * @returns Response containing either the file content or error details.
   */
  private async fetchFileContent(fileInfo: GitHubFileInfo): Promise<FetchFileContentResponse> {
    try {
      const octokitResponse = await this.octokit.repos.getContent({ ...fileInfo });
      const fileData = octokitResponse.data as GitHubFileData;
      const content = decoder.decode(new Uint8Array(Buffer.from(fileData.content, fileData.encoding)));
      return { success: true, content, path: fileInfo.path };
    } catch (e) {
      if (e.status === HttpErrors.FORBIDDEN && e.headers[GITHUB_HEADER_RATE_LIMIT_REMAINING] === "0") {
        return { success: false, error: "Unauthenticated" };
      }
      if (e.status === HttpErrors.NOT_FOUND) {
        return { success: false, error: "NotFound" };
      }
      return await this.fetchRawFileContent(fileInfo);
    }
  }

  /**
   * Fetch the contents of a folder.
   * @param args.fileInfo Information of the folder to be fetched.
   * @param args.onlyFilesRecursively Fetch only the files, recursively.
   * @returns Response containing either the folder content or error details.
   */
  private async fetchFolderContent(args: {
    fileInfo: GitHubFileInfo;
    onlyFilesRecursively: boolean;
  }): Promise<FetchFolderContentResponse> {
    try {
      const octokitResponse = await this.octokit.repos.getContent({ ...args.fileInfo });

      const contents = octokitResponse.data as GitHubContentData[];

      if (!args.onlyFilesRecursively) {
        return { success: true, contents };
      }

      const promises: Promise<FetchFolderContentResponse>[] = [];

      for (const content of contents) {
        if (content.type === "file") {
          promises.push(Promise.resolve({ success: true, contents: [content] }));
        } else if (content.type === "dir") {
          promises.push(
            this.fetchFolderContent({
              onlyFilesRecursively: true,
              fileInfo: { ...args.fileInfo, path: content.path },
            })
          );
        }
      }

      const resolvedPromises = await Promise.all(promises);

      const firstNotSucceeded = resolvedPromises.find((response) => !response.success);
      if (firstNotSucceeded && !firstNotSucceeded.success) {
        return { ...firstNotSucceeded };
      }

      const folderContentDataArray = resolvedPromises
        .map((response) => {
          if (response.success) {
            return response.contents;
          }
        })
        .flat()
        .filter((r): r is GitHubContentData => r !== undefined);

      return { success: true, contents: folderContentDataArray };
    } catch (e) {
      if (e.status === HttpErrors.FORBIDDEN && e.headers[GITHUB_HEADER_RATE_LIMIT_REMAINING] === "0") {
        return { success: false, error: "Unauthenticated" };
      }
      if (e.status === HttpErrors.NOT_FOUND) {
        return { success: false, error: "NotFound" };
      }
      return {
        success: false,
        error: "Generic",
        message: e.message,
      };
    }
  }
}
