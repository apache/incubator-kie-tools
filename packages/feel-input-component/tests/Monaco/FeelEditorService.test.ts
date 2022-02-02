import { act } from "react-dom/test-utils";
/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { FeelEditorService } from "@kie-tools/feel-input-component";

describe("FeelEditorService", () => {
  it("createEditor, when DOM element is not present", () => {
    expect(() => {
      editorBuilder().createEditor();
    }).toThrow("FEEL editor cannot be created without a DOM element.");
  });

  it("isInitialized, when it's not intialized", () => {
    expect(FeelEditorService.isInitialized()).toBeFalsy();
  });

  it("isInitialized, when it's intialized", () => {
    act(() => {
      editorBuilder().withDomElement(domElement()).createEditor();
    });
    expect(FeelEditorService.isInitialized()).toBeTruthy();
  });

  it("createEditor when all elements are present", () => {
    const onBlur = jest.fn();
    const onChange = jest.fn();
    const onKeyDown = jest.fn();
    const options = {};

    FeelEditorService.setServiceInstance(undefined);

    FeelEditorService.getEditorBuilder()
      .withDomElement(domElement())
      .withOnBlur(onBlur)
      .withOnChange(onChange)
      .withOnKeyDown(onKeyDown)
      .withOptions(options)
      .createEditor();

    expect(onBlur).toBeCalled();
    expect(onChange).toBeCalled();
    expect(onKeyDown).toBeCalled();
  });

  it("getEditorBuilder", () => {
    expect(editorBuilder()).toBe(FeelEditorService.getEditorBuilder());
  });

  it("getStandaloneEditor", () => {
    const editor = editorBuilder().withDomElement(domElement()).createEditor();
    expect(editor).toBe(FeelEditorService.getStandaloneEditor());
  });

  it("colorize", () => {
    FeelEditorService.setServiceInstance(undefined);

    const builder = editorBuilder().withDomElement(domElement());
    const spyCreateEditor = jest.spyOn(builder, "createEditor");
    const spyDispose = jest.spyOn(builder, "dispose");

    builder.colorize("");

    expect(spyCreateEditor).toBeCalled();
    expect(spyDispose).toBeCalled();
  });

  it("dispose", () => {
    const editor = editorBuilder().withDomElement(domElement()).createEditor();
    const disposeSpy = jest.spyOn(editor, "dispose");

    FeelEditorService.dispose();

    expect(disposeSpy).toBeCalled();
  });
});

const domElement = () => document.createElement("span");
const editorBuilder = () => FeelEditorService.getEditorBuilder();

jest.mock("@kie-tools/feel-input-component/dist/Monaco", () => {
  const actualMonacoModule = jest.requireActual("@kie-tools/feel-input-component/dist/Monaco");
  return {
    ...actualMonacoModule,
    initializeFeelLanguage: jest.fn(),
    initializeMonacoTheme: jest.fn(),
    initializeFeelTokensProvider: jest.fn(),
    initializeFeelCompletionItemProvider: jest.fn(),
  };
});
