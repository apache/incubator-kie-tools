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

import * as Core from "@kogito-tooling/core-api";
import { EditorContent, LanguageData, ResourceContent, ResourcesList } from "@kogito-tooling/core-api";
import { ChannelKeyboardEvent } from "@kogito-tooling/keyboard-shortcuts";
import { CompositeEditorFactory } from "../CompositeEditorFactory";
import { EditorFactory } from "../EditorFactory";
import { EnvelopeBusInnerMessageHandler } from "../EnvelopeBusInnerMessageHandler";
import { DummyEditor } from "./DummyEditor";

const dummyEditor: Core.Editor = new DummyEditor();
const languageData: LanguageData = { type: "unused" };

const handler: EnvelopeBusInnerMessageHandler = new EnvelopeBusInnerMessageHandler(
  {
    postMessage: (_1, _2) => {
      /*NOP*/
    }
  },
  self => ({
    receive_contentResponse: (_: EditorContent) => {
      /*NOP*/
    },
    receive_languageResponse: (_: LanguageData) => {
      /*NOP*/
    },
    receive_contentRequest: () => {
      /*NOP*/
    },
    receive_resourceContentResponse: (_: ResourceContent) => {
      /*NOP*/
    },
    receive_resourceContentList: (_: ResourcesList) => {
      /*NOP*/
    },
    receive_editorRedo(): void {
      /*NOP*/
    },
    receive_editorUndo(): void {
      /*NOP*/
    },
    receive_previewRequest: () => {
      /*NOP*/
    },
    receive_guidedTourElementPositionRequest: (_: string) => {
      /*NOP*/
    },
    receive_channelKeyboardEvent: (_: ChannelKeyboardEvent) => {
      /*NOP*/
    }
  })
);

describe("CompositeEditorFactory", () => {
  test("Unsupported type", () => {
    const factories: Array<EditorFactory<LanguageData>> = new Array();
    factories.push(makeEditorFactory(false));
    factories.push(makeEditorFactory(false));

    const compositeFactory: CompositeEditorFactory = new CompositeEditorFactory(factories);
    expect(() => compositeFactory.supports(languageData)).toThrowError(Error);
  });

  test("Supported type", () => {
    const factories: Array<EditorFactory<LanguageData>> = new Array();
    factories.push(makeEditorFactory(false));
    factories.push(makeEditorFactory(true));

    const compositeFactory: CompositeEditorFactory = new CompositeEditorFactory(factories);
    expect(compositeFactory.supports(languageData)).toBeTruthy();
  });

  test("Supported type::MultipleMatchingFactories", () => {
    const factories: Array<EditorFactory<LanguageData>> = new Array();
    factories.push(makeEditorFactory(true));
    factories.push(makeEditorFactory(true));

    const compositeFactory: CompositeEditorFactory = new CompositeEditorFactory(factories);
    expect(() => compositeFactory.supports(languageData)).toThrowError(Error);
  });

  test("Supported type::CreateEditor", () => {
    const factories: Array<EditorFactory<LanguageData>> = new Array();
    const factory1: EditorFactory<LanguageData> = makeEditorFactory(false);
    const factory2: EditorFactory<LanguageData> = makeEditorFactory(true);
    factories.push(factory1);
    factories.push(factory2);

    jest.spyOn(factory2, "createEditor");

    const compositeFactory: CompositeEditorFactory = new CompositeEditorFactory(factories);
    expect(compositeFactory.createEditor(languageData, handler)).resolves.toBe(dummyEditor);
    expect(factory2.createEditor).toBeCalledTimes(1);
  });

  test("Supported type::CreateEditor::MultipleMatchingFactories", () => {
    const factories: Array<EditorFactory<LanguageData>> = new Array();
    const factory1: EditorFactory<LanguageData> = makeEditorFactory(true);
    const factory2: EditorFactory<LanguageData> = makeEditorFactory(true);
    factories.push(factory1);
    factories.push(factory2);

    const compositeFactory: CompositeEditorFactory = new CompositeEditorFactory(factories);
    expect(() => compositeFactory.createEditor(languageData, handler)).toThrowError(Error);
  });

  function makeEditorFactory(supported: boolean): EditorFactory<LanguageData> {
    const factory: EditorFactory<LanguageData> = {
      supports: data => supported,
      createEditor: (_1: LanguageData, _2: EnvelopeBusInnerMessageHandler) => Promise.resolve(dummyEditor)
    };
    return factory;
  }
});
