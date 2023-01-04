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

import React from "react";
import { Redirect, Switch } from "react-router";
import { Route } from "react-router-dom";
import { useRoutes } from "../../navigation/Hooks";
import { GitHubSettings } from "../github/GitHubSettings";
import { KieSandboxExtendedServicesSettings } from "../extendedServices/KieSandboxExtendedServicesSettings";
import { FeaturePreviewSettingsTab } from "../../settings/featurePreview/FeaturePreviewSettingsTab";
import { ApacheKafkaSettingsTab } from "../../settings/kafka/ApacheKafkaSettingsTab";
import { OpenShiftSettings } from "../../newSettings/openshift/OpenShiftSettings";
import { ServiceAccountSettingsTab } from "../../settings/serviceAccount/ServiceAccountSettingsTab";
import { ServiceRegistrySettingsTab } from "../../settings/serviceRegistry/ServiceRegistrySettingsTab";

export function SettingsPageRoutes() {
  const routes = useRoutes();
  return (
    <Switch>
      <Route path={routes.settings.github.path({})}>
        <GitHubSettings />
      </Route>
      <Route path={routes.settings.kie_sandbox_extended_services.path({})}>
        <KieSandboxExtendedServicesSettings />
      </Route>
      <Route path={routes.settings.openshift.path({})}>
        <OpenShiftSettings />
      </Route>
      <Route path={routes.settings.service_account.path({})}>
        <ServiceAccountSettingsTab />
      </Route>
      <Route path={routes.settings.service_registry.path({})}>
        <ServiceRegistrySettingsTab />
      </Route>
      <Route path={routes.settings.kafka.path({})}>
        <ApacheKafkaSettingsTab />
      </Route>
      <Route path={routes.settings.feature_preview.path({})}>
        <FeaturePreviewSettingsTab />
      </Route>
      <Route>
        <Redirect to={routes.settings.github.path({})} />
      </Route>
    </Switch>
  );
}
