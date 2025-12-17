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

import { EditorEnvelopeViewApi, KogitoEditorEnvelopeApiImpl } from "@kie-tools-core/editor/dist/envelope";
import { YardEditorApi, YardEditorChannelApi, YardEditorEnvelopeApi } from "../api";
import { EditorFactory, KogitoEditorEnvelopeContextType } from "@kie-tools-core/editor/dist/api";
import { EnvelopeApiFactoryArgs } from "@kie-tools-core/envelope";
import { Position } from "monaco-editor";

export type YardEnvelopeApiFactoryArgs = EnvelopeApiFactoryArgs<
  YardEditorEnvelopeApi,
  YardEditorChannelApi,
  EditorEnvelopeViewApi<YardEditorApi>,
  KogitoEditorEnvelopeContextType<YardEditorEnvelopeApi, YardEditorChannelApi>
>;

export class YardEditorEnvelopeApiImpl
  extends KogitoEditorEnvelopeApiImpl<YardEditorApi, YardEditorEnvelopeApi, YardEditorChannelApi>
  implements YardEditorEnvelopeApi
{
  constructor(
    private readonly factoryArgs: YardEnvelopeApiFactoryArgs,
    editorFactory: EditorFactory<YardEditorApi, YardEditorEnvelopeApi, YardEditorChannelApi>
  ) {
    super(factoryArgs, editorFactory);
  }

  public yardTextEditor_moveCursorToPosition(position: Position): void {
    this.getEditorOrThrowError().moveCursorToPosition(position);
  }
}
