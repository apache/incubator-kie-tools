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

import * as React from "react";
import { HashRouter } from "react-router-dom";
import { EnvContextProvider } from "./env/EnvContextProvider";
import { EditorEnvelopeLocatorContextProvider } from "./envelopeLocator/EditorEnvelopeLocatorContext";
import { AppI18nContextProvider } from "./i18n";
import { NavigationContextProvider } from "./navigation/NavigationContextProvider";
import { RoutesSwitch } from "./navigation/RoutesSwitch";
import { OpenShiftContextProvider } from "./openshift/OpenShiftContextProvider";
import { SettingsContextProvider } from "./settings/SettingsContext";
import { VirtualServiceRegistryContextProvider } from "./virtualServiceRegistry/VirtualServiceRegistryContextProvider";
import { SampleContextProvider } from "./samples/hooks/SampleContext";
import { DevModeContextProvider } from "./openshift/swfDevMode/DevModeContext";
import { GlobalAlertsContextProvider } from "./alerts/GlobalAlertsContext";
import { EditorContextProvider } from "./editor/hooks/EditorContext";
import { WebToolsWorkspaceContextProvider } from "./workspace/hooks/WebToolsWorkspaceContextProvider";
import { UpgradeContextProvider } from "./upgrade/UpgradeContext";
import { WebToolsWorkflowDefinitionListContextProvider } from "./runtimeTools/contexts/WebToolsWorkflowDefinitionListContextProvider";
import { WebToolsWorkflowListContextProvider } from "./runtimeTools/contexts/WebToolsWorkflowListContextProvider";
import { WebToolsWorkflowDetailsContextProvider } from "./runtimeTools/contexts/WebToolsWorkflowDetailsContextProvider";
import { WebToolsWorkflowFormContextProvider } from "./runtimeTools/contexts/WebToolsWorkflowFormContextProvider";
import { WebToolsCloudEventFormContextProvider } from "./runtimeTools/contexts/WebToolsCloudEventFormContextProvider";

export const App = () => (
  <HashRouter>
    {nest(
      [AppI18nContextProvider, {}],
      [EditorEnvelopeLocatorContextProvider, {}],
      [EnvContextProvider, {}],
      [SettingsContextProvider, {}],
      [GlobalAlertsContextProvider, []],
      [WebToolsWorkspaceContextProvider, []],
      [UpgradeContextProvider, []],
      [OpenShiftContextProvider, {}],
      [DevModeContextProvider, {}],
      [VirtualServiceRegistryContextProvider, {}],
      [SampleContextProvider, {}],
      [NavigationContextProvider, {}],
      [EditorContextProvider, {}],
      [WebToolsWorkflowDefinitionListContextProvider, {}],
      [WebToolsWorkflowListContextProvider, {}],
      [WebToolsWorkflowDetailsContextProvider, {}],
      [WebToolsWorkflowFormContextProvider, {}],
      [WebToolsCloudEventFormContextProvider, {}],
      // Insert new context providers from here to beginning, always before RoutesSwitch
      [RoutesSwitch, {}]
    )}
  </HashRouter>
);

function nest(...components: Array<[(...args: any[]) => any, object]>) {
  return components.reduceRight((acc, [Component, props]) => {
    return <Component {...props}>{acc}</Component>;
  }, <></>);
}
