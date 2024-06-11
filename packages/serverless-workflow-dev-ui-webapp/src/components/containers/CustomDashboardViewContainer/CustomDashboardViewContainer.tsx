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

import React from "react";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { EmbeddedCustomDashboardView } from "@kie-tools/runtime-tools-shared-enveloped-components/dist/customDashboardView";
import { useCustomDashboardViewGatewayApi } from "../../../channel/CustomDashboardView/CustomDashboardViewContext";
import { CustomDashboardViewGatewayApi } from "../../../channel/CustomDashboardView";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";

interface CustomDashboardViewContainerContainerProps {
  dashboardName: string;
}

const CustomDashboardViewContainer: React.FC<CustomDashboardViewContainerContainerProps & OUIAProps> = ({
  dashboardName,
  ouiaId,
  ouiaSafe,
}) => {
  const gatewayApi: CustomDashboardViewGatewayApi = useCustomDashboardViewGatewayApi();
  const appContext = useDevUIAppContext();

  return (
    <EmbeddedCustomDashboardView
      {...componentOuiaProps(ouiaId, "workflow-details-container", ouiaSafe)}
      driver={gatewayApi}
      targetOrigin={appContext.getDevUIUrl()}
      dashboardName={dashboardName}
    />
  );
};

export default CustomDashboardViewContainer;
