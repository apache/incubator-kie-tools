/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import Dexie from "dexie";
import * as React from "react";
import { useState, useEffect } from "react";
import { SAMPLES_FS_MOUNT_POINT_PREFIX } from "../samples/SampleConstants";
import { useEnv } from "../env/EnvContext";

export const UpgradeContext = React.createContext<{}>({} as any);

const APP_CURRENT_VERSION_STORAGE_KEY = "SERVERLESS_LOGIC_WEB_TOOLS_VERSION";

export function UpgradeContextProvider(props: React.PropsWithChildren<{}>) {
  const { env } = useEnv();
  const [shouldExecuteUpgrade, setShouldExecuteUpgrade] = useState(false);

  useEffect(() => {
    const storedVersion = localStorage.getItem(APP_CURRENT_VERSION_STORAGE_KEY);
    if (storedVersion === env.SERVERLESS_LOGIC_WEB_TOOLS_VERSION) {
      return;
    }
    localStorage.setItem(APP_CURRENT_VERSION_STORAGE_KEY, env.SERVERLESS_LOGIC_WEB_TOOLS_VERSION);
    setShouldExecuteUpgrade(true);
  }, [env]);

  useEffect(() => {
    if (!shouldExecuteUpgrade) {
      return;
    }

    async function execute() {
      try {
        // clean up sample cache from other versions
        await Promise.all(
          (await Dexie.getDatabaseNames())
            .filter(
              (dbName) =>
                dbName !== env.SERVERLESS_LOGIC_WEB_TOOLS_VERSION && dbName.startsWith(SAMPLES_FS_MOUNT_POINT_PREFIX)
            )
            .map(async (dbName) => Dexie.delete(dbName))
        );
      } catch (e) {
        console.error(e);
      } finally {
        setShouldExecuteUpgrade(false);
      }
    }

    execute();
  }, [env, shouldExecuteUpgrade]);

  return <UpgradeContext.Provider value={{}}>{props.children}</UpgradeContext.Provider>;
}
