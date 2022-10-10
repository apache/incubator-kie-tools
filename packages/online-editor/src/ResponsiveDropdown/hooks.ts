import { useEffect, useState } from "react";
import {
  Breakpoint,
  RelationToBreakpoint,
  responsiveBreakpoints,
} from "../responsiveBreakpoints/ResponsiveBreakpoints";

export function useWindowWidth() {
  const [width, setWidth] = useState(() => window.innerWidth);

  useEffect(() => {
    let task: ReturnType<typeof setTimeout>;
    const refreshWidth = () => {
      clearTimeout(task);
      task = setTimeout(() => setWidth(window.innerWidth), 100);
    };

    window.addEventListener("resize", refreshWidth);
    return () => {
      window.removeEventListener("resize", refreshWidth);
      clearTimeout(task);
    };
  }, []);

  return width;
}

export function useWindowSizeRelationToBreakpoint(breakpoint: Breakpoint): RelationToBreakpoint {
  const width = useWindowWidth();

  if (width >= responsiveBreakpoints[breakpoint]) {
    return RelationToBreakpoint.Above;
  }
  return RelationToBreakpoint.Below;
}
