/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { File } from "@kie-tooling-core/editor/dist/channel";
import * as React from "react";
import { useMemo } from "react";
import { Route, Switch } from "react-router";
import { HashRouter } from "react-router-dom";
import { GlobalContextProvider, SupportedFileExtensions, useGlobals } from "./common/GlobalContext";
import { EditorPage } from "./editor/EditorPage";
import { OnlineI18nContextProvider } from "./common/i18n";
import { HomePage } from "./home/HomePage";
import { DownloadHubModal } from "./home/DownloadHubModal";
import { NoMatchPage } from "./NoMatchPage";
import { KieToolingExtendedServicesContextProvider } from "./editor/KieToolingExtendedServices/KieToolingExtendedServicesContextProvider";
import { SettingsContextProvider } from "./settings/SettingsContext";

export function App(props: { externalFile?: File; senderTabId?: string }) {
  return (
    <HashRouter>
      {nest(
        [OnlineI18nContextProvider, {}],
        [GlobalContextProvider, props],
        [KieToolingExtendedServicesContextProvider, {}],
        [SettingsContextProvider, {}],
        [RoutesSwitch, {}]
      )}
    </HashRouter>
  );
}

function RoutesSwitch() {
  const globals = useGlobals();

  const supportedExtensions = useMemo(
    () => Array.from(globals.editorEnvelopeLocator.mapping.keys()).join("|"),
    [globals.editorEnvelopeLocator]
  );

  return (
    <Switch>
      <Route path={globals.routes.editor.path({ extension: `:extension(${supportedExtensions})` })}>
        {({ match }) => <EditorPage forExtension={match!.params.extension as SupportedFileExtensions} />}
      </Route>
      <Route exact={true} path={globals.routes.home.path({})}>
        <HomePage />
      </Route>
      <Route exact={true} path={globals.routes.download.path({})}>
        <HomePage />
        <DownloadHubModal />
      </Route>
      <Route component={NoMatchPage} />
    </Switch>
  );
}

function nest(...components: Array<[(...args: any[]) => any, object]>) {
  return components.reduceRight((acc, [Component, props]) => {
    return <Component {...props}>{acc}</Component>;
  }, <></>);
}
