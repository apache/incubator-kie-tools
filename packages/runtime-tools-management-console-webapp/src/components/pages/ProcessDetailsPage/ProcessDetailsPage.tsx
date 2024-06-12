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
import * as React from "react";
import { useState, useEffect, useCallback, useMemo } from "react";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { RouteComponentProps } from "react-router-dom";
import ProcessDetailsContainer from "../../containers/ProcessDetailsContainer/ProcessDetailsContainer";
import { StaticContext, useHistory } from "react-router";
import * as H from "history";
import "../../styles.css";
import { OUIAProps, ouiaPageTypeAndObjectId } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { ProcessInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { PageSectionHeader } from "@kie-tools/runtime-tools-components/dist/components/PageSectionHeader";
import {
  useProcessDetailsGatewayApi,
  ProcessDetailsGatewayApi,
} from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessDetails";

interface MatchProps {
  instanceID: string;
}

const ProcessDetailsPage: React.FC<RouteComponentProps<MatchProps, StaticContext, H.LocationState> & OUIAProps> = ({
  ...props
}) => {
  const processId = props.match.params.instanceID;
  useEffect(() => {
    return ouiaPageTypeAndObjectId("process-instances", processId);
  });

  const gatewayApi: ProcessDetailsGatewayApi = useProcessDetailsGatewayApi();

  const history = useHistory();
  const [processInstance, setProcessInstance] = useState<ProcessInstance>();
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>();

  useEffect(() => {
    window.onpopstate = () => {
      props.history.push({ state: Object.assign({}, props.location.state) });
    };
  });

  const fetchDetails = useCallback(async () => {
    try {
      setIsLoading(true);
      const response = await gatewayApi.processDetailsQuery(processId);
      setProcessInstance(response);
    } catch (error) {
      setError(error);
    } finally {
      setIsLoading(false);
    }
  }, [gatewayApi, processId]);

  useEffect(() => {
    /* istanbul ignore else */
    if (processId) {
      fetchDetails();
    }
  }, [processId, fetchDetails]);

  useEffect(() => {
    // Redirecting to NoData page if the ProcessInstance cannot be found.
    if (!isLoading && !error && !processInstance) {
      let currentPage = JSON.parse(window.localStorage.getItem("state"));
      let prevPath;
      /* istanbul ignore else */
      if (currentPage) {
        currentPage = Object.assign({}, currentPage, props.location.state);
        const tempPath = currentPage.prev.split("/");
        prevPath = tempPath.filter((item) => item);
      }
      history.push({
        pathname: "/NoData",
        state: {
          prev: currentPage ? currentPage.prev : "/ProcessInstances",
          title: "Process not found",
          description: `Process instance with the id ${processId} not found`,
          buttonText: currentPage
            ? `Go to ${prevPath[0]
                .replace(/([A-Z])/g, " $1")
                .trim()
                .toLowerCase()}`
            : "Go to process instances",
          rememberedData: Object.assign({}, props.location.state),
        },
      });
    }
  }, [error, history, isLoading, processId, processInstance, props.location.state]);

  const body = useMemo(() => {
    // Loading State
    if (isLoading) {
      return (
        <Card>
          <KogitoSpinner spinnerText="Loading process details..." />
        </Card>
      );
    }

    // Error State
    if (error) {
      return (
        <>
          <Card className="kogito-management-console__card-size">
            <Bullseye>
              <ServerErrors error={error} variant="large" />
            </Bullseye>
          </Card>
        </>
      );
    }

    // Process Instance Details
    if (processInstance) {
      return <ProcessDetailsContainer processInstance={processInstance} />;
    }
  }, [error, isLoading, processInstance]);

  return (
    <React.Fragment>
      <PageSectionHeader
        titleText="Process Details"
        breadcrumbText={["Home", "Processes", processInstance ? processInstance.processName : ""]}
        breadcrumbPath={[
          "/",
          {
            pathname: "/ProcessInstances",
            state: Object.assign({}, props.location.state),
          },
        ]}
      />
      <PageSection>{body}</PageSection>
    </React.Fragment>
  );
};

export default ProcessDetailsPage;
