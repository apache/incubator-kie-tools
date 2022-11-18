/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { ReactNode, useEffect, useMemo, useState } from "react";
import { useHistory, useLocation } from "react-router";
import { Dashboard } from "../data";
import { useAppDataPromise } from "../hooks/useAppDataPromise";
import { routes } from "../routes";
import { AppContext } from "./AppContext";

interface Props {
  children: ReactNode;
}

export function AppContextProvider(props: Props) {
  const history = useHistory();
  const location = useLocation();
  const appDataPromise = useAppDataPromise();
  const [current, setCurrent] = useState<Dashboard>();
  const [dashboards, setDashboards] = useState<Dashboard[]>([]);

  useEffect(() => {
    if (!appDataPromise.data) {
      return;
    }

    const pathUri = location.pathname.slice(1);
    const dashboards = [appDataPromise.data.appData.primary, ...appDataPromise.data.appData.secondary];
    const current = dashboards.find((d) => d.uri === pathUri) ?? appDataPromise.data.appData.primary;

    setCurrent(current);
    setDashboards(dashboards);

    history.replace({
      pathname: routes.dashboard.path({
        filePath: current.uri,
      }),
    });
  }, [appDataPromise.data, history, location.pathname]);

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
