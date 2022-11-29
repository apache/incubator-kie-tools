/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { useEffect, useRef } from "react";
import { Th } from "@patternfly/react-table";
import { BeeTableCellComponent } from "../../api";

export interface BeeThCellProps extends BeeTableCellComponent {
  children?: React.ReactElement;
  className: string;
  headerProps: any;
  isFocusable: boolean;
  onClick?: () => void;
  rowSpan: number;
  thProps?: any;
}

export function BeeThCell({
  children,
  className,
  headerProps,
  isFocusable = true,
  onKeyDown,
  onClick,
  rowIndex,
  thProps,
  rowSpan = 1,
  xPosition,
  yPosition,
}: BeeThCellProps) {
  const thRef = useRef<HTMLElement>(null);

  useEffect(() => {
    const onKeyDownForIndex = onKeyDown(rowSpan);
    const cell = thRef.current;
    cell?.addEventListener("keydown", onKeyDownForIndex);
    return () => {
      cell?.removeEventListener("keydown", onKeyDownForIndex);
    };
  }, [onKeyDown, rowIndex, rowSpan]);

  return (
    <Th
      {...headerProps}
      {...thProps}
      ref={thRef}
      onClick={onClick}
      className={className}
      tabIndex={isFocusable ? "-1" : undefined}
      data-xposition={xPosition}
      data-yposition={yPosition}
    >
      {children}
    </Th>
  );
}
