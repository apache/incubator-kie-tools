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

import { GwtEditorWrapper } from "../../common";
import { CanvasConsumedInteropApi } from "../../canvas/CanvasConsumedInteropApi";

interface CustomWindow extends Window {
  canvas: CanvasConsumedInteropApi;
}

declare let window: CustomWindow;

export interface BpmnEditor extends GwtEditorWrapper, CanvasConsumedInteropApi {
  myBpmnMethod(): string;
}

export class BpmnEditorImpl extends GwtEditorWrapper implements BpmnEditor {
  public myBpmnMethod() {
    return "bpmn-specific--configured";
  }

  public getNodeIds() {
    return window.canvas.getNodeIds();
  }

  public getBackgroundColor(uuid: string) {
    return window.canvas.getBackgroundColor(uuid);
  }

  public setBackgroundColor(uuid: string, backgroundColor: string) {
    window.canvas.setBackgroundColor(uuid, backgroundColor);
  }

  public getBorderColor(uuid: string) {
    return window.canvas.getBorderColor(uuid);
  }

  public setBorderColor(uuid: string, borderColor: string) {
    window.canvas.setBorderColor(uuid, borderColor);
  }

  public getLocation(uuid: string) {
    return window.canvas.getLocation(uuid);
  }

  public getAbsoluteLocation(uuid: string) {
    return window.canvas.getAbsoluteLocation(uuid);
  }

  public getDimensions(uuid: string) {
    return window.canvas.getDimensions(uuid);
  }

  public applyState(uuid: string, state: string) {
    window.canvas.applyState(uuid, state);
  }

  public centerNode(uuid: string) {
    window.canvas.centerNode(uuid);
  }
}
