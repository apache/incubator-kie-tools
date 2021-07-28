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

import { KogitoEditorEnvelopeContextType } from "@kie-tooling-core/editor/dist/api";
import { EditorEnvelopeViewApi, KogitoEditorEnvelopeApiImpl } from "@kie-tooling-core/editor/dist/envelope";
import { EnvelopeApiFactoryArgs } from "@kie-tooling-core/envelope";
import { BpmnEditorChannelApi, BpmnEditorEnvelopeApi } from "../api";
import { BpmnEditor } from "./BpmnEditor";
import { BpmnEditorFactory } from "./BpmnEditorFactory";

export type BpmnEnvelopeApiFactoryArgs = EnvelopeApiFactoryArgs<
  BpmnEditorEnvelopeApi,
  BpmnEditorChannelApi,
  EditorEnvelopeViewApi<BpmnEditor>,
  KogitoEditorEnvelopeContextType<BpmnEditorChannelApi>
>;

export class BpmnEditorEnvelopeApiImpl
  extends KogitoEditorEnvelopeApiImpl<BpmnEditor, BpmnEditorEnvelopeApi, BpmnEditorChannelApi>
  implements BpmnEditorEnvelopeApi
{
  constructor(
    private readonly bpmnArgs: BpmnEnvelopeApiFactoryArgs,
    gwtEditorEnvelopeConfig: { shouldLoadResourcesDynamically: boolean }
  ) {
    super(bpmnArgs, new BpmnEditorFactory(gwtEditorEnvelopeConfig));
  }

  public myBpmnEnvelopeMethod() {
    this.bpmnArgs.envelopeContext.channelApi.notifications.myBpmnChannelMethod();
    const editor = this.bpmnArgs.view().getEditor();
    const ret = editor?.myBpmnMethod() ?? "bpmn-specific--default";
    return Promise.resolve(ret);
  }
}
