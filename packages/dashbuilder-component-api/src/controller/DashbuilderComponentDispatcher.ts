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

import { DataSet } from "../dataset";
import { FunctionResponse } from "../function";
import { ComponentMessage, MessageType } from "../message";
import { MessageProperty } from "../message/MessageProperty";
import { ComponentBus } from "./ComponentBus";

import { DashbuilderComponentController } from "./DashbuilderComponentController";
import { InternalComponentDispatcher } from "./InternalComponentListener";

export class DashbuilderComponentDispatcher implements InternalComponentDispatcher {
  private componentId: string;

  constructor(private readonly bus: ComponentBus, public readonly componentController: DashbuilderComponentController) {
    // no op
  }

  private readonly messageDispatcher = (message: ComponentMessage) => {
    if (message.type === MessageType.INIT) {
      this.componentId = message.properties.get(MessageProperty.COMPONENT_ID);
      this.componentController.init(message.properties);
    }

    if (message.type === MessageType.DATASET) {
      const dataSet = message.properties.get(MessageProperty.DATASET) as DataSet;
      this.componentController.onDataSet(dataSet, message.properties);
    }

    if (message.type === MessageType.FUNCTION_RESPONSE) {
      const functionResponse = message.properties.get(MessageProperty.FUNCTION_RESPONSE) as FunctionResponse;
      this.componentController.receiveFunctionResponse(functionResponse);
    }
  };

  public isAutoReady(): boolean {
    // READY not implemented at the moment
    return true;
  }

  public init(): void {
    this.bus.setListener(this.messageDispatcher);
    this.bus.start();
  }

  public sendMessage(componentMessage: ComponentMessage): void {
    componentMessage.properties.set(MessageProperty.COMPONENT_ID, this.componentId);
    window.parent.postMessage(componentMessage, window.location.href);
  }

  public stop(): void {
    this.bus.destroy();
  }
}
