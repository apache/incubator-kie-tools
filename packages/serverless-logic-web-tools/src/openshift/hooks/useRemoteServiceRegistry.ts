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

import { useCallback } from "react";
import { isServiceAccountConfigValid } from "../../settings/serviceAccount/ServiceAccountConfig";
import { isServiceRegistryConfigValid } from "../../settings/serviceRegistry/ServiceRegistryConfig";
import { useSettings, useSettingsDispatch } from "../../settings/SettingsContext";

const DEFAULT_GROUP_ID = "org.kie";

export function useRemoteServiceRegistry() {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();

  const uploadArtifact = useCallback(
    async (args: { groupId?: string; artifactId: string; content: string }) => {
      if (!isServiceAccountConfigValid(settings.serviceAccount.config)) {
        throw new Error("Invalid service account config");
      }

      if (!isServiceRegistryConfigValid(settings.serviceRegistry.config)) {
        throw new Error("Invalid service registry config");
      }

      await settingsDispatch.serviceRegistry.catalogStore.uploadArtifact({
        artifactId: args.artifactId,
        groupId: args.groupId ?? DEFAULT_GROUP_ID,
        content: args.content,
      });
    },
    [settings.serviceAccount.config, settings.serviceRegistry.config, settingsDispatch.serviceRegistry.catalogStore]
  );

  return { uploadArtifact };
}
