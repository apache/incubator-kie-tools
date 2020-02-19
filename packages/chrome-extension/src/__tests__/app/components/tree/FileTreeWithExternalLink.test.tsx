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

import { createTargetUrl } from "../../../../app/components/tree/FileTreeWithExternalLink";

const externalEditorManager = {
  name: "test",
  getLink(path: string) {
    return `http://mock.com/?file=${path}`;
  },
  open(filePath: string, fileContent: string, readonly: boolean): void {
    console.log("open");
  },
  listenToComeBack(
    setFileName: (fileName: string) => unknown,
    setFileContent: (content: string) => unknown
  ): { stopListening: () => void } {
    console.log("listen to come back");
    return {
      stopListening: () => console.log("stopListening")
    };
  }
};

describe("FileTreeWithExternalLink", () => {
  test("createTargetUrl", () => {
    const pathname = "/org/repo/blob/ref/file.bpmn";
    const externalEditorUrl = createTargetUrl(pathname, externalEditorManager);
    expect(externalEditorUrl).toEqual("http://mock.com/?file=org/repo/ref/file.bpmn");
  });
});
