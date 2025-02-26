/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { GwtEditorWrapperFactory, XmlFormatter } from "../../common";
import { DmnEditorChannelApi, DmnEditorEnvelopeApi, getDmnLanguageData } from "../api";
import { EditorFactory, EditorInitArgs, KogitoEditorEnvelopeContextType } from "@kie-tools-core/editor/dist/api";
import { DmnEditor, DmnEditorImpl } from "./DmnEditor";
import { PmmlEditorMarshallerExposedInteropApi } from "./exposedInteropApi/PmmlEditorMarshallerExposedInteropApi";
import { PMMLEditorMarshallerService } from "@kie-tools/pmml-editor-marshaller";

export interface CustomWindow extends Window {
  envelope: {
    pmmlEditorMarshallerService: PmmlEditorMarshallerExposedInteropApi;
  };
}

declare let window: CustomWindow;

export class DmnEditorFactory implements EditorFactory<DmnEditor, DmnEditorEnvelopeApi, DmnEditorChannelApi> {
  constructor(private readonly gwtEditorEnvelopeConfig: { shouldLoadResourcesDynamically: boolean }) {}

  public createEditor(
    ctx: KogitoEditorEnvelopeContextType<DmnEditorEnvelopeApi, DmnEditorChannelApi>,
    initArgs: EditorInitArgs
  ): Promise<DmnEditor> {
    const exposedInteropApi: CustomWindow["envelope"] = {
      pmmlEditorMarshallerService: new PMMLEditorMarshallerService(),
    };

    window.envelope = {
      ...(window.envelope ?? {}),
      ...exposedInteropApi,
    };

    const languageData = getDmnLanguageData(initArgs.resourcesPathPrefix);
    const factory = new GwtEditorWrapperFactory<DmnEditor>(
      languageData,
      (self) =>
        new DmnEditorImpl(
          languageData.editorId,
          self.gwtAppFormerConsumedInteropApi.getEditor(languageData.editorId),
          ctx.channelApi,
          new XmlFormatter(),
          self.gwtStateControlService,
          self.kieBcEditorsI18n
        ),
      this.gwtEditorEnvelopeConfig
    );

    return factory.createEditor(ctx, initArgs);
  }
}
