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
  EditorFactory,
  EditorInitArgs,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeContextType,
} from "@kie-tools-core/editor/dist/api";
import { Base64PngEditorInterface } from "./Base64PngEditorInterface";

/**
 * Factory to be used by the Envelope to create a Base64 PNG Editor, It implements an EditorFactory.
 * It tells which extension the Editor supports and how to create a new Editor
 */
export class Base64PngEditorFactory implements EditorFactory<Base64PngEditorInterface, KogitoEditorChannelApi> {
  public supports(fileExtension: string) {
    return fileExtension === "base64png";
  }

  public createEditor(
    envelopeContext: KogitoEditorEnvelopeContextType<KogitoEditorChannelApi>,
    initArgs: EditorInitArgs
  ) {
    return Promise.resolve(new Base64PngEditorInterface(envelopeContext, initArgs));
  }
}
