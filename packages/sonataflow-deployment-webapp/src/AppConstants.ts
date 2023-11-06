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

import { AppData } from "./data";

export const SONATAFLOW_DEPLOYMENT_DOCUMENTATION_URL = "https://sonataflow.org/serverlessworkflow/latest/index.html";
export const SONATAFLOW_DEPLOYMENT_DATAINDEX_DOCUMENTATION_URL =
  "https://sonataflow.org/serverlessworkflow/latest/data-index/data-index-core-concepts.html";
export const KUBESMARTS_URL = "https://start.kubesmarts.org";
export const APPDATA_JSON_FILENAME = "sonataflow-deployment-webapp-data.json";

export const DEFAULT_APPDATA_VALUES: AppData = {
  appName: "Deployment",
  showDisclaimer: false,
  dataIndexUrl: "/graphql",
};
