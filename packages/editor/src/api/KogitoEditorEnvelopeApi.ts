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

import { EditorContent } from "./EditorContent";
import { KeyboardShortcutsEnvelopeApi } from "@kie-tools-core/keyboard-shortcuts/dist/api";
import { I18nEnvelopeApi } from "@kie-tools-core/i18n/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";

export interface Association {
  origin: string;
  envelopeServerId: string;
}

export enum ChannelType {
  VSCODE_DESKTOP = "VSCODE_DESKTOP",
  VSCODE_WEB = "VSCODE_WEB",
  ONLINE = "ONLINE",
  GITHUB = "GITHUB",
  EMBEDDED = "EMBEDDED",
  OTHER = "OTHER",
  ONLINE_MULTI_FILE = "ONLINE_MULTI_FILE",
  STANDALONE = "STANDALONE",
}

export const DEFAULT_WORKSPACE_ROOT_ABSOLUTE_POSIX_PATH = "/";

export interface EditorInitArgs {
  resourcesPathPrefix: string;
  fileExtension: string;
  initialLocale: string;
  isReadOnly: boolean;
  channel: ChannelType;
  workspaceRootAbsolutePosixPath: string;
}

export interface KogitoEditorEnvelopeApi extends KeyboardShortcutsEnvelopeApi, I18nEnvelopeApi {
  kogitoEditor_contentChanged(content: EditorContent, args: { showLoadingOverlay: boolean }): Promise<void>;
  kogitoEditor_editorUndo(): void;
  kogitoEditor_editorRedo(): void;
  kogitoEditor_initRequest(association: Association, editorInit: EditorInitArgs): Promise<void>;
  kogitoEditor_contentRequest(): Promise<EditorContent>;
  kogitoEditor_previewRequest(): Promise<string>;
  kogitoEditor_validate(): Promise<Notification[]>;
}
