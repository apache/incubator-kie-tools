/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import * as AppFormer from "appformer-js-core";
import { GwtEditor } from "./GwtEditor";

export class GwtEditorWrapper extends AppFormer.Editor {
  public af_componentTitle: string;

  private gwtEditor: GwtEditor;

  constructor(gwtEditor: GwtEditor) {
    super("gwt-editor-wrapper");
    this.af_componentTitle = "GwtEditorWrapper";
    this.af_isReact = true;
    this.gwtEditor = gwtEditor;
  }

  public af_onOpen() {
    this.removeBusinessCentralHeaderPanel();
    this.removeBusinessCentralPanelHeader();
  }

  public af_componentRoot() {
    //Component will already be rendered when the script loads.
    return <></>;
  }

  public getContent() {
    return this.gwtEditor.getContent();
  }

  public isDirty() {
    return this.gwtEditor.isDirty();
  }

  public setContent(content: string) {
    try {
      //FIXME: Make setContent return a promise.
      this.gwtEditor.setContent(content.trim());
    } catch (e) {
      return Promise.reject(e);
    }

    return Promise.resolve();
  }

  private removeBusinessCentralHeaderPanel() {
    const headerPanel = document.getElementById("workbenchHeaderPanel");
    if (headerPanel) {
      const parentNode = headerPanel.parentNode as HTMLElement;
      if (parentNode) {
        parentNode.remove();
      }
    }
  }

  private removeBusinessCentralPanelHeader() {
    setTimeout(() => {
      const panelHeader = document.querySelector(".panel-heading.uf-listbar-panel-header");
      if (panelHeader) {
        panelHeader.remove();
      }
    }, 100);
  }
}
