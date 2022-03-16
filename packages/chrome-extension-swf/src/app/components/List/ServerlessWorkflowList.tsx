/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { useMemo, useState } from "react";
import { useHistory } from "react-router";
import { DeploymentWorkflow } from "../../openshift/OpenShiftContext";
import { useRoutes } from "../../navigation/Hooks";
import { OnlineEditorPage } from "../../pageTemplate/OnlineEditorPage";

export function ServerlessWorkflowList() {
  const [workflows, setWorkflows] = useState<DeploymentWorkflow[]>([]);
  const routes = useRoutes();
  const history = useHistory();

  const emptyState = useMemo(
    () => (
      <EmptyState>
        <EmptyStateIcon icon={PlusCircleIcon} />
        <Title headingLevel="h4" size="lg">
          Your deployed Serverless Workflows are shown here
        </Title>
        <EmptyStateBody>
          For help getting started, access the <a>quick start guide</a>.
        </EmptyStateBody>
        <Button variant="primary" onClick={() => history.push({ pathname: routes.newWorskapce.path({}) })}>
          Create Serverless Workflow
        </Button>
      </EmptyState>
    ),
    [routes, history]
  );

  return (
    <OnlineEditorPage>
      <Page style={{ position: "relative" }}>
        <PageSection>
          {workflows.length === 0 ? (
            emptyState
          ) : (
            <div>{`# workflows ${workflows.length} - (TODO: workflow list + create workflow button go here)`}</div>
          )}
        </PageSection>
      </Page>
    </OnlineEditorPage>
  );
}
