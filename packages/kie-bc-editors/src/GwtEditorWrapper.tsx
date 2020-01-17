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
import * as AppFormer from "@kogito-tooling/core-api";
import { GwtEditor } from "./GwtEditor";
import { EnvelopeBusInnerMessageHandler } from "@kogito-tooling/microeditor-envelope";
import { editors } from "./GwtEditorRoutes";
import { XmlFormatter } from "./XmlFormatter";

const KOGITO_JIRA_LINK = "https://issues.jboss.org/projects/KOGITO";

export class GwtEditorWrapper extends AppFormer.Editor {
  public readonly af_componentTitle: string;
  public readonly editorId: string;

  private readonly gwtEditor: GwtEditor;
  private readonly xmlFormatter: XmlFormatter;
  private readonly messageBus: EnvelopeBusInnerMessageHandler;

  constructor(
    editorId: string,
    gwtEditor: GwtEditor,
    messageBus: EnvelopeBusInnerMessageHandler,
    xmlFormatter: XmlFormatter
  ) {
    super("gwt-editor-wrapper");
    this.af_componentTitle = editorId;
    this.af_isReact = true;
    this.gwtEditor = gwtEditor;
    this.messageBus = messageBus;
    this.editorId = editorId;
    this.xmlFormatter = xmlFormatter;
  }

  public af_onOpen() {
    this.removeBusinessCentralHeaderPanel();
    if (this.editorId !== editors.dmn.id) {
      this.removeHeaderIfOnlyOneItemOnTable();
    } else {
      this.injectStyleToFixResponsivenessIssue_DROOLS_3995();
    }
  }

  public af_componentRoot() {
    //Component will already be rendered when the script loads.
    return <></>;
  }

  public getContent() {
    return this.gwtEditor.getContent().then(content => this.xmlFormatter.format(content));
  }

  public isDirty() {
    return this.gwtEditor.isDirty();
  }

  public setContent(path: string, content: string) {
    //FIXME: Make setContent return a promise.
    try {
      this.gwtEditor.setContent(path, content.trim());
      setTimeout(() => this.removeBusinessCentralPanelHeader(), 100);
    } catch (e) {
      this.messageBus.notify_setContentError(
        `This file contains a construct that is not yet supported. Please refer to ${KOGITO_JIRA_LINK} and report an issue. Don't forget to upload the current file.`
      );
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
