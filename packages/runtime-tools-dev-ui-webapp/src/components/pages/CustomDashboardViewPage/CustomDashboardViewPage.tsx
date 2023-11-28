/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from "react";
import { OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { PageTitle } from "@kogito-apps/consoles-common/dist/components/layout/PageTitle";
import { useHistory } from "react-router-dom";
import CustomDashboardViewContainer from "../../containers/CustomDashboardViewContainer/CustomDashboardViewContainer";
import { CustomDashboardInfo } from "@kie-tools/runtime-tools-enveloped-components/dist/customDashboardList";

const CustomDashboardViewPage: React.FC<OUIAProps> = () => {
  const history = useHistory();
  const dashboardInfo: CustomDashboardInfo = history.location.state["data"];
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
