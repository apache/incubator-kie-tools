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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Resizable } from "react-resizable";
import { ResizingWidth, useResizerRef, useResizingWidthsDispatch } from "../../resizing/ResizingWidthsContext";
import { DEFAULT_MIN_WIDTH } from "../WidthConstants";
import "./Resizer.css";

export interface ResizerProps {
  minWidth: number | undefined;
  width: number | undefined;
  setWidth: React.Dispatch<React.SetStateAction<number | undefined>> | undefined;
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
  //
  // React batching strategy (begin)
  //
  // This is a hack to make React batch the multiple state updates we're doing here with the calls to `setWidth`.
  // Every call to `setWidth` mutates the expression, so batching is essential for performance reasons.
  // This effect runs once when resizingStop__data is truthy. Then, after running, it sets resizingStop__data to a falsy value, which short-circuits it.
  // This whole thing is responsible for allowing any cell to shrink the entire table when resized.

  const { getResizerRefs } = useResizingWidthsDispatch();

  const [resizingStop__data, setResizingStop__data] = useState(0);
  const onResizeStop = useCallback((_, data) => {
    setResizingStop__data(data.size.width);
  }, []);

  useEffect(() => {
    if (!resizingStop__data) {
      return;
    }

    for (const resizerRef of getResizerRefs()) {
      resizerRef.setWidth?.((prev) => {
        const prevWidth = prev ?? 0;
        const resizingWidthValue = resizerRef.resizingWidth?.value ?? prevWidth;
        return Math.min(resizingWidthValue, prevWidth);
      });
    }

    setResizing?.(false);
    setResizingWidth?.((prev) => ({ value: Math.floor(resizingStop__data), isPivoting: false }));
    setWidth?.(resizingWidth?.value);

    setResizingStop__data(0); // Prevent this effect from running after it ran. Let onResizeStop trigger it.
  }, [getResizerRefs, resizingWidth?.value, resizingStop__data, setResizing, setResizingWidth, setWidth]);

  //
  // React batching strategy (end)
  //

  const minConstraints = useMemo<[number, number]>(() => {
    return [minWidth ?? DEFAULT_MIN_WIDTH, 0];
  }, [minWidth]);

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
