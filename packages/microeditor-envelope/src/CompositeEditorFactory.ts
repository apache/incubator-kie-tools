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

import { Editor, LanguageData } from "@kogito-tooling/core-api";
import { KogitoChannelApi, MessageBusClient } from "@kogito-tooling/microeditor-envelope-protocol";
import { EditorFactory } from "./EditorFactory";

/**
 * Composite Factory of Editors to be created inside the envelope. This implementation delegates potential construction
 * to specific concrete factories. The first concrete factory to return a promise to create an editor is selected.
 */
export class CompositeEditorFactory implements EditorFactory<LanguageData> {
  constructor(private readonly factories: Array<EditorFactory<LanguageData>>) {}

  public supports(languageData: LanguageData) {
    const candidates = this.factories.filter(f => f.supports(languageData));
    this.assertSingleEditorFactory(candidates, languageData);
    return true;
  }

  public createEditor(
    languageData: LanguageData,
    messageBusClient: MessageBusClient<KogitoChannelApi>
  ): Promise<Editor> {
    const candidates = this.factories.filter(f => f.supports(languageData));
    this.assertSingleEditorFactory(candidates, languageData);
    return candidates[0].createEditor(languageData, messageBusClient);
  }

  private assertSingleEditorFactory(candidates: Array<EditorFactory<LanguageData>>, languageData: LanguageData): void {
    if (candidates.length === 0) {
      throw new Error(`An EditorFactory for '${languageData.type}' could not be found.`);
    } else if (candidates.length > 1) {
      throw new Error(`Multiple EditorFactories matched '${languageData.type}' when only one should be found.`);
    }
  }
}
