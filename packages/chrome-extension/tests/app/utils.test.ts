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

import { extractOpenFilePath } from "@kie-tools-core/chrome-extension/dist/app/utils";

describe("runScriptOnPage", () => {
  test("test 1", async () => {
    expect(true).toBeTruthy();
  });
});

describe("runAfterUriChange", () => {
  test("test 1", async () => {
    expect(true).toBeTruthy();
  });
});

describe("removeAllChildren", () => {
  test("test 1", async () => {
    expect(true).toBeTruthy();
  });
});

describe("mainContainer", () => {
  test("test 1", async () => {
    expect(true).toBeTruthy();
  });
});

describe("createAndGetMainContainer", () => {
  test("test 1", async () => {
    expect(true).toBeTruthy();
  });
});

describe("iframeFullscreenContainer", () => {
  test("test 1", async () => {
    expect(true).toBeTruthy();
  });
});

describe("kogitoMenuContainer", () => {
  test("test 1", async () => {
    expect(true).toBeTruthy();
  });
});

describe("extractOpenFileExtension", () => {
  test("test 1", async () => {
    expect(true).toBeTruthy();
  });
});

describe("extractOpenFilePath", () => {
  test("test file with simple format", async () => {
    expect(extractOpenFilePath("/path/to/file/filename.format1")).toBe("/path/to/file/filename.format1");
  });

  test("test file with composed format", async () => {
    expect(extractOpenFilePath("/path/to/file/filename.format1.format2")).toBe(
      "/path/to/file/filename.format1.format2"
    );
  });

  test("test path with simple format and other parameters", async () => {
    expect(extractOpenFilePath("/path/to/file/filename.format1&otherParameters")).toBe(
      "/path/to/file/filename.format1"
    );
  });

  test("test path with simple format and other parameters", async () => {
    expect(extractOpenFilePath("/path/to/file/filename.format1.format2&otherParameters")).toBe(
      "/path/to/file/filename.format1.format2"
    );
  });
});
