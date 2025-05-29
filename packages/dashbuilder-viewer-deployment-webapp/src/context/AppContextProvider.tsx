/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import React, { PropsWithChildren, useEffect, useMemo, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { Dashboard } from "../data";
import { useAppDataPromise } from "../hooks/useAppDataPromise";
import { routes } from "../routes";
import { AppContext } from "./AppContext";

export function AppContextProvider(props: PropsWithChildren<{}>) {
  const navigate = useNavigate();
  const location = useLocation();
  const appDataPromise = useAppDataPromise();
  const [current, setCurrent] = useState<Dashboard>();
  const [dashboards, setDashboards] = useState<Dashboard[]>([]);

  useEffect(() => {
    if (!appDataPromise.data) {
      return;
    }

    const pathUri = location.pathname.slice(1);
    const dashboards = [appDataPromise.data.primary, ...appDataPromise.data.secondary];
    const current = dashboards.find((d) => d.uri === pathUri) ?? appDataPromise.data.primary;

    setCurrent(current);
    setDashboards(dashboards);

    navigate(
      {
        pathname: routes.dashboard.path({
          filePath: current.uri,
        }),
      },
      { replace: true }
    );
  }, [appDataPromise.data, navigate, location.pathname]);

  const value = useMemo(
    () => ({
      dashboards,
      appDataPromise,
      current,
    }),
    [dashboards, current, appDataPromise]
  );

  return <AppContext.Provider value={value}>{props.children}</AppContext.Provider>;
}
