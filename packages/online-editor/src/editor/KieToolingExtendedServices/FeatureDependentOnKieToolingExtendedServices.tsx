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
import { useOnlineI18n } from "../../common/i18n";
import { useKieToolingExtendedServices } from "./KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesStatus } from "./KieToolingExtendedServicesStatus";

interface Props {
  children: any;
  isLight: boolean;
  position: "auto" | "top" | "bottom" | "left" | "right";
}

export function FeatureDependentOnKieToolingExtendedServices(props: Props) {
  const { i18n } = useOnlineI18n();
  const kieToolingExtendedServices = useKieToolingExtendedServices();

  if (kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING) {
    return props.children;
  }
  return (
    <Tooltip
      content={i18n.kieToolingExtendedServices.dropdown.tooltip.install}
      position={props.position}
      className={props.isLight ? "kogito--editor__kie-tooling-extended-services-dropdown-tooltip" : ""}
    >
      {props.children}
    </Tooltip>
  );
}
