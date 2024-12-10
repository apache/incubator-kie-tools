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
import { BrowserRouter } from "react-router-dom";
import { AuthSessionsContextProvider } from "../authSessions/AuthSessionsContext";
import { ManagementConsoleRoutes } from "./ManagementConsoleRoutes";
import { EnvContextProvider } from "../env/hooks/EnvContextProvider";
import { NavigationContextProvider } from "../navigation/NavigationContextProvider";

export const App: React.FC = () => {
  return (
    <BrowserRouter basename="" /** TODO: Tiago --> ENV VAR FOR BASE PATH */>
      <ManagementConsole />
    </BrowserRouter>
  );
};

export const ManagementConsole: React.FC = () => {
  return (
    <EnvContextProvider>
      <NavigationContextProvider>
        <AuthSessionsContextProvider>
          <ManagementConsoleRoutes />
        </AuthSessionsContextProvider>
      </NavigationContextProvider>
    </EnvContextProvider>
  );
};
