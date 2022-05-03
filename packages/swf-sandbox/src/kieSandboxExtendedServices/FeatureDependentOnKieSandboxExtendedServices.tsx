/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import * as React from "react";
import { useAppI18n } from "../i18n";
import { useKieSandboxExtendedServices } from "./KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "./KieSandboxExtendedServicesStatus";

interface Props {
  children: any;
  isLight: boolean;
  position: "auto" | "top" | "bottom" | "left" | "right";
}

export function FeatureDependentOnKieSandboxExtendedServices(props: Props) {
  const { i18n } = useAppI18n();
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();

  if (kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.RUNNING) {
    return props.children;
  }
  return (
    <Tooltip
      content={i18n.kieSandboxExtendedServices.dropdown.tooltip.install}
      position={props.position}
      className={props.isLight ? "kogito--editor__light-tooltip" : ""}
    >
      {props.children}
    </Tooltip>
  );
}
