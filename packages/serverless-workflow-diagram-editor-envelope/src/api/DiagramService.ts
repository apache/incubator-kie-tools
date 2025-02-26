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

import { KogitoEditorEnvelopeContextType } from "@kie-tools-core/editor/dist/api";
import { DiagramExposedInteropApi } from "./DiagramExposedInteropApi";
import { ServerlessWorkflowDiagramEditorChannelApi } from "./ServerlessWorkflowDiagramEditorChannelApi";
import { ServerlessWorkflowDiagramEditorEnvelopeApi } from "./ServerlessWorkflowDiagramEditorEnvelopeApi";

/**
 * Class for diagram window interactions.
 */
export class DiagramService implements DiagramExposedInteropApi {
  constructor(
    private readonly envelopeContext: KogitoEditorEnvelopeContextType<
      ServerlessWorkflowDiagramEditorEnvelopeApi,
      ServerlessWorkflowDiagramEditorChannelApi
    >
  ) {}

  public onNodeSelected(nodeName: string) {
    this.envelopeContext.channelApi.notifications.kogitoSwfDiagramEditor__onNodeSelected.send({ nodeName });
  }

  public setContentSuccess() {
    this.envelopeContext.channelApi.notifications.kogitoSwfDiagramEditor__setContentSuccess.send();
  }
}
