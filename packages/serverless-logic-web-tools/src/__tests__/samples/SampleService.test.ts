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
import { SampleService } from "../../samples/SampleService";
import { KIE_SAMPLES_REPOSITORY_INFO } from "../../samples/SampleConstants";
import fetchMock from "jest-fetch-mock";
import {
  FetchErrorResponse,
  FetchFileContentSuccess,
  FetchSampleDefinitionsSuccess,
  FetchSampleFilesSuccess,
  GitHubContentData,
  Sample,
  SampleDefinition,
} from "../../samples/types";
import { LocalFile } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/LocalFile";
import { basename } from "path";
import { encoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";

(global as any).fetch = fetchMock;

export const createOctokitMock = (mockGetContent: jest.Mock<any, any>): Octokit => {
  const mockRepos = {
    getContent: mockGetContent,
  };

  const mockOctokit = {
    repos: mockRepos,
  } as unknown as Octokit;

  return mockOctokit;
};

const sampleDefinitionTest: SampleDefinition = {
  type: "sample",
  title: "Sample Test",
  description: "Sample Test",
  category: "serverless-workflow",
  cover: "./foo.svg",
  sample_path: "samples/sample-test",
  status: "ok",
  authors: [{ name: "", email: "", github: "", social: [{ id: "", network: "" }] }],
  tags: [],
  dependencies: [],
  related_to: [],
  resources: [],
};

const throwErrorForUnauthenticatedFn = async () => {
  const error = new Error("Forbidden") as any;
  error.status = 403;
  error.headers = {
    "x-ratelimit-remaining": "0",
  };
  throw error;
};

const throwErrorForNotFoundFn = async () => {
  const error = new Error("NotFound") as any;
  error.status = 404;
  throw error;
};

const throwErrorForGenericFn = async () => {
  throw new Error("Generic error");
};

describe("SampleService", () => {
  describe("fetchDefinitions", () => {
    beforeEach(() => {
      fetchMock.resetMocks();
    });
    it("should respond with success", async () => {
      const mockGetContent = jest.fn().mockImplementation(async () => ({
        data: { content: JSON.stringify([sampleDefinitionTest]) },
      }));
      const service = new SampleService(createOctokitMock(mockGetContent), KIE_SAMPLES_REPOSITORY_INFO);
      const response = await service.fetchDefinitions();
      expect(fetchMock).not.toHaveBeenCalled();
      expect(response.success).toBeTruthy();
      expect((response as FetchSampleDefinitionsSuccess).samples.length).toBe(1);
    });
    it("should respond with error Unauthenticated", async () => {
      const mockGetContent = jest.fn().mockImplementation(throwErrorForUnauthenticatedFn);
      const service = new SampleService(createOctokitMock(mockGetContent), KIE_SAMPLES_REPOSITORY_INFO);
      const response = await service.fetchDefinitions();
      expect(response.success).toBeFalsy();
      expect((response as FetchErrorResponse).error).toBe("Unauthenticated");
    });
    it("should respond with error NotFound", async () => {
      const mockGetContent = jest.fn().mockImplementation(throwErrorForNotFoundFn);
      const service = new SampleService(createOctokitMock(mockGetContent), KIE_SAMPLES_REPOSITORY_INFO);
      const response = await service.fetchDefinitions();
      expect(response.success).toBeFalsy();
      expect((response as FetchErrorResponse).error).toBe("NotFound");
    });
    it("should respond with error Generic", async () => {
      fetchMock.mockReject(new Error());
      const mockGetContent = jest.fn().mockImplementation(throwErrorForGenericFn);
      const service = new SampleService(createOctokitMock(mockGetContent), KIE_SAMPLES_REPOSITORY_INFO);
      const response = await service.fetchDefinitions();
      expect(fetchMock).toHaveBeenCalled();
      expect(response.success).toBeFalsy();
      expect((response as FetchErrorResponse).error).toBe("Generic");
    });
    it("should respond with success but using fallback fetch", async () => {
      fetchMock.mockResponseOnce(JSON.stringify([sampleDefinitionTest]));
      const mockGetContent = jest.fn().mockImplementation(throwErrorForGenericFn);
      const service = new SampleService(createOctokitMock(mockGetContent), KIE_SAMPLES_REPOSITORY_INFO);
      const response = await service.fetchDefinitions();
      expect(fetchMock).toHaveBeenCalled();
      expect(response.success).toBeTruthy();
      expect((response as FetchSampleDefinitionsSuccess).samples.length).toBe(1);
    });
  });

  describe("fetchCover", () => {
    const sampleTest: Sample = { sampleId: "sample-test", definition: sampleDefinitionTest };
    const svgContent = JSON.stringify("svg-content");
    beforeEach(() => {
      fetchMock.resetMocks();
    });
    it("should respond with success", async () => {
      const mockGetContent = jest.fn().mockImplementation(async () => ({ data: { content: svgContent } }));
      const service = new SampleService(createOctokitMock(mockGetContent), KIE_SAMPLES_REPOSITORY_INFO);
      const response = await service.fetchCover(sampleTest);
      expect(fetchMock).not.toHaveBeenCalled();
      expect(response.success).toBeTruthy();
      expect((response as FetchFileContentSuccess).content).toBe(svgContent);
    });
    it("should respond with error Unauthenticated", async () => {
      const mockGetContent = jest.fn().mockImplementation(throwErrorForUnauthenticatedFn);
      const service = new SampleService(createOctokitMock(mockGetContent), KIE_SAMPLES_REPOSITORY_INFO);
      const response = await service.fetchCover(sampleTest);
      expect(response.success).toBeFalsy();
      expect((response as FetchErrorResponse).error).toBe("Unauthenticated");
    });
    it("should respond with error NotFound", async () => {
      const mockGetContent = jest.fn().mockImplementation(throwErrorForNotFoundFn);
      const service = new SampleService(createOctokitMock(mockGetContent), KIE_SAMPLES_REPOSITORY_INFO);
      const response = await service.fetchCover(sampleTest);
      expect(response.success).toBeFalsy();
      expect((response as FetchErrorResponse).error).toBe("NotFound");
    });
    it("should respond with error Generic", async () => {
      fetchMock.mockReject(new Error());
      const mockGetContent = jest.fn().mockImplementation(throwErrorForGenericFn);
      const service = new SampleService(createOctokitMock(mockGetContent), KIE_SAMPLES_REPOSITORY_INFO);
      const response = await service.fetchCover(sampleTest);
      expect(fetchMock).toHaveBeenCalled();
      expect(response.success).toBeFalsy();
      expect((response as FetchErrorResponse).error).toBe("Generic");
    });
    it("should respond with success but using fallback fetch", async () => {
      fetchMock.mockResponseOnce(svgContent);
      const mockGetContent = jest.fn().mockImplementation(throwErrorForGenericFn);
      const service = new SampleService(createOctokitMock(mockGetContent), KIE_SAMPLES_REPOSITORY_INFO);
      const response = await service.fetchCover(sampleTest);
      expect(fetchMock).toHaveBeenCalled();
      expect(response.success).toBeTruthy();
      expect((response as FetchFileContentSuccess).content).toBe(svgContent);
    });
  });

  describe("fetchFiles", () => {
    const folderData: GitHubContentData[] = [
      { name: "test1.sw.json", path: "samples/sample-test/test1.sw.json", type: "file" },
      { name: "test2.sw.json", path: "samples/sample-test/test2.sw.json", type: "file" },
      { name: "test3.sw.json", path: "samples/sample-test/test3.sw.json", type: "file" },
    ];
    const files: LocalFile[] = [
      { path: "/test1.sw.json", fileContents: encoder.encode("content") },
      { path: "/test2.sw.json", fileContents: encoder.encode("content") },
      { path: "/test3.sw.json", fileContents: encoder.encode("content") },
    ];
    beforeEach(() => {
      fetchMock.resetMocks();
    });
    it("should respond with success", async () => {
      const mockGetContent = jest.fn().mockImplementation(async ({ _owner, _repo, _ref, path }) => {
        const folderName = basename(path);
        const isFolder = folderName.indexOf(".") === -1;
        if (isFolder) {
          return {
            data: folderData,
          };
        }
        return {
          data: {
            content: encoder.encode("content"),
          },
        };
      });
      const service = new SampleService(createOctokitMock(mockGetContent), KIE_SAMPLES_REPOSITORY_INFO);
      const response = await service.fetchFiles("sample-test");
      expect(fetchMock).not.toHaveBeenCalled();
      expect(response.success).toBeTruthy();
      expect((response as FetchSampleFilesSuccess).files).toStrictEqual(files);
    });
    it("should respond with error Unauthenticated", async () => {
      const mockGetContent = jest.fn().mockImplementation(throwErrorForUnauthenticatedFn);
      const service = new SampleService(createOctokitMock(mockGetContent), KIE_SAMPLES_REPOSITORY_INFO);
      const response = await service.fetchFiles("sample-test");
      expect(response.success).toBeFalsy();
      expect((response as FetchErrorResponse).error).toBe("Unauthenticated");
    });
    it("should respond with error NotFound", async () => {
      const mockGetContent = jest.fn().mockImplementation(throwErrorForNotFoundFn);
      const service = new SampleService(createOctokitMock(mockGetContent), KIE_SAMPLES_REPOSITORY_INFO);
      const response = await service.fetchFiles("sample-test");
      expect(response.success).toBeFalsy();
      expect((response as FetchErrorResponse).error).toBe("NotFound");
    });
    it("should respond with error Generic", async () => {
      fetchMock.mockReject(new Error());
      const mockGetContent = jest.fn().mockImplementation(throwErrorForGenericFn);
      const service = new SampleService(createOctokitMock(mockGetContent), KIE_SAMPLES_REPOSITORY_INFO);
      const response = await service.fetchFiles("sample-test");
      expect(response.success).toBeFalsy();
      expect((response as FetchErrorResponse).error).toBe("Generic");
    });
  });
});
