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

import * as index from "@kie-tools-core/chrome-extension";
import { GitHubPageType } from "@kie-tools-core/chrome-extension/dist/app/github/GitHubPageType";

function setWindowLocationPathname(pathname: string) {
  window = Object.create(window);
  delete (window as any).location;
  window.location = { pathname: pathname } as any;
}

describe("extractFileInfoFromUrl", () => {
  test("default", async () => {
    setWindowLocationPathname("/org/repo/blob/my-branch/this/is/a/foo.test");
    const fileInfo = index.extractFileInfoFromUrl();

    expect(fileInfo).toStrictEqual({
      gitRef: "my-branch",
      repo: "repo",
      org: "org",
      path: "this/is/a/foo.test",
    });
  });
});

describe("discoverCurrentGitHubPageType", () => {
  test("view", async () => {
    setWindowLocationPathname("/org/repo/blob/my-branch/this/is/a/foo.test");
    const type = index.discoverCurrentGitHubPageType();
    expect(type).toStrictEqual(GitHubPageType.VIEW);
  });

  test("edit", async () => {
    setWindowLocationPathname("/org/repo/edit/my-branch/this/is/a/foo.test");
    const type = index.discoverCurrentGitHubPageType();
    expect(type).toStrictEqual(GitHubPageType.EDIT);
  });

  test("repo home", async () => {
    setWindowLocationPathname("github.com/organization/repositoryName");
    const type = index.discoverCurrentGitHubPageType();
    expect(type).toStrictEqual(GitHubPageType.CAN_NOT_BE_DETERMINED_FROM_URL);
  });

  test("repo home with branch", async () => {
    setWindowLocationPathname("github.com/organization/repositoryName/tree/main");
    const type = index.discoverCurrentGitHubPageType();
    expect(type).toStrictEqual(GitHubPageType.REPO_HOME);
  });

  test("repo folder with branch", async () => {
    setWindowLocationPathname("github.com/organization/repositoryName/tree/main/some/folders/here");
    const type = index.discoverCurrentGitHubPageType();
    expect(type).toStrictEqual(GitHubPageType.NOT_SUPPORTED);
  });

  test("pr home", async () => {
    setWindowLocationPathname("/org/repo/pull/1");
    const type = index.discoverCurrentGitHubPageType();
    expect(type).toStrictEqual(GitHubPageType.PR_HOME);
  });

  test("pr files", async () => {
    setWindowLocationPathname("/org/repo/pull/1/files");
    const type = index.discoverCurrentGitHubPageType();
    expect(type).toStrictEqual(GitHubPageType.PR_FILES);
  });

  test("pr commit", async () => {
    setWindowLocationPathname("/org/repo/pull/1/commits");
    const type = index.discoverCurrentGitHubPageType();
    expect(type).toStrictEqual(GitHubPageType.PR_COMMITS);
  });

  test("pr checks", async () => {
    setWindowLocationPathname("/org/repo/pull/1/checks");
    const type = index.discoverCurrentGitHubPageType();
    expect(type).toStrictEqual(GitHubPageType.NOT_SUPPORTED);
  });

  test("any", async () => {
    setWindowLocationPathname("/");
    const type = index.discoverCurrentGitHubPageType();
    expect(type).toStrictEqual(GitHubPageType.CAN_NOT_BE_DETERMINED_FROM_URL);
  });
});
