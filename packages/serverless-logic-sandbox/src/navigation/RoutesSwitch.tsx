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
import { useMemo } from "react";
import { Route, Switch } from "react-router-dom";
import { useRoutes } from "./Hooks";
import { OnlineEditorPage } from "../newHomepage/pageTemplate/OnlineEditorPage";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { HomePageRoutes } from "../newHomepage/routes/HomePageRoutes";

export function RoutesSwitch() {
  const routes = useRoutes();
  const buildInfo = useMemo(() => {
    return process.env["WEBPACK_REPLACE__buildInfo"];
  }, []);

  const renderPage = (routeProps: {
    location: {
      pathname:
        | string
        | number
        | boolean
        | {}
        | React.ReactElement<any, string | React.JSXElementConstructor<any>>
        | React.ReactNodeArray
        | React.ReactPortal
        | null
        | undefined;
    };
  }) => {
    return (
      <OnlineEditorPage>
        <HomePageRoutes />
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
