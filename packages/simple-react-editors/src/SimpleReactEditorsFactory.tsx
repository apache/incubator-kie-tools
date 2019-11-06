/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import * as MicroEditorEnvelope from "@kogito-tooling/microeditor-envelope";
import { EnvelopeBusInnerMessageHandler } from "@kogito-tooling/microeditor-envelope";
import { SimpleReactEditorsLanguageData } from "./SimpleReactEditorsLanguageData";
import { SimpleReactEditorInterface } from "./SimpleReactEditorInterface";
import { LanguageData } from "@kogito-tooling/core-api";

export class SimpleReactEditorsFactory implements MicroEditorEnvelope.EditorFactory<SimpleReactEditorsLanguageData> {
  public createEditor(languageData: LanguageData, messageBus: EnvelopeBusInnerMessageHandler) {
    return Promise.resolve(new SimpleReactEditorInterface(messageBus));
  }
}
