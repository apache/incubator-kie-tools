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

import { ChannelType, ContentType, ResourceContentRequest, ResourceListRequest } from "@kogito-tooling/core-api";
import { EnvelopeBusMessageType, EnvelopeBusOuterMessageHandler } from "@kogito-tooling/microeditor-envelope-protocol";
import { mount } from "enzyme";
import * as React from "react";
import { EditorType, File } from "../../common";
import { EmbeddedEditorRouter, EmbeddedViewer } from "../../embedded";
import { StateControl } from "../../stateControl";
import { incomingMessage } from "./EmbeddedEditorTestUtils";

describe("EmbeddedViewer::ONLINE", () => {
  const file: File = {
    fileName: "test",
    editorType: EditorType.DMN,
    getFileContents: () => Promise.resolve(""),
    isReadOnly: false
  };
  const router: EmbeddedEditorRouter = new EmbeddedEditorRouter();
  const channelType: ChannelType = ChannelType.ONLINE;
  const holder: HTMLElement = document.body.appendChild(document.createElement("div"));
  const stateControl = new StateControl();

  beforeEach(() => {
    spyOn<any>(EnvelopeBusOuterMessageHandler, "generateRandomBusId");
  });

  test("EmbeddedViewer::defaults", () => {
    mount(<EmbeddedViewer file={file} router={router} channelType={channelType} stateControl={stateControl} />, {
      attachTo: holder
    });

    expect(holder.firstElementChild?.getAttribute("id")).toBe("kogito-iframe");
    expect(holder.firstElementChild?.getAttribute("data-envelope-channel")).toBe(ChannelType.ONLINE);
    expect(holder.firstElementChild?.getAttribute("src")).toBe("envelope/envelope.html");

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedViewer::onResourceContentRequest", async () => {
    const onResourceContentRequest = jest.fn((request: ResourceContentRequest) =>
      Promise.resolve({ path: "", type: ContentType.TEXT })
    );

    mount(
      <EmbeddedViewer
        file={file}
        router={router}
        channelType={channelType}
        onResourceContentRequest={onResourceContentRequest}
        stateControl={stateControl}
      />,
      { attachTo: holder }
    );

    await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_RESOURCE_CONTENT, data: { path: "" } });

    expect(onResourceContentRequest).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedViewer::onResourceListRequest", async () => {
    const onResourceListRequest = jest.fn((request: ResourceListRequest) =>
      Promise.resolve({ pattern: "", paths: [] })
    );

    mount(
      <EmbeddedViewer
        file={file}
        router={router}
        channelType={channelType}
        onResourceListRequest={onResourceListRequest}
        stateControl={stateControl}
      />,
      { attachTo: holder }
    );

    await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_RESOURCE_LIST, data: { pattern: "", paths: [] } });

    expect(onResourceListRequest).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });
});
