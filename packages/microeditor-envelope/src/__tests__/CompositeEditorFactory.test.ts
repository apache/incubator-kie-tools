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

import {
  ChannelType,
  EditorContext,
  EditorInitArgs,
  EnvelopeBus,
  EnvelopeBusMessage,
  OperatingSystem
} from "@kogito-tooling/microeditor-envelope-protocol";
import { CompositeEditorFactory } from "../CompositeEditorFactory";
import { Editor, EditorFactory, KogitoEditorEnvelopeContextType } from "@kogito-tooling/editor-api";
import { DummyEditor } from "./editor/DummyEditor";
import { DefaultKeyboardShortcutsService } from "@kogito-tooling/keyboard-shortcuts";

const dummyEditor: Editor = new DummyEditor();

const bus: EnvelopeBus = {
  postMessage<D, T>(message: EnvelopeBusMessage<D, T>, targetOrigin?: string, _?: any) {
    /*NOP*/
  }
};

const messageBusClient = {
  notify: jest.fn(),
  request: jest.fn()
};

const editorContext: EditorContext = { channel: ChannelType.EMBEDDED, operatingSystem: OperatingSystem.LINUX };
const envelopeContext: KogitoEditorEnvelopeContextType = {
  channelApi: messageBusClient,
  context: editorContext,
  services: {
    guidedTour: { isEnabled: () => false },
    keyboardShortcuts: new DefaultKeyboardShortcutsService({ editorContext: editorContext })
  }
};

describe("CompositeEditorFactory", () => {
  test("Unsupported type", () => {
    const factories: EditorFactory[] = [];
    factories.push(makeEditorFactory(false));
    factories.push(makeEditorFactory(false));

    const compositeFactory: CompositeEditorFactory = new CompositeEditorFactory(factories);
    expect(() => compositeFactory.supports("any")).toThrowError(Error);
  });

  test("Supported type", () => {
    const factories: EditorFactory[] = [];
    factories.push(makeEditorFactory(false));
    factories.push(makeEditorFactory(true));

    const compositeFactory: CompositeEditorFactory = new CompositeEditorFactory(factories);
    expect(compositeFactory.supports("one")).toBeTruthy();
  });

  test("Supported type::MultipleMatchingFactories", () => {
    const factories: EditorFactory[] = [];
    factories.push(makeEditorFactory(true));
    factories.push(makeEditorFactory(true));

    const compositeFactory: CompositeEditorFactory = new CompositeEditorFactory(factories);
    expect(() => compositeFactory.supports("mutliple")).toThrowError(Error);
  });

  test("Supported type::CreateEditor", () => {
    const factories: EditorFactory[] = [];
    const factory1: EditorFactory = makeEditorFactory(false);
    const factory2: EditorFactory = makeEditorFactory(true);
    factories.push(factory1);
    factories.push(factory2);

    jest.spyOn(factory2, "createEditor");

    const compositeFactory: CompositeEditorFactory = new CompositeEditorFactory(factories);
    const initArgs = { fileExtension: "txt", resourcesPathPrefix: "" };
    expect(compositeFactory.createEditor(envelopeContext, initArgs)).resolves.toBe(dummyEditor);
    expect(factory2.createEditor).toBeCalledTimes(1);
  });

  test("Supported type::CreateEditor::MultipleMatchingFactories", () => {
    const factories: EditorFactory[] = [];
    const factory1: EditorFactory = makeEditorFactory(true);
    const factory2: EditorFactory = makeEditorFactory(true);
    factories.push(factory1);
    factories.push(factory2);
    const compositeFactory: CompositeEditorFactory = new CompositeEditorFactory(factories);
    const initArgs = { fileExtension: "txt", resourcesPathPrefix: "" };
    expect(() => compositeFactory.createEditor(envelopeContext, initArgs)).toThrowError(Error);
  });

  function makeEditorFactory(supported: boolean): EditorFactory {
    return {
      supports: () => supported,
      createEditor: (_1: KogitoEditorEnvelopeContextType, _2: EditorInitArgs) => Promise.resolve(dummyEditor),
    };
  }
});
