/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { GithubService } from "../../settings/GithubService";

const githubService = new GithubService();

describe("githubService::isGithub", () => {
  test("should be true", () => {
    [
      "https://github.com/the_org/the_repo/blob/the_ref/the_file.bpmn",
      "http://github.com/the_org/the_repo/blob/the_ref/the_file.bpmn",
      "http://www.github.com/the_org/the_repo/blob/the_ref/the_file.bpmn",
      "https://www.github.com/the_org/the_repo/blob/the_ref/the_file.bpmn",
      "www.github.com/the_org/the_repo/blob/the_ref/the_file.bpmn",
      "github.com/the_org/the_repo/blob/the_ref/the_file.bpmn",
    ].forEach((url) => expect(githubService.isGithub(url)).toBeTruthy());
  });

  test("should be false", () => {
    [
      "https://gathub.com/the_org/the_repo/blob/the_ref/the_file.bpmn",
      "http://redhat.com/the_org/the_repo/blob/the_ref/the_file.bpmn",
    ].forEach((url) => expect(githubService.isGithub(url)).toBeFalsy());
  });
});

describe("githubService::retrieveFileInfo", () => {
  test("check file info", () => {
    const fileUrl = "https://github.com/the_org/the_repo/blob/the_ref/the_file.bpmn";
    const fileInfo = githubService.retrieveFileInfo(fileUrl);
    expect(fileInfo.org).toEqual("the_org");
    expect(fileInfo.repo).toEqual("the_repo");
    expect(fileInfo.gitRef).toEqual("the_ref");
    expect(fileInfo.path).toEqual("the_file.bpmn");
  });
});

describe("githubService::isGist", () => {
  test("should be true", () => {
    [
      "https://gist.github.com/user/gist_id",
      "http://gist.github.com/user/gist_id",
      "http://www.gist.github.com/user/gist_id",
      "https://www.gist.github.com/user/gist_id",
      "www.gist.github.com/user/gist_id",
      "gist.github.com/user/gist_id",
    ].forEach((url) => expect(githubService.isGist(url)).toBeTruthy());
  });

  test("should be false", () => {
    ["https://gist.gathub.com/user/gist_id", "http://gist.redhat.com/user/gist_id"].forEach((url) =>
      expect(githubService.isGist(url)).toBeFalsy()
    );
  });
});

describe("githubService::extractGistId", () => {
  test("check gist id", () => {
    const fileUrl = "https://gist.github.com/user/gist_id";
    const gistId = githubService.extractGistId(fileUrl);
    expect(gistId).toEqual("gist_id");
  });
});

describe("githubService::extractGistIdFromRawUrl", () => {
  test("extract gist id from raw url", () => {
    const rawUrl = "https://gist.githubusercontent.com/test/gist-id/raw/commit-hash/test.bpmn";
    const gistId = githubService.extractGistIdFromRawUrl(rawUrl);
    expect(gistId).toEqual("gist-id");
  });
});

describe("githubService::extractUserLoginFromGistRawUrl", () => {
  test("extract user login from gist raw url", () => {
    const rawUrl = "https://gist.githubusercontent.com/test/gist-id/raw/commit-hash/test.bpmn";
    const userLogin = githubService.extractUserLoginFromFileUrl(rawUrl);
    expect(userLogin).toEqual("test");
  });

  test("extract user login from repo raw url", () => {
    const rawUrl = "https://raw.githubusercontent.com/test/gist-id/raw/commit-hash/test.bpmn";
    const userLogin = githubService.extractUserLoginFromFileUrl(rawUrl);
    expect(userLogin).toEqual("test");
  });
});

describe("githubService::removeCommitHashFromGistRawUrl", () => {
  test("should remove commit has from raw url", () => {
    const rawUrl = "https://gist.githubusercontent.com/test/gist-id/raw/commit-hash/test.bpmn";
    const urlWithoutCommitHash = githubService.removeCommitHashFromGistRawUrl(rawUrl);
    expect(urlWithoutCommitHash).toEqual("https://gist.githubusercontent.com/test/gist-id/raw/test.bpmn");
  });
});

describe("githubService::isGistRaw", () => {
  test("should be a raw url", () => {
    expect(githubService.isGistRaw("gist.githubusercontent.com/")).toBeTruthy();
  });

  test("shouldn't be a raw url", () => {
    expect(githubService.isGistRaw("github.com/")).toBeFalsy();
  });
});

describe("githubService::extractGistFilename", () => {
  [
    { rawUrl: "https://gist.github.com/ljmotta/gist-id#file-test-bpmn", expected: "test.bpmn" },
    { rawUrl: "https://gist.github.com/ljmotta/gist-id#file-test-1-bpmn", expected: "test-1.bpmn" },
  ].forEach(({ rawUrl, expected }) => {
    const urlWithoutCommitHash = githubService.extractGistFilename(rawUrl);
    expect(urlWithoutCommitHash).toEqual(expected);
  });

  test("shouldn't extract the file name from the gist url", () => {
    const rawUrl = "https://gist.github.com/ljmotta/db1c79d9919ffa3eb40ad8b7cada5b7f";
    const urlWithoutCommitHash = githubService.extractGistFilename(rawUrl);
    expect(urlWithoutCommitHash).toBeUndefined();
  });
});

describe("githubService::extractGistFilenameFromRawUrl", () => {
  test("should extract the file name from the gist raw url", () => {
    [
      { rawUrl: "https://gist.githubusercontent.com/test/gist-id/raw/commit-hash/test.bpmn", expected: "test.bpmn" },
      {
        rawUrl: "https://gist.githubusercontent.com/test/gist-id/raw/commit-hash/test-1.bpmn",
        expected: "test-1.bpmn",
      },
      {
        rawUrl: "https://gist.githubusercontent.com/test/gist-id/raw/commit-hash/test%25201.dmn",
        expected: "test 1.dmn",
      },
      {
        rawUrl: "https://gist.githubusercontent.com/test/gist-id/raw/commit-hash/Test%201.dmn",
        expected: "Test 1.dmn",
      },
    ].forEach(({ rawUrl, expected }) => {
      const urlWithoutCommitHash = githubService.extractGistFilenameFromRawUrl(rawUrl);
      expect(urlWithoutCommitHash).toEqual(expected);
    });
  });
});

describe("githubService::hasGistScope", () => {
  test("should have gist scope", () => {
    [{ "x-oauth-scopes": "gist" }, { "x-oauth-scopes": "gist, repo" }, { "x-oauth-scopes": "user, gist" }].forEach(
      (headers) => {
        const hasGistScope = githubService.hasGistScope(headers);
        expect(hasGistScope).toBeTruthy();
      }
    );
  });

  test("shouldn't have gist scope", () => {
    [{ "x-oauth-scopes": "" }, { "x-oauth-scopes": "repo" }, { "x-oauth-scopes": "user, gis" }].forEach((headers) => {
      const hasGistScope = githubService.hasGistScope(headers);
      expect(hasGistScope).toBeFalsy();
    });
  });
});
