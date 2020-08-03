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

import { Editor, EditorFactory, EditorInitArgs, KogitoEditorEnvelopeContextType } from "../api";

/**
 * Composite Factory of Editors to be created inside the envelope. This implementation delegates potential construction
 * to specific concrete factories. The first concrete factory to return a promise to create an editor is selected.
 */
export class CompositeEditorFactory implements EditorFactory {
  constructor(private readonly factories: EditorFactory[]) {}

  public supports(fileExtension: string) {
    const candidates = this.factories.filter(f => f.supports(fileExtension));
    this.assertSingleEditorFactory(candidates, fileExtension);
    return true;
  }

  public createEditor(envelopeContext: KogitoEditorEnvelopeContextType, initArgs: EditorInitArgs): Promise<Editor> {
    const candidates = this.factories.filter(f => f.supports(initArgs.fileExtension));
    this.assertSingleEditorFactory(candidates, initArgs.fileExtension);
    return candidates[0].createEditor(envelopeContext, initArgs);
  }

  private assertSingleEditorFactory(candidates: EditorFactory[], fileExtension: string): void {
    if (candidates.length === 0) {
      throw new Error(`An EditorFactory for '${fileExtension}' could not be found.`);
    } else if (candidates.length > 1) {
      throw new Error(`Multiple EditorFactories matched '${fileExtension}' when only one should be found.`);
    }
  }
}
