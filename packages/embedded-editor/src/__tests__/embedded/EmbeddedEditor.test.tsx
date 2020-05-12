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

import { ChannelType } from "@kogito-tooling/core-api";
import { render } from "@testing-library/react";
import * as React from "react";
import { RefObject } from "react";
import { EditorType } from "../../common/EditorTypes";
import { File } from "../../common/File";
import { EmbeddedEditor, EmbeddedEditorRef } from "../../embedded/EmbeddedEditor";
import { EmbeddedEditorRouter } from "../../embedded/EmbeddedEditorRouter";

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

        test("EmbeddedEditor::defaults",
            async () => {
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
            async () => {
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
            async () => {
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
            async () => {
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
    });
