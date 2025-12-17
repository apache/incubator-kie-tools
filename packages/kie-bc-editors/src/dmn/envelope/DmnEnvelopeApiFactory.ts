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
import { DmnEditorChannelApi, DmnEditorEnvelopeApi } from "../api";
import { EditorEnvelopeViewApi, KogitoEditorEnvelopeApiImpl } from "@kie-tools-core/editor/dist/envelope";
import { DmnEditor } from "./DmnEditor";
import { KogitoEditorEnvelopeContextType } from "@kie-tools-core/editor/dist/api";
import { DmnEditorFactory } from "./DmnEditorFactory";

export type DmnEnvelopeApiFactoryArgs = EnvelopeApiFactoryArgs<
  DmnEditorEnvelopeApi,
  DmnEditorChannelApi,
  EditorEnvelopeViewApi<DmnEditor>,
  KogitoEditorEnvelopeContextType<DmnEditorEnvelopeApi, DmnEditorChannelApi>
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
    this.dmnArgs.envelopeContext.channelApi.notifications.myDmnChannelMethod.send();
    const editor = this.view().getEditor();
    const ret = editor?.myDmnMethod() ?? "dmn-specific--default";
    return Promise.resolve(ret);
  }

  public async canvas_getNodeIds() {
    const editor = this.view().getEditor();
    if (!editor) {
      throw new Error("Editor not found.");
    }
    return editor.getNodeIds();
  }

  public async canvas_getBackgroundColor(uuid: string) {
    const editor = this.view().getEditor();
    if (!editor) {
      throw new Error("Editor not found.");
    }
    return editor.getBackgroundColor(uuid);
  }

  public async canvas_setBackgroundColor(uuid: string, backgroundColor: string) {
    const editor = this.view().getEditor();
    if (!editor) {
      throw new Error("Editor not found.");
    }
    return editor.setBackgroundColor(uuid, backgroundColor);
  }

  public async canvas_getBorderColor(uuid: string) {
    const editor = this.view().getEditor();
    if (!editor) {
      throw new Error("Editor not found.");
    }
    return editor.getBorderColor(uuid);
  }

  public async canvas_setBorderColor(uuid: string, borderColor: string) {
    const editor = this.view().getEditor();
    if (!editor) {
      throw new Error("Editor not found.");
    }
    return editor.setBorderColor(uuid, borderColor);
  }

  public async canvas_getLocation(uuid: string) {
    const editor = this.view().getEditor();
    if (!editor) {
      throw new Error("Editor not found.");
    }
    return editor.getLocation(uuid);
  }

  public async canvas_getAbsoluteLocation(uuid: string) {
    const editor = this.view().getEditor();
    if (!editor) {
      throw new Error("Editor not found.");
    }
    return editor.getAbsoluteLocation(uuid);
  }

  public async canvas_getDimensions(uuid: string) {
    const editor = this.view().getEditor();
    if (!editor) {
      throw new Error("Editor not found.");
    }
    return editor.getDimensions(uuid);
  }

  public async canvas_applyState(uuid: string, state: string) {
    const editor = this.view().getEditor();
    if (!editor) {
      throw new Error("Editor not found.");
    }
    return editor.applyState(uuid, state);
  }

  public async canvas_centerNode(uuid: string) {
    const editor = this.view().getEditor();
    if (!editor) {
      throw new Error("Editor not found.");
    }
    return editor.centerNode(uuid);
  }
}
