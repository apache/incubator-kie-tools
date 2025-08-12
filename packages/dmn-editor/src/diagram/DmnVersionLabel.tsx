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
import { DMN_LATEST_VERSION } from "@kie-tools/dmn-marshaller";
import { useDmnEditorI18n } from "../i18n";

const latestChangelogHref = `https://www.omg.org/spec/DMN/1.5/Beta1/PDF/changebar`;

export function DmnVersionLabel(props: { version: string }) {
  const { i18n } = useDmnEditorI18n();
  const label = useMemo(
    () => (
      <Label style={{ cursor: "pointer", position: "absolute", bottom: "8px", left: "calc(50% - 34px)", zIndex: 100 }}>
        {i18n.nodes.dmnversion(DMN_LATEST_VERSION)}
      </Label>
    ),
    [i18n.nodes]
  );

  if (props.version === DMN_LATEST_VERSION) {
    return <>{label}</>;
  }

  return (
    <Popover
      showClose={false}
      className={"kie-dmn-editor--version-popover"}
      aria-label="DMN version popover"
      position={PopoverPosition.top}
      headerContent={<div>Version upgraded!</div>}
      bodyContent={
        <div>
          {i18n.nodes.originallyImportedDmn(props.version, DMN_LATEST_VERSION)}
          <a href={latestChangelogHref} target={"_blank"}>
            &nbsp;{i18n.nodes.newInDmn(DMN_LATEST_VERSION)}.
          </a>
        </div>
      }
    >
      {label}
    </Popover>
  );
}
