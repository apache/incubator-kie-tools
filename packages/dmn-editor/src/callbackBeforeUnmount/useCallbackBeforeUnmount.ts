import { useRef, useEffect } from "react";

export function useCallbackBeforeUnmount(callback: () => void) {
  const shouldRun = useRef(false);
  useEffect(() => {
    return () => {
      shouldRun.current = true;
    };
  }, []);

  useEffect(() => {
    return () => {
      if (shouldRun.current) {
        callback();
      }
    };
  }, [callback]);
}
