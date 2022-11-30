/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { EditorFactory, KogitoEditorEnvelopeContextType } from "@kie-tools-core/editor/dist/api";
import { EditorEnvelopeViewApi, KogitoEditorEnvelopeApiImpl } from "@kie-tools-core/editor/dist/envelope";
import { EnvelopeApiFactoryArgs } from "@kie-tools-core/envelope";
import { Position } from "monaco-editor";
import { ServerlessWorkflowTextEditorChannelApi, ServerlessWorkflowTextEditorEnvelopeApi } from "../api";
import { ServerlessWorkflowTextEditorApi } from "../editor";

export type ServerlessWorkflowTextEnvelopeApiFactoryArgs = EnvelopeApiFactoryArgs<
  ServerlessWorkflowTextEditorEnvelopeApi,
  ServerlessWorkflowTextEditorChannelApi,
  EditorEnvelopeViewApi<ServerlessWorkflowTextEditorApi>,
  KogitoEditorEnvelopeContextType<ServerlessWorkflowTextEditorChannelApi>
>;

export class ServerlessWorkflowTextEditorEnvelopeApiImpl
  extends KogitoEditorEnvelopeApiImpl<
    ServerlessWorkflowTextEditorApi,
    ServerlessWorkflowTextEditorEnvelopeApi,
    ServerlessWorkflowTextEditorChannelApi
  >
  implements ServerlessWorkflowTextEditorEnvelopeApi
{
  constructor(
    private readonly serverlessWorkflowArgs: ServerlessWorkflowTextEnvelopeApiFactoryArgs,
    editorFactory: EditorFactory<ServerlessWorkflowTextEditorApi, ServerlessWorkflowTextEditorChannelApi>
  ) {
    super(serverlessWorkflowArgs, editorFactory);
  }

  public kogitoSwfTextEditor__moveCursorToNode(args: { nodeName: string; documentUri: string }): void {
    this.getEditorOrThrowError().moveCursorToNode(args.nodeName);
  }

  public kogitoSwfTextEditor__moveCursorToPosition(position: Position): void {
    this.getEditorOrThrowError().moveCursorToPosition(position);
  }
}
