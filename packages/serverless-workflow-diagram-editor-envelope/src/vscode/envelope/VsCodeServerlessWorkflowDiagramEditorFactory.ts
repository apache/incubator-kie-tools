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
import { JavaCodeCompletionApi } from "@kie-tools-core/vscode-java-code-completion/dist/api";
import { DiagramExposedInteropApi } from "../../api/DiagramExposedInteropApi";
import { DiagramService } from "../../api/DiagramService";
import { ServerlessWorkflowDiagramEditor, ServerlessWorkflowDiagramEditorFactory } from "../../envelope";
import { VsCodeServerlessWorkflowDiagramEditorChannelApi } from "../api/VsCodeServerlessWorkflowDiagramEditorChannelApi";
import { ServerlessWorkflowDiagramEditorEnvelopeApi } from "../../api";

export interface CustomWindow extends Window {
  envelope: {
    javaCodeCompletionService: JavaCodeCompletionApi;
    diagramService: DiagramExposedInteropApi;
  };
}

declare let window: CustomWindow;

class JavaCodeCompletionService implements JavaCodeCompletionApi {
  constructor(
    private readonly envelopeContext: KogitoEditorEnvelopeContextType<
      ServerlessWorkflowDiagramEditorEnvelopeApi,
      VsCodeServerlessWorkflowDiagramEditorChannelApi
    >
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

export class VsCodeServerlessWorkflowDiagramEditorFactory
  implements
    EditorFactory<
      ServerlessWorkflowDiagramEditor,
      ServerlessWorkflowDiagramEditorEnvelopeApi,
      VsCodeServerlessWorkflowDiagramEditorChannelApi
    >
{
  constructor(private readonly gwtEditorEnvelopeConfig: { shouldLoadResourcesDynamically: boolean }) {}

  public createEditor(
    ctx: KogitoEditorEnvelopeContextType<
      ServerlessWorkflowDiagramEditorEnvelopeApi,
      VsCodeServerlessWorkflowDiagramEditorChannelApi
    >,
    initArgs: EditorInitArgs
  ): Promise<ServerlessWorkflowDiagramEditor> {
    window.envelope = {
      ...(window.envelope ?? {}),
      ...{ javaCodeCompletionService: new JavaCodeCompletionService(ctx) },
      diagramService: new DiagramService(ctx),
    };

    const factory = new ServerlessWorkflowDiagramEditorFactory(this.gwtEditorEnvelopeConfig);
    return factory.createEditor(ctx, initArgs);
  }
}
