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

import React, { useEffect, useRef, useState } from "react";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { CustomDashboardViewDriver } from "../../../api/CustomDashboardViewDriver";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";

export interface CustomDashboardViewProps {
  isEnvelopeConnectedToChannel: boolean;
  driver: CustomDashboardViewDriver;
  customDashboardName: string;
  targetOrigin: string;
}

const CustomDashboardView: React.FC<CustomDashboardViewProps & OUIAProps> = ({
  isEnvelopeConnectedToChannel,
  driver,
  ouiaId,
  customDashboardName,
  targetOrigin,
  ouiaSafe,
}) => {
  const ref = useRef<HTMLIFrameElement>(null);
  const [dashboardContent, setDashboardContent] = useState<string>();
  const [errorMessage, setErrorMessage] = useState<string>();
  const [isError, setError] = useState<boolean>(false);
  const [isReady, setReady] = useState<boolean>(false);
  driver
    .getCustomDashboardContent(customDashboardName)
    .then((value) => setDashboardContent(value))
    .catch((error) => {
      setError(true);
      setErrorMessage(error.message);
    });

  window.addEventListener("message", (e) => {
    if (e.origin !== targetOrigin) {
      return;
    }
    if (e.data == "ready") {
      setReady(true);
    }
  });

  useEffect(() => {
    if (isReady && ref) {
      ref.current?.contentWindow?.postMessage(dashboardContent, "*");
    }
  });

  return (
    <>
      {isError ? (
        <>
          {isEnvelopeConnectedToChannel && (
            <Card className="kogito-custom-dashboard-view-__card-size">
              <Bullseye>
                <ServerErrors error={errorMessage} variant="large" />
              </Bullseye>
            </Card>
          )}
        </>
      ) : (
        <iframe
          ref={ref}
          id="db"
          src="resources/webapp/custom-dashboard-view/dashbuilder/index.html"
          style={{ width: "100%", height: "100%", padding: "10px" }}
          {...componentOuiaProps(ouiaId, "customDashboard-view", ouiaSafe)}
        />
      )}
    </>
  );
};

export default CustomDashboardView;
