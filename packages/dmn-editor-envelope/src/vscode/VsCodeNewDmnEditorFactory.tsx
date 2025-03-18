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

import {
  Editor,
  EditorFactory,
  EditorInitArgs,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeContextType,
} from "@kie-tools-core/editor/dist/api";
import {
  JavaCodeCompletionAccessor,
  JavaCodeCompletionClass,
  JavaCodeCompletionChannelApi,
} from "@kie-tools-core/vscode-java-code-completion/dist/api";

import { DmnEditorInterface } from "../DmnEditorFactory";

/**
 * EXPOSED INTEROP API
 *
 * This API is exposed from the Envelope to be consumed on Java code.
 */
interface JavaCodeCompletionExposedInteropApi {
  getAccessors(fqcn: string, query: string): Promise<JavaCodeCompletionAccessor[]>;
  getClasses(query: string): Promise<JavaCodeCompletionClass[]>;
  isLanguageServerAvailable(): Promise<boolean>;
}

export interface VsCodeNewDmnEditorChannelApi extends KogitoEditorChannelApi, JavaCodeCompletionChannelApi {}

export interface CustomWindow extends Window {
  envelope: {
    javaCodeCompletionService: JavaCodeCompletionExposedInteropApi;
  };
}

declare let window: CustomWindow;

export class VsCodeNewDmnEditorFactory implements EditorFactory<Editor, VsCodeNewDmnEditorChannelApi> {
  public createEditor(
    envelopeContext: KogitoEditorEnvelopeContextType<VsCodeNewDmnEditorChannelApi>,
    initArgs: EditorInitArgs
  ): Promise<Editor> {
    const exposedInteropApi: CustomWindow["envelope"] = {
      javaCodeCompletionService: {
        getAccessors: (fqcn: string, query: string) => {
          return envelopeContext.channelApi.requests.kogitoJavaCodeCompletion__getAccessors(fqcn, query);
        },
        getClasses: (query: string) => {
          return envelopeContext.channelApi.requests.kogitoJavaCodeCompletion__getClasses(query);
        },
        isLanguageServerAvailable: () => {
          return envelopeContext.channelApi.requests.kogitoJavaCodeCompletion__isLanguageServerAvailable();
        },
      },
    };

    window.envelope = {
      ...(window.envelope ?? {}),
      ...exposedInteropApi,
    };
    return Promise.resolve(new DmnEditorInterface(envelopeContext, initArgs));
  }
}
