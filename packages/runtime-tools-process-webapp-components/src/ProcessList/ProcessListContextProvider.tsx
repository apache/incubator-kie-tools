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
import React, { useMemo, FC, ReactElement } from "react";
import { ApolloClient } from "apollo-client";
import ProcessListContext from "./ProcessListContext";
import { ProcessListGatewayApiImpl } from "./ProcessListGatewayApi";
import { GraphQLProcessListQueries } from "./ProcessListQueries";

interface ProcessListContextProviderProps {
  apolloClient: ApolloClient<any>;
  children: ReactElement;
}

export const ProcessListContextProvider: FC<ProcessListContextProviderProps> = ({ apolloClient, children }) => {
  const gatewayApiImpl = useMemo(() => {
    return new ProcessListGatewayApiImpl(new GraphQLProcessListQueries(apolloClient));
  }, []);
  return <ProcessListContext.Provider value={gatewayApiImpl}>{children}</ProcessListContext.Provider>;
};

export default ProcessListContextProvider;
