/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { useEffect, useMemo, useState } from "react";
import { fetchEnvJson } from "./EnvironmentApi";
import { DEFAULT_ENVIRONMENT_VARIABLES, EnvironmentContext } from "./EnvironmentContext";

interface Props {
  children: React.ReactNode;
}

export function EnvironmentContextProvider(props: Props) {
  const [variables, setVariables] = useState(DEFAULT_ENVIRONMENT_VARIABLES);

  useEffect(() => {
    fetchEnvJson()
      .then((envVars) => {
        setVariables((previous) => ({ ...previous, ...envVars }));
      })
      .catch((e) => {
        // env json file could not be fetched, so we keep the default values
        console.debug(e);
      });
  }, []);

  const value = useMemo(
    () => ({
      variables,
    }),
    [variables]
  );

  return <EnvironmentContext.Provider value={value}>{props.children}</EnvironmentContext.Provider>;
}
