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

import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { Minimatch } from "minimatch";
import { GLOB_PATTERN, splitFiles } from "../../extension";

const createWorkspaceFileMock = (args: Partial<ConstructorParameters<typeof WorkspaceFile>[0]>): WorkspaceFile =>
  new WorkspaceFile({
    workspaceId: args.workspaceId ?? "1",
    relativePath: args.relativePath ?? "model.sw.json",
    getFileContents: args.getFileContents ?? (async () => new Uint8Array(255)),
  });

describe("extension", () => {
  describe("splitFiles", () => {
    it("should split files into 0 editable and 0 readonly files ", () => {
      const { editableFiles, readonlyFiles } = splitFiles([]);
      expect(editableFiles).toHaveLength(0);
      expect(readonlyFiles).toHaveLength(0);
    });
    it("should split files into 1 editable and 0 readonly files ", () => {
      const files = [createWorkspaceFileMock({ relativePath: "some/path/model.sw.json" })];
      const { editableFiles, readonlyFiles } = splitFiles(files);
      expect(editableFiles).toHaveLength(1);
      expect(readonlyFiles).toHaveLength(0);
    });
    it("should split files into 2 editable and 0 readonly files ", () => {
      const files = [
        createWorkspaceFileMock({ relativePath: "some/path/model.sw.json" }),
        createWorkspaceFileMock({ relativePath: "some/path/model.dash.yaml" }),
      ];
      const { editableFiles, readonlyFiles } = splitFiles(files);
      expect(editableFiles).toHaveLength(2);
      expect(readonlyFiles).toHaveLength(0);
    });
    it("should split files into 0 editable and 1 readonly files ", () => {
      const files = [createWorkspaceFileMock({ relativePath: "some/path/file.txt" })];
      const { editableFiles, readonlyFiles } = splitFiles(files);
      expect(editableFiles).toHaveLength(0);
      expect(readonlyFiles).toHaveLength(1);
    });
    it("should split files into 0 editable and 2 readonly files ", () => {
      const files = [
        createWorkspaceFileMock({ relativePath: "some/path/file.txt" }),
        createWorkspaceFileMock({ relativePath: "some/path/file.java" }),
      ];
      const { editableFiles, readonlyFiles } = splitFiles(files);
      expect(editableFiles).toHaveLength(0);
      expect(readonlyFiles).toHaveLength(2);
    });
    it("should split files into 1 editable and 1 readonly files ", () => {
      const files = [
        createWorkspaceFileMock({ relativePath: "some/path/model.sw.json" }),
        createWorkspaceFileMock({ relativePath: "some/path/file.java" }),
      ];
      const { editableFiles, readonlyFiles } = splitFiles(files);
      expect(editableFiles).toHaveLength(1);
      expect(readonlyFiles).toHaveLength(1);
    });
  });

  describe("GLOB_PATTERN", () => {
    describe("all", () => {
      it.each([[true, "anything", "foo.txt"]])("should be %s when path is %s", (result, _desc, path) => {
        const matcher = new Minimatch(GLOB_PATTERN.all, { dot: true });
        expect(matcher.match(path)).toBe(result);
      });
    });

    describe("allExceptDockerfiles", () => {
      it.each([
        [false, "a Dockerfile in the root", "Dockerfile"],
        [false, "a Dockerfile in a folder", "bar/Dockerfile"],
        [false, "a .dockerignore in the root", ".dockerignore"],
        [false, "a .dockerignore in a folder", "bar/.dockerignore"],
        [true, "not a docker file in the root", "foo.txt"],
        [true, "not a docker file in a folder", "bar/foo.txt"],
      ])("should be %s when path is %s", (result, _desc, path) => {
        const matcher = new Minimatch(GLOB_PATTERN.allExceptDockerfiles, { dot: true });
        expect(matcher.match(path)).toBe(result);
      });
    });

    describe("sw", () => {
      it.each([
        [false, "not a serverless workflow file in the root", "foo.txt"],
        [false, "not a serverless workflow file in the root", "foo.sw"],
        [false, "json file in the root", "foo.json"],
        [false, "yaml file in the root", "foo.yaml"],
        [false, "yml file in the root", "foo.yml"],
        [false, "json file in the root", "foo.json"],
        [true, "sw json file in the root", "foo.sw.json"],
        [true, "sw yaml file in the root", "foo.sw.yaml"],
        [true, "sw yml file in the root", "foo.sw.yml"],
        [true, "sw json file in a folder", "bar/foo.sw.json"],
        [true, "sw yaml file in a folder", "bar/foo.sw.yaml"],
        [true, "sw yml file in a folder", "bar/foo.sw.yml"],
      ])("should be %s when path is %s", (result, _desc, path) => {
        const matcher = new Minimatch(GLOB_PATTERN.sw, { dot: true });
        expect(matcher.match(path)).toBe(result);
      });
    });

    describe("yard", () => {
      it.each([
        [false, "not a serverless decision file in the root", "foo.txt"],
        [false, "not a serverless decision file in the root", "foo.yard"],
        [false, "json file in the root", "foo.json"],
        [false, "yaml file in the root", "foo.yaml"],
        [false, "yml file in the root", "foo.yml"],
        [false, "json file in the root", "foo.json"],
        [false, "yard json file in the root", "foo.yard.json"],
        [true, "yard yaml file in the root", "foo.yard.yaml"],
        [true, "yard yml file in the root", "foo.yard.yml"],
        [false, "yard json file in a folder", "bar/foo.yard.json"],
        [true, "yard yaml file in a folder", "bar/foo.yard.yaml"],
        [true, "yard yml file in a folder", "bar/foo.yard.yml"],
      ])("should be %s when path is %s", (result, _desc, path) => {
        const matcher = new Minimatch(GLOB_PATTERN.yard, { dot: true });
        expect(matcher.match(path)).toBe(result);
      });
    });

    describe("dash", () => {
      it.each([
        [false, "not a dashboard file in the root", "foo.txt"],
        [false, "not a dashboard file in the root", "foo.dash"],
        [false, "json file in the root", "foo.json"],
        [false, "yaml file in the root", "foo.yaml"],
        [false, "yml file in the root", "foo.yml"],
        [false, "json file in the root", "foo.json"],
        [false, "dash json file in the root", "foo.dash.json"],
        [false, "dash json file in a folder", "bar/foo.dash.json"],
        [true, "dash yaml file in the root", "foo.dash.yaml"],
        [true, "dash yml file in the root", "foo.dash.yml"],
        [true, "dash yaml file in a folder", "bar/foo.dash.yaml"],
        [true, "dash yml file in a folder", "bar/foo.dash.yml"],
      ])("should be %s when path is %s", (result, _desc, path) => {
        const matcher = new Minimatch(GLOB_PATTERN.dash, { dot: true });
        expect(matcher.match(path)).toBe(result);
      });
    });

    describe("spec", () => {
      it.each([
        [false, "not a spec file in the root", "foo.txt"],
        [false, "not a spec file in the root", "foo.spec"],
        [false, "json file in the root", "foo.json"],
        [false, "yaml file in the root", "foo.yaml"],
        [false, "yml file in the root", "foo.yml"],
        [false, "json file in the root", "foo.json"],
        [true, "spec json file in the root", "foo.spec.json"],
        [true, "spec yaml file in the root", "foo.spec.yaml"],
        [true, "spec yml file in the root", "foo.spec.yml"],
        [true, "spec json file in a folder", "bar/foo.spec.json"],
        [true, "spec yaml file in a folder", "bar/foo.spec.yaml"],
        [true, "spec yml file in a folder", "bar/foo.spec.yml"],
        [true, "specs json file in a folder", "bar/foo.specs.json"],
        [true, "specs yaml file in a folder", "bar/foo.specs.yaml"],
        [true, "specs yml file in a folder", "bar/foo.specs.yml"],
        [true, "spec json file in the root", "spec.json"],
        [true, "spec yaml file in the root", "spec.yaml"],
        [true, "spec yml file in the root", "spec.yml"],
        [true, "spec json file in a folder", "bar/spec.json"],
        [true, "spec yaml file in a folder", "bar/spec.yaml"],
        [true, "spec yml file in a folder", "bar/spec.yml"],
        [true, "specs json file in a folder", "bar/specs.json"],
        [true, "specs yaml file in a folder", "bar/specs.yaml"],
        [true, "specs yml file in a folder", "bar/specs.yml"],
      ])("should be %s when path is %s", (result, _desc, path) => {
        const matcher = new Minimatch(GLOB_PATTERN.spec, { dot: true });
        expect(matcher.match(path)).toBe(result);
      });
    });

    describe("sw_spec", () => {
      it.each([
        [false, "not a spec or serverless workflow file in the root", "foo.txt"],
        [false, "not a spec  or serverless workflow file in the root", "foo.spec"],
        [false, "not a spec  or serverless workflow file in the root", "foo.sw"],
        [false, "json file in the root", "foo.json"],
        [false, "yaml file in the root", "foo.yaml"],
        [false, "yml file in the root", "foo.yml"],
        [false, "json file in the root", "foo.json"],
        [true, "spec json file in the root", "foo.spec.json"],
        [true, "spec yaml file in the root", "foo.spec.yaml"],
        [true, "spec yml file in the root", "foo.spec.yml"],
        [true, "spec json file in a folder", "bar/foo.spec.json"],
        [true, "spec yaml file in a folder", "bar/foo.spec.yaml"],
        [true, "spec yml file in a folder", "bar/foo.spec.yml"],
        [true, "specs json file in a folder", "bar/foo.specs.json"],
        [true, "specs yaml file in a folder", "bar/foo.specs.yaml"],
        [true, "specs yml file in a folder", "bar/foo.specs.yml"],
        [true, "spec json file in the root", "spec.json"],
        [true, "spec yaml file in the root", "spec.yaml"],
        [true, "spec yml file in the root", "spec.yml"],
        [true, "spec json file in a folder", "bar/spec.json"],
        [true, "spec yaml file in a folder", "bar/spec.yaml"],
        [true, "spec yml file in a folder", "bar/spec.yml"],
        [true, "specs json file in a folder", "bar/specs.json"],
        [true, "specs yaml file in a folder", "bar/specs.yaml"],
        [true, "specs yml file in a folder", "bar/specs.yml"],
        [true, "sw json file in the root", "foo.sw.json"],
        [true, "sw yaml file in the root", "foo.sw.yaml"],
        [true, "sw yml file in the root", "foo.sw.yml"],
        [true, "sw json file in a folder", "bar/foo.sw.json"],
        [true, "sw yaml file in a folder", "bar/foo.sw.yaml"],
        [true, "sw yml file in a folder", "bar/foo.sw.yml"],
      ])("should be %s when path is %s", (result, _desc, path) => {
        const matcher = new Minimatch(GLOB_PATTERN.sw_spec, { dot: true });
        expect(matcher.match(path)).toBe(result);
      });
    });
  });
});
