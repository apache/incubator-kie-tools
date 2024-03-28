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
import React from "react";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import {
  JobsManagementGatewayApi,
  useJobsManagementGatewayApi,
} from "@kie-tools/runtime-tools-process-webapp-components/dist/JobsManagement";
import { EmbeddedJobsManagement } from "@kie-tools/runtime-tools-process-enveloped-components/dist/jobsManagement";

const JobsManagementContainer: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  const gatewayApi: JobsManagementGatewayApi = useJobsManagementGatewayApi();
  const appContext = useDevUIAppContext();
  return (
    <EmbeddedJobsManagement
      driver={gatewayApi}
      targetOrigin={appContext.getDevUIUrl()}
      {...componentOuiaProps(ouiaId, "jobs-management-container", ouiaSafe)}
    />
  );
};

export default JobsManagementContainer;
