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

import { EditorEnvelopeLocator } from "../../../api";
import { ChannelType } from "@kogito-tooling/channel-common-api";
import { EnvelopeServer } from "@kogito-tooling/envelope-bus/dist/channel"
import * as React from "react";
import { File } from "../../common";
import { EmbeddedViewer } from "../../embedded";
import { incomingMessage } from "./EmbeddedEditorTestUtils";
import { render } from "@testing-library/react";
import { EnvelopeBusMessagePurpose } from "@kogito-tooling/envelope-bus/dist/api";

describe("EmbeddedViewer::ONLINE", () => {
  const file: File = {
    fileName: "test",
    fileExtension: "dmn",
    getFileContents: () => Promise.resolve(""),
    isReadOnly: false
  };

  const editorEnvelopeLocator: EditorEnvelopeLocator = {
    targetOrigin: "localhost:8888",
    mapping: new Map([["dmn", { envelopePath: "envelope/envelope.html", resourcesPathPrefix: "envelope" }]])
  };

  const channelType = ChannelType.ONLINE;
  const busId = "test-bus-id";

  beforeAll(() => {
    jest.spyOn(EnvelopeServer.prototype, "generateRandomId").mockReturnValue(busId);
  });

  test("EmbeddedViewer::defaults", () => {
    const { getByTestId, container } = render(
      <EmbeddedViewer file={file} editorEnvelopeLocator={editorEnvelopeLocator} channelType={channelType} />
    );

    expect(getByTestId("kogito-iframe")).toBeVisible();
    expect(getByTestId("kogito-iframe")).toHaveAttribute("data-envelope-channel", ChannelType.ONLINE);
    expect(getByTestId("kogito-iframe")).toHaveAttribute("src", "envelope/envelope.html");

    expect(container.firstChild).toMatchSnapshot();
  });

  test("EmbeddedViewer::onResourceContentRequest", async () => {
    const onResourceContentRequest = jest.fn(() => Promise.resolve({} as any));

    const { container } = render(
      <EmbeddedViewer
        file={file}
        editorEnvelopeLocator={editorEnvelopeLocator}
        channelType={channelType}
        receive_resourceContentRequest={onResourceContentRequest}
      />
    );

    await incomingMessage({
      busId: busId,
      requestId: "1",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "receive_resourceContentRequest",
      data: [{ path: "" }]
    });

    expect(onResourceContentRequest).toBeCalled();
    expect(container.firstChild).toMatchSnapshot();
  });

  test("EmbeddedViewer::onResourceListRequest", async () => {
    const onResourceListRequest = jest.fn(() => Promise.resolve({} as any));

    const { container } = render(
      <EmbeddedViewer
        file={file}
        editorEnvelopeLocator={editorEnvelopeLocator}
        channelType={channelType}
        receive_resourceListRequest={onResourceListRequest}
      />
    );

    await incomingMessage({
      busId: busId,
      requestId: "1",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "receive_resourceListRequest",
      data: [{ pattern: "", paths: [] }]
    });

    expect(onResourceListRequest).toBeCalled();
    expect(container.firstChild).toMatchSnapshot();
  });
});
