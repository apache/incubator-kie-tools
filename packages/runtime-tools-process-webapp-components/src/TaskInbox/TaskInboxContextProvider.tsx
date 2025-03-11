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
import React, { ReactElement, useMemo } from "react";
import { ApolloClient } from "apollo-client";
import TaskInboxContext from "./TaskInboxContext";
import { TaskInboxGatewayApiImpl } from "./TaskInboxGatewayApi";
import { GraphQLTaskInboxQueries } from "./TaskInboxQueries";
import { useKogitoAppContext } from "@kie-tools/runtime-tools-components/dist/contexts/KogitoAppContext";

interface IOwnProps {
  apolloClient: ApolloClient<any>;
  children: ReactElement;
  options?: { transformEndpointBaseUrl?: (url?: string) => string };
}

export const TaskInboxContextProvider: React.FC<IOwnProps> = ({ apolloClient, children }) => {
  const appContext = useKogitoAppContext();
  const gatewayApi = useMemo(
    () => new TaskInboxGatewayApiImpl(new GraphQLTaskInboxQueries(apolloClient), () => appContext.getCurrentUser()),
    [apolloClient, appContext]
  );

  return <TaskInboxContext.Provider value={gatewayApi}>{children}</TaskInboxContext.Provider>;
};

export default TaskInboxContextProvider;
