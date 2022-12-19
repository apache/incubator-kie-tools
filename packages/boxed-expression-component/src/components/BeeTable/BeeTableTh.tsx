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
import * as PfReactTable from "@patternfly/react-table";
import { BeeTableTdsAndThsProps } from "../../api";

export interface BeeTableThProps extends BeeTableTdsAndThsProps {
  children?: React.ReactElement;
  className: string;
  thProps: Partial<PfReactTable.ThProps>;
  isFocusable: boolean;
  onClick?: React.MouseEventHandler;
  rowSpan: number;
  contextMenuThProps?: Pick<PfReactTable.ThProps, "onMouseDown">;
}

export function BeeTableTh({
  children,
  className,
  thProps,
  isFocusable = true,
  onKeyDown,
  onClick,
  rowIndex,
  contextMenuThProps,
  rowSpan = 1,
  xPosition,
  yPosition,
}: BeeTableThProps) {
  const thRef = useRef<HTMLTableCellElement>(null);

  useEffect(() => {
    const onKeyDownForIndex = onKeyDown(rowSpan);
    const th = thRef.current;
    th?.addEventListener("keydown", onKeyDownForIndex);
    return () => {
      th?.removeEventListener("keydown", onKeyDownForIndex);
    };
  }, [onKeyDown, rowIndex, rowSpan]);

  return (
    <PfReactTable.Th
      {...thProps}
      {...contextMenuThProps}
      ref={thRef}
      onClick={onClick}
      className={className}
      tabIndex={isFocusable ? -1 : undefined}
      data-xposition={xPosition}
      data-yposition={yPosition}
    >
      {children}
    </PfReactTable.Th>
  );
}
