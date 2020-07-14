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
import { getGuidedTourElementPosition, GwtEditor } from "./GwtAppFormerApi";
import { Editor } from "@kogito-tooling/editor-api";
import { editors } from "./GwtEditorRoutes";
import { XmlFormatter } from "./XmlFormatter";
import { GwtStateControlService } from "./gwtStateControl";
import { KogitoChannelApi, MessageBusClient } from "@kogito-tooling/microeditor-envelope-protocol";

const KOGITO_JIRA_LINK = "https://issues.jboss.org/projects/KOGITO";

export class GwtEditorWrapper extends Editor {
  public readonly af_componentTitle: string;
  public readonly editorId: string;

  private readonly gwtEditor: GwtEditor;
  private readonly xmlFormatter: XmlFormatter;
  private readonly messageBusClient: MessageBusClient<KogitoChannelApi>;
  private readonly stateControlService: GwtStateControlService;

  constructor(
    editorId: string,
    gwtEditor: GwtEditor,
    messageBus: MessageBusClient<KogitoChannelApi>,
    xmlFormatter: XmlFormatter,
    stateControlService: GwtStateControlService
  ) {
    super("gwt-editor-wrapper");
    this.af_componentTitle = editorId;
    this.stateControlService = stateControlService;
    this.af_isReact = true;
    this.gwtEditor = gwtEditor;
    this.messageBusClient = messageBus;
    this.editorId = editorId;
    this.xmlFormatter = xmlFormatter;
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
    return this.gwtEditor.getContent().then(content => this.xmlFormatter.format(content));
  }

  public getElementPosition(selector: string) {
    return Promise.resolve(getGuidedTourElementPosition(selector));
  }

  public setContent(path: string, content: string) {
    setTimeout(() => this.removeBusinessCentralPanelHeader(), 100);
    return this.gwtEditor.setContent(path, content.trim()).catch(() => {
      this.messageBusClient.notify(
        "receive_setContentError",
        `This file contains a construct that is not yet supported. Please refer to ${KOGITO_JIRA_LINK} and report an issue. Don't forget to upload the current file.`
      );
      return Promise.resolve();
    });
  }

  public getPreview(): Promise<string | undefined> {
    return this.gwtEditor.getPreview();
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
