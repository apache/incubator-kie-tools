import { useEffect, useRef } from "react";

export function useEffectAfterFirstRender(effect: Parameters<typeof useEffect>[0], b: React.DependencyList) {
  const didMountRef = useRef(false);

  useEffect(() => {
    if (didMountRef.current) {
      return effect();
    }
    didMountRef.current = true;
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, b);
}
