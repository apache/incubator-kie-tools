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

import * as React from "react";
import { HashRouter } from "react-router-dom";
import { EnvContextProvider } from "./env/EnvContextProvider";
import { EditorEnvelopeLocatorContextProvider } from "./envelopeLocator/EditorEnvelopeLocatorContext";
import { AppI18nContextProvider } from "./i18n";
import { KieSandboxExtendedServicesContextProvider } from "./kieSandboxExtendedServices/KieSandboxExtendedServicesContextProvider";
import { NavigationContextProvider } from "./navigation/NavigationContextProvider";
import { RoutesSwitch } from "./navigation/RoutesSwitch";
import { OpenShiftContextProvider } from "./openshift/OpenShiftContextProvider";
import { SettingsContextProvider } from "./settings/SettingsContext";
import { VirtualServiceRegistryContextProvider } from "./workspace/services/virtualServiceRegistry/VirtualServiceRegistryContextProvider";
import { WorkspacesContextProvider } from "./workspace/WorkspacesContextProvider";

export const App = () => (
  <HashRouter>
    {nest(
      [AppI18nContextProvider, {}],
      [EditorEnvelopeLocatorContextProvider, {}],
      [EnvContextProvider, {}],
      [KieSandboxExtendedServicesContextProvider, {}],
      [SettingsContextProvider, {}],
      [WorkspacesContextProvider, {}],
      [OpenShiftContextProvider, {}],
      [VirtualServiceRegistryContextProvider, {}],
      [NavigationContextProvider, {}],
      [RoutesSwitch, {}]
    )}
  </HashRouter>
);

function nest(...components: Array<[(...args: any[]) => any, object]>) {
  return components.reduceRight((acc, [Component, props]) => {
    return <Component {...props}>{acc}</Component>;
  }, <></>);
}
