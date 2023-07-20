/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import {
  ParsedWorkspaceFileRelativePath,
  extractExtension,
  parseWorkspaceFileRelativePath,
} from "../../src/relativePath/WorkspaceFileRelativePathParser";

describe("WorkspaceFileRelativePathParser :: extractExtension", () => {
  it.each([
    ["foo.yaml", "yaml"],
    ["foo.yml", "yml"],
    ["foo.json", "json"],
    [".gitignore", "gitignore"],
    ["noExtension", ""],
    ["bar.sw.yml", "sw.yml"],
    ["bar.yard.json", "yard.json"],
    ["bar.yard.yaml.yard.yaml", "yard.yaml"],
    ["bar.dash.yaml", "dash.yaml"],
    ["a/b/c/foo.json", "json"],
    ["a/b/c/.gitignore", "gitignore"],
    ["a/b/c/noExtension", ""],
    ["a/b/c/bar.sw.json", "sw.json"],
    ["asd.dmn.dmn", "dmn"],
    ["a/b/c'asd.DMn", "DMn"],
    ["foo.bpmN2", "bpmN2"],
    ["pmml.pmml", "pmml"],
    ["TEST.BPMN", "BPMN"],
  ])("should extract extension properly for %p", (relativePath: string, extension: string) => {
    expect(extractExtension(relativePath)).toBe(extension);
  });
});

describe("WorkspaceFileRelativePathParser :: parseWorkspaceFileRelativePath", () => {
  it.each([
    [
      "foo.yaml",
      {
        relativePathWithoutExtension: "foo",
        relativeDirPath: "",
        extension: "yaml",
        nameWithoutExtension: "foo",
        name: "foo.yaml",
      },
    ],
    [
      "foo.yml",
      {
        relativePathWithoutExtension: "foo",
        relativeDirPath: "",
        extension: "yml",
        nameWithoutExtension: "foo",
        name: "foo.yml",
      },
    ],
    [
      "foo.json",
      {
        relativePathWithoutExtension: "foo",
        relativeDirPath: "",
        extension: "json",
        nameWithoutExtension: "foo",
        name: "foo.json",
      },
    ],
    [
      ".gitignore",
      {
        relativePathWithoutExtension: "",
        relativeDirPath: "",
        extension: "gitignore",
        nameWithoutExtension: "",
        name: ".gitignore",
      },
    ],
    [
      ".gitignore.gitignore",
      {
        relativePathWithoutExtension: ".gitignore",
        relativeDirPath: "",
        extension: "gitignore",
        nameWithoutExtension: ".gitignore",
        name: ".gitignore.gitignore",
      },
    ],
    [
      "noExtension",
      {
        relativePathWithoutExtension: "noExtension",
        relativeDirPath: "",
        extension: "",
        nameWithoutExtension: "noExtension",
        name: "noExtension",
      },
    ],
    [
      "bar.sw.yml",
      {
        relativePathWithoutExtension: "bar",
        relativeDirPath: "",
        extension: "sw.yml",
        nameWithoutExtension: "bar",
        name: "bar.sw.yml",
      },
    ],
    [
      "bar.yard.yml",
      {
        relativePathWithoutExtension: "bar",
        relativeDirPath: "",
        extension: "yard.yml",
        nameWithoutExtension: "bar",
        name: "bar.yard.yml",
      },
    ],
    [
      "bar.yard.yaml.yard.yaml",
      {
        relativePathWithoutExtension: "bar.yard.yaml",
        relativeDirPath: "",
        extension: "yard.yaml",
        nameWithoutExtension: "bar.yard.yaml",
        name: "bar.yard.yaml.yard.yaml",
      },
    ],
    [
      "bar.dash.yml",
      {
        relativePathWithoutExtension: "bar",
        relativeDirPath: "",
        extension: "dash.yml",
        nameWithoutExtension: "bar",
        name: "bar.dash.yml",
      },
    ],
    [
      "a/b/c/foo.json",
      {
        relativePathWithoutExtension: "a/b/c/foo",
        relativeDirPath: "a/b/c",
        extension: "json",
        nameWithoutExtension: "foo",
        name: "foo.json",
      },
    ],
    [
      "a/b/c/.gitignore",
      {
        relativePathWithoutExtension: "a/b/c/",
        relativeDirPath: "a/b/c",
        extension: "gitignore",
        nameWithoutExtension: "",
        name: ".gitignore",
      },
    ],
    [
      "a/b/c/.gitignore.gitignore",
      {
        relativePathWithoutExtension: "a/b/c/.gitignore",
        relativeDirPath: "a/b/c",
        extension: "gitignore",
        nameWithoutExtension: ".gitignore",
        name: ".gitignore.gitignore",
      },
    ],
    [
      "a/b/c/noExtension",
      {
        relativePathWithoutExtension: "a/b/c/noExtension",
        relativeDirPath: "a/b/c",
        extension: "",
        nameWithoutExtension: "noExtension",
        name: "noExtension",
      },
    ],
    [
      "a/b/c/bar.sw.json",
      {
        relativePathWithoutExtension: "a/b/c/bar",
        relativeDirPath: "a/b/c",
        extension: "sw.json",
        nameWithoutExtension: "bar",
        name: "bar.sw.json",
      },
    ],
    [
      "asd.dmn.dmn",
      {
        relativePathWithoutExtension: "asd.dmn",
        relativeDirPath: "",
        extension: "dmn",
        nameWithoutExtension: "asd.dmn",
        name: "asd.dmn.dmn",
      },
    ],
    [
      "a/b/c'asd.DMn",
      {
        relativePathWithoutExtension: "a/b/c'asd",
        relativeDirPath: "a/b",
        extension: "DMn",
        nameWithoutExtension: "c'asd",
        name: "c'asd.DMn",
      },
    ],
    [
      "foo.bpmN2",
      {
        relativePathWithoutExtension: "foo",
        relativeDirPath: "",
        extension: "bpmN2",
        nameWithoutExtension: "foo",
        name: "foo.bpmN2",
      },
    ],
    [
      "pmml.pmml",
      {
        relativePathWithoutExtension: "pmml",
        relativeDirPath: "",
        extension: "pmml",
        nameWithoutExtension: "pmml",
        name: "pmml.pmml",
      },
    ],
    [
      "TEST.BPMN",
      {
        relativePathWithoutExtension: "TEST",
        relativeDirPath: "",
        extension: "BPMN",
        nameWithoutExtension: "TEST",
        name: "TEST.BPMN",
      },
    ],
  ])(
    "should parse workspace file relative path properly for %p",
    (relativePath: string, expectedResult: ParsedWorkspaceFileRelativePath) => {
      expect(parseWorkspaceFileRelativePath(relativePath)).toStrictEqual(expectedResult);
    }
  );
});
