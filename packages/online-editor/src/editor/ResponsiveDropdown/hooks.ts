import { useEffect, useState } from "react";
import {
  Breakpoint,
  RelationToBreakpoint,
  responsiveBreakpoints,
} from "../../responsiveBreakpoints/ResponsiveBreakpoints";

function debounce(func: (...args: any) => any, timeout: number) {
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
    const getWidth = debounce(() => setWidth(window.innerWidth), 200);
    window.addEventListener("resize", getWidth);
    return () => window.removeEventListener("resize", getWidth);
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
