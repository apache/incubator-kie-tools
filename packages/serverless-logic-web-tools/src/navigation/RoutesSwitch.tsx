/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { useMemo, useRef, useState } from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { useRoutes } from "./Hooks";
import { OnlineEditorPage } from "../homepage/pageTemplate/OnlineEditorPage";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { HomePageRoutes } from "../homepage/routes/HomePageRoutes";
import { SettingsPageRoutes } from "../settings/routes/SettingsPageRoutes";

export function RoutesSwitch() {
  const routes = useRoutes();
  const isRouteInSettingsSection = useRouteMatch(routes.settings.home.path({}));
  const buildInfo = useMemo(() => {
    return process.env["WEBPACK_REPLACE__buildInfo"];
  }, []);
  const pageContainerRef = useRef<HTMLDivElement>(null);
  const [isNavOpen, setIsNavOpen] = useState(true);

  const renderPage = () => {
    return (
      <OnlineEditorPage pageContainerRef={pageContainerRef} isNavOpen={isNavOpen} setIsNavOpen={setIsNavOpen}>
        {!isRouteInSettingsSection ? (
          <HomePageRoutes isNavOpen={isNavOpen} />
        ) : (
          <SettingsPageRoutes pageContainerRef={pageContainerRef} />
        )}
        {buildInfo && (
          <div className={"kie-tools--build-info"}>
            <Label>{buildInfo}</Label>
          </div>
        )}
      </OnlineEditorPage>
    );
  };

  return (
    <Switch>
      <Route path={routes.home.path({})} render={renderPage}></Route>
    </Switch>
  );
}
