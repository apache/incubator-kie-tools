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
import { WorkflowFormEnvelopeViewApi } from "./WorkflowFormEnvelopeView";
import { Association, WorkflowFormChannelApi, WorkflowFormEnvelopeApi } from "../api";
import { WorkflowFormEnvelopeContext } from "./WorkflowFormEnvelopeContext";

/**
 * Implementation of the WorkflowFormEnvelopeApi
 */
export class WorkflowFormEnvelopeApiImpl implements WorkflowFormEnvelopeApi {
  private view: () => WorkflowFormEnvelopeViewApi;
  private capturedInitRequestYet = false;
  constructor(
    private readonly args: EnvelopeApiFactoryArgs<
      WorkflowFormEnvelopeApi,
      WorkflowFormChannelApi,
      WorkflowFormEnvelopeViewApi,
      WorkflowFormEnvelopeContext
    >
  ) {}

  private hasCapturedInitRequestYet() {
    return this.capturedInitRequestYet;
  }

  private ackCapturedInitRequest() {
    this.capturedInitRequestYet = true;
  }

  workflowForm__init = async (association: Association, workflowDefinition: any): Promise<void> => {
    this.args.envelopeClient.associate(association.origin, association.envelopeServerId);

    if (this.hasCapturedInitRequestYet()) {
      return;
    }
    this.view = await this.args.viewDelegate();
    this.ackCapturedInitRequest();
    this.view().initialize(workflowDefinition);
  };
}
