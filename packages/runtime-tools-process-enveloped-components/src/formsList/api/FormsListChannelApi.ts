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

import { FormInfo } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { FormFilter } from "./FormsListEnvelopeApi";

export interface OnOpenFormListener {
  onOpen: (formData: FormInfo) => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}

export interface FormsListChannelApi {
  formsList__getFormFilter(): Promise<FormFilter>;
  formsList__applyFilter(formFilter: FormFilter): void;
  formsList__getFormsQuery(): Promise<FormInfo[]>;
  formsList__openForm(formData: FormInfo): void;
  formsList__onOpenFormListen(listener: OnOpenFormListener): Promise<UnSubscribeHandler>;
}
