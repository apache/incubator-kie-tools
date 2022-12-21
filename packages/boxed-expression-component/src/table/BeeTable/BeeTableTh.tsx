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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import * as PfReactTable from "@patternfly/react-table";
import { BeeTableTdsAndThsProps } from "../../api";
import PlusIcon from "@patternfly/react-icons/dist/js/icons/plus-icon";

export interface BeeTableThProps extends BeeTableTdsAndThsProps {
  groupType: string | undefined;
  onColumnAdded?: (args: { beforeIndex: number; groupType: string | undefined }) => void;
  className: string;
  thProps: Partial<PfReactTable.ThProps>;
  isFocusable: boolean;
  onClick?: React.MouseEventHandler;
  rowSpan: number;
  contextMenuThProps?: Pick<PfReactTable.ThProps, "onMouseDown">;
  isLastLevelColumn: boolean;
}

export type HoverInfo =
  | {
      isHovered: false;
    }
  | {
      isHovered: true;
      part: "left" | "right";
    };

export function BeeTableTh({
  onColumnAdded,
  children,
  className,
  thProps,
  isFocusable = true,
  onKeyDown,
  onClick,
  rowIndex,
  columnIndex,
  contextMenuThProps,
  rowSpan = 1,
  xPosition,
  yPosition,
  groupType,
  isLastLevelColumn,
}: React.PropsWithChildren<BeeTableThProps>) {
  const thRef = useRef<HTMLTableCellElement>(null);

  useEffect(() => {
    const onKeyDownForIndex = onKeyDown(rowSpan);
    const th = thRef.current;
    th?.addEventListener("keydown", onKeyDownForIndex);
    return () => {
      th?.removeEventListener("keydown", onKeyDownForIndex);
    };
  }, [onKeyDown, rowIndex, rowSpan]);

  const [hoverInfo, setHoverInfo] = useState<HoverInfo>({
    isHovered: false,
  });

  const onAddColumnButtonClick = useCallback(
    (e: React.MouseEvent) => {
      if (!hoverInfo.isHovered) {
        return;
      }
      e.stopPropagation();

      // This index doesn't take into account the row index column, so we actually need to subtract 1.
      onColumnAdded?.({ beforeIndex: hoverInfo.part === "left" ? columnIndex - 1 : columnIndex, groupType: groupType });

      if (hoverInfo.part === "left") {
        setHoverInfo({ isHovered: false });
      }
    },
    [columnIndex, groupType, hoverInfo, onColumnAdded]
  );

  const onMouseEnter = useCallback((e: React.MouseEvent<HTMLTableCellElement>) => {
    e.stopPropagation();
    return setHoverInfo(getHoverInfo(e, thRef.current!));
  }, []);

  const onMouseLeave = useCallback((e: React.MouseEvent<HTMLTableCellElement>) => {
    e.stopPropagation();
    return setHoverInfo({ isHovered: false });
  }, []);

  const onMouseMove = useCallback((e: React.MouseEvent) => {
    return setHoverInfo((prev) => {
      e.stopPropagation();
      return getHoverInfo(e, thRef.current!);
    });
  }, []);

  const addColumButtonStyle = useMemo(() => {
    return {
      bottom: "5px",
      ...(hoverInfo.isHovered && hoverInfo.part === "left"
        ? {
            left: "-10px",
          }
        : {
            right: "-10px",
          }),
    };
  }, [hoverInfo]);

  return (
    <PfReactTable.Th
      {...thProps}
      {...contextMenuThProps}
      ref={thRef}
      onClick={onClick}
      onMouseEnter={onMouseEnter}
      onMouseMove={onMouseMove}
      onMouseLeave={onMouseLeave}
      className={className}
      tabIndex={isFocusable ? -1 : undefined}
      data-xposition={xPosition}
      data-yposition={yPosition}
    >
      {children}
      {hoverInfo.isHovered && onColumnAdded && isLastLevelColumn && (
        <div onClick={onAddColumnButtonClick} className={"add-column-button"} style={addColumButtonStyle}>
          <PlusIcon size="sm" />
        </div>
      )}
    </PfReactTable.Th>
  );
}

function getHoverInfo(e: React.MouseEvent, elem: HTMLElement): HoverInfo {
  const rect = elem.getBoundingClientRect();
  const localX = e.clientX - rect.left; // x position within the element.
  const part = localX < rect.width / 2 ? "left" : "right"; // upper part is the upper half
  return { isHovered: true, part };
}
