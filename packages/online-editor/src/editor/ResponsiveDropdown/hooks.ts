import { useEffect, useState } from "react";

import global_breakpoint_xs from "@patternfly/react-tokens/dist/esm/global_breakpoint_xs";
import global_breakpoint_sm from "@patternfly/react-tokens/dist/esm/global_breakpoint_sm";
import global_breakpoint_md from "@patternfly/react-tokens/dist/esm/global_breakpoint_md";
import global_breakpoint_lg from "@patternfly/react-tokens/dist/esm/global_breakpoint_lg";
import global_breakpoint_xl from "@patternfly/react-tokens/dist/esm/global_breakpoint_xl";
import global_breakpoint_2xl from "@patternfly/react-tokens/dist/esm/global_breakpoint_2xl";

export type Breakpoint = "2xl" | "xl" | "lg" | "md" | "sm" | "xs";

export enum RelationToBreakpoint {
  Above,
  Below,
}

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

export function useWindowWidth() {
  const [width, setWidth] = useState(() => window.innerWidth);

  useEffect(() => {
    const getWidth = throttle(() => setWidth(window.innerWidth), 200);
    window.addEventListener("resize", getWidth);
    return () => window.removeEventListener("resize", getWidth);
  }, []);

  return width;
}

export function useWindowSizeRelationToBreakpoint(breakpoint: Breakpoint): RelationToBreakpoint {
  const width = useWindowWidth();

  if (width >= breakpoints[breakpoint]) {
    return RelationToBreakpoint.Above;
  }
  return RelationToBreakpoint.Below;
}
