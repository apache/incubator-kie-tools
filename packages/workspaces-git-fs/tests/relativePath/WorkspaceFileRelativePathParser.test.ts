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

import { extractExtension } from "../../src/relativePath/WorkspaceFileRelativePathParser";

describe("WorkspaceFileRelativePathParser :: extractExtension", () => {
  it.each([
    ["foo.json", "json"],
    [".gitignore", "gitignore"],
    ["noExtension", ""],
    ["bar.sw.json", "sw.json"],
    ["a/b/c/foo.json", "json"],
    ["a/b/c/.gitignore", "gitignore"],
    ["a/b/c/noExtension", ""],
    ["a/b/c/bar.sw.json", "sw.json"],
  ])("should extract extension properly for %p", (relativePath: string, extension: string) => {
    expect(extractExtension(relativePath)).toBe(extension);
  });
});
