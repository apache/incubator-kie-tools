import { useCallback } from "react";
import React, { useEffect, useState } from "react";

import global_breakpoint_xs from "@patternfly/react-tokens/dist/esm/global_breakpoint_xs";
import global_breakpoint_sm from "@patternfly/react-tokens/dist/esm/global_breakpoint_sm";
import global_breakpoint_md from "@patternfly/react-tokens/dist/esm/global_breakpoint_md";
import global_breakpoint_lg from "@patternfly/react-tokens/dist/esm/global_breakpoint_lg";
import global_breakpoint_xl from "@patternfly/react-tokens/dist/esm/global_breakpoint_xl";
import global_breakpoint_2xl from "@patternfly/react-tokens/dist/esm/global_breakpoint_2xl";

export type Breakpoints = "2xl" | "xl" | "lg" | "md" | "sm" | "xs";

const getPxValue = ({ value }: { value: string }) => {
  return parseInt(value.replace("px", ""), 10);
};

const breakpoints: Record<Breakpoints, number> = {
  xs: getPxValue(global_breakpoint_xs),
  sm: getPxValue(global_breakpoint_sm),
  md: getPxValue(global_breakpoint_md),
  lg: getPxValue(global_breakpoint_lg),
  xl: getPxValue(global_breakpoint_xl),
  "2xl": getPxValue(global_breakpoint_2xl),
};

function throttle(func: (...args: any) => any, timeout: number) {
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
}

const getBreakpointFromWidth = (width: number): Breakpoints => {
  if (width > breakpoints["2xl"]) {
    return "2xl";
  } else if (width > breakpoints.xl) {
    return "xl";
  } else if (width > breakpoints.lg) {
    return "lg";
  } else if (width > breakpoints.md) {
    return "md";
  } else if (width > breakpoints.sm) {
    return "sm";
  }
  return "xs";
};

const useBreakpoint = () => {
  const [breakpoint, setBreakpoint] = useState(() => getBreakpointFromWidth(window.innerWidth));

  useEffect(() => {
    const calcBreakpoint = throttle(() => setBreakpoint(getBreakpointFromWidth(window.innerWidth)), 200);
    window.addEventListener("resize", calcBreakpoint);
    return () => window.removeEventListener("resize", calcBreakpoint);
  }, []);

  return breakpoint;
};
export default useBreakpoint;
