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
  EditorContent,
  StateControlCommand,
  Tutorial,
  UserInteraction
} from "./api";
import { KogitoChannelCommonApi } from "@kogito-tooling/channel-common-api";

export interface KogitoEditorChannelApi extends KogitoChannelCommonApi {
  receive_setContentError(errorMessage: string): void;
  receive_guidedTourUserInteraction(userInteraction: UserInteraction): void;
  receive_guidedTourRegisterTutorial(tutorial: Tutorial): void;
  receive_stateControlCommandUpdate(command: StateControlCommand): void;
  receive_contentRequest(): Promise<EditorContent>;
}
