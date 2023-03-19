/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { isProject, isSingleModuleProject } from "../../project";

const newFile = (path: string) =>
  new WorkspaceFile({
    relativePath: path,
    workspaceId: "workspace-id",
    getFileContents: async () => new Uint8Array(),
  });

describe("project", () => {
  describe("isProject", () => {
    it.each([
      [false, "an empty file list", []],
      [false, "one file that is not pom.xml in the root", [newFile("foo.txt")]],
      [false, "one file that is not pom.xml in a folder", [newFile("bar/foo.txt")]],
      [false, "two files that are not pom.xml in a folder", [newFile("bar/foo.txt"), newFile("bar/foo2.txt")]],
      [false, "a pom.xml not in the root", [newFile("bar/pom.xml")]],
      [false, "a pom.xml not in the root and more files", [newFile("bar/foo.txt"), newFile("bar/pom.xml")]],
      [true, "a pom.xml in the root", [newFile("pom.xml")]],
      [true, "a pom.xml in the root and more files", [newFile("foo.txt"), newFile("pom.xml")]],
    ])(`should be %s when having %s`, (result, _desc, files) => {
      expect(isProject(files)).toBe(result);
    });
  });

  describe("isSingleModuleProject", () => {
    test.each([
      [false, "an empty file list", []],
      [false, "one file that is not pom.xml in the root", [newFile("foo.txt")]],
      [false, "one file that is not pom.xml in a folder", [newFile("bar/foo.txt")]],
      [false, "two files that are not pom.xml in a folder", [newFile("bar/foo.txt"), newFile("bar/foo2.txt")]],
      [false, "a pom.xml not in the root", [newFile("bar/pom.xml")]],
      [false, "a pom.xml not in the root and more files", [newFile("bar/foo.txt"), newFile("bar/pom.xml")]],
      [false, "a pom.xml in the root an another in a folder", [newFile("pom.xml"), newFile("bar/pom.xml")]],
      [true, "a pom.xml in the root", [newFile("pom.xml")]],
      [true, "a pom.xml in the root and more files", [newFile("foo.txt"), newFile("pom.xml"), newFile("bar/foo.txt")]],
    ])("should be %s when having %s", (result, _desc, files) => {
      expect(isSingleModuleProject(files)).toBe(result);
    });
  });
});
