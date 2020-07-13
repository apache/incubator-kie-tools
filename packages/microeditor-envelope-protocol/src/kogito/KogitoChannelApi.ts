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

import { Tutorial, UserInteraction } from "@kogito-tooling/guided-tour";
import {
  EditorContent,
  KogitoEdit,
  ResourceContent,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList,
  StateControlCommand
} from "@kogito-tooling/core-api";
import { LanguageData } from "./LanguageData";

export interface KogitoChannelApi {
  receive_setContentError(errorMessage: string): void;
  receive_ready(): void;
  receive_openFile(path: string): void;
  receive_guidedTourUserInteraction(userInteraction: UserInteraction): void;
  receive_guidedTourRegisterTutorial(tutorial: Tutorial): void;
  receive_newEdit(edit: KogitoEdit): void;
  receive_stateControlCommandUpdate(command: StateControlCommand): void;
  receive_languageRequest(): Promise<LanguageData | undefined>;
  receive_contentRequest(): Promise<EditorContent>;
  receive_resourceContentRequest(request: ResourceContentRequest): Promise<ResourceContent | undefined>;
  receive_resourceListRequest(request: ResourceListRequest): Promise<ResourcesList>;
}
