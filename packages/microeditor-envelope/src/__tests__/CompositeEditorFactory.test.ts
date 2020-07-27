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
import {
  KogitoEdit,
  LanguageData,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList,
  StateControlCommand
} from "@kogito-tooling/core-api";
import { Tutorial, UserInteraction } from "@kogito-tooling/guided-tour";
import {
  EnvelopeBus,
  EnvelopeBusMessage,
  KogitoChannelApi,
  KogitoChannelBus,
  MessageBusClient
} from "@kogito-tooling/microeditor-envelope-protocol";
import { CompositeEditorFactory } from "../CompositeEditorFactory";
import { EditorFactory } from "../EditorFactory";
import { DummyEditor } from "./DummyEditor";

const dummyEditor: Core.Editor = new DummyEditor();
const languageData: LanguageData = { type: "unused" };

const bus: EnvelopeBus = {
  postMessage<D, T>(message: EnvelopeBusMessage<D, T>, targetOrigin?: string, _?: any) {
    /*NOP*/
  }
};
const api: KogitoChannelApi = {
  receive_setContentError(_: string) {
    /*NOP*/
  },
  receive_ready() {
    /*NOP*/
  },
  receive_openFile(_: string) {
    /*NOP*/
  },
  receive_guidedTourUserInteraction(_: UserInteraction) {
    /*NOP*/
  },
  receive_guidedTourRegisterTutorial(_: Tutorial) {
    /*NOP*/
  },
  receive_newEdit(_: KogitoEdit) {
    /*NOP*/
  },
  receive_stateControlCommandUpdate(_: StateControlCommand) {
    /*NOP*/
  },
  receive_languageRequest() {
    return Promise.resolve(languageData);
  },
  receive_contentRequest() {
    return Promise.resolve({ content: "" });
  },
  receive_resourceContentRequest(_: ResourceContentRequest) {
    return Promise.resolve(undefined);
  },
  receive_resourceListRequest(_: ResourceListRequest) {
    return Promise.resolve(new ResourcesList("", []));
  }
};
const messageBus: KogitoChannelBus = new KogitoChannelBus(bus, api);

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
    expect(compositeFactory.createEditor(languageData, messageBus.client)).resolves.toBe(dummyEditor);
    expect(factory2.createEditor).toBeCalledTimes(1);
  });

  test("Supported type::CreateEditor::MultipleMatchingFactories", () => {
    const factories: Array<EditorFactory<LanguageData>> = new Array();
    const factory1: EditorFactory<LanguageData> = makeEditorFactory(true);
    const factory2: EditorFactory<LanguageData> = makeEditorFactory(true);
    factories.push(factory1);
    factories.push(factory2);

    const compositeFactory: CompositeEditorFactory = new CompositeEditorFactory(factories);
    expect(() => compositeFactory.createEditor(languageData, messageBus.client)).toThrowError(Error);
  });

  function makeEditorFactory(supported: boolean): EditorFactory<LanguageData> {
    const factory: EditorFactory<LanguageData> = {
      supports: data => supported,
      createEditor: (_1: LanguageData, _2: MessageBusClient<KogitoChannelApi>) => Promise.resolve(dummyEditor)
    };
    return factory;
  }
});
