/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { EditorEnvelopeLocator, ChannelType } from "../../api";
import * as React from "react";
import { EmbeddedEditorFile } from "../../channel";
import { EmbeddedEditor, Props as EmbeddedEditorProps } from "./EmbeddedEditor";

type Omit<T, K extends keyof T> = Pick<T, Exclude<keyof T, K>>;

type ChannelApiMethodsThatAreNoOpOnEmbeddedViewer =
  | "kogitoEditor_setContentError"
  | "kogitoEditor_ready"
  | "kogitoWorkspace_openFile"
  | "kogitoWorkspace_newEdit"
  | "kogitoEditor_stateControlCommandUpdate";

type EmbeddedViewerChannelApiOverrides = Partial<
  Omit<EmbeddedEditorProps, ChannelApiMethodsThatAreNoOpOnEmbeddedViewer>
>;

export type Props = EmbeddedViewerChannelApiOverrides & {
  file: EmbeddedEditorFile;
  editorEnvelopeLocator: EditorEnvelopeLocator;
  channelType: ChannelType;
  locale: string;
};

export const EmbeddedViewer = (props: Props) => <EmbeddedEditor {...props} />;
