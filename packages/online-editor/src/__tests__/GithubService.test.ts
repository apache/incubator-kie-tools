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

import { GithubService } from "../common/GithubService";

const githubService = new GithubService();

describe("githubService::isGithub", () => {
  test("should be true", () => {
    [
      "https://github.com/the_org/the_repo/blob/the_ref/the_file.bpmn",
      "http://github.com/the_org/the_repo/blob/the_ref/the_file.bpmn",
      "http://www.github.com/the_org/the_repo/blob/the_ref/the_file.bpmn",
      "https://www.github.com/the_org/the_repo/blob/the_ref/the_file.bpmn",
      "www.github.com/the_org/the_repo/blob/the_ref/the_file.bpmn",
      "github.com/the_org/the_repo/blob/the_ref/the_file.bpmn"
    ].forEach(url => expect(githubService.isGithub(url)).toBeTruthy());
  });

  test("should be false", () => {
    [
      "https://gathub.com/the_org/the_repo/blob/the_ref/the_file.bpmn",
      "http://redhat.com/the_org/the_repo/blob/the_ref/the_file.bpmn"
    ].forEach(url => expect(githubService.isGithub(url)).toBeFalsy());
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
