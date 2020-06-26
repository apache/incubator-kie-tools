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
import { EditorContent, LanguageData, ResourceContent, ResourcesList } from "@kogito-tooling/core-api";
import { ChannelKeyboardEvent } from "@kogito-tooling/keyboard-shortcuts";
import { EnvelopeBusInnerMessageHandler } from "@kogito-tooling/microeditor-envelope";
import { FACTORY_TYPE, PMMLEditorFactory } from "../editor/PMMLEditorFactory";
import { PMMLEditorInterface } from "../editor/PMMLEditorInterface";

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

    const created: Promise<AppFormer.Editor> = factory.createEditor({ type: FACTORY_TYPE }, handler);
    expect(created).resolves.toBeInstanceOf(PMMLEditorInterface);
  });
});
