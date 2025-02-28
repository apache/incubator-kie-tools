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

import { EditorFactory, EditorInitArgs, KogitoEditorEnvelopeContextType } from "@kie-tools-core/editor/dist/api";
import { BpmnEditor } from "../BpmnEditor";
import { JavaCodeCompletionApi } from "@kie-tools-core/vscode-java-code-completion/dist/api";
import { BpmnEditorFactory } from "../BpmnEditorFactory";
import { VsCodeBpmnEditorChannelApi } from "./VsCodeBpmnEditorChannelApi";
import { BpmnEditorEnvelopeApi } from "../../api";

export interface CustomWindow extends Window {
  envelope: {
    javaCodeCompletionService: JavaCodeCompletionApi;
  };
}

declare let window: CustomWindow;

class JavaCodeCompletionService implements JavaCodeCompletionApi {
  constructor(
    private readonly envelopeContext: KogitoEditorEnvelopeContextType<BpmnEditorEnvelopeApi, VsCodeBpmnEditorChannelApi>
  ) {}
  getAccessors(fqcn: string, query: string) {
    return this.envelopeContext.channelApi.requests.kogitoJavaCodeCompletion__getAccessors(fqcn, query);
  }
  getClasses(query: string) {
    return this.envelopeContext.channelApi.requests.kogitoJavaCodeCompletion__getClasses(query);
  }
  isLanguageServerAvailable() {
    return this.envelopeContext.channelApi.requests.kogitoJavaCodeCompletion__isLanguageServerAvailable();
  }
}

export class VsCodeBpmnEditorFactory
  implements EditorFactory<BpmnEditor, BpmnEditorEnvelopeApi, VsCodeBpmnEditorChannelApi>
{
  constructor(private readonly gwtEditorEnvelopeConfig: { shouldLoadResourcesDynamically: boolean }) {}

  public createEditor(
    ctx: KogitoEditorEnvelopeContextType<BpmnEditorEnvelopeApi, VsCodeBpmnEditorChannelApi>,
    initArgs: EditorInitArgs
  ): Promise<BpmnEditor> {
    window.envelope = {
      ...(window.envelope ?? {}),
      ...{ javaCodeCompletionService: new JavaCodeCompletionService(ctx) },
    };

    const factory = new BpmnEditorFactory(this.gwtEditorEnvelopeConfig);

    return factory.createEditor(ctx, initArgs);
  }
}
