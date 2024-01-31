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

import { AuthProvider } from "../authProviders/AuthProvidersApi";
import { AcceleratorConfig } from "../accelerators/AcceleratorsApi";
import { EditorConfig } from "../envelopeLocator/EditorEnvelopeLocatorApi";

export interface EnvJson {
  KIE_SANDBOX_VERSION: string;
  KIE_SANDBOX_EXTENDED_SERVICES_URL: string;
  KIE_SANDBOX_CORS_PROXY_URL: string;
  KIE_SANDBOX_DEV_DEPLOYMENT_BASE_IMAGE_URL: string;
  KIE_SANDBOX_DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE_URL: string;
  KIE_SANDBOX_DEV_DEPLOYMENT_DMN_FORM_WEBAPP_IMAGE_URL: string;
  KIE_SANDBOX_DEV_DEPLOYMENT_IMAGE_PULL_POLICY: string;
  KIE_SANDBOX_REQUIRE_CUSTOM_COMMIT_MESSAGE: boolean;
  KIE_SANDBOX_CUSTOM_COMMIT_MESSAGE_VALIDATION_SERVICE_URL: string;
  KIE_SANDBOX_AUTH_PROVIDERS: AuthProvider[];
  KIE_SANDBOX_ACCELERATORS: AcceleratorConfig[];
  KIE_SANDBOX_EDITORS: EditorConfig[];
  KIE_SANDBOX_APP_NAME: string;
}
