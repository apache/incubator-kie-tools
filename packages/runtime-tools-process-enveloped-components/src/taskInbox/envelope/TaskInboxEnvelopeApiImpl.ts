/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import { EnvelopeApiFactoryArgs } from "@kie-tools-core/envelope";
import { TaskInboxEnvelopeViewApi } from "./TaskInboxEnvelopeView";
import { Association, TaskInboxChannelApi, TaskInboxEnvelopeApi, TaskInboxInitArgs } from "../api";
import { TaskInboxEnvelopeContext } from "./TaskInboxEnvelopeContext";

/**
 * Implementation of the TaskInboxEnvelopeApi
 */
export class TaskInboxEnvelopeApiImpl implements TaskInboxEnvelopeApi {
  private view: () => TaskInboxEnvelopeViewApi;
  private capturedInitRequestYet = false;
  constructor(
    private readonly args: EnvelopeApiFactoryArgs<
      TaskInboxEnvelopeApi,
      TaskInboxChannelApi,
      TaskInboxEnvelopeViewApi,
      TaskInboxEnvelopeContext
    >
  ) {}

  private hasCapturedInitRequestYet() {
    return this.capturedInitRequestYet;
  }

  private ackCapturedInitRequest() {
    this.capturedInitRequestYet = true;
  }

  taskInbox__init = async (association: Association, initArgs: TaskInboxInitArgs): Promise<void> => {
    this.args.envelopeClient.associate(association.origin, association.envelopeServerId);

    if (this.hasCapturedInitRequestYet()) {
      return;
    }
    this.view = await this.args.viewDelegate();
    this.ackCapturedInitRequest();
    this.view().initialize(initArgs.initialState, initArgs.allTaskStates, initArgs.activeTaskStates);
  };

  taskInbox__notify = (userName: string): Promise<void> => {
    this.view().notify(userName);
    return Promise.resolve();
  };
}
