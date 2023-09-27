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
import { WorkflowDefinitionListEnvelopeViewApi } from "./WorkflowDefinitionListEnvelopeView";
import {
  Association,
  WorkflowDefinitionListChannelApi,
  WorkflowDefinitionListEnvelopeApi,
  WorkflowDefinitionListInitArgs,
} from "../api";
import { WorkflowDefinitionListEnvelopeContext } from "./WorkflowDefinitionListEnvelopeContext";

/**
 * Implementation of the WorkflowDefinitionListEnvelopeApi
 */
export class WorkflowDefinitionListEnvelopeApiImpl implements WorkflowDefinitionListEnvelopeApi {
  private view: () => WorkflowDefinitionListEnvelopeViewApi;
  private capturedInitRequestYet = false;
  constructor(
    private readonly args: EnvelopeApiFactoryArgs<
      WorkflowDefinitionListEnvelopeApi,
      WorkflowDefinitionListChannelApi,
      WorkflowDefinitionListEnvelopeViewApi,
      WorkflowDefinitionListEnvelopeContext
    >
  ) {}

  private hasCapturedInitRequestYet() {
    return this.capturedInitRequestYet;
  }

  private ackCapturedInitRequest() {
    this.capturedInitRequestYet = true;
  }

  workflowDefinitionList__init = async (
    association: Association,
    initArgs: WorkflowDefinitionListInitArgs
  ): Promise<void> => {
    this.args.envelopeClient.associate(association.origin, association.envelopeServerId);

    if (this.hasCapturedInitRequestYet()) {
      return;
    }

    this.ackCapturedInitRequest();
    this.view = await this.args.viewDelegate();
    this.view().initialize(initArgs);
  };
}
