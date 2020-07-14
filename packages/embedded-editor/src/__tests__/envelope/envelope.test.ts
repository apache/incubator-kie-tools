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
import { ChannelType } from "@kogito-tooling/microeditor-envelope-protocol";
import * as MicroEditorEnvelope from "@kogito-tooling/microeditor-envelope";

let channelType: ChannelType = ChannelType.DESKTOP;

jest.mock("@kogito-tooling/microeditor-envelope");
const mockMicroEditorEnvelope = MicroEditorEnvelope as jest.Mocked<typeof MicroEditorEnvelope>;

Object.defineProperty(global, "frameElement",
    {
        get: jest.fn().mockImplementation(() => {
            return {
                attributes: {
                    getNamedItem: (key: string) => {
                        return { value: key === "data-envelope-channel" ? channelType : undefined };
                    }
                }
            };
        })
    });

//Lazy load module as it executes once loaded and, if import'ed, it's before the mocks are setup. 
import module = require("../../envelope/envelope");

describe("MicroEditorEnvelope.init",
    () => {
        test("initialisation",
            () => {
                expect(mockMicroEditorEnvelope.init.mock.calls.length).toEqual(1);
                expect(mockMicroEditorEnvelope.init.mock.calls[0][0].editorContext.channel).toBe(ChannelType.DESKTOP);
            });
    });

describe("ChannelType",
    () => {
        test("ChannelType::DESKTOP",
            () => {
                channelType = ChannelType.DESKTOP;
                expect(module.getChannelType()).toEqual(ChannelType.DESKTOP);
            });

        test("ChannelType::GITHUB",
            () => {
                channelType = ChannelType.GITHUB;
                expect(module.getChannelType()).toEqual(ChannelType.GITHUB);
            });

        test("ChannelType::ONLINE",
            () => {
                channelType = ChannelType.ONLINE;
                expect(module.getChannelType()).toEqual(ChannelType.ONLINE);
            });

        test("ChannelType::VSCODE",
            () => {
                channelType = ChannelType.VSCODE;
                expect(module.getChannelType()).toEqual(ChannelType.VSCODE);
            });
    });
