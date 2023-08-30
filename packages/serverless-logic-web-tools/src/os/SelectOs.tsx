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

import { Select, SelectDirection, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { OperatingSystem } from "@kie-tools-core/operating-system";
import { AppI18n } from "../i18n";

interface Props {
  i18n?: AppI18n;
  direction: SelectDirection;
  className?: string;
  selected: OperatingSystem;
  onSelect: React.Dispatch<OperatingSystem>;
}

export type SelectOsRef = { getOperationalSystem: () => OperatingSystem } | null;

const LINUX = "Linux";
const MACOS = "macOS";
const WINDOWS = "Windows";

export function SelectOs(props: Props) {
  const [isSelectExpanded, setSelectIsExpanded] = useState(false);

  const onSelectOsToggle = useCallback((isExpanded) => {
    setSelectIsExpanded(isExpanded);
  }, []);

  const onSelectOperatingSystem = useCallback(
    (e, selection) => {
      props.onSelect(selection);
      setSelectIsExpanded(false);
    },
    [props]
  );

  const availableOperatingSystems = useMemo(
    () =>
      new Map<OperatingSystem, string>([
        [OperatingSystem.LINUX, props.i18n?.names.linux ?? LINUX],
        [OperatingSystem.MACOS, props.i18n?.names.macos ?? MACOS],
        [OperatingSystem.WINDOWS, props.i18n?.names.windows ?? WINDOWS],
      ]),
    [props.i18n]
  );

  return (
    <div className={props.className ?? ""} style={{ width: "140px" }}>
      <Select
        variant={SelectVariant.single}
        aria-label="Select operating system"
        onToggle={onSelectOsToggle}
        onSelect={onSelectOperatingSystem}
        selections={props.selected}
        isOpen={isSelectExpanded}
        aria-labelledby={"select-os"}
        isDisabled={false}
        direction={props.direction}
        menuAppendTo={"parent"}
      >
        {Array.from(availableOperatingSystems.entries()).map(([key, label]) => (
          <SelectOption isDisabled={false} key={key} value={key} isPlaceholder={false}>
            {label}
          </SelectOption>
        ))}
      </Select>
    </div>
  );
}
