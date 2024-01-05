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
import { BpmnEditorChannelApi, getBpmnLanguageData } from "../api";
import { EditorFactory, EditorInitArgs, KogitoEditorEnvelopeContextType } from "@kie-tools-core/editor/dist/api";
import { BpmnEditor, BpmnEditorImpl } from "./BpmnEditor";
import { DmnLanguageServiceExposedInteropApi } from "./exposedInteropApi/DmnLanguageServiceExposedInteropApi";
import { DmnLanguageService } from "@kie-tools/dmn-language-service";

export interface CustomWindow extends Window {
  envelope: {
    dmnLanguageService: DmnLanguageServiceExposedInteropApi;
  };
}

declare let window: CustomWindow;

export class BpmnEditorFactory implements EditorFactory<BpmnEditor, BpmnEditorChannelApi> {
  constructor(private readonly gwtEditorEnvelopeConfig: { shouldLoadResourcesDynamically: boolean }) {}

  public async createEditor(
    ctx: KogitoEditorEnvelopeContextType<BpmnEditorChannelApi>,
    initArgs: EditorInitArgs
  ): Promise<BpmnEditor> {
    const dmnLs = new DmnLanguageService({
      // currently does not need to implement it since we don't need a way to read other Dmn files.
      getModelXml: () => Promise.resolve(""),
    });

    const exposedInteropApi: CustomWindow["envelope"] = {
      dmnLanguageService: {
        getDmnDocumentData: (dmnContent) => {
          return dmnLs.getDmnDocumentData(dmnContent);
        },
      },
    };

    window.envelope = {
      ...(window.envelope ?? {}),
      ...exposedInteropApi,
    };

    const languageData = getBpmnLanguageData(initArgs.resourcesPathPrefix);
    const factory = new GwtEditorWrapperFactory<BpmnEditor>(
      languageData,
      (self) =>
        new BpmnEditorImpl(
          languageData.editorId,
          self.gwtAppFormerConsumedInteropApi.getEditor(languageData.editorId),
          ctx.channelApi,
          new XmlFormatter(),
          self.gwtStateControlService,
          self.kieBcEditorsI18n
        ),
      this.gwtEditorEnvelopeConfig
    );

    const editor = await factory.createEditor(ctx, initArgs);
    return editor;
  }
}
