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
import { ProcessListChannelApiImpl } from "./ProcessListChannelApiImpl";

interface ProcessListContextProviderProps {
  apolloClient: ApolloClient<any>;
  children: ReactElement;
  options?: { transformEndpointBaseUrl?: (url?: string) => string | undefined };
}

export const ProcessListContextProvider: FC<ProcessListContextProviderProps> = ({
  apolloClient,
  children,
  options,
}) => {
  const processListChannelApiImpl = useMemo(() => {
    return new ProcessListChannelApiImpl(apolloClient, options);
  }, [apolloClient, options]);
  return <ProcessListContext.Provider value={processListChannelApiImpl}>{children}</ProcessListContext.Provider>;
};
