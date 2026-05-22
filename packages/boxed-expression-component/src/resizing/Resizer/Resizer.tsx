/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { ResizeCallbackData, Resizable } from "react-resizable";
import { ResizingWidth, useResizingWidthsDispatch } from "../../resizing/ResizingWidthsContext";
import { DEFAULT_MIN_WIDTH } from "../WidthConstants";
import "./Resizer.css";

export interface ResizerProps {
  minWidth: number | undefined;
  width: number | undefined;
  setWidth: ((newWidth: number) => void) | undefined;
  resizingWidth: ResizingWidth | undefined;
  setResizingWidth: (newResizingWidth: ResizingWidth) => void;
  setResizing?: React.Dispatch<React.SetStateAction<boolean>>;
  getWidthToFitData?: () => number | undefined;
}

export const Resizer: React.FunctionComponent<ResizerProps> = ({
  minWidth,
  width,
  setWidth,
  resizingWidth,
  setResizingWidth,
  setResizing,
  getWidthToFitData,
}) => {
  //
  // React 18 automatic batching handles multiple state updates efficiently.
  // This whole thing is responsible for allowing any cell to shrink the entire table when resized.

  const { getResizerRefs, setResizing: _setResizing } = useResizingWidthsDispatch();

  const [startResizingWidth, setStartResizingWidth] = useState({ width: 0 });
  const onResizeStop = useCallback(
    (e: React.SyntheticEvent, data: ResizeCallbackData) => {
      if (e.nativeEvent instanceof MouseEvent && e.nativeEvent.detail === 2) {
        console.debug("Skipping resizeStop onMouseUp because onDoubleClick will handle it.");
        return;
      }

      const resizingStopWidth = Math.floor(data.size.width);

      if (resizingStopWidth === startResizingWidth.width) {
        console.debug(`Stop resizing (equal): ${resizingStopWidth}`);
      } else {
        console.debug(`Stop resizing (different): ${resizingStopWidth}`);
        for (const resizerRef of getResizerRefs()) {
          if (resizerRef.resizingWidth?.value !== resizerRef.width) {
            resizerRef.setWidth?.((prev) => resizerRef.resizingWidth?.value ?? prev ?? 0);
          } else {
            // Ignoring. Nothing to do.
          }
        }

        if (resizingStopWidth !== width) {
          setWidth?.(resizingStopWidth);
        }
      }

      setResizing?.(false);
      _setResizing(false);
      setResizingWidth?.({ value: resizingStopWidth, isPivoting: false });
    },
    [_setResizing, getResizerRefs, setResizing, setResizingWidth, setWidth, startResizingWidth.width, width]
  );

  //
  // onResizeStop batching strategy (end)
  //

  const minConstraints = useMemo<[number, number]>(() => {
    return [minWidth ?? DEFAULT_MIN_WIDTH, 0];
  }, [minWidth]);

  const onResize = useCallback(
    (_event: React.SyntheticEvent<Element>, data: ResizeCallbackData) => {
      setResizingWidth?.({ value: Math.floor(data.size.width), isPivoting: true });
    },
    [setResizingWidth]
  );

  const onResizeStart = useCallback(
    (_event: React.SyntheticEvent<Element> | undefined, data: ResizeCallbackData) => {
      const startResizingWidth = Math.floor(data.size.width);

      console.debug(`Start resizing: ${startResizingWidth}`);
      setStartResizingWidth({ width: startResizingWidth });
      setResizingWidth?.({ value: startResizingWidth, isPivoting: true });
      setResizing?.(true);
      _setResizing(true);
    },
    [_setResizing, setResizing, setResizingWidth]
  );

  const onDoubleClick = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();

      let widthToFitData;
      try {
        widthToFitData = getWidthToFitData?.();
      } catch (e) {
        // Ignore, as bugs can appear...
      }

      const newWidth = Math.max(widthToFitData ?? minWidth ?? DEFAULT_MIN_WIDTH, minWidth ?? DEFAULT_MIN_WIDTH);

      // This starts the resizing process again with the correct width.
      onResizeStart(undefined, { size: { width: newWidth } } as ResizeCallbackData);

      // React 18 automatic batching handles the state updates efficiently.
      // Pretend that the startResizingWidth is different from the one we're going to stop with.
      // NOTE: We cannot call onResizeStop(e, ...) here because e.nativeEvent.detail === 2 (double-click),
      // which causes onResizeStop to return early without saving widths to widthsById.
      setTimeout(() => {
        setStartResizingWidth({ width: 0 });
        for (const resizerRef of getResizerRefs()) {
          if (resizerRef.resizingWidth?.value !== resizerRef.width) {
            resizerRef.setWidth?.((prev) => resizerRef.resizingWidth?.value ?? prev ?? 0);
          }
        }
        if (newWidth !== width) {
          setWidth?.(newWidth);
        }
        setResizing?.(false);
        _setResizing(false);
        setResizingWidth?.({ value: newWidth, isPivoting: false });
      }, 0);
    },
    [
      _setResizing,
      getResizerRefs,
      getWidthToFitData,
      minWidth,
      onResizeStart,
      setResizing,
      setResizingWidth,
      setWidth,
      width,
    ]
  );

  const style = useMemo(() => {
    return { width: resizingWidth?.value, minWidth };
  }, [minWidth, resizingWidth?.value]);

  // COMMENTED OUT FOR DEBUGGING PURPOSES.
  const debuggingHandleClassNames = "";
  // `
  //   ${minWidth === resizingWidth?.value ? "min" : ""}
  //   ${(resizingWidth?.value ?? 0) < (minWidth ?? 0) ? "error" : ""}
  // `;

  return (
    <>
      {/* {width && (
        <div className="pf-v5-c-drawer" style={{ position: "absolute", left: width - 10 }}>
          <div className={`pf-v5-c-drawer__splitter pf-m-vertical actual`}>
            <div className={`pf-v5-c-drawer__splitter-handle`} />
          </div>
        </div>
      )}

      {width && minWidth && (
        <div className="pf-v5-c-drawer" style={{ position: "absolute", left: minWidth - 10 }}>
          <div className={`pf-v5-c-drawer__splitter pf-m-vertical min-basis`}>
            <div className={`pf-v5-c-drawer__splitter-handle`} />
          </div>
        </div>
      )} */}

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
            <div
              className="pf-v5-c-drawer"
              onDoubleClick={onDoubleClick}
              data-testid={"kie-tools--bee--resizer-handle"}
            >
              <div className={`pf-v5-c-drawer__splitter pf-m-vertical ${debuggingHandleClassNames}`}>
                <div className={`pf-v5-c-drawer__splitter-handle`} />
              </div>
            </div>
          }
        >
          <div style={style} />
        </Resizable>
      )}
    </>
  );
};
