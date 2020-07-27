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

import * as AppFormer from "@kogito-tooling/core-api";
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
  KogitoChannelBus
} from "@kogito-tooling/microeditor-envelope-protocol";
import { FACTORY_TYPE, PMMLEditorFactory } from "../editor/PMMLEditorFactory";
import { PMMLEditorInterface } from "../editor/PMMLEditorInterface";

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

describe("PMMLEditorFactory", () => {
  test("Unsupported LanguageData type", () => {
    const factory: PMMLEditorFactory = new PMMLEditorFactory();
    expect(factory.supports({ type: "unsupported" })).toBeFalsy();
  });

  test("Supported LanguageData type", () => {
    const factory: PMMLEditorFactory = new PMMLEditorFactory();
    expect(factory.supports({ type: FACTORY_TYPE })).toBeTruthy();
  });

  test("Supported type::CreateEditor", () => {
    const factory: PMMLEditorFactory = new PMMLEditorFactory();

    jest.spyOn(factory, "createEditor");

    const created: Promise<AppFormer.Editor> = factory.createEditor({ type: FACTORY_TYPE }, messageBus.client);
    expect(created).resolves.toBeInstanceOf(PMMLEditorInterface);
  });
});
