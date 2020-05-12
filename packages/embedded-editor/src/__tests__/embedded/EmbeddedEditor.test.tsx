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

import { ChannelType, EditorContent, KogitoEdit, ResourceContentRequest, ResourceListRequest } from "@kogito-tooling/core-api";
import { EnvelopeBusOuterMessageHandler } from "@kogito-tooling/microeditor-envelope-protocol";
import { cleanup, render } from "@testing-library/react";
import * as React from "react";
import { RefObject } from "react";
import { EditorType } from "../../common/EditorTypes";
import { File } from "../../common/File";
import { EmbeddedEditor, EmbeddedEditorRef } from "../../embedded/EmbeddedEditor";
import { EmbeddedEditorRouter } from "../../embedded/EmbeddedEditorRouter";
import * as EnvelopeFactory from "../../embedded/EnvelopeBusOuterMessageHandlerFactory";

afterEach(cleanup);

describe("EmbeddedEditor",
    () => {

        const file: File = {
            fileName: "test",
            editorType: EditorType.DMN,
            getFileContents: () => Promise.resolve(""),
            isReadOnly: false
        };
        const router: EmbeddedEditorRouter = new EmbeddedEditorRouter();
        const channelType: ChannelType = ChannelType.ONLINE;
        const editorRef: RefObject<EmbeddedEditorRef> = {
            current: {
                requestContent: () => "",
                requestPreview: () => "",
                setContent: (content: string) => null
            }
        };
        const envelopeFactorySpy = jest.spyOn(EnvelopeFactory, "newEnvelopeBusOuterMessageHandler")

        afterEach(() => {
            jest.clearAllMocks();
        });

        test("EmbeddedEditor::defaults",
            () => {
                render(
                    <EmbeddedEditor
                        ref={editorRef}
                        file={file}
                        router={router}
                        channelType={channelType}
                    />
                );

                expect(document.body).toMatchSnapshot();
            });

        test("EmbeddedEditor::setContent",
            () => {
                render(
                    <EmbeddedEditor
                        ref={editorRef}
                        file={file}
                        router={router}
                        channelType={channelType}
                    />
                );

                editorRef.current?.setContent("");
            });

        test("EmbeddedEditor::requestContent",
            () => {
                render(
                    <EmbeddedEditor
                        ref={editorRef}
                        file={file}
                        router={router}
                        channelType={channelType}
                    />
                );

                editorRef.current?.requestContent();
            });

        test("EmbeddedEditor::requestPreview",
            () => {
                render(
                    <EmbeddedEditor
                        ref={editorRef}
                        file={file}
                        router={router}
                        channelType={channelType}
                    />
                );

                editorRef.current?.requestPreview();
            });

        test("EmbeddedEditor::envelopeFactory",
            () => {
                render(
                    <EmbeddedEditor
                        ref={editorRef}
                        file={file}
                        router={router}
                        channelType={channelType}
                    />
                );

                expect(envelopeFactorySpy).toBeCalled();
            });

        test("EmbeddedEditor::onContentResponse",
            () => {
                const onContentResponse = jest.fn((c: EditorContent) => null);
                render(
                    <EmbeddedEditor
                        ref={editorRef}
                        file={file}
                        router={router}
                        channelType={channelType}
                        onContentResponse={onContentResponse}
                    />
                );

                const envelopeFactory: EnvelopeBusOuterMessageHandler = envelopeFactorySpy.mock.results[0].value;
                const content: EditorContent = { content: "hello" };
                envelopeFactory.impl.receive_contentResponse(content);

                expect(onContentResponse).toBeCalled();
            });

        test("EmbeddedEditor::onSetContentError",
            () => {
                const onSetContentError = jest.fn((errorMessage: string) => null);
                render(
                    <EmbeddedEditor
                        ref={editorRef}
                        file={file}
                        router={router}
                        channelType={channelType}
                        onSetContentError={onSetContentError}
                    />
                );

                const envelopeFactory: EnvelopeBusOuterMessageHandler = envelopeFactorySpy.mock.results[0].value;
                envelopeFactory.impl.receive_setContentError("error");

                expect(onSetContentError).toBeCalled();
            });

        test("EmbeddedEditor::onDirtyIndicatorChange",
            () => {
                const onDirtyIndicatorChange = jest.fn((isDirty: boolean) => null);
                render(
                    <EmbeddedEditor
                        ref={editorRef}
                        file={file}
                        router={router}
                        channelType={channelType}
                        onDirtyIndicatorChange={onDirtyIndicatorChange}
                    />
                );

                const envelopeFactory: EnvelopeBusOuterMessageHandler = envelopeFactorySpy.mock.results[0].value;
                envelopeFactory.impl.receive_dirtyIndicatorChange(true);

                expect(onDirtyIndicatorChange).toBeCalled();
            });

        test("EmbeddedEditor::onReady",
            () => {
                const onReady = jest.fn(() => null);
                render(
                    <EmbeddedEditor
                        ref={editorRef}
                        file={file}
                        router={router}
                        channelType={channelType}
                        onReady={onReady}
                    />
                );

                const envelopeFactory: EnvelopeBusOuterMessageHandler = envelopeFactorySpy.mock.results[0].value;
                envelopeFactory.impl.receive_ready();

                expect(onReady).toBeCalled();
            });

        test("EmbeddedEditor::onResourceContentRequest",
            () => {
                const onResourceContentRequest = jest.fn((request: ResourceContentRequest) => Promise.resolve(undefined));
                render(
                    <EmbeddedEditor
                        ref={editorRef}
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

        test("EmbeddedEditor::onResourceListRequest",
            () => {
                const onResourceListRequest = jest.fn((request: ResourceListRequest) => Promise.resolve({ pattern: "", paths: [] }));
                render(
                    <EmbeddedEditor
                        ref={editorRef}
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

        test("EmbeddedEditor::onEditorUndo",
            () => {
                const onEditorUndo = jest.fn(() => null);
                render(
                    <EmbeddedEditor
                        ref={editorRef}
                        file={file}
                        router={router}
                        channelType={channelType}
                        onEditorUndo={onEditorUndo}
                    />
                );

                const envelopeFactory: EnvelopeBusOuterMessageHandler = envelopeFactorySpy.mock.results[0].value;
                envelopeFactory.impl.notify_editorUndo();

                expect(onEditorUndo).toBeCalled();
            });

        test("EmbeddedEditor::onEditorRedo",
            () => {
                const onEditorRedo = jest.fn(() => null);
                render(
                    <EmbeddedEditor
                        ref={editorRef}
                        file={file}
                        router={router}
                        channelType={channelType}
                        onEditorRedo={onEditorRedo}
                    />
                );

                const envelopeFactory: EnvelopeBusOuterMessageHandler = envelopeFactorySpy.mock.results[0].value;
                envelopeFactory.impl.notify_editorRedo();

                expect(onEditorRedo).toBeCalled();
            });

        test("EmbeddedEditor::onNewEdit",
            () => {
                const onNewEdit = jest.fn((edit: KogitoEdit) => null);
                render(
                    <EmbeddedEditor
                        ref={editorRef}
                        file={file}
                        router={router}
                        channelType={channelType}
                        onNewEdit={onNewEdit}
                    />
                );

                const envelopeFactory: EnvelopeBusOuterMessageHandler = envelopeFactorySpy.mock.results[0].value;
                envelopeFactory.impl.receive_newEdit({ id: "abc" });

                expect(onNewEdit).toBeCalled();
            });

        test("EmbeddedEditor::onPreviewResponse",
            () => {
                const onPreviewResponse = jest.fn((previewSvg: string) => null);
                render(
                    <EmbeddedEditor
                        ref={editorRef}
                        file={file}
                        router={router}
                        channelType={channelType}
                        onPreviewResponse={onPreviewResponse}
                    />
                );

                const envelopeFactory: EnvelopeBusOuterMessageHandler = envelopeFactorySpy.mock.results[0].value;
                envelopeFactory.impl.receive_previewRequest("svg");

                expect(onPreviewResponse).toBeCalled();
            });

    });
