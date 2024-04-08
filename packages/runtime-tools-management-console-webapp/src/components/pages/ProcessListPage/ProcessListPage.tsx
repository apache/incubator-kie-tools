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
import { useEffect } from "react";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { RouteComponentProps } from "react-router-dom";
import ProcessListContainer from "../../containers/ProcessListContainer/ProcessListContainer";
import { StaticContext } from "react-router";
import * as H from "history";
import "../../styles.css";
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

const ProcessListPage: React.FC<RouteComponentProps<MatchProps, StaticContext, H.LocationState> & OUIAProps> = (
  props
) => {
  useEffect(() => {
    return ouiaPageTypeAndObjectId("process-instances");
  });
  const initialState: ProcessListState = props.location && (props.location.state as ProcessListState);

  return (
    <>
      <PageSectionHeader
        titleText="Process Instances"
        breadcrumbText={["Home", "Processes"]}
        breadcrumbPath={["/"]}
        ouiaId={props.ouiaId}
      />
      <PageSection {...componentOuiaProps(props.ouiaId, "page-section-content", props.ouiaSafe)}>
        <Card className="kogito-management-console__card-size">
          <ProcessListContainer initialState={initialState} />
        </Card>
      </PageSection>
    </>
  );
};

export default ProcessListPage;
