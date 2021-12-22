import React, { useEffect, useState, useCallback, useMemo } from "react";

import global_breakpoint_xs from "@patternfly/react-tokens/dist/esm/global_breakpoint_xs";
import global_breakpoint_sm from "@patternfly/react-tokens/dist/esm/global_breakpoint_sm";
import global_breakpoint_md from "@patternfly/react-tokens/dist/esm/global_breakpoint_md";
import global_breakpoint_lg from "@patternfly/react-tokens/dist/esm/global_breakpoint_lg";
import global_breakpoint_xl from "@patternfly/react-tokens/dist/esm/global_breakpoint_xl";
import global_breakpoint_2xl from "@patternfly/react-tokens/dist/esm/global_breakpoint_2xl";

export type Breakpoint = "2xl" | "xl" | "lg" | "md" | "sm" | "xs";

const getPxValue = ({ value }: { value: string }) => {
  return parseInt(value.replace("px", ""), 10);
};

const breakpoints: Record<Breakpoint, number> = {
  xs: getPxValue(global_breakpoint_xs),
  sm: getPxValue(global_breakpoint_sm),
  md: getPxValue(global_breakpoint_md),
  lg: getPxValue(global_breakpoint_lg),
  xl: getPxValue(global_breakpoint_xl),
  "2xl": getPxValue(global_breakpoint_2xl),
};

export function useWindowWidth() {
  const [width, setWidth] = useState(() => window.innerWidth);
  const throttle = useCallback((func: (...args: any) => any, timeout: number) => {
    let ready = true;
    return (...args: any) => {
      if (!ready) {
        return;
      }

      ready = false;
      func(...args);
      setTimeout(() => {
        ready = true;
      }, timeout);
    };
  }, []);
  const getWidth = useMemo(() => throttle(() => setWidth(window.innerWidth), 200), [throttle]);

  useEffect(() => {
    window.addEventListener("resize", getWidth);
    return () => window.removeEventListener("resize", getWidth);
  }, [getWidth]);

  return width;
}

export function useIsAboveBreakpoint(targetBreakpoint: Breakpoint) {
  const width = useWindowWidth();

  return width >= breakpoints[targetBreakpoint];
}

export function useIsBelowBreakpoint(targetBreakpoint: Breakpoint) {
  const width = useWindowWidth();

  return width < breakpoints[targetBreakpoint];
}
