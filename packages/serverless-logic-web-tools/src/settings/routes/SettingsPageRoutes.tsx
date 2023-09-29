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

import React from "react";
import { Redirect, Switch } from "react-router";
import { Route } from "react-router-dom";
import { useRoutes } from "../../navigation/Hooks";
import { GitHubSettings } from "../github/GitHubSettings";
import { OpenShiftSettings } from "../openshift/OpenShiftSettings";
import { ServiceAccountSettings } from "../serviceAccount/ServiceAccountSettings";
import { ServiceRegistrySettings } from "../serviceRegistry/ServiceRegistrySettings";
import { SettingsPageProps } from "../types";
import { StorageSettings } from "../storage/StorageSettings";
import { RuntimeToolsSettings } from "../runtimeTools/RuntimeToolsSettings";

export function SettingsPageRoutes(props: {} & SettingsPageProps) {
  const routes = useRoutes();
  const settingsPageProps: SettingsPageProps = {
    pageContainerRef: props.pageContainerRef,
  };

  return (
    <Switch>
      <Route path={routes.settings.github.path({})}>
        <GitHubSettings {...settingsPageProps} />
      </Route>
      <Route path={routes.settings.openshift.path({})}>
        <OpenShiftSettings {...settingsPageProps} />
      </Route>
      <Route path={routes.settings.service_account.path({})}>
        <ServiceAccountSettings {...settingsPageProps} />
      </Route>
      <Route path={routes.settings.service_registry.path({})}>
        <ServiceRegistrySettings {...settingsPageProps} />
      </Route>
      <Route path={routes.settings.storage.path({})}>
        <StorageSettings />
      </Route>
      <Route path={routes.settings.runtime_tools.path({})}>
        <RuntimeToolsSettings {...settingsPageProps} />
      </Route>
      <Route>
        <Redirect to={routes.settings.github.path({})} />
      </Route>
    </Switch>
  );
}
