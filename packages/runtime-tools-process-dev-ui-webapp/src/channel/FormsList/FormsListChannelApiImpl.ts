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

import { FormInfo } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import {
  FormFilter,
  FormsListChannelApi,
  OnOpenFormListener,
  UnSubscribeHandler,
} from "@kie-tools/runtime-tools-process-enveloped-components/dist/formsList";
import { getForms } from "@kie-tools/runtime-tools-process-gateway-api/dist/gatewayApi";

export class FormsListChannelApiImpl implements FormsListChannelApi {
  private _formFilter: FormFilter = {
    formNames: [],
  };
  private readonly listeners: OnOpenFormListener[] = [];

  constructor(private baseUrl: string) {}

  formsList__getFormFilter(): Promise<FormFilter> {
    return Promise.resolve(this._formFilter);
  }

  formsList__applyFilter(formFilter: FormFilter): void {
    this._formFilter = formFilter;
  }

  formsList__getFormsQuery(): Promise<FormInfo[]> {
    return getForms(this.baseUrl, this._formFilter.formNames);
  }

  formsList__openForm(formData: FormInfo): void {
    this.listeners.forEach((listener) => listener.onOpen(formData));
  }

  formsList__onOpenFormListen(listener: OnOpenFormListener): Promise<UnSubscribeHandler> {
    this.listeners.push(listener);

    const unSubscribe = () => {
      const index = this.listeners.indexOf(listener);
      if (index > -1) {
        this.listeners.splice(index, 1);
      }
    };

    return Promise.resolve({
      unSubscribe,
    });
  }
}
