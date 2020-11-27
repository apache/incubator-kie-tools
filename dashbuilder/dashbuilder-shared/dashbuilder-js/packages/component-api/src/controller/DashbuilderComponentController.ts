/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import * as Bus from "./ComponentBus";
import { DataSet, FilterRequest } from "../dataset";
import { FunctionCallRequest, FunctionResponse, FunctionResultType } from "../function";
import { MessageType } from "../message";
import { MessageProperty } from "../message/MessageProperty";
import { ComponentController } from "./ComponentController";

interface FunctionCallbacks {
  onSucess: (result: any) => void;

  onError: (message: string) => void;
}

export class DashbuilderComponentController implements ComponentController {
  private callbacks: Map<string, FunctionCallbacks> = new Map();

  private bus = Bus.INSTANCE;

  public onInit: (params: Map<string, any>) => void = p => {
    console.log("Received INIT.");
    console.log(p);
  };

  public onDataSet: (dataSet: DataSet, params?: Map<string, any>) => void = ds => {
    console.log("Received DataSet.");
    console.log(ds);
  };

  public setOnDataSet(onDataSet: (dataSet: DataSet, params?: Map<string, any>) => void) {
    this.onDataSet = onDataSet;
  }

  public setOnInit(onInit: (params: Map<string, any>) => void) {
    this.onInit = onInit;
  }

  public ready(): void {
    // do nothing because it is not support ATM
  }

  public requireConfigurationFix(message: string): void {
    const props = new Map<MessageProperty, any>();
    props.set(MessageProperty.CONFIGURATION_ISSUE, message);
    this.bus.send({
      type: MessageType.FIX_CONFIGURATION,
      properties: props
    });
  }
  public configurationOk(): void {
    this.bus.send({
      type: MessageType.CONFIGURATION_OK,
      properties: new Map()
    });
  }

  public filter(filterRequest: FilterRequest): void {
    const props = new Map<MessageProperty, any>();
    props.set(MessageProperty.FILTER, filterRequest);
    this.bus.send({
      type: MessageType.FILTER,
      properties: props
    });
  }
  public callFunction(functionCallRequest: FunctionCallRequest): Promise<any> {
    const props = new Map<MessageProperty, any>();
    props.set(MessageProperty.FUNCTION_CALL, functionCallRequest);
    this.bus.send({
      type: MessageType.FUNCTION_CALL,
      properties: props
    });
    return new Promise((resolve, error) => {
      const key = this.buildFunctionKey(functionCallRequest);
      this.callbacks.set(key, {
        onSucess: resolve,
        onError: error
      });
    });
  }

  public receiveFunctionResponse(functionResponse: FunctionResponse): void {
    const key = this.buildFunctionKey(functionResponse.request);
    const functionCallbacks = this.callbacks.get(key);
    if (functionCallbacks) {
      if (
        functionResponse.resultType === FunctionResultType.ERROR ||
        functionResponse.resultType === FunctionResultType.NOT_FOUND
      ) {
        functionCallbacks.onError(functionResponse.message);
      } else {
        functionCallbacks.onSucess(functionResponse.result);
      }
    } else {
      console.warn("Callbacks for function call not found. Key: " + key);
    }
    this.callbacks.delete(key);
  }

  public setComponentBus(bus: Bus.ComponentBus) {
    this.bus = bus;
  }

  private buildFunctionKey(functionRequest: FunctionCallRequest): string {
    return functionRequest.functionName;
  }
}
