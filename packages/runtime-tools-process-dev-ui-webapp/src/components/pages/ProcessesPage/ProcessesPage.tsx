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
import React, { ReactText, useEffect, useState } from "react";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Tab, Tabs, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import { RouteComponentProps } from "react-router-dom";
import { StaticContext } from "react-router";
import * as H from "history";
import ProcessListContainer from "../../containers/ProcessListContainer/ProcessListContainer";
import "../../styles.css";
import ProcessDefinitionListContainer from "../../containers/ProcessDefinitionListContainer/ProcessDefinitionListContainer";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";
import {
  OUIAProps,
  componentOuiaProps,
  ouiaPageTypeAndObjectId,
} from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { ProcessListState } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { PageSectionHeader } from "@kie-tools/runtime-tools-components/dist/components/PageSectionHeader";

interface MatchProps {
  instanceID: string;
}

const ProcessesPage: React.FC<RouteComponentProps<MatchProps, StaticContext, H.LocationState> & OUIAProps> = ({
  ouiaId,
  ouiaSafe,
  ...props
}) => {
  const apiContext = useDevUIAppContext();

  const [activeTabKey, setActiveTabKey] = useState<ReactText>(0);
  useEffect(() => {
    return ouiaPageTypeAndObjectId("process-instances");
  });

  const initialState: ProcessListState = props.location && (props.location.state as ProcessListState);

  const handleTabClick = (event, tabIndex) => {
    setActiveTabKey(tabIndex);
  };
  return (
    <React.Fragment>
      {activeTabKey === 0 && (
        <PageSectionHeader titleText={`${apiContext.customLabels.singularProcessLabel} Instances`} ouiaId={ouiaId} />
      )}
      {activeTabKey === 1 && (
        <PageSectionHeader titleText={`${apiContext.customLabels.singularProcessLabel} Definitions`} ouiaId={ouiaId} />
      )}
      <div>
        <Tabs
          activeKey={activeTabKey}
          onSelect={handleTabClick}
          isBox
          variant="light300"
          style={{
            background: "white",
          }}
        >
          <Tab
            id="process-list-tab"
            eventKey={0}
            title={<TabTitleText>{apiContext.customLabels.singularProcessLabel} Instances</TabTitleText>}
          >
            <PageSection {...componentOuiaProps(ouiaId, "process-list-page-section", ouiaSafe)}>
              <Card className="Dev-ui__card-size">
                <ProcessListContainer initialState={initialState} />
              </Card>
            </PageSection>
          </Tab>
          <Tab
            id="process-definitions-tab"
            eventKey={1}
            title={<TabTitleText>{apiContext.customLabels.singularProcessLabel} Definitions</TabTitleText>}
          >
            <PageSection {...componentOuiaProps(ouiaId, "process-definition-list-page-section", ouiaSafe)}>
              <Card className="Dev-ui__card-size">
                <ProcessDefinitionListContainer />
              </Card>
            </PageSection>
          </Tab>
        </Tabs>
      </div>
    </React.Fragment>
  );
};

export default ProcessesPage;
