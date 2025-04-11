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

import { EnvelopeApiFactoryArgs } from "@kie-tools-core/envelope";
import { EditorEnvelopeViewApi, KogitoEditorEnvelopeApiImpl } from "@kie-tools-core/editor/dist/envelope";
import { KogitoEditorEnvelopeContextType } from "@kie-tools-core/editor/dist/api";
import { NewDmnEditorInterface } from "./NewDmnEditorFactory";
import { NewDmnEditorEnvelopeApi } from "./NewDmnEditorEnvelopeApi";
import { NewDmnEditorChannelApi } from "./NewDmnEditorChannelApi";
import { NewDmnEditorFactory } from "./NewDmnEditorFactory";
import { NewDmnEditorTypes } from "./NewDmnEditorTypes";
import { SharedValueProvider } from "../../envelope-bus/dist/api";

export type NewDmnEnvelopeApiFactoryArgs = EnvelopeApiFactoryArgs<
  NewDmnEditorEnvelopeApi,
  NewDmnEditorChannelApi,
  EditorEnvelopeViewApi<NewDmnEditorInterface>,
  KogitoEditorEnvelopeContextType<NewDmnEditorEnvelopeApi, NewDmnEditorChannelApi>
>;

export class NewDmnEditorEnvelopeApiImpl
  extends KogitoEditorEnvelopeApiImpl<NewDmnEditorInterface, NewDmnEditorEnvelopeApi, NewDmnEditorChannelApi>
  implements NewDmnEditorEnvelopeApi
{
  constructor(readonly dmnArgs: NewDmnEnvelopeApiFactoryArgs) {
    super(dmnArgs, new NewDmnEditorFactory());
  }

  public newDmnEditor_openBoxedExpressionEditor(nodeId: string): void {
    this.getEditorOrThrowError().openBoxedExpressionEditor(nodeId);
  }

  public newDmnEditor_openedBoxedExpressionEditorNodeId(): SharedValueProvider<string | undefined> {
    return {
      defaultValue: undefined,
    };
  }

  public newDmnEditor_showDmnEvaluationResults(
    evaluationResultsByNodeId: NewDmnEditorTypes.EvaluationResultsByNodeId
  ): void {
    this.getEditorOrThrowError().showDmnEvaluationResults(evaluationResultsByNodeId);
  }
}
