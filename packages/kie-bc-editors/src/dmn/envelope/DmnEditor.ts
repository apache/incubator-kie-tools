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
import { JsLienzo } from "../../jslienzo/JsLienzo";

interface CustomWindow extends Window {
  jsLienzo: JsLienzo;
}

declare let window: CustomWindow;

export interface DmnEditor extends GwtEditorWrapper, JsLienzo {
  myDmnMethod(): string;
}

export class DmnEditorImpl extends GwtEditorWrapper implements DmnEditor {
  public myDmnMethod() {
    return "dmn-specific--configured";
  }

  public getNodeIds() {
    return window.jsLienzo.getNodeIds();
  }

  public getBackgroundColor(UUID: string) {
    return window.jsLienzo.getBackgroundColor(UUID);
  }

  public setBackgroundColor(UUID: string, backgroundColor: string) {
    window.jsLienzo.setBackgroundColor(UUID, backgroundColor);
  }

  public getBorderColor(UUID: string) {
    return window.jsLienzo.getBorderColor(UUID);
  }

  public setBorderColor(UUID: string, borderColor: string) {
    window.jsLienzo.setBorderColor(UUID, borderColor);
  }

  public getLocation(UUID: string) {
    return window.jsLienzo.getLocation(UUID);
  }

  public getAbsoluteLocation(UUID: string) {
    return window.jsLienzo.getAbsoluteLocation(UUID);
  }

  public getDimensions(UUID: string) {
    return window.jsLienzo.getDimensions(UUID);
  }
}
