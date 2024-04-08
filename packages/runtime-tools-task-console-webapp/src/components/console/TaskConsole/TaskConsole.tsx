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
import React from "react";
import { BrowserRouter, Route, Switch } from "react-router-dom";
import { ApolloClient } from "apollo-client";
import TaskConsoleContextsProvider from "../../../context/TaskConsoleContext/TaskConsoleContextsProvider";
import taskConsoleLogo from "../../../static/taskConsoleLogo.svg";
import TaskConsoleNav from "../TaskConsoleNav/TaskConsoleNav";
import {
  KogitoAppContextProvider,
  UserContext,
} from "@kie-tools/runtime-tools-components/dist/contexts/KogitoAppContext";
import { PageLayout } from "@kie-tools/runtime-tools-components/dist/components/PageLayout";

interface Props {
  apolloClient: ApolloClient<any>;
  userContext: UserContext;
  children: React.ReactElement;
}

const TaskConsole: React.FC<Props> = ({ apolloClient, userContext, children }) => {
  const renderPage = (routeProps) => {
    return (
      <PageLayout
        BrandSrc={taskConsoleLogo}
        pageNavOpen={false}
        BrandAltText={"Task Console Logo"}
        BrandClick={() => routeProps.history.push("/")}
        withHeader={true}
        PageNav={<TaskConsoleNav pathname={routeProps.location.pathname} />}
      >
        {children}
      </PageLayout>
    );
  };

  return (
    <KogitoAppContextProvider userContext={userContext}>
      <TaskConsoleContextsProvider client={apolloClient}>
        <BrowserRouter>
          <Switch>
            <Route path="/" render={renderPage} />
          </Switch>
        </BrowserRouter>
      </TaskConsoleContextsProvider>
    </KogitoAppContextProvider>
  );
};

export default TaskConsole;
