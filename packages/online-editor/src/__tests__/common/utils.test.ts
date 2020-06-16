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

import { EditorType } from "@kogito-tooling/embedded-editor";
import { extractEditorTypeFromUrl } from "../../common/utils";

describe("utils::extractEditorTypeFromUrl", () => {
  const originalLocation = window.location;
  const baseUrl = "https://kiegroup.github.io/kogito-online";

  function setWindowLocationHref(url: string) {
    delete window.location;
    window.location = { href: `${baseUrl}/${url}` } as any;
  }

  afterEach(() => {
    window.location = originalLocation;
  });

  test("should be EditorType.BPMN when #/editor/bpmn", () => {
    setWindowLocationHref("#/editor/bpmn");
    expect(extractEditorTypeFromUrl()).toEqual(EditorType.BPMN);
  });

  test("should be EditorType.DMN when #/editor/dmn", () => {
    setWindowLocationHref("#/editor/dmn");
    expect(extractEditorTypeFromUrl()).toEqual(EditorType.DMN);
  });

  test("should be EditorType.BPMN when #/editor/bpmn?key=value", () => {
    setWindowLocationHref("#/editor/bpmn?key=value");
    expect(extractEditorTypeFromUrl()).toEqual(EditorType.BPMN);
  });

  test("should be undefined when #/editor/invalid", () => {
    setWindowLocationHref("#/editor/invalid");
    expect(extractEditorTypeFromUrl()).toBeUndefined();
  });

  test("should be undefined when #/editor", () => {
    setWindowLocationHref("#/editor");
    expect(extractEditorTypeFromUrl()).toBeUndefined();
  });

  test("should be undefined when #/editor?key=value", () => {
    setWindowLocationHref("#/editor?key=value");
    expect(extractEditorTypeFromUrl()).toBeUndefined();
  });

  test("should be undefined when empty", () => {
    setWindowLocationHref("");
    expect(extractEditorTypeFromUrl()).toBeUndefined();
  });
});
