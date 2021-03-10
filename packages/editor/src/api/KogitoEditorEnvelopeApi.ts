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

import { EditorContent } from "./EditorContent";
import { KeyboardShortcutsEnvelopeApi } from "@kogito-tooling/keyboard-shortcuts/dist/api";
import { GuidedTourEnvelopeApi } from "@kogito-tooling/guided-tour/dist/api";
import { I18nEnvelopeApi } from "@kogito-tooling/i18n/dist/api";
import { Notification } from "@kogito-tooling/notifications/dist/api";

export interface Association {
  origin: string;
  envelopeServerId: string;
}

export enum ChannelType {
  VSCODE = "VSCODE",
  ONLINE = "ONLINE",
  GITHUB = "GITHUB",
  DESKTOP = "DESKTOP",
  EMBEDDED = "EMBEDDED",
  OTHER = "OTHER"
}

export interface EditorInitArgs {
  resourcesPathPrefix: string;
  fileExtension: string;
  initialLocale: string;
  isReadOnly: boolean;
  channel: ChannelType;
}

export interface KogitoEditorEnvelopeApi extends KeyboardShortcutsEnvelopeApi, GuidedTourEnvelopeApi, I18nEnvelopeApi {
  receive_contentChanged(content: EditorContent): Promise<void>;
  receive_editorUndo(): void;
  receive_editorRedo(): void;
  receive_initRequest(association: Association, editorInit: EditorInitArgs): Promise<void>;
  receive_contentRequest(): Promise<EditorContent>;
  receive_previewRequest(): Promise<string>;
  validate(): Promise<Notification[]>;
}
