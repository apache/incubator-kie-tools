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
import React, { useState, useEffect, useCallback, useRef } from "react";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { PageSectionHeader } from "@kie-tools/runtime-tools-components/dist/components/PageSectionHeader";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import ProcessDetailsContainer from "../../containers/ProcessDetailsContainer/ProcessDetailsContainer";
import { useProcessDetailsChannelApi } from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessDetails";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import "../../styles.css";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";
import {
  OUIAProps,
  componentOuiaProps,
  ouiaPageTypeAndObjectId,
} from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { ProcessInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";

const ProcessDetailsPage: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe, ...props }) => {
  useEffect(() => {
    return ouiaPageTypeAndObjectId("process-details");
  });

  const channelApi = useProcessDetailsChannelApi();
  const appContext = useDevUIAppContext();

  const navigate = useNavigate();
  const { processId } = useParams<{ processId?: string }>();
  const location = useLocation();
  const [processInstance, setProcessInstance] = useState<ProcessInstance>({} as ProcessInstance);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [fetchError, setFetchError] = useState<string>("");
  const currentPage = useRef(JSON.parse(window.localStorage.getItem("state")));

  useEffect(() => {
    window.onpopstate = () => {
      navigate({}, { state: Object.assign({}, location.state) });
    };
  });

  const fetchDetails = useCallback(async () => {
    let response: ProcessInstance = {} as ProcessInstance;
    let responseError: string = "";
    try {
      setIsLoading(true);
      response = await channelApi.processDetails__getProcessDetails(processId);
      setProcessInstance(response);
    } catch (error) {
      responseError = error;
      setFetchError(error);
    } finally {
      setIsLoading(false);
      if (responseError.length === 0 && fetchError.length === 0 && Object.keys(response).length === 0) {
        let prevPath;
        if (currentPage.current) {
          currentPage.current = Object.assign({}, currentPage.current, location.state);
          const tempPath = currentPage.current.prev.split("/");
          prevPath = tempPath.filter((item) => item);
        }
        navigate(
          {
            pathname: "../NoData",
          },
          {
            state: {
              prev: currentPage ? currentPage.current.prev : "/ProcessInstances",
              title: "Process not found",
              description: `Process instance with the id ${processId} not found`,
              buttonText: currentPage
                ? `Go to ${prevPath[0]
                    .replace(/([A-Z])/g, " $1")
                    .trim()
                    .toLowerCase()}`
                : "Go to process instances",
              rememberedData: Object.assign({}, location.state),
            },
          }
        );
      }
    }
  }, [channelApi, fetchError.length, navigate, processId, location.state]);

  useEffect(() => {
    if (processId) {
      fetchDetails();
    }
  }, [fetchDetails, processId]);

  const renderItems = () => {
    if (!isLoading) {
      return (
        <>
          {processInstance && Object.keys(processInstance).length > 0 && !fetchError ? (
            <ProcessDetailsContainer processInstance={processInstance} />
          ) : (
            <>
              {fetchError.length > 0 && (
                <Card className="kogito-management-console__card-size">
                  <Bullseye>
                    <ServerErrors error={fetchError} variant="large" />
                  </Bullseye>
                </Card>
              )}
            </>
          )}
        </>
      );
    } else {
      return (
        <Card>
          <KogitoSpinner spinnerText="Loading process details..." />
        </Card>
      );
    }
  };

  return (
    <>
      <PageSectionHeader titleText={`${appContext.customLabels.singularProcessLabel} Details`} ouiaId={ouiaId} />
      <PageSection {...componentOuiaProps(ouiaId, "process-details-page-section", ouiaSafe)}>
        {renderItems()}
      </PageSection>
    </>
  );
};

export default ProcessDetailsPage;
