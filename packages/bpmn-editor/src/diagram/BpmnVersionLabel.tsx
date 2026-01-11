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
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { Popover, PopoverPosition } from "@patternfly/react-core/dist/js/components/Popover";
import { useMemo } from "react";
import { BPMN_LATEST_VERSION } from "@kie-tools/bpmn-marshaller";
import { useBpmnEditorI18n } from "../i18n";

const latestChangelogHref = `https://www.omg.org/spec/BPMN/2.0.2/PDF/changebar`;

export function BpmnVersionLabel(props: { version: string }) {
  const { i18n } = useBpmnEditorI18n();
  const label = useMemo(
    () => (
      <Label
        style={{ cursor: "pointer", position: "absolute", bottom: "8px", left: "calc(50% - 34px)", zIndex: 100 }}
      >{`BPMN ${BPMN_LATEST_VERSION}`}</Label>
    ),
    []
  );

  if (props.version === BPMN_LATEST_VERSION) {
    return <>{label}</>;
  }

  return (
    <Popover
      className={"kie-bpmn-editor--version-popover"}
      aria-label="BPMN version popover"
      position={PopoverPosition.top}
      headerContent={<div>{i18n.versionUpgraded}</div>}
      bodyContent={
        <div>
          {i18n.importedBpmn(props.version, BPMN_LATEST_VERSION)}
          <a href={latestChangelogHref} target={"_blank"}>
            &nbsp;{i18n.newInBpmn}.
          </a>
        </div>
      }
    >
      {label}
    </Popover>
  );
}
