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

import * as React from "react";
import { GwtEditor } from "./consumedInteropApi/GwtAppFormerConsumedInteropApi";
import { Editor, EditorTheme, KogitoEditorChannelApi } from "@kie-tools-core/editor/dist/api";
import { editors } from "./GwtEditorMapping";
import { TextFormatter } from "./TextFormatter";
import { GwtStateControlService } from "./gwtStateControl";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import { KieBcEditorsI18n } from "./i18n";
import { Notification } from "@kie-tools-core/notifications/dist/api";

export class GwtEditorWrapper implements Editor {
  public readonly af_isReact = true;
  public readonly af_componentId = "gwt-editor-wrapper";

  public readonly af_componentTitle: string;
  public readonly editorId: string;

  private readonly gwtEditor: GwtEditor;
  private readonly textFormatter: TextFormatter;
  private readonly channelApi: MessageBusClientApi<KogitoEditorChannelApi>;
  private readonly stateControlService: GwtStateControlService;
  private readonly kieBcEditorsI18n: I18n<KieBcEditorsI18n>;

  public normalizedPosixPathRelativeToTheWorkspaceRoot: string;

  constructor(
    editorId: string,
    gwtEditor: GwtEditor,
    channelApi: MessageBusClientApi<KogitoEditorChannelApi>,
    textFormatter: TextFormatter,
    stateControlService: GwtStateControlService,
    kieBcEditorsI18n: I18n<KieBcEditorsI18n>
  ) {
    this.af_componentTitle = editorId;
    this.stateControlService = stateControlService;
    this.af_isReact = true;
    this.gwtEditor = gwtEditor;
    this.channelApi = channelApi;
    this.editorId = editorId;
    this.textFormatter = textFormatter;
    this.kieBcEditorsI18n = kieBcEditorsI18n;
  }

  public af_onOpen() {
    this.removeBusinessCentralHeaderPanel();
    if (this.editorId === editors.bpmn.id) {
      this.removeHeaderIfOnlyOneItemOnTable();
    } else {
      this.injectStyleToFixResponsivenessIssue_DROOLS_3995();
    }
  }

  public af_componentRoot() {
    //Component will already be rendered when the script loads.
    return <></>;
  }

  public async undo() {
    return this.stateControlService.undo();
  }

  public async redo() {
    return this.stateControlService.redo();
  }

  public getContent() {
    return this.gwtEditor.getContent().then((content) => this.textFormatter.format(content));
  }

  public setContent(normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string) {
    this.normalizedPosixPathRelativeToTheWorkspaceRoot = normalizedPosixPathRelativeToTheWorkspaceRoot;
    setTimeout(() => this.removeBusinessCentralPanelHeader(), 100);
    return this.gwtEditor.setContent(normalizedPosixPathRelativeToTheWorkspaceRoot, content.trim());
  }

  public selectStateByName(name: string | null) {
    return this.gwtEditor.selectStateByName(name);
  }

  public getPreview(): Promise<string | undefined> {
    return this.gwtEditor.getPreview();
  }

  public validate(): Promise<Notification[]> {
    return this.gwtEditor.validate();
  }

  public setTheme(theme: EditorTheme): Promise<void> {
    if (!theme) {
      Promise.resolve();
    }

    switch (theme) {
      case EditorTheme.DARK: {
        return Promise.resolve(this.gwtEditor.applyTheme("dark"));
      }
      default: {
        return Promise.resolve(this.gwtEditor.applyTheme("light"));
      }
    }
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
    const panelHeaderSpan = document.querySelector(".panel-heading.uf-listbar-panel-header span");
    if (panelHeaderSpan) {
      panelHeaderSpan.textContent = "";
    }
  }

  private removeHeaderIfOnlyOneItemOnTable() {
    const headerTable = document.querySelector(".tabbable.uf-tabbar-panel.uf-multi-page-editor > table");
    if (headerTable && headerTable.querySelectorAll("td > ul > li").length <= 1) {
      headerTable.remove();
    }
  }

  private injectStyleToFixResponsivenessIssue_DROOLS_3995() {
    const style = document.createElement("style");
    style.textContent = '[data-i18n-prefix="DataTypeListItemView."] .list-view-pf-body { display: flex !important; }';
    document.head.appendChild(style);
  }
}
