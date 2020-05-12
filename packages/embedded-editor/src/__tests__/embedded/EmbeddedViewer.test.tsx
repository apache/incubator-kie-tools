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

import { ChannelType, ResourceContentRequest, ResourceListRequest } from "@kogito-tooling/core-api";
import { EnvelopeBusOuterMessageHandler } from "@kogito-tooling/microeditor-envelope-protocol";
import { render } from "@testing-library/react";
import * as React from "react";
import { EditorType } from "../../common/EditorTypes";
import { File } from "../../common/File";
import { EmbeddedEditorRouter } from "../../embedded/EmbeddedEditorRouter";
import { EmbeddedViewer } from "../../embedded/EmbeddedViewer";
import * as EnvelopeFactory from "../../embedded/EnvelopeBusOuterMessageHandlerFactory";

describe("EmbeddedViewer",
    () => {

        const file: File = {
            fileName: "test",
            editorType: EditorType.DMN,
            getFileContents: () => Promise.resolve(""),
            isReadOnly: false
        };
        const router: EmbeddedEditorRouter = new EmbeddedEditorRouter();
        const channelType: ChannelType = ChannelType.ONLINE;
        const envelopeFactorySpy = jest.spyOn(EnvelopeFactory, "newEnvelopeBusOuterMessageHandler")

        afterEach(() => {
            jest.clearAllMocks();
        });

        test("EmbeddedViewer::defaults",
            async () => {
                render(
                    <EmbeddedViewer
                        file={file}
                        router={router}
                        channelType={channelType}
                    />
                );

                expect(document.body).toMatchSnapshot();
            });

        test("EmbeddedViewer::envelopeFactory",
            () => {
                render(
                    <EmbeddedViewer
                        file={file}
                        router={router}
                        channelType={channelType}
                    />
                );

                expect(envelopeFactorySpy).toBeCalled();
            });


        test("EmbeddedViewer::onResourceContentRequest",
            () => {
                const onResourceContentRequest = jest.fn((request: ResourceContentRequest) => Promise.resolve(undefined));
                render(
                    <EmbeddedViewer
                        file={file}
                        router={router}
                        channelType={channelType}
                        onResourceContentRequest={onResourceContentRequest}
                    />
                );

                const envelopeFactory: EnvelopeBusOuterMessageHandler = envelopeFactorySpy.mock.results[0].value;
                envelopeFactory.impl.receive_resourceContentRequest({ path: "" });

                expect(onResourceContentRequest).toBeCalled();
            });

        test("EmbeddedViewer::onResourceListRequest",
            () => {
                const onResourceListRequest = jest.fn((request: ResourceListRequest) => Promise.resolve({ pattern: "", paths: [] }));
                render(
                    <EmbeddedViewer
                        file={file}
                        router={router}
                        channelType={channelType}
                        onResourceListRequest={onResourceListRequest}
                    />
                );

                const envelopeFactory: EnvelopeBusOuterMessageHandler = envelopeFactorySpy.mock.results[0].value;
                envelopeFactory.impl.receive_resourceListRequest({ pattern: "" });

                expect(onResourceListRequest).toBeCalled();
            });

    });
