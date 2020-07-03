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
import * as React from "react";
import { EditorType, File } from "../../common";
import { EmbeddedEditorRouter, EmbeddedViewer } from "../../embedded";
import { incomingMessage } from "./EmbeddedEditorTestUtils";
import { render } from "@testing-library/react";

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

  beforeEach(() => {
    spyOn<any>(EnvelopeBusOuterMessageHandler, "generateRandomBusId");
  });

  test("EmbeddedViewer::defaults", () => {
    const { getByTestId } = render(<EmbeddedViewer file={file} router={router} channelType={channelType} />);

    expect(getByTestId("kogito-iframe")).toBeVisible();
    expect(getByTestId("kogito-iframe")).toHaveAttribute("data-envelope-channel", ChannelType.ONLINE);
    expect(getByTestId("kogito-iframe")).toHaveAttribute("src", "envelope/envelope.html");

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedViewer::onResourceContentRequest", async () => {
    const onResourceContentRequest = jest.fn((request: ResourceContentRequest) =>
      Promise.resolve({ path: "", type: ContentType.TEXT })
    );

    render(
      <EmbeddedViewer
        file={file}
        router={router}
        channelType={channelType}
        onResourceContentRequest={onResourceContentRequest}
      />
    );

    await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_RESOURCE_CONTENT, data: { path: "" } });

    expect(onResourceContentRequest).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });

  test("EmbeddedViewer::onResourceListRequest", async () => {
    const onResourceListRequest = jest.fn((request: ResourceListRequest) =>
      Promise.resolve({ pattern: "", paths: [] })
    );

    render(
      <EmbeddedViewer
        file={file}
        router={router}
        channelType={channelType}
        onResourceListRequest={onResourceListRequest}
      />
    );

    await incomingMessage({ type: EnvelopeBusMessageType.REQUEST_RESOURCE_LIST, data: { pattern: "", paths: [] } });

    expect(onResourceListRequest).toBeCalled();

    expect(document.body).toMatchSnapshot();
  });
});
