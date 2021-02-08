/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { Select, SelectDirection, SelectOption, SelectVariant } from "@patternfly/react-core";
import * as React from "react";
import { useCallback, useImperativeHandle, useMemo, useState } from "react";
import { getOperatingSystem, OperatingSystem } from "./utils";
import { OnlineI18n } from "./i18n";

interface Props {
  i18n?: OnlineI18n;
  direction: SelectDirection;
  style?: React.CSSProperties;
  className?: string;
}

export type SelectOsRef = { getOperationalSystem: () => OperatingSystem } | null;

const LINUX = "Linux";
const MACOS = "MacOS";
const WINDOWS = "Windows";

const SelectOsForwardRef: React.ForwardRefRenderFunction<SelectOsRef, Props> = (props: Props, forwardRef) => {
  const [operationalSystem, setOperationalSystem] = useState(getOperatingSystem() ?? OperatingSystem.LINUX);
  const [isSelectExpanded, setSelectIsExpanded] = useState(false);

  const onSelectOsToggle = useCallback(isExpanded => {
    setSelectIsExpanded(isExpanded);
  }, []);

  const onSelectOperatingSystem = useCallback((e, selection) => {
    setOperationalSystem(selection);
    setSelectIsExpanded(false);
  }, []);

  const availableOperatingSystems = useMemo(
    () =>
      new Map<OperatingSystem, string>([
        [OperatingSystem.LINUX, props.i18n?.names.linux ?? LINUX],
        [OperatingSystem.MACOS, props.i18n?.names.macos ?? MACOS],
        [OperatingSystem.WINDOWS, props.i18n?.names.windows ?? WINDOWS]
      ]),
    []
  );

  useImperativeHandle(forwardRef, () => ({ getOperationalSystem: () => operationalSystem }));

  return (
    <div className={props.className ?? ""} style={{ width: "140px", ...props.style }}>
      <Select
        variant={SelectVariant.single}
        aria-label="Select operating system"
        onToggle={onSelectOsToggle}
        onSelect={onSelectOperatingSystem}
        selections={operationalSystem}
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
};

export const SelectOs = React.forwardRef(SelectOsForwardRef);
