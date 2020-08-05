/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ChannelType, EditorEnvelopeLocator } from "@kogito-tooling/microeditor-envelope-protocol";
import * as React from "react";
import { File } from "../common";
import { EmbeddedEditor, Props as EmbeddedEditorProps } from "./EmbeddedEditor";

type Omit<T, K extends keyof T> = Pick<T, Exclude<keyof T, K>>;

type ChannelApiMethodsThatAreNoOpOnEmbeddedViewer =
  | "receive_setContentError"
  | "receive_ready"
  | "receive_openFile"
  | "receive_newEdit"
  | "receive_stateControlCommandUpdate";

type EmbeddedViewerChannelApiOverrides = Partial<
  Omit<EmbeddedEditorProps, ChannelApiMethodsThatAreNoOpOnEmbeddedViewer>
>;

export type Props = EmbeddedViewerChannelApiOverrides & {
  file: File;
  editorEnvelopeLocator: EditorEnvelopeLocator;
  channelType: ChannelType;
};

export const EmbeddedViewer = (props: Props) => <EmbeddedEditor {...props} />;
