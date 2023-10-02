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

import React, { useMemo } from "react";
import { WorkflowFormContext } from "./WorkflowFormContext";
import { WorkflowFormGatewayApiImpl } from "./WorkflowFormGatewayApi";
import { useSettings } from "../../../settings/SettingsContext";
import { useEnv } from "../../../env/EnvContext";

export function WorkflowFormContextProvider(props: React.PropsWithChildren<{}>) {
  const settings = useSettings();
  const { env } = useEnv();

  const gatewayApiImpl = useMemo(() => {
    return new WorkflowFormGatewayApiImpl(
      settings.runtimeTools.config.kogitoServiceUrl,
      "q/openapi.json",
      env.SERVERLESS_LOGIC_WEB_TOOLS_CORS_PROXY_URL
    );
  }, [settings, env]);

  return <WorkflowFormContext.Provider value={gatewayApiImpl}>{props.children}</WorkflowFormContext.Provider>;
}

export default WorkflowFormContextProvider;
