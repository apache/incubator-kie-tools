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

import { createEditor } from "../../common/Editor";
import { EnvelopeServer } from "@kogito-tooling/envelope-bus/dist/channel";
import { KogitoEditorChannelApi, KogitoEditorEnvelopeApi } from "@kogito-tooling/editor/dist/api";
import { StateControl } from "@kogito-tooling/editor/dist/channel";

jest.mock("@kogito-tooling/editor/dist/api", () => {
  return {
    KogitoEditorEnvelopeApi: jest.fn().mockImplementation()
  };
});

describe("createEditor", () => {
  const envelopeServer = new EnvelopeServer<KogitoEditorChannelApi, KogitoEditorEnvelopeApi>(
    {
      postMessage: message => {
        /**/
      }
    },
    "",
    self => Promise.resolve()
  );

  const stateControl = new StateControl();

  test("setContent calls envelope with path and content", () => {
    const editor = createEditor(
      envelopeServer,
      stateControl,
      message => {
        /**/
      },
      document.createElement("iframe")
    );

    const spyOnContentChangedNotification = jest.spyOn(
      envelopeServer.envelopeApi.notifications,
      "receive_contentChanged"
    );

    return editor.setContent("my-path", "my-content").then(() => {
      expect(spyOnContentChangedNotification).toHaveBeenCalledWith({
        path: "my-path",
        content: "my-content"
      });
    });
  });
});
