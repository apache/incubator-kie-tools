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
import { useCallback, useLayoutEffect, useMemo, useState } from "react";
import { Resizable } from "react-resizable";
import { v4 as uuid } from "uuid";
import { DEFAULT_MIN_WIDTH } from "./dom";
import "./Resizer.css";

export interface ResizerProps {
  width?: number;
  setWidth?: (width: number) => void;
  setResizingWidth?: (width: number) => void;
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
  setResizingWidth,
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

  const [isResizing, setResizing] = useState(false);
  const onResizeStop = useCallback(
    (_, data) => {
      setResizing(false);
      setWidth?.(data.size.width);
      _setResizingWidth(data.size.width);
      setResizingWidth?.(data.size.width);
    },
    [setResizingWidth, setWidth]
  );

  const [_resizingWidth, _setResizingWidth] = useState(width);
  useLayoutEffect(() => {
    _setResizingWidth(width);
  }, [width]);

  const onResize = useCallback(
    (_, data) => {
      console.info(data);
      _setResizingWidth(data.size.width);
      setResizingWidth?.(data.size.width);
    },
    [setResizingWidth]
  );

  const onResizeStart = useCallback((_, data) => {
    setResizing(true);
  }, []);

  const onDoubleClick = useCallback(() => {
    setWidth?.(minWidth ?? DEFAULT_MIN_WIDTH);
  }, [minWidth, setWidth]);

  return (
    <>
      {(width && (
        <Resizable
          width={_resizingWidth!}
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
                  minWidth === _resizingWidth ? (isResizing ? "smallest" : "min") : ""
                }`}
              >
                <div className={`pf-c-drawer__splitter-handle`} />
              </div>
            </div>
          }
        >
          <div style={{ width: _resizingWidth, minWidth }}>{children}</div>
        </Resizable>
      )) || (
        <>
          <div style={{ width: width, minWidth }}>{children}</div>
        </>
      )}
    </>
  );
};
