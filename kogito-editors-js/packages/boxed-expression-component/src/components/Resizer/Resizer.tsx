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

import * as _ from "lodash";
import * as React from "react";
import { useCallback, useContext, useLayoutEffect, useMemo, useState } from "react";
import { ResizableBox } from "react-resizable";
import { v4 as uuid } from "uuid";
import { BoxedExpressionGlobalContext } from "../../context";
import { widthValue as commonWidthValue } from "./common";
import { Cell, DEFAULT_MIN_WIDTH, DOMSession } from "./dom";
import "./Resizer.css";

export interface ResizerProps {
  width: number;
  height?: number | "100%";
  minWidth?: number;
  onHorizontalResizeStop?: (width: number) => void;
  children?: React.ReactElement;
}

export const Resizer: React.FunctionComponent<ResizerProps> = ({
  children,
  height = "100%",
  minWidth,
  onHorizontalResizeStop,
  width,
}) => {
  /*
   * States
   */

  const [resizerWidth, setResizerWidth] = useState(width);
  const [initialResizerWidth, setInitialResizerWidth] = useState(0);
  const [cells, setCells] = useState<Cell[]>([]);
  const { setSupervisorHash } = useContext(BoxedExpressionGlobalContext);

  /*
   * Memos
   */

  const id = useMemo(() => {
    return `uuid-${uuid()}`;
  }, []);

  const resizerMinWidth = useMemo(() => {
    return minWidth ?? DEFAULT_MIN_WIDTH;
  }, [minWidth]);

  const resizerClassName = useMemo(() => {
    const heightClass = height === "100%" ? "height-based-on-content" : "";
    return `${heightClass} ${id}`;
  }, [height, id]);

  /*
   * Effects
   */

  useLayoutEffect(() => {
    function listener(event: CustomEvent) {
      const width = Math.round(event.detail.width);
      setResizerWidth(width);
      onHorizontalResizeStop?.(width);
    }

    document.addEventListener(id, listener);
    return () => {
      document.removeEventListener(id, listener);
    };
  }, [id, onHorizontalResizeStop, resizerWidth]);

  /*
   * Callbacks
   */

  const widthValue = useCallback(commonWidthValue, []);

  const getApplicableCells = useCallback((allCells: Cell[], currentCell: Cell) => {
    const applicableCells: Cell[] = [];
    const parent = currentCell.element.closest("table");
    const currentRect = currentCell.getRect();

    const hasSameParent = (cell: Cell) => parent?.contains(cell.element);
    const isCellParent = (cell: Cell) => cell.element?.contains(currentCell.element);
    const containsCurrent = (cell: Cell) => {
      const cellRect = cell.getRect();
      // Rounding errors
      return (
        +Number(cellRect.x).toFixed(3) <= +Number(currentRect.x).toFixed(3) &&
        +Number(cellRect.right).toFixed(3) >= +Number(currentRect.right).toFixed(3)
      );
    };
    const isLastGroupColumn = (cell: Cell) => {
      if (!currentCell.isColSpanHeader()) {
        return false;
      }
      return cell.getRect().right === currentRect.right;
    };

    if (currentCell.isLastColumn()) {
      allCells
        .filter((cell) => cell.isLastColumn())
        .forEach((cell) => {
          applicableCells.push(cell);
        });
    } else {
      let hasSomeLastColumn = false;

      allCells.forEach((cell) => {
        const hasParentRelationship = hasSameParent(cell) || isCellParent(cell);
        const shareSamePosition = containsCurrent(cell) || isLastGroupColumn(cell);
        if (hasParentRelationship && shareSamePosition) {
          applicableCells.push(cell);
          if (cell.isLastColumn()) {
            hasSomeLastColumn = true;
          }
        }
      });

      if (hasSomeLastColumn) {
        allCells
          .filter((cell) => {
            return cell.isLastColumn() && !hasSameParent(cell);
          })
          .forEach((cell) => {
            applicableCells.push(cell);
          });
      }
    }

    applicableCells.forEach((cell) => {
      cell.element.dataset.initialWidth = cell.element.style.width;
    });

    return _.uniqBy(applicableCells, (cell) => cell.getId());
  }, []);

  const onResizeStart = useCallback(() => {
    const allCells = new DOMSession().getCells();
    const currentCell = allCells.find((c) => c.getId() === id)!;
    const applicableCells = getApplicableCells(allCells, currentCell);
    const initialWidth = widthValue(currentCell.getRect().width);

    setCells(applicableCells);
    setInitialResizerWidth(initialWidth);
  }, [getApplicableCells, id, widthValue]);

  const onResize = useCallback(
    (_, data) => {
      const newResizerWidth = parseInt(data.size.width + "");
      cells.forEach((cell) => {
        const delta = newResizerWidth - initialResizerWidth;
        const cellElement = cell.element;
        const isSameCell = cell.getId() === id;

        if (!isSameCell) {
          const cellInitialWidth = parseInt(cellElement.dataset.initialWidth + "");
          cellElement.style.width = cellInitialWidth + delta + "px";
        }
      });
    },
    [cells, id, initialResizerWidth]
  );

  const onResizeStop = useCallback(
    (_, data) => {
      const newResizerWidth = widthValue(data.size.width);

      cells.forEach((cell) => {
        const delta = newResizerWidth - initialResizerWidth;
        const cellInitialWidth = widthValue(cell.element.dataset.initialWidth);
        cell.setWidth(cellInitialWidth + delta);
      });

      setSupervisorHash("-");
    },
    [cells, initialResizerWidth, setSupervisorHash, widthValue]
  );

  return (
    <ResizableBox
      className={resizerClassName}
      width={resizerWidth}
      minConstraints={[resizerMinWidth, 0]}
      height={0}
      axis="x"
      onResize={onResize}
      onResizeStop={onResizeStop}
      onResizeStart={onResizeStart}
      handle={
        <div className="pf-c-drawer">
          <div className="pf-c-drawer__splitter pf-m-vertical">
            <div className="pf-c-drawer__splitter-handle" />
          </div>
        </div>
      }
    >
      {children}
    </ResizableBox>
  );
};
