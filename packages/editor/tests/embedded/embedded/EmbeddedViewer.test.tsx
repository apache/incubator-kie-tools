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
  EditorEnvelopeLocator,
  EnvelopeMapping,
  ChannelType,
  EnvelopeContentType,
} from "@kie-tools-core/editor/dist/api";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import * as React from "react";
import { EmbeddedEditorFile } from "@kie-tools-core/editor/dist/channel";
import { EmbeddedViewer } from "@kie-tools-core/editor/dist/embedded";
import { incomingMessage } from "./EmbeddedEditorTestUtils";
import { render } from "@testing-library/react";
import { EnvelopeBusMessagePurpose } from "@kie-tools-core/envelope-bus/dist/api";

describe("EmbeddedViewer::ONLINE", () => {
  const file: EmbeddedEditorFile = {
    fileName: "test.dmn",
    fileExtension: "dmn",
    getFileContents: () => Promise.resolve(""),
    isReadOnly: false,
  };

  const editorEnvelopeLocator = new EditorEnvelopeLocator("localhost:8888", [
    new EnvelopeMapping({
      type: "dmn",
      filePathGlob: "**/*.dmn",
      resourcesPathPrefix: "envelope",
      envelopeContent: { type: EnvelopeContentType.PATH, path: "envelope/envelope.html" },
    }),
  ]);

  const channelType = ChannelType.ONLINE;
  const envelopeServerId = "test-bus-id";

  beforeAll(() => {
    jest.spyOn(EnvelopeServer.prototype, "generateRandomId").mockReturnValue(envelopeServerId);
  });

  test("EmbeddedViewer::defaults", () => {
    const { getByTestId, container } = render(
      <EmbeddedViewer
        file={file}
        editorEnvelopeLocator={editorEnvelopeLocator}
        channelType={channelType}
        locale={"en"}
      />
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
        kogitoWorkspace_resourceContentRequest={onResourceContentRequest}
        locale={"en"}
      />
    );

    await incomingMessage({
      targetEnvelopeServerId: envelopeServerId,
      requestId: "1",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "kogitoWorkspace_resourceContentRequest",
      data: [{ path: "" }],
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
        kogitoWorkspace_resourceListRequest={onResourceListRequest}
        locale={"en"}
      />
    );

    await incomingMessage({
      targetEnvelopeServerId: envelopeServerId,
      requestId: "1",
      purpose: EnvelopeBusMessagePurpose.REQUEST,
      type: "kogitoWorkspace_resourceListRequest",
      data: [{ pattern: "", paths: [] }],
    });

    expect(onResourceListRequest).toBeCalled();
    expect(container.firstChild).toMatchSnapshot();
  });
});
