/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { isOpenShiftConnectionValid } from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";
import { useCallback } from "react";
import { useSettings, useSettingsDispatch } from "../../settings/SettingsContext";

const OPEN_API_PATH = "q/openapi?format=json";

export function useOpenApi() {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();

  const fetchOpenApiContent = useCallback(
    async (resourceName: string) => {
      if (!isOpenShiftConnectionValid(settings.openshift.config)) {
        throw new Error("Invalid OpenShift config");
      }

      const routeUrl = await settingsDispatch.openshift.service.knative.getDeploymentRoute(resourceName);
      if (!routeUrl) {
        throw new Error(`No route found for ${resourceName}`);
      }

      try {
        const response = await fetch(`${routeUrl}/${OPEN_API_PATH}`);
        if (!response.ok) {
          return;
        }

        return await response.text();
      } catch (error) {
        console.debug(error);
        return;
      }
    },
    [settings.openshift.config, settingsDispatch.openshift.service]
  );

  return { fetchOpenApiContent };
}
