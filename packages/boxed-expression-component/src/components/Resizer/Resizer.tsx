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

import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { Resizable } from "react-resizable";
import { v4 as uuid } from "uuid";
import { ResizingWidth } from "../ExpressionDefinitionRoot";
import "./Resizer.css";

export const DEFAULT_MIN_WIDTH = 100;

export interface ResizerProps {
  actualWidth?: number;
  width?: number;
  setWidth?: (width: number | undefined) => void;
  resizingWidth?: ResizingWidth;
  setResizingWidth?: (getNewResizingWidth: (prev: ResizingWidth) => ResizingWidth) => void;
  height?: number | "100%";
  minWidth?: number;
  children?: React.ReactElement;
}

export const Resizer: React.FunctionComponent<ResizerProps> = ({
  children,
  height = "100%",
  minWidth,
  width,
  setWidth,
  resizingWidth,
  setResizingWidth,
  actualWidth,
}) => {
  const id = useMemo(() => {
    return `uuid-${uuid()}`;
  }, []);

  const minConstraints = useMemo<[number, number]>(() => {
    return [minWidth ?? DEFAULT_MIN_WIDTH, 0];
  }, [minWidth]);

  const resizerClassName = useMemo(() => {
    const heightClass = height === "100%" ? "height-based-on-content" : "";
    return `${heightClass} ${id}`;
  }, [height, id]);

  const [__resizingWidth, __setResizingWidth] = useState<ResizingWidth>({
    value: width ?? DEFAULT_MIN_WIDTH,
    isPivoting: false,
  }); // internal

  const onResizeStop = useCallback(
    (_, data) => {
      (setResizingWidth ?? __setResizingWidth)((prev) => ({ value: Math.floor(data.size.width), isPivoting: false }));
      setWidth?.(resizingWidth?.value);
    },
    [resizingWidth, setResizingWidth, setWidth]
  );

  const onResize = useCallback(
    (_, data) => {
      (setResizingWidth ?? __setResizingWidth)((prev) => ({ value: Math.floor(data.size.width), isPivoting: true }));
    },
    [setResizingWidth]
  );

  const onResizeStart = useCallback(
    (_, data) => {
      (setResizingWidth ?? __setResizingWidth)((prev) => ({ value: Math.floor(data.size.width), isPivoting: true }));
    },
    [setResizingWidth]
  );

  const onDoubleClick = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();
      (setResizingWidth ?? __setResizingWidth)((prev) => ({
        value: minWidth ?? DEFAULT_MIN_WIDTH,
        isPivoting: false,
      }));
      setWidth?.(minWidth ?? DEFAULT_MIN_WIDTH);
    },
    [minWidth, setResizingWidth, setWidth]
  );

  return (
    <div style={{ position: "relative", height: "100%" }}>
      {actualWidth && (
        <div className="pf-c-drawer" style={{ position: "absolute", left: actualWidth - 8 }}>
          <div className={`pf-c-drawer__splitter pf-m-vertical actual`}>
            <div className={`pf-c-drawer__splitter-handle`} />
          </div>
        </div>
      )}

      {width && minWidth && (
        <div className="pf-c-drawer" style={{ position: "absolute", left: minWidth - 8 }}>
          <div className={`pf-c-drawer__splitter pf-m-vertical min-basis`}>
            <div className={`pf-c-drawer__splitter-handle`} />
          </div>
        </div>
      )}

      {(width && (
        <Resizable
          width={resizingWidth?.value ?? __resizingWidth.value}
          height={0}
          onResize={onResize}
          onResizeStop={onResizeStop}
          onResizeStart={onResizeStart}
          minConstraints={minConstraints}
          className={resizerClassName}
          axis={"x"}
          handle={
            <div className="pf-c-drawer" onDoubleClick={onDoubleClick}>
              <div
                className={`pf-c-drawer__splitter pf-m-vertical ${
                  minWidth === (resizingWidth?.value ?? __resizingWidth.value) ? "min" : ""
                } ${(resizingWidth?.value ?? __resizingWidth.value ?? 0) < (minWidth ?? 0) ? "error" : ""}`}
              >
                <div className={`pf-c-drawer__splitter-handle`} />
              </div>
            </div>
          }
        >
          <div style={{ width: resizingWidth?.value ?? __resizingWidth.value, minWidth }}>{children}</div>
        </Resizable>
      )) || (
        <>
          <div style={{ minWidth }}>{children}</div>
        </>
      )}
    </div>
  );
};
