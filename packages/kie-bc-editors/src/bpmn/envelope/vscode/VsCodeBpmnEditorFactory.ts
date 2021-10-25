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

import { BpmnEditorChannelApi } from "../../api";
import { EditorFactory, EditorInitArgs, KogitoEditorEnvelopeContextType } from "@kie-tooling-core/editor/dist/api";
import { BpmnEditor } from "../BpmnEditor";
import { JavaCodeCompletionApi } from "@kie-tooling-core/vscode-lsp/dist/api";
import { BpmnEditorFactory } from "../BpmnEditorFactory";
import { VsCodeBpmnEditorChannelApi } from "./VsCodeBpmnEditorChannelApi";

export interface CustomWindow extends Window {
  envelope: {
    lspService: JavaCodeCompletionApi;
  };
}

declare let window: CustomWindow;

class JavaCodeCompletionService implements JavaCodeCompletionApi {
  constructor(private readonly envelopeContext: KogitoEditorEnvelopeContextType<VsCodeBpmnEditorChannelApi>) {}
  getAccessors(fqcn: string, query: string) {
    return this.envelopeContext.channelApi.requests.kogitoLsp_getAccessors(fqcn, query);
  }
  getClasses(query: string) {
    return this.envelopeContext.channelApi.requests.kogitoLsp_getClasses(query);
  }
  isLanguageServerAvailable() {
    return this.envelopeContext.channelApi.requests.kogitoLsp_isLanguageServerAvailable();
  }
}

export class VsCodeBpmnEditorFactory implements EditorFactory<BpmnEditor, BpmnEditorChannelApi> {
  constructor(private readonly gwtEditorEnvelopeConfig: { shouldLoadResourcesDynamically: boolean }) {}

  public createEditor(
    ctx: KogitoEditorEnvelopeContextType<VsCodeBpmnEditorChannelApi>,
    initArgs: EditorInitArgs
  ): Promise<BpmnEditor> {
    window.envelope = {
      ...(window.envelope ?? {}),
      ...{ lspService: new JavaCodeCompletionService(ctx) },
    };

    const factory = new BpmnEditorFactory(this.gwtEditorEnvelopeConfig);

    return factory.createEditor(ctx, initArgs);
  }
}
