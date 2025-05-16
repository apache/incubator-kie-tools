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
import React, { useState, useCallback, useMemo, ReactElement } from "react";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import ProcessFormContainer from "../../containers/ProcessFormContainer/ProcessFormContainer";
import { useLocation, useNavigate } from "react-router-dom";
import { InlineEdit } from "./components/InlineEdit/InlineEdit";
import { ProcessDefinition } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { FormNotification, Notification } from "@kie-tools/runtime-tools-components/dist/components/FormNotification";
import { PageTitle } from "@kie-tools/runtime-tools-components/dist/components/PageTitle";
import { useProcessFormChannelApi } from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessForm";
import "../../styles.css";

const ProcessFormPage: React.FC = () => {
  const [notification, setNotification] = useState<Notification>();

  const location = useLocation();
  const navigate = useNavigate();

  const channelApi = useProcessFormChannelApi();

  const processDefinition = useMemo(() => location.state["processDefinition"] as ProcessDefinition, [location.state]);

  const showNotification = useCallback(
    (notificationType: Notification["type"], submitMessage: string, notificationDetails?: string) => {
      setNotification({
        type: notificationType,
        message: submitMessage,
        details: notificationDetails,
        customActions: [
          {
            label: "Go to Processes",
            onClick: () => {
              setNotification(null);
              navigate({ pathname: "/Processes" });
            },
          },
        ],
        close: () => {
          setNotification(null);
        },
      });
    },
    [navigate]
  );

  const onSubmitSuccess = useCallback(
    (id: string): void => {
      const message = `The process with id: ${id} has started successfully`;
      showNotification("success", message);
    },
    [showNotification]
  );

  const onSubmitError = useCallback(
    (details?: string) => {
      const message = "Failed to start the process.";
      showNotification("error", message, details);
    },
    [showNotification]
  );

  return (
    <React.Fragment>
      <PageSection variant="light">
        <PageTitle
          title={`Start ${processDefinition.processName}`}
          extra={
            <InlineEdit
              setBusinessKey={(bk) => channelApi.processForm__setBusinessKey(bk)}
              getBusinessKey={async () => await channelApi.processForm__getBusinessKey()}
            />
          }
        />
        {notification && (
          <div>
            <FormNotification notification={notification} />
          </div>
        )}
      </PageSection>
      <PageSection>
        <ProcessFormContainer
          processDefinitionData={processDefinition}
          onSubmitSuccess={onSubmitSuccess}
          onSubmitError={onSubmitError}
        />
      </PageSection>
    </React.Fragment>
  );
};

export default ProcessFormPage;
