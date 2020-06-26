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
import * as AppFormer from "@kogito-tooling/core-api";
import { LanguageData } from "@kogito-tooling/core-api";
import * as MicroEditorEnvelope from "@kogito-tooling/microeditor-envelope";
import { EnvelopeBusInnerMessageHandler } from "@kogito-tooling/microeditor-envelope";
import { PMMLEditorInterface } from "./PMMLEditorInterface";

export const FACTORY_TYPE = "pmml";

export class PMMLEditorFactory implements MicroEditorEnvelope.EditorFactory<LanguageData> {
  public supports(languageData: LanguageData) {
    return languageData.type === FACTORY_TYPE;
  }

  public createEditor(
    languageData: LanguageData,
    messageBus: EnvelopeBusInnerMessageHandler
  ): Promise<AppFormer.Editor> {
    return Promise.resolve(new PMMLEditorInterface(messageBus));
  }
}
