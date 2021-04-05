/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { GwtEditorWrapperFactory } from "../../common";
import { DmnEditorChannelApi, getDmnLanguageData } from "../api";
import { EditorFactory, EditorInitArgs, KogitoEditorEnvelopeContextType } from "@kogito-tooling/editor/dist/api";
import { DmnEditor, DmnEditorImpl } from "./DmnEditor";
import { PMMLEditorMarshallerApi } from "../../common/api/PMMLEditorMarshallerApi";
import { PMMLEditorMarshallerService } from "@kogito-tooling/pmml-editor-marshaller";

export interface CustomWindow extends Window {
  envelope: {
    pmmlEditorMarshallerService: PMMLEditorMarshallerApi;
  };
}

declare let window: CustomWindow;

export class DmnEditorFactory implements EditorFactory<DmnEditor, DmnEditorChannelApi> {
  constructor(private readonly gwtEditorEnvelopeConfig: { shouldLoadResourcesDynamically: boolean }) {}

  public createEditor(
    ctx: KogitoEditorEnvelopeContextType<DmnEditorChannelApi>,
    initArgs: EditorInitArgs
  ): Promise<DmnEditor> {
    window.envelope = {
      ...(window.envelope ?? {}),
      ...{ pmmlEditorMarshallerService: new PMMLEditorMarshallerService() }
    };

    const languageData = getDmnLanguageData(initArgs.resourcesPathPrefix);
    const factory = new GwtEditorWrapperFactory<DmnEditor>(
      languageData,
      self =>
        new DmnEditorImpl(
          languageData.editorId,
          self.gwtAppFormerApi.getEditor(languageData.editorId),
          ctx.channelApi,
          self.xmlFormatter,
          self.gwtStateControlService,
          self.kieBcEditorsI18n
        ),
      this.gwtEditorEnvelopeConfig
    );

    return factory.createEditor(ctx, initArgs);
  }
}
