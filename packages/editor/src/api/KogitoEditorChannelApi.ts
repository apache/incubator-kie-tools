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

import { CapabilityChannelApi } from "@kogito-tooling/backend/dist/channel-api";
import { KogitoToolingChannelCommonApi } from "@kogito-tooling/channel-common-api";
import { NotificationsApi } from "@kogito-tooling/notifications/dist/api";
import { GuidedTourChannelApi } from "@kogito-tooling/guided-tour/dist/api";
import { I18nChannelApi } from "@kogito-tooling/i18n/dist/api";
import { WorkspaceApi } from "@kogito-tooling/workspace/dist/api";
import { EditorContent } from "./EditorContent";
import { StateControlCommand } from "./StateControlCommand";

export interface KogitoEditorChannelApi
  extends KogitoToolingChannelCommonApi,
    GuidedTourChannelApi,
    I18nChannelApi,
    CapabilityChannelApi,
    WorkspaceApi,
    NotificationsApi {
  receive_setContentError(content: EditorContent): void;
  receive_stateControlCommandUpdate(command: StateControlCommand): void;
  receive_contentRequest(): Promise<EditorContent>;
}
