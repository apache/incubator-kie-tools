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

import { EnvelopeApiFactoryArgs } from "@kie-tooling-core/envelope";
import { DmnEditorChannelApi, DmnEditorEnvelopeApi } from "../api";
import { EditorEnvelopeViewApi, KogitoEditorEnvelopeApiImpl } from "@kie-tooling-core/editor/dist/envelope";
import { DmnEditor } from "./DmnEditor";
import { KogitoEditorEnvelopeContextType } from "@kie-tooling-core/editor/dist/api";
import { DmnEditorFactory } from "./DmnEditorFactory";

export type DmnEnvelopeApiFactoryArgs = EnvelopeApiFactoryArgs<
  DmnEditorEnvelopeApi,
  DmnEditorChannelApi,
  EditorEnvelopeViewApi<DmnEditor>,
  KogitoEditorEnvelopeContextType<DmnEditorChannelApi>
>;

export class DmnEditorEnvelopeApiImpl
  extends KogitoEditorEnvelopeApiImpl<DmnEditor, DmnEditorEnvelopeApi, DmnEditorChannelApi>
  implements DmnEditorEnvelopeApi
{
  constructor(
    private readonly dmnArgs: DmnEnvelopeApiFactoryArgs,
    gwtEditorEnvelopeConfig: { shouldLoadResourcesDynamically: boolean }
  ) {
    super(dmnArgs, new DmnEditorFactory(gwtEditorEnvelopeConfig));
  }

  public myDmnEnvelopeMethod() {
    this.dmnArgs.envelopeContext.channelApi.notifications.myDmnChannelMethod();
    const editor = this.dmnArgs.view().getEditor();
    const ret = editor?.myDmnMethod() ?? "dmn-specific--default";
    return Promise.resolve(ret);
  }
}
