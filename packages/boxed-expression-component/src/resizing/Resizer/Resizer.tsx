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
import { useCallback, useMemo } from "react";
import { Resizable } from "react-resizable";
import { v4 as uuid } from "uuid";
import { ResizingWidth } from "../../resizing/ResizingWidthsContext";
import "./Resizer.css";

export const DEFAULT_MIN_WIDTH = 100;

export interface ResizerProps {
  minWidth: number | undefined;
  width: number | undefined;
  setWidth: ((width: number | undefined) => void) | undefined;
  resizingWidth: ResizingWidth | undefined;
  setResizingWidth: ((getNewResizingWidth: (prev: ResizingWidth) => ResizingWidth) => void) | undefined;
  setResizing?: React.Dispatch<React.SetStateAction<boolean>>;
}

export const Resizer: React.FunctionComponent<ResizerProps> = ({
  minWidth,
  width,
  setWidth,
  resizingWidth,
  setResizingWidth,
  setResizing,
}) => {
  const minConstraints = useMemo<[number, number]>(() => {
    return [minWidth ?? DEFAULT_MIN_WIDTH, 0];
  }, [minWidth]);

  const onResizeStop = useCallback(
    (_, data) => {
      setResizing?.(false);
      setResizingWidth?.((prev) => ({ value: Math.floor(data.size.width), isPivoting: false }));
      setWidth?.(resizingWidth?.value);
    },
    [resizingWidth?.value, setResizing, setResizingWidth, setWidth]
  );

  const onResize = useCallback(
    (_, data) => {
      setResizingWidth?.(() => ({ value: Math.floor(data.size.width), isPivoting: true }));
    },
    [setResizingWidth]
  );

  const onResizeStart = useCallback(
    (_, data) => {
      setResizingWidth?.(() => ({ value: Math.floor(data.size.width), isPivoting: true }));
      setResizing?.(true);
    },
    [setResizing, setResizingWidth]
  );

  const onDoubleClick = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();
      setWidth?.(minWidth ?? DEFAULT_MIN_WIDTH);
    },
    [minWidth, setWidth]
  );

  const style = useMemo(() => {
    return { width: resizingWidth?.value, minWidth };
  }, [minWidth, resizingWidth?.value]);

  const debuggingHandleClassNames = `
    ${minWidth === resizingWidth?.value ? "min" : ""} 
    ${(resizingWidth?.value ?? 0) < (minWidth ?? 0) ? "error" : ""}
  `;

  return (
    <div>
      {width && (
        <div className="pf-c-drawer" style={{ position: "absolute", left: width - 8 }}>
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

      {width && resizingWidth && (
        <Resizable
          width={resizingWidth?.value}
          height={0}
          onResize={onResize}
          onResizeStop={onResizeStop}
          onResizeStart={onResizeStart}
          minConstraints={minConstraints}
          className={"resizable-div"}
          axis={"x"}
          handle={
            <div className="pf-c-drawer" onDoubleClick={onDoubleClick}>
              <div className={`pf-c-drawer__splitter pf-m-vertical ${debuggingHandleClassNames}`}>
                <div className={`pf-c-drawer__splitter-handle`} />
              </div>
            </div>
          }
        >
          <div style={style} />
        </Resizable>
      )}
    </div>
  );
};
