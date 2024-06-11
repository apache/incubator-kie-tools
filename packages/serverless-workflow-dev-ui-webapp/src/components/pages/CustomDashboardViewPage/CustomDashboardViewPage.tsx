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
import { OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { PageTitle } from "@kie-tools/runtime-tools-components/dist/components/PageTitle";
import { useHistory } from "react-router-dom";
import CustomDashboardViewContainer from "../../containers/CustomDashboardViewContainer/CustomDashboardViewContainer";
import { CustomDashboardInfo } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

export interface CustomDashboardViewPageState {
  data: CustomDashboardInfo;
}

const CustomDashboardViewPage: React.FC<OUIAProps> = () => {
  const history = useHistory();
  const initialState = history.location && (history.location.state as CustomDashboardViewPageState);
  const dashboardInfo: CustomDashboardInfo = initialState.data;

  return (
    <React.Fragment>
      <PageSection variant="light">
        <PageTitle title={dashboardInfo.name} />
      </PageSection>
      <PageSection>
        <Card className="Dev-ui__card-size Dev-ui__custom-dashboard-viewer">
          <CustomDashboardViewContainer dashboardName={dashboardInfo.name} />
        </Card>
      </PageSection>
    </React.Fragment>
  );
};
export default CustomDashboardViewPage;
