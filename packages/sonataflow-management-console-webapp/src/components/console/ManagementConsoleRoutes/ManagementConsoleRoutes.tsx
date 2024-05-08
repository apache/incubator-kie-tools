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
import { Redirect, Route, Switch } from "react-router-dom";
import EmptyPage from "../../pages/EmptyPage";
import { RuntimeToolsWorkflowDefinitions } from "../../../runtimeTools/pages/RuntimeToolsWorkflowDefinitions";
import { routes } from "../../../navigation/Routes";
import { RuntimeToolsWorkflowForm } from "../../../runtimeTools/pages/RuntimeToolsWorkflowForm";
import { RuntimeToolsWorkflowInstances } from "../../../runtimeTools/pages/RuntimeToolsWorkflowInstances";
import { RuntimeToolsWorkflowDetails } from "../../../runtimeTools/pages/RuntimeToolsWorkflowDetails";

const ManagementConsoleRoutes: React.FC = () => {
  return (
    <Switch>
      <Route
        exact
        path={routes.home.path({})}
        render={() => <Redirect to={routes.runtimeToolsWorkflowInstances.path({})} />}
      />
      <Route exact path={routes.runtimeToolsWorkflowInstances.path({})} component={RuntimeToolsWorkflowInstances} />
      <Route exact path={routes.runtimeToolsWorkflowDefinitions.path({})} component={RuntimeToolsWorkflowDefinitions} />
      <Route path={routes.runtimeToolsWorkflowDetails.path({ workflowId: ":workflowId" })}>
        {({ match }) => <RuntimeToolsWorkflowDetails workflowId={match!.params.workflowId!} />}
      </Route>
      <Route exact path={routes.runtimeToolsTriggerCloudEvent.path({})} component={EmptyPage} />
      <Route path={routes.runtimeToolsWorkflowForm.path({ workflowName: ":workflowName" })}>
        <RuntimeToolsWorkflowForm />
      </Route>
      <Route exact path={routes.monitoring.path({})} component={EmptyPage} />
    </Switch>
  );
};

export default ManagementConsoleRoutes;
