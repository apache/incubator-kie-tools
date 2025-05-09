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

import { Form, FormContent } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { getFormContent, saveFormContent } from "@kie-tools/runtime-tools-process-gateway-api/dist/gatewayApi";
import { FormDetailsChannelApi } from "@kie-tools/runtime-tools-process-enveloped-components/dist/formDetails";

export class FormDetailsChannelApiImpl implements FormDetailsChannelApi {
  constructor(private baseUrl: string) {}

  formDetails__getFormContent(formName: string): Promise<Form> {
    return getFormContent(this.baseUrl, formName);
  }
  formDetails__saveFormContent(formName: string, formContent: FormContent): Promise<void> {
    return saveFormContent(this.baseUrl, formName, formContent);
  }
}
