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
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { CogIcon } from "@patternfly/react-icons/dist/js/icons/cog-icon";
import { useSettingsDispatch } from "./SettingsContext";
import { Link } from "react-router-dom";
import { useRoutes } from "../navigation/Hooks";

export function SettingsButton() {
  const settingsDispatch = useSettingsDispatch();
  const routes = useRoutes();

  return (
    <Link to={routes.settings.home.path({})}>
      <Button
        variant={ButtonVariant.plain}
        aria-label="Settings"
        className={"kie-tools--masthead-hoverable-dark"}
        ouiaId={"settings-button"}
      >
        <CogIcon />
      </Button>
    </Link>
  );
}
