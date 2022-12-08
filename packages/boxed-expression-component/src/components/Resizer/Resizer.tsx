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
import "./Resizer.css";

export const DEFAULT_MIN_WIDTH = 100;

export interface ResizerProps {
  actualWidth?: number;
  width?: number;
  setWidth?: (width: number | undefined) => void;
  resizingWidth?: number;
  setResizingWidth?: (width: number | undefined) => void;
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

  const [isResizingHappening, setResizeHappening] = useState(false);
  const [__resizingWidth, __setResizingWidth] = useState(width);

  React.useEffect(() => {
    const BC = new BroadcastChannel("resize");
    BC.onmessage = () => {
      if (resizingWidth !== width) {
        setWidth?.(resizingWidth); // This needs to be batched to avoid multiple states updates.
      }
    };

    return () => {
      BC.close();
    };
  }, [width, resizingWidth, setWidth]);

  const onResizeStop = useCallback(
    (_, data) => {
      setResizeHappening(false);
      (setResizingWidth ?? __setResizingWidth)(data.size.width);
      const BC = new BroadcastChannel("resize");
      BC.postMessage("RESIZE_STOP");
    },
    [setResizingWidth]
  );

  const onResize = useCallback(
    (_, data) => {
      (setResizingWidth ?? __setResizingWidth)(data.size.width);
    },
    [setResizingWidth]
  );

  const onResizeStart = useCallback((_, data) => {
    setResizeHappening(true);
  }, []);

  const onDoubleClick = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();
      (setResizingWidth ?? __setResizingWidth)(minWidth ?? DEFAULT_MIN_WIDTH);
      setWidth?.(minWidth ?? DEFAULT_MIN_WIDTH);
    },
    [minWidth, setResizingWidth, setWidth]
  );

  return (
    <>
      {actualWidth && (
        <div className="pf-c-drawer" style={{ position: "absolute", left: actualWidth }}>
          <div className={`pf-c-drawer__splitter pf-m-vertical actual`}>
            <div className={`pf-c-drawer__splitter-handle`} />
          </div>
        </div>
      )}

      {width && minWidth && (
        <div className="pf-c-drawer" style={{ position: "absolute", left: minWidth }}>
          <div className={`pf-c-drawer__splitter pf-m-vertical min-basis`}>
            <div className={`pf-c-drawer__splitter-handle`} />
          </div>
        </div>
      )}

      {(width && (
        <Resizable
          width={
            resizingWidth ??
            __resizingWidth ??
            (() => {
              throw new Error("oh-ow");
            })()
          }
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
                  minWidth === (resizingWidth ?? __resizingWidth) ? (isResizingHappening ? "smallest" : "min") : ""
                } ${(resizingWidth ?? __resizingWidth ?? 0) < (minWidth ?? 0) ? "error" : ""}`}
              >
                <div className={`pf-c-drawer__splitter-handle`} />
              </div>
            </div>
          }
        >
          <div style={{ width: resizingWidth ?? __resizingWidth, minWidth }}>{children}</div>
        </Resizable>
      )) || (
        <>
          <div style={{ minWidth }}>{children}</div>
        </>
      )}
    </>
  );
};
