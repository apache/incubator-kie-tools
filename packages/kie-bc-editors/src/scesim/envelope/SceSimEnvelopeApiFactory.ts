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
import { SceSimEditorChannelApi, SceSimEditorEnvelopeApi } from "../api";
import { SceSimEditor } from "./SceSimEditor";
import { SceSimEditorFactory } from "./SceSimEditorFactory";

export type SceSimEnvelopeApiFactoryArgs = EnvelopeApiFactoryArgs<
  SceSimEditorEnvelopeApi,
  SceSimEditorChannelApi,
  EditorEnvelopeViewApi<SceSimEditor>,
  KogitoEditorEnvelopeContextType<SceSimEditorChannelApi>
>;

export class SceSimEditorEnvelopeApiImpl
  extends KogitoEditorEnvelopeApiImpl<SceSimEditor, SceSimEditorEnvelopeApi, SceSimEditorChannelApi>
  implements SceSimEditorEnvelopeApi
{
  constructor(
    private readonly sceSimArgs: SceSimEnvelopeApiFactoryArgs,
    gwtEditorEnvelopeConfig: { shouldLoadResourcesDynamically: boolean }
  ) {
    super(sceSimArgs, new SceSimEditorFactory(gwtEditorEnvelopeConfig));
  }

  public mySceSimEnvelopeMethod() {
    const editor = this.sceSimArgs.view().getEditor();
    const ret = editor?.mySceSimMethod() ?? "scesim-specific--default";
    return Promise.resolve(ret);
  }
}
